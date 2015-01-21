package ctrl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Date;
import java.util.ListIterator;
import java.util.Map;
import java.util.PriorityQueue;

import org.joda.time.DateTime;

import db.BusStopInfo;
import ctrl.BusStop;
import db.database;
import db.NoticeInfo;
import db.database;
import db.TimetableInfo;

public class JourneyHandler {
	
	public static final int MAX_CHANGES = 3;
	
	public JourneyHandler() {
	}
	
	/**
	 * Take all information about starting point, destination and time
	 * @return detailed information about the journey, which are wrapped into 1 OBJECT
	 *         or null if no journey exists
	 */
	public Journey generate(String startAreaCode, String startPointName, 
							String endAreaCode, String endPointName, DateTime time) {
		
		if (database.busDatabase == null)
			database.openBusDatabase();
		BusStop source = new BusStop(BusStopInfo.findBusStop(startAreaCode, startPointName));
		BusStop dest   = new BusStop(BusStopInfo.findBusStop(endAreaCode, endPointName));
		ArrayList<BusStop> graph = createData(source, time);
		
		// check the dest can be reached
		boolean reachableDest = false;
		for (BusStop b : graph)
			if (b.equals(dest)) {
				reachableDest = true;
				break;
			}
		
		if (! reachableDest)
			return null;
		
		Journey j = Dijkstra(graph, source, dest);
		ListIterator<Integer> it = j.durations.listIterator();
		int minutes = 0;
	    while (it.hasNext()) {
	    	minutes = it.next();
	    	j.times.add((time = time.plusMinutes(minutes)));
	    }
	    	
		return j;
	}
	
	private ArrayList<BusStop> createData(BusStop source, DateTime time) {
		LinkedList<BusStop> queue = new LinkedList<BusStop>();    // for BFS discovering new stops
		HashMap<Integer, BusStop> reached = new HashMap<Integer, BusStop>(100);   // for not having a stop twice
		
		queue.add(source);
		reached.put(source.getId(), source);
		
		int neighbourId = -1;
		int[] toNeighbour = new int[2];
		int[] inSameArea;
		BusStop neighbour, head;
		Edge newEdge;
		
		while (! queue.isEmpty()) {
			head = queue.remove();
			
			
			// first, say you can walk to any timing point which is in the same area
			inSameArea = BusStopInfo.getBusStopsInArea(head.getArea());
			for (int nId : inSameArea) {
				if (nId == head.getId())
					continue;
				if ((neighbour = reached.get(nId)) == null && BusStopInfo.isTimingPoint(nId)) {
					// this stop was NOT added before, so create a new object for it
					queue.add((neighbour = new BusStop(nId)));
					reached.put(nId, neighbour);
				}
				if (neighbour != null)    // don add non timing-points
					head.connections.add(Edge.makeEdge(head, neighbour));
			} 
			
			// then explore the neighbours and add edges to them
			for (int route : head.getRoutes()) {
				neighbourId = BusStopInfo.getNextStop(head.getId(), route);
				while (neighbourId != 0 && (                                       // if there is a neighbour 
					   ! BusStopInfo.isTimingPointOnRoute(neighbourId, route) ||  // but it's not a timing point
					   (toNeighbour = TimetableInfo.getTimeBetweenPoints(head.getId(), // or there is no connection
							   neighbourId, route, time))[1] == 0))                // to the neighbour, then
					neighbourId = BusStopInfo.getNextStop(neighbourId, route);    // go along until you find a good one
				
				if (neighbourId == 0)    // didn't reach another timing point on this route
					continue;
				
				// from here on neighbourId is the next timing point along route
				neighbour = reached.get(neighbourId);
				if (neighbour != null && neighbour.getArea() == head.getArea())
					// it has been already added as WALK, so do nothing
					continue; 
					
				if (neighbour ==  null) {
					// this stop was NOT added before, so create a new object for it
					queue.add((neighbour = new BusStop(neighbourId)));
					reached.put(neighbourId, neighbour);
				}
				// neighbour now contains either an existing neighbour, or a new one, but never a duplicate
				// get distance / time from this to neighbour
				newEdge = new Edge(head, neighbour, toNeighbour[0], Edge.Type.RIDE);
				newEdge.serviceId = toNeighbour[1];
				head.connections.add(newEdge);
			} // for
		} // while
		
		return new ArrayList<BusStop>(reached.values());
	}
	
	
	private Journey Dijkstra(ArrayList<BusStop> graph, BusStop source, BusStop dest) {
		
		HashMap<BusStop, Integer> distance = new HashMap<BusStop, Integer>();
		PriorityQueue<Edge> queue = new PriorityQueue<Edge>();
		
		distance.put(source, 0);
		queue.add(new Edge(source, source, 0, Edge.Type.WALK));    // you walk to source
		Edge head, newEdge;
		Integer d = -1, newCost;
		
		while (! queue.isEmpty()) {
			head = queue.remove();
			
			for (Edge neighbour : head.destination.connections) {
				newEdge = new Edge(head, neighbour);
				if ((((d = distance.get(newEdge.destination)) == null) ||    // not explored
						newEdge.cost < d) && newEdge.walks <= MAX_CHANGES) {         // Dijkstra condition & max walks
					distance.put(newEdge.destination, newEdge.cost);        // relax the cost
					newEdge.destination.parent = head.destination;          // save the parent for the path
					newEdge.destination.reachedWithService = neighbour.serviceId; // and the service from parent
					queue.add(newEdge);
				}
			}
		}
		
		// search for the end to start making the path
		Iterator it = distance.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        if (dest.equals(pairs.getKey()))
	        	dest = (BusStop) pairs.getKey();
	        // System.out.printf("Dist to %s is %d\n", pairs.getKey(), pairs.getValue());
	        //it.remove(); // avoids a ConcurrentModificationException
	    }
	    
		Journey j = new Journey();
		j.changes.add(dest);
	    while (dest != null && ! dest.equals(source)) {
	    	//System.out.printf("(%d, %d) ", dest.getId(), dest.reachedWithService);
	    	j.stops.add(dest);
	    	j.durations.add(distance.get(dest));
	    	if (dest.parent != null && dest.reachedWithService != dest.parent.reachedWithService)
	    		j.changes.add(dest.parent);
	    	dest = dest.parent;
	    }
	    
	    Collections.reverse(j.stops);
	    Collections.reverse(j.changes);
	    Collections.reverse(j.durations);
		return j;
	}

	/**
	 * A passenger gets real time information about a bus due from a particular place at a particular time.
	 * Take: BusStop + Time
	 * @return Actual info including: actual time + (reason + apologise) + (delay OR cancel);
	 */
	public static RealTimeInfo checkServiceTime(String startAreaName, String startBusStopName, String desAreaName, 
												String desBusStopName, int time) {
		
		// TODO deal with invalid input information
		
		//database.openBusDatabase();
		
		// From areaName + busStopName specified by the passenger
		// Translate into: busStopID
		// for both: Start & destination
		int startAreaID = BusStopInfo.findAreaByName(startAreaName);
		String startAreaCode = BusStopInfo.getAreaCode(startAreaID);
		int startBusStopID = BusStopInfo.findBusStop(startAreaName, startBusStopName);
		
		int desAreaID = BusStopInfo.findAreaByName(desAreaName);
		String desAreaCode = BusStopInfo.getAreaCode(desAreaID);
		int desBusStopID = BusStopInfo.findBusStop(desAreaName, desBusStopName);
		
		// From start & destination busStop
		// get: Route ID
		int routeID = -1;
		
		int[] startRoute = BusStopInfo.getRoutes(startBusStopID);
		int[] desRoute = BusStopInfo.getRoutes(desBusStopID);
		if (startRoute.length == 1) {
			routeID = startRoute[0];
		}
		else {
			for (int i = 0; i < startRoute.length; i++) {
				for (int j = 0; j < desRoute.length; j++) {
					if (startRoute[i] == desRoute[j]) {
						routeID = startRoute[i];
					}
				}
			}
		}
			
		// Get timing points the busStop 
		// return: timing point
		int timingPoint = startBusStopID;

		while (!BusStopInfo.isTimingPointOnRoute(timingPoint, routeID)) {
			timingPoint = BusStopInfo.getNextStop(timingPoint, routeID);
		}
		
		// get the dateType
		Date today = database.today();
		int dateType; // 0 means Monday
		switch (TimetableInfo.timetableKind(today)) {
			case saturday:
				dateType = 5;
				break;
			case sunday:
				dateType = 6;
				break;
			default:
				dateType = 0;
				break;
		}
		
		// arrays of all info we need
		int[] dailyTimetable = database.busDatabase.select_ids("daily_timetable_id", "daily_timetable", "kind", dateType, "route", routeID, "kind");
		int[] dailyTimetableID = database.busDatabase.select_ids("timetable_line_id", "timetable_line", "daily_timetable", dailyTimetable[0], "timing_point", timingPoint, "daily_timetable");
		int[] service = new int[dailyTimetableID.length];
		int[] expectedServiceTimeArriving = new int[service.length];
		int[] actualServiceTimeArriving = new int[service.length];
		
		for (int index = 0; index < service.length; index++) {
			service[index] = database.busDatabase.get_int("timetable_line", dailyTimetableID[index], "service");
			expectedServiceTimeArriving[index] = database.busDatabase.get_int("timetable_line", dailyTimetableID[index], "time");
		
			Notice notice = NoticeInfo.findNotice(service[index]);
			if (notice == null)
				actualServiceTimeArriving[index] = expectedServiceTimeArriving[index];
			else if (notice.getType() == 1)
				actualServiceTimeArriving[index] = -1;
			else
				actualServiceTimeArriving[index] = expectedServiceTimeArriving[index] + notice.getDelayTime();
		}
		
		// looking for the best service after input time
		// took delay and cancellation into account
		int bestServiceIndex = -1;
		int bestServiceTime = 2000; // just a large number, largest time we got so far is 1446
		for (int index = 0; index < service.length; index++) {
			if (actualServiceTimeArriving[index] > time) {
				if (actualServiceTimeArriving[index] < bestServiceTime) {
					bestServiceIndex = index;
					bestServiceTime = actualServiceTimeArriving[index];
				}
			}
		}
			
		RealTimeInfo realTimeInfo;
		if (bestServiceIndex == -1) {
			// this means input is not VALID
			// return a null object
			realTimeInfo = null;
		}
		else {
			Notice notice = NoticeInfo.findNotice(service[bestServiceIndex]);
			if (notice == null)
				realTimeInfo = new RealTimeInfo(bestServiceTime, "", 2, 0);
			else
				realTimeInfo = new RealTimeInfo(bestServiceTime, notice.getReason(), notice.getType(), notice.getDelayTime());
		}
		
		/*
		System.out.println("YOur looking service will Arrive at:" +  Info.getTime());
		if (realTimeInfo.getType() == 2) System.out.println("Status: on time");
		else System.out.println("Status: delay for " + realTimeInfo.getDelayTime() + " minutes because: " + realTimeInfo.getReason());
		*/
		
		return realTimeInfo;
	}
}
