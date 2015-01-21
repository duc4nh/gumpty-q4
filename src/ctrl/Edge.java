package ctrl;

import org.joda.time.DateTime;

import db.TimetableInfo;

/**
 * Class to combine info about relations between bus stops
 * No functionality whatsoever
 * @author mbax2ct2
 *
 */
public class Edge implements Comparable<Edge> {
	public static enum Type {RIDE, WALK};
	public final static int WALKING_TIME = 5;   // minutes to get to other bus stops in same area 
	public BusStop source, destination;
	public int cost;                            // cost of getting from source to dest
	public int serviceId = -1;      			// if it's a RIDE, then this is the associated service
	public int walks = -1;      // for Dijkstra, we keep track of how many 'walks' it formed of
	public Type type;
	
	/**
	 * Each edge should have all its params set upon creation, so no other constructor is defined
	 */
	public Edge(BusStop source, BusStop destination, int cost, Type type) {
		this.source = source;
		this.destination = destination;
		this.cost = cost;
		this.type = type;
		if (type == Type.WALK)
			this.walks = 0;
	}
	
	/**
	 * Constructs a new Edge by joining the given two, in the order e1 -> e2
	 */
	public Edge(Edge e1, Edge e2) {
		if (! e1.destination.equals(e2.source))
			throw new IllegalArgumentException("Could not bind the two edges");
		this.source = e1.source;
		this.destination = e2.destination;
		this.cost = e1.cost + e2.cost;
		this.walks = e1.walks + (e2.type == Type.WALK ? 1 : 0);
	}
	
	/**
	 * Creates a new edge between the two bus stops when they are in the same area
	 * The edge's type is WALK
	 */
	public static Edge makeEdge(BusStop source, BusStop dest) {
		if (source.getArea() == dest.getArea())
			return new Edge(source, dest, WALKING_TIME, Type.WALK);
		else
			throw new IllegalArgumentException("No route specified and stops not in same area");
		
	}
	
	/**
	 * Creates and returns a new edge between the two bus stops, if it actually exists
	 * or null otherwise
	 * If route is <= 0 then it is assumed the two are in the same area and the edge is of type WALK
	 * Code extracted separately to be able to change the cost of the edge easily without affecting the rest
	 */
	public static Edge makeEdge(BusStop source, BusStop dest, int route, DateTime time) {
		if (route <= 0)
			return makeEdge(source, dest);
		
		// it is logically assumed by the caller that source and dest are on the same route
		int[] t = TimetableInfo.getTimeBetweenPoints(source.getId(), dest.getId(), route, time);
		if (t[1] == 0)
			return null;
		Edge r = new Edge(source, dest, t[0], Type.RIDE);
		r.serviceId = t[1];
		return r;
	}
	
	/**
	 * Implements a total order among Edges based on their cost
	 */
	public int compareTo(Edge other) {
		return this.cost - other.cost;
	}
}
