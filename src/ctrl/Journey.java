package ctrl;

import java.util.LinkedList;

import org.joda.time.DateTime;

public class Journey {

	public LinkedList<BusStop> stops;
	public LinkedList<BusStop> changes;
	public LinkedList<Integer> durations;
	public LinkedList<DateTime> times;
	
	public Journey() {
		stops = new LinkedList<BusStop>();
		changes = new LinkedList<BusStop>();
		durations = new LinkedList<Integer>();
		times = new LinkedList<DateTime>();
	}
}
