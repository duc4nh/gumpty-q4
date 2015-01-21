package ctrl;

import db.BusStopInfo;
import db.TimetableInfo;
import db.BusInfo;
import db.DriverInfo;
import db.RosterInfo;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import javax.swing.JTextArea;

import org.joda.time.DateTime;

public class RosterGenerator {
	
	private static final int NUMBER_OF_WEEKDAYS = 5;
	private static final int DAYS_IN_A_WEEK = 7;
	
	private static int servicesFilledToday = 0;
	private static DateTime startDate;
	
	// Skewed service array. Stores the services by [day][services]. Different days may have a different number of services
	// 0 is Monday
	public static Service[][] weeklyServices;
	
	// Stores a list of every driver
	public static Driver[] drivers;
	
	// Stores a list of every bus
	public static Bus[] buses;
	
	/**
	 * Creates all the objects necesary for a roster, binds them together and saves the result in the db
	 */
	public static void generateRoster(JTextArea display)
	{
		ArrayList<Driver> driverList = new ArrayList<Driver>();
		Iterator<Driver> driver;
		
		//Get todays date
		DateTime currentDate = DateTime.now();
		
		//get the date of the commencing monday
		while (currentDate.getDayOfWeek() != 1)
			currentDate = currentDate.plusDays(1);
		startDate = new DateTime(currentDate);
		
		display.append("Creating new data objects... ");
		createData(false);    // creates the data from scratch
		display.append("Done\n");
		
		display.append("Assigning drivers for day: ");
		// Assign the drivers for the week
		for (int day = 0; day < DAYS_IN_A_WEEK; day++)
		{
			 display.append(day + " ");
			 driverList = getAvailableDrivers(currentDate);
			 driver = driverList.iterator();
			 Arrays.sort(buses);             // sort buses by workload, to get fair allocation
			 while(servicesFilledToday < weeklyServices[day].length && driver.hasNext())
				 driver.next().fillDuties(currentDate, weeklyServices[day], buses);
			 currentDate = currentDate.plusDays(1);
		}
		display.append("Finished assignment!\n");
		
		display.append("Updating the database...\n");
		updateDB();
		display.append("Finished");
	}
	
	/**
	 * Returns a list of available drivers for a given date
	 * @param date The date for filtering the drivers
	 * @return An ArrayList of Drivers available on the day
	 */
	private static ArrayList<Driver> getAvailableDrivers(DateTime date)
	{
		ArrayList<Driver> availableDrivers = new ArrayList<Driver>();
		
		//Add a driver to the list if they are available
		for (int driverIndex = 0; driverIndex < drivers.length; driverIndex++)
			if (DriverInfo.isAvailable(drivers[driverIndex].getId(), date.toDate()))
				availableDrivers.add(drivers[driverIndex]);
		
		return availableDrivers;
	}
	
	/**
	 * Saves the roster information
	 */
	private static void updateDB()
	{
		// go through all drivers and set his working hour this week and this year
		for (int index = 0; index < drivers.length; index++) 
		{
			DriverInfo.setTimeThisWeek(drivers[index].getId(), drivers[index].getWorkingTimeThisWeek());
			DriverInfo.setTimeThisYear(drivers[index].getId(),
					DriverInfo.getTimeThisYear(drivers[index].getId()) + drivers[index].getWorkingTimeThisWeek());
		}

		// go through all the services, which contain the binding, and add them in roster
		for (int day = 0; day < DAYS_IN_A_WEEK; day++)
			for (Service s : weeklyServices[day])
				RosterInfo.setWorking(s.getDriverID(), s.getBusID(), s.getServiceID(), 
						startDate.plusDays(day).toDate());
		
		// go through all buses and update their workload
		for (Bus bus : buses)
			BusInfo.setWorkload(bus.getId(), bus.getWorkload());
		
		try {
			FileOutputStream fos = new FileOutputStream("roster.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(weeklyServices);
			oos.writeObject(drivers);
			oos.writeObject(buses);
			oos.flush();
			oos.close();
		}
		catch (Exception ex) {
			System.err.println(ex);
		}
	}
	
	/**
	 * Creates the required data for this program: services, drivers, busses
	 * @param existing Specifies wheter data should be new or loaded from file
	 */
	public static void createData(boolean existing) {
		if (!existing) {
			splitServices();    //Generate the service objects
			createDrivers();    //Generate the driver objects
			makeBuses();        // Generate the bus objects
			return;
		}
		
		try {
			FileInputStream fis = new FileInputStream("roster.ser");
			ObjectInputStream ois = new ObjectInputStream(fis);
			weeklyServices = (Service[][]) ois.readObject();
			drivers = (Driver[]) ois.readObject();
			buses   = (Bus[]) ois.readObject();
			
			ois.close();
		}
		catch (Exception ex) {
			System.err.println(ex);
		}
		
	}
	
	/**
	 * Generate the array of driver objects
	 */
	public static void createDrivers()
	{
		int[] driverIDs = DriverInfo.getDrivers();
		drivers = new Driver[driverIDs.length];
		
		for (int driverIndex = 0; driverIndex < driverIDs.length; driverIndex++)
			drivers[driverIndex] = new Driver(driverIDs[driverIndex]);
	}
	
	
	/**
	 * Splits the timetabled services into their individual services based on their kind
	 */
	public static void splitServices() {
		// get the route numbers
		int[] routes = BusStopInfo.getRoutes();

		// records the total number of services for each timetable kind
		int numOfWeekdayServices, numOfSaturdayServices, numOfSundayServices;
		numOfWeekdayServices = numOfSaturdayServices = numOfSundayServices = 0;

		// used as an offset when initialising services
		int weekdayServiceIndex, saturdayServiceIndex, sundayServiceIndex;
		weekdayServiceIndex = saturdayServiceIndex = sundayServiceIndex = 0;

		// get the number of services that run on each day, for every route
		for (int routeIndex = 0; routeIndex < routes.length; routeIndex++) {
			numOfWeekdayServices += TimetableInfo.getNumberOfServices(routes[routeIndex], TimetableInfo.timetableKind.weekday);
			numOfSaturdayServices += TimetableInfo.getNumberOfServices(routes[routeIndex], TimetableInfo.timetableKind.saturday);
			numOfSundayServices += TimetableInfo.getNumberOfServices(routes[routeIndex], TimetableInfo.timetableKind.sunday);
		}

		// create the space for each type of service
		
		weeklyServices = new Service[DAYS_IN_A_WEEK][];
		
		for(int day = 0; day < DAYS_IN_A_WEEK; day++)
		{
			switch (day)
			{
			case 5: // Assign space for saturday
				weeklyServices[day] = new Service[numOfSaturdayServices];
				break;
			case 6: // Assign space for sunday
				weeklyServices[day] = new Service[numOfSundayServices];
				break;
			default: // Assign space for weekdays
				weeklyServices[day] = new Service[numOfWeekdayServices];
				break;
			}
			
		}
		
		// Create the service object for every route for every timetableKind and store them in the corresponding service arrays
		for (int routeIndex = 0; routeIndex < routes.length; routeIndex++) {
			
			
			// Initialise the weekday services
			int[] weekdayServiceIds = TimetableInfo.getServices(routes[routeIndex], TimetableInfo.timetableKind.weekday);
			for (int serviceIndex = 0; serviceIndex < weekdayServiceIds.length; serviceIndex++) {
				for (int day = 0; day < NUMBER_OF_WEEKDAYS; day++)
					weeklyServices[day][weekdayServiceIndex] = new Service(routes[routeIndex], 
							weekdayServiceIds[serviceIndex],TimetableInfo.timetableKind.weekday);
	
				weekdayServiceIndex++;
			}
	
			// Initialise the Saturday services
			int[] saturdayServiceIds = TimetableInfo.getServices(routes[routeIndex], TimetableInfo.timetableKind.saturday);
			for (int serviceIndex = 0; serviceIndex < saturdayServiceIds.length; serviceIndex++) {
				Service nextService = new Service(routes[routeIndex], saturdayServiceIds[serviceIndex], TimetableInfo.timetableKind.saturday);
				weeklyServices[5][saturdayServiceIndex] = nextService;
				saturdayServiceIndex++;
			}
	
			// Initialise the Sunday services
			int[] sundayServiceIds = TimetableInfo.getServices(
					routes[routeIndex], TimetableInfo.timetableKind.sunday);
			for (int serviceIndex = 0; serviceIndex < sundayServiceIds.length; serviceIndex++) {
				Service nextService = new Service(routes[routeIndex], sundayServiceIds[serviceIndex], TimetableInfo.timetableKind.sunday);
				weeklyServices[6][sundayServiceIndex] = nextService;
				sundayServiceIndex++;
			}
		}

	}

	/**
	 * Creates the array of busses
	 */
	public static void makeBuses() {
		int[] busIds = BusInfo.getBuses();
		buses = new Bus[busIds.length];
		int busIdx = 0;
		for (int busId : busIds) {
			buses[busIdx++] = new Bus(busId);
		}
	}
}
