package ctrl;

import java.io.Serializable;
import java.util.ArrayList;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import db.DriverInfo;
import db.database;

public class Driver implements Serializable {
	public final static long serialVersionUID = 11L;
	public final static int MAX_TIME_PER_WEEK  = 50 * 60;    // max 50 hours of work per week
	public final static int MAX_TIME_PER_DAY   = 10 * 60;    // max 10 hours of work per day
	public final static int MAX_TIME_PER_SHIFT =  5 * 60;    // max  5 hours of work per shift
	public final static int MIN_BREAK_TIME     = 60;         // minimum 1h break

	private String name;
	private int id;
	private int timeWorkedThisWeek, timeLeftOnDay;
	private Service serviceBeforeBreak;
	private ArrayList<ArrayList<Service>> assignedServices;

	public Driver(int id) {
		this.id = id;
		this.setName();
		timeWorkedThisWeek = 0;
		assignedServices = new ArrayList<ArrayList<Service>>(7);
		for (int day = 0; day < 7; ++day)
			assignedServices.add(new ArrayList<Service>());
	}
	
	/**
	 * Connects to the database and creates a Driver with the login number
	 * @param number  -- the number identifying the user in the database (driver No, not driverId)
	 * @return        -- the driver if login was successful or null otherwise
	 */
	public static Driver tryLogin(String number) {
		if (database.busDatabase == null)             // normally it hasn't been opened, so open it
			database.openBusDatabase();
		
		int driverId = DriverInfo.findDriver(number);
		if (driverId == 0)                           // no driver with the given number in the db
			return null;
		
		Driver[] drivers = RosterGenerator.drivers;
		for (Driver d : drivers)
			if (d.getId() == driverId)
				return d;
		return new Driver(driverId);
	}

	/**
	 * Check all the holiday request is valid, and modify driver availability
	 * for his holiday
	 * 
	 * @param id
	 * @param startDate
	 * @param endDate
	 * @return An int identifying the error (0 = noError, 1 - ... , 2 - ... )
	 */
	public int requestHoliday(DateTime startDate, DateTime endDate) {
		int requestStatus = 0;

		// Situation 01:input start date without end date
		// Situation 02:input end date without start date
		// Done in the view

		// Situation 03:input end date before start date
		// Done in the view
		
		// Situation 04:date period selected is longer than left holiday date
		if (endDate.isAfter(startDate.plusDays(getHolidayRemaining())))
			requestStatus = 4;
		
		// Situation 05:select date is today or past date
		// Done in the view
		
		// Situation 06:there are weekdays that 10 drivers already requested
		else if (checkNumberOfRequest(startDate, endDate))
			requestStatus = 6;
		
		// Situation 07:there are weekdays that drivers already requested for holiday
		else if (checkDuplicatedRequest(startDate, endDate))
			requestStatus = 7;
		
		// If everything is fine, modify driver availability for his holiday
		if (requestStatus == 0)
			setHoliday(startDate, endDate);

		return requestStatus;
	}

	/**
	 * @param startDate
	 * @param endDate
	 * @return true if there is any weekdays that 10 drivers already requested
	 */
	private boolean checkNumberOfRequest(DateTime startDate, DateTime endDate) {
		while (startDate.isBefore(endDate)) {
			if (DriverInfo.totalOnHoliday(startDate.toDate()) >= 10)
				return true;
			startDate = startDate.plusDays(1);
		}
		return false;
	}

	/**
	 * @param startDate
	 * @param endDate
	 * @return true if there is any weekdays that drivers already requested for holiday
	 */
	private boolean checkDuplicatedRequest(DateTime startDate, DateTime endDate) {
		while (startDate.isBefore(endDate)) {
			if (!DriverInfo.isAvailable(id, startDate.toDate()))
				return true;
			startDate = startDate.plusDays(1);
		}
		return false;
	}

	/**
	 * From start to end date, set each day to be unavailable for given driver
	 * ID, and recalculate his holiday taken
	 * 
	 * @param driver
	 * @param startDate
	 * @param endDate
	 */
	private void setHoliday(DateTime startDate, DateTime endDate) {
		DateTime aDateTime = new DateTime(startDate);
		while (aDateTime.isBefore(endDate)) {
			DriverInfo.setAvailable(id, aDateTime.toDate(), false, 0);
			aDateTime = aDateTime.plusDays(1);
		}
		
		DriverInfo.setHolidaysTaken(id, DriverInfo.getHolidaysTaken(id) + 
										(int) new Duration(startDate, endDate).getStandardDays());
	}
	
	/**
	 * Verifies all rostering rules are met 
	 */
	private boolean canWork(Service s, ArrayList<Service> dayServices) {
		if (s.getDriverID() != 0)    // service is already taken
			return false;
		
		if (dayServices.size() == 0)    // hasn't worked at all
			return true;                // so can take this one
		
		// service needs to be after last one
		Service lastWorked = dayServices.get(dayServices.size() - 1);
		if (s.getStartTime() < lastWorked.getEndTime())
			return false;
		
		// how long this service takes, including waiting time
		int timeForThisService = s.getServiceLength();
		if (serviceBeforeBreak == null ||    // we didn't take a break or 
				lastWorked != serviceBeforeBreak) // this is not the first one after the break
			timeForThisService += s.getStartTime() - lastWorked.getEndTime(); // add the time between them

		/* RULE 1: 10 working hours in a DAY */
		if (timeLeftOnDay < timeForThisService)
			return false;

		/* RULE 2: 50 working hours in a WEEK */
		// working time left of that driver in that WEEK;
		int timeLeftThisWeek = MAX_TIME_PER_WEEK - timeWorkedThisWeek ;
		if (timeLeftThisWeek < timeForThisService)
			return false;

		/*
		 * RULE 3: maximum 5 hours in shift 1 -> have a break for at least 1
		 * hour then try again
		 */
		if (serviceBeforeBreak != null &&   // took a break and this service starts during break
				s.getStartTime() <  serviceBeforeBreak.getEndTime() + MIN_BREAK_TIME )
			return false;
		
		return true;
	}
	
	private void bindService(Service s, ArrayList<Service> dayServices, DateTime day, Bus[] buses) {
		s.setDriverID(this.id);
		
		// how long this service takes
		int timeForThisService = s.getServiceLength();
		Service lastWorked = null;
		
		if (dayServices.size() != 0) {                                  // this is not the first service
			lastWorked = dayServices.get(dayServices.size() - 1);
			if (serviceBeforeBreak == null ||
					lastWorked != serviceBeforeBreak)                  // nor the first one after the break
				timeForThisService += s.getStartTime() - lastWorked.getEndTime(); // so add the time between them
		}
		
		this.timeLeftOnDay -= timeForThisService;
		this.timeWorkedThisWeek += timeForThisService;
		if (serviceBeforeBreak == null && timeLeftOnDay <= MAX_TIME_PER_SHIFT) // this service should be in 2nd shift
			serviceBeforeBreak = lastWorked;
		dayServices.add(s);
		
		for (Bus bus : buses)
			if (bus.addService(s, day, timeForThisService))
				break;
	}
	
	/**
	 * Fills a driver with as many services as possible from the given list, while not breaking rostering rules
	 * @param day The day whose duties are filled
	 * @param services A list of daily services to choose from; the array should be sorted by start time
	 * @param buses A list of buses to choose from
	 * @return Number of filled services
	 */
	public int fillDuties(DateTime day, Service[] services, Bus[] buses) {
		int dayOfWeek = day.getDayOfWeek() - 1;    // indexed from 0
		timeLeftOnDay = MAX_TIME_PER_DAY; 
		serviceBeforeBreak = null;
		int filledServices = 0;
		
		ArrayList<Service> dayServices = assignedServices.get(dayOfWeek);
		
		for (Service s : services)
			if (canWork(s, dayServices)) {
				bindService(s, dayServices, day, buses);
				filledServices++;
			}

		return filledServices;
	}
	
	//========================== GETTERS & SETTERS ==========================
	public int getId() {
		return this.id;
	}
	
	public ArrayList<ArrayList<Service>> getServices() {
		return this.assignedServices;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName() {
		this.name = DriverInfo.getName(this.id);
	}

	/**
	 * get working time of this driver in this week (in minutes)
	 */
	public int getWorkingTimeThisWeek() {
		return timeWorkedThisWeek;
	}
	
	/**
	 * set working hour of this driver in this week (in minutes)
	 */
	public void setWorkingTimeThisWeek(int time) {
		timeWorkedThisWeek = time;
	}
	
	/**
	 * @return number of holidays remaining for that driver
	 */
	public int getHolidayRemaining() {
		return 25 - DriverInfo.getHolidaysTaken(this.id);
	}
}
