package db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Duration;

/**
 * A class providing information about timetables. This is given in a low-level
 * manner, e.g. with arrays numbers representing service times. The IBMS
 * itself should implement sensible classes to represent this information
 * in a more high-level intuitive way.<br><br>
 * 
 * A Service identifies a row of the timetable. The times for each service,
 * along with the timing point information in the BusStopInfo class provide
 * all the data in the timetable. 
 * 
 * None of the UCs in the pilot IBMS change timetable information, so this
 * interface provides read-only access
 */
public class TimetableInfo
{
  private static Calendar calendar = new GregorianCalendar();

  /**
   * The kinds of timetale available. Sunday services run on bank holidays.
   */
  public enum timetableKind {weekday, saturday, sunday};

  // This class is not intended to be instantiated
  private TimetableInfo() 
  { 
  }

  /**
   * Find the route with a given name. Route names can be found via the
   * BusStopInfo.getRouteName() method
   */
  public static int findRoute(String name)
  {
    return database.busDatabase.find_id("route", "name", name);
  }

  /**
   * Get the timing points on a given route
   */
  public static int[] getTimingPoints(int route)
  {
    if (route == 0) throw new InvalidQueryException("Nonexistent route");
    String source = database.join("timetable_line", "daily_timetable", "daily_timetable");
    return database.busDatabase.select_ids("Distinct timing_point", source, "daily_timetable.route", route, "time");
  }

  /**
   * Get the number of services on a route for the given timetable kind:
   * This will be more on weekdays than Sundays, for example.
   */
  public static int getNumberOfServices(int route, timetableKind kind)
  {
    if (route == 0) throw new InvalidQueryException("Nonexistent route");
    String source = database.join("service", "daily_timetable", "daily_timetable");
    return database.busDatabase.record_count("*", source, "daily_timetable.route", route, "daily_timetable.kind", kind.ordinal());
  }

  /**
   * Get all the services in a route for the given timetable kind.
   */
  public static int[] getServices(int route, timetableKind kind)
  {
    if (route == 0) throw new InvalidQueryException("Nonexistent route");
    String source = database.join("service", "daily_timetable", "daily_timetable");
    return database.busDatabase.select_ids("service_id", source, "daily_timetable.route", route, "daily_timetable.kind", kind.ordinal(), "");
  }
  
  /**
   * Get the number of services on a given route and data
   */
  public static int getNumberOfServices(int route, Date date)
  {
    return getNumberOfServices(route, timetableKind(date));
  }
  
   /**
   * Get the number of services on a given route today
   */
  public static int getNumberOfServices(int route)
  {
    return getNumberOfServices(route, database.today());
  }
  
  /**
   * Returns time it takes to get from p1 to p2 on `route` after `date`, and which takes you to p2 the earliest
   * result[0] = time, result[1] = serviceId 
   */
  public static int[] getTimeBetweenPoints(int p1, int p2, int route, DateTime date) {
	  String query =  "SELECT duration, service " +
					  "FROM " +
					  "  (SELECT " +
					  "     IF(t2.time - t1.time < 0, t2.time - t1.time + 1440, t2.time - t1.time) " +
					  "       as 'duration',  t1.service, t1.daily_timetable " +
					  "  FROM timetable_line t1, timetable_line t2 " +
					  "  WHERE t1.time > %d " +
					  //"    AND t2.time > %d " +
					  "    AND t1.timing_point = %d " +
					  "    AND t2.timing_point = %d " +
					  "    AND t1.service = t2.service " +
					  "  ORDER BY t2.time) AS t " +
					  "JOIN daily_timetable d  " +
					  "ON t.daily_timetable = d.daily_timetable_id " +
					  "WHERE route = %d " +
					  "  AND kind = %d ";
	  DateTime midnight = date.withTimeAtStartOfDay();
	  Duration duration = new Duration(midnight, date);
	  long minutes = duration.getStandardMinutes();
	  int kind = timetableKind(date.toDate()).ordinal();
	  
	  query = String.format(query, minutes, p1, p2, route, kind);
	  
	  try {
		  Statement stmt = database.busDatabase.connection.createStatement();
		  ResultSet rs = stmt.executeQuery(query);
		  
		  int[] result = new int[2];
		  if (rs.next()) {
			  result[0] = rs.getInt(1);
			  result[1] = rs.getInt(2);
		  }
		  return result;
	  }
	  catch (SQLException ex) {
		  throw new InvalidQueryException("Something failed on getting the time between points");
	  }
  }
  
  
  /**
   * Get the service times on a given route for a given timetable kind
   * for a particular service. This method along with the methods to
   * get service information can be used to build up a complete timetable
   */
  public static int[] getServiceTimes(int route, timetableKind kind, int serviceId)
  {
    if (route == 0) throw new InvalidQueryException("Nonexistent route");
    String source = database.join("timetable_line", "service", "service");
    return database.busDatabase.select_ids("time", source, "service", serviceId, "time");
  }
  
  /**
   * Get the service times on a given route for a givrn date
   * for a particular service. This method along with the methods to
   * get service information can be used to build up a complete timetable
   */
  public static int[] getServiceTimes(int route, Date date, int serviceId)
  {
    return getServiceTimes(route, timetableKind(date), serviceId);
  }
     
   /**
   * Get the service times on a given route today
   * for a particular service.
   */
  public static int[] getServiceTimes(int route, int serviceId)
  {
    return getServiceTimes(route, new Date(), serviceId);
  }
  
  /**
   * Returns the time at which the service is at the specified timing point
   */
  public static int getTime(int serviceId, int timingPoint) {
	  if (serviceId == 0 || timingPoint <= 0)
		  throw new InvalidQueryException("Invalid service or timing point");
	  
	  String query = "SELECT time FROM timetable_line WHERE service = %d AND timing_point = %d";
	  query = String.format(query, serviceId, timingPoint);
	
	  try {
		  Statement stmt = database.busDatabase.connection.createStatement();
		  ResultSet rs = stmt.executeQuery(query);
		  
		  int result = -1;
		  if (rs.next())
			  result = rs.getInt(0);
		  return result;
	  }
	  catch (SQLException ex) {
		  throw new InvalidQueryException("Something failed on getting the time of the service");
	  }
			  
  }

  /**
   * Get the timetable kind for a day
   */
  public static timetableKind timetableKind(Date day)
  {
    calendar.setTime(day);
    switch (calendar.get(Calendar.DAY_OF_WEEK))
    {
      case Calendar.SATURDAY: return timetableKind.saturday;
      case Calendar.SUNDAY:   return timetableKind.sunday;
      default:                return timetableKind.weekday;
    }
  }

}
