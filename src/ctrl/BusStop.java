package ctrl;

import java.util.ArrayList;

import db.BusStopInfo;

public class BusStop {
	public ArrayList<Edge> connections;
	public BusStop parent;    // no need to have getters and setters as there is no rule to verify
	public int reachedWithService = -1;
	private int[] routes;
	private String fullName;
	private int area, id;
	
	public BusStop(int id) {
		this.id = id;
		this.routes = BusStopInfo.getRoutes(id);
		this.fullName = BusStopInfo.getFullName(id);
		this.area = BusStopInfo.getAreaByStop(id);
		this.connections = new ArrayList<Edge>();
	}
	
	public static BusStop[] loadNodes() {
		
		return new BusStop[5];
	};
	
	@Override
	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (other == this)
			return true;
		if (! (other instanceof BusStop))
			return false;
		return this.id == ((BusStop) other).id;
	}
	
	@Override
	public int hashCode() {
		// although each is uniquely identified by AREA + NAME, it is more efficient to use the id
		// this is also guaranteed to be unique, since it represents the primary key in the database
		return this.id;
	}
	
	public String toString() {
		return this.fullName;
	}
	
	/* only getters; BusStops cannot be modified from outside */
	public int getArea() { return this.area; }
	public int getId()   { return this.id;   }
	public String getFullName() { return this.fullName; }
	public int[] getRoutes() { return this.routes; }
}
