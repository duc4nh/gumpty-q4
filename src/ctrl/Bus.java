package ctrl;

import java.io.Serializable;
import java.util.ArrayList;

import org.joda.time.DateTime;

import db.BusInfo;

public class Bus implements Comparable<Bus>, Serializable {
	public final static long serialVersionUID = 11L;
	
	private int id;
	private int workload;
	private ArrayList<ArrayList<Service>> services;    // the services it is needed for in a roster
	
	public Bus (int id) {
		this.id = id;
		services = new ArrayList<ArrayList<Service>>(7);
		this.workload = BusInfo.getWorkload(this.id);
		for (int day = 0; day < 7; ++day)
			services.add(new ArrayList<Service>());
	}
	
	/**
	 * Tries to binds this bus to the given service
	 * @param newS The service to consider this bus for
	 * @param day The day of service
	 * @return True if bus is available for specified service
	 */
	public boolean addService(Service newS, DateTime day, int workload) {
		if (!BusInfo.isAvailable(this.id, day.toDate()))
			return false;
		int start = newS.getStartTime(), end = newS.getEndTime();
		for (Service s : services.get(day.getDayOfWeek() - 1))
			if (s.getStartTime() < start && start < s.getEndTime() ||
				s.getStartTime() < end && end < s.getEndTime())
				return false;
		newS.setBusID(this.id);
		services.get(day.getDayOfWeek() - 1).add(newS);
		this.workload += workload;
		return true;
	}

	/**
	 * @return The work load of this bus
	 */
	public int getWorkload() {
		return workload;
	}
	// no setter, as workload is updated internally

	public int getId() {
		return this.id;
	}

	@Override
	public int compareTo(Bus other) {
		return this.workload - other.workload;
	}
}
