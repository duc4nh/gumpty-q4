package ctrl;

import java.io.Serializable;

import db.BusStopInfo;
import db.TimetableInfo;


public class Service implements Serializable, Comparable<Service> {
	public final static long serialVersionUID = 11L;

	private int busRoute;
	private String routeName;
	private int serviceID;
	private int driverID;
	private int busID;
	private TimetableInfo.timetableKind kind;
	private int[] serviceTimes;
	private int startTime;	//time after midnight
	private int endTime;	//time after midnight
	private boolean running;	//to be used in emergency situations e.g. a bus breaks down
	
	/**
	 * Constructor method for a service, sets a start time , end time and the service ID
	 * @param start
	 * @param end
	 * @param ID
	 */
	public Service(int route, int ID, TimetableInfo.timetableKind kind)
	{
		this.busRoute = route;
		this.routeName = BusStopInfo.getRouteName(route);
		this.serviceID = ID;
		this.kind = kind;
		setRunning(true);
		this.serviceTimes = TimetableInfo.getServiceTimes(route, kind, ID);
		startTime = this.serviceTimes[0];
		endTime = this.serviceTimes[this.serviceTimes.length - 1];
	}
	
	public String getRouteName() {
		return this.routeName;
	}
	
	/**
	 * 
	 * @return startTime - start time of the service
	 */
	public int getStartTime()
	{
		return startTime;
	}
	
	/**
	 * 
	 * @return endTime - end time of the service
	 */
	public int getEndTime()
	{
		return endTime;
	}
	
	/**
	 * Duration of service
	 * @return endTime-startTime
	 */
	public int getServiceLength()
	{
		return endTime - startTime;
	}
	
	/**
	 * Sets the driverID
	 * @param ID
	 */
	public void setDriverID(int ID)
	{
		this.driverID = ID;
	}
	
	/**
	 * Returns the driverID for this service
	 * @return driverID
	 */
	public int getDriverID()
	{
		return driverID;
	}
	
	/**
	 * Sets the busID for this service
	 * @param ID
	 */
	public void setBusID(int ID)
	{
		this.busID = ID;
	}
	
	/**
	 * Returns the driverID for this service
	 * @return busID
	 */
	public int getBusID()
	{
		return busID;
	}
	
	/**
	 * Gets the serviceID
	 * @return serviceID
	 */
	public int getServiceID()
	{
		return serviceID;
	}
	
	/**
	 * 
	 * @return busRoute - the route on which the service runs
	 */
	public int getRoute()
	{
		return busRoute;
	}
	
	/**
	 * Gets the timetable kind for the service
	 * @return kind
	 */
	public TimetableInfo.timetableKind getKind()
	{
		return kind;
	}
	
	/**
	 * Returns the service times
	 * @return serviceTimes
	 */
	public int[] getServiceTimes()
	{
		return serviceTimes;
	}
	
	/**
	 * 
	 * @param running
	 */
	public void setRunning(boolean running)
	{
		this.running = running;
	}
	
	/**
	 * 
	 * @return running
	 */
	public boolean isRunning()
	{
		return running;
	}
	
	/**
	 * 
	 * @param otherService
	 * @return
	 */
	@Override
	public int compareTo(Service otherService)
	{
       // return this.id - otherStudent.id ; //result of this operation can overflow
       return (this.startTime < otherService.startTime ) ? -1: (this.startTime > otherService.startTime) ? 1:0 ;

    }
}
