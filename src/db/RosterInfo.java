package db;

import java.util.Date;

/**
 * A class containing information about rosters. 
 * The methods contain checks for invalid queries, which will result in
 * InvalidQueryExceptions being thrown, but it does not enforce "business
 * rules" such as checking for dates in the past.
 */
public class RosterInfo {

    // This class is not intended to be instantiated.
    private RosterInfo() {}

    /**
     * Gets the IDs of all drivers working on date
     */
    public static int[] getDrivers(Date date) {
        return database.busDatabase.select_ids("driver", "roster", "day", date, "");
    }
    
    /**
     * Gets the IDs of all drivers working today
     */
    public static int[] getDrivers() {
        return getDrivers(database.today());
    }
    
    /**
     * Gets the services each driver has to complete for the given date 
     */
    public static int[] getServices(int driver_id, Date date) {
        return database.busDatabase.select_ids("service", "roster", "day", date, "driver", driver_id, "service");
    }
    
    /**
     * Gets the services each driver has to complete for today
     */
    public static int[] getServices(int driver_id) {
        return getServices(driver_id, database.today());
    }
    
    /**
     * Gets the id of the bus allocated on the specified service and date 
     */
    public static int getBus(int service_id, Date date) {
    	return database.busDatabase.find_id("bus", "roster", "day", date, "service", service_id);
    }
    
    /**
     * Gets the id of the bus allocated today on the specified service 
     */
    public static int getBus(int service_id) {
    	return getBus(service_id, database.today());
    }

    /**
     * Determine whether a driver is required to work on a given date
     */
    public static boolean isWorking(int driver_id, Date date) {
        if (date == null) throw new InvalidQueryException("Date is null");
        if (driver_id  == 0   ) throw new InvalidQueryException("Nonexistent driver");
        database db = database.busDatabase;
        if (db.select_record("roster", "driver", driver_id, "day", date))
            return true;
        else
            return false;
    }
    
    /**
     * Determine whether a driver is required to work on a given date
     */
    public static boolean isWorking(int driver_id) {
        return isWorking(driver_id, database.today());
    }
    
    /**
     * Make a new association between driver, bus, service and date
     */
    public static void setWorking(int driver_id, int bus_id, int service_id, Date date) {
    	if (date == null || driver_id == 0 || bus_id == 0 || service_id == 0) 
    		throw new InvalidQueryException("Invalid Driver(" + driver_id +") / BusService("
    				+ bus_id + ") / Date("+ date + ")");
    	database.busDatabase.new_record("roster", new Object[][] {{"driver", driver_id},  {"bus", bus_id},
    															  {"day", date}, {"service", service_id}});
    }

}
