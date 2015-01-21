package Testing;

import static org.junit.Assert.*;

import org.joda.time.DateTime;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ctrl.BusStop;
import ctrl.Journey;
import ctrl.JourneyHandler;
import ctrl.RealTimeInfo;
import db.InvalidQueryException;
import db.database;

public class JourneyHandlerTest {
	
	private JourneyHandler jh;

	@BeforeClass
	public static void initDb() {
		database.openBusDatabase();    // open database before all tests
	}
	
	@AfterClass
	public static void closeDb() {
		database.busDatabase.close();  // close database after finishing tests
	}
	
	@Before
	public void setup() {
		jh = new JourneyHandler();
	}
	
	/**
	 * It doesn't deal with errors, as the data comes in right format from UI
	 */
	@Test (expected = InvalidQueryException.class)
	public void testInvalidBusStop() {
		Object ret = jh.generate("", "", "", "", DateTime.now());
	}
	
	/**
	 * It doesn't deal with errors as the UI sends the current time
	 */
	@Test (expected = NullPointerException.class)
	public void testNullDate() {
		Object ret = jh.generate("Stockport", "Bus Station 1", "Stockport", "Bus Station 1", null);
	}
	
	/**
	 * Test correct Journey creation
	 */
	@Test
	public void testJourneyCreation() {
		Object ret = jh.generate("Stockport", "Bus Station 1", "Stockport", "Bus Station 1", DateTime.now());
		assertTrue(ret instanceof Journey);
		Journey j = (Journey) ret;
		// since start is same as stop, it should give no journey
		assertTrue(j.changes.size() == 0);
		assertTrue(j.times.size() == 0);
		assertTrue(j.durations.size() == 0);
		assertTrue(j.stops.size() == 0);
	}
	
	/**
	 * SKP - Intermediate road is not a timing point, therefore is not reachable
	 */
	@Test
	public void testNotReachable() {
		Journey j = jh.generate("Stockport", "Bus Station 1", "Stockport", "Intermediate Road", DateTime.now());
		assertTrue(j == null);
	}
	
	/**
	 * Dialstone Lane/Hillcrest Road is the first timing point on route. There should be no changes
	 */
	@Test
	public void testNextStop() {
		Journey j = jh.generate("Stockport", "Bus Station 1", "Stockport", "Dialstone Lane/Hillcrest Road", 
								DateTime.parse("2014-05-05T08:15:30+00:00").plusMinutes(100));
		assertTrue(j.stops.size() == 1);
		assertTrue(j.times.size() == 1);
		assertTrue(j.durations.size() == 1);
		assertTrue(j.changes.size() == 0);
		
		BusStop b = j.stops.peek();    // one peek is enough
		// full name ensures both area and name are right
		assertTrue(b.getFullName().equals("Stockport, Dialstone Lane/Hillcrest Road"));
		assertTrue(b.parent.getFullName().equals("Stockport, Bus Station 1"));
	}
	
	/**
	 * Need to find a way to force it prefer a direct route
	 */
	@Test
	public void testStopsSameRoute() {
		Journey j = jh.generate("Stockport", "Bus Station 1", "Romiley", "Train Station 2", 
				DateTime.parse("2014-05-05T08:15:30+00:00").plusMinutes(100));

		assertTrue(j.stops.size() == 3);    // remember not all are timing points
		assertTrue(j.times.size() == 3);    // times should be for each of them
		assertTrue(j.durations.size() == 3);// same for durations
		// assertTrue(j.changes.size() == 0);  // and no changes
	}
	
	@Test
	public void testStopsWithChanges() {
		Journey j = jh.generate("Hayfield", "Bus Station 2", "Romiley", "Corcoran Drive 2", 
				DateTime.parse("2014-05-05T08:15:30+00:00").plusMinutes(100));

		assertTrue(j.changes.size() == 4);  // 3 + the source (1)
	}
	
	
	@Test
	public void testNoDelay1() {
		RealTimeInfo rt = new RealTimeInfo(1235, "", 2, 0);
		assertEquals(jh.checkServiceTime("Stockport", "Bus Station 1", "Hayfield", "Bus Station 2", 1000), rt);	
	}
	
	@Test
	public void testNoDelay2() {
		RealTimeInfo rt = new RealTimeInfo(559, "", 2, 0);
		assertEquals(jh.checkServiceTime("Romiley", "Corcoran Drive 2", "Marple", "Norfolk Arms 1", 600), rt);	
	}
	
	@Test
	public void testWithDelay1() {
		RealTimeInfo rt = new RealTimeInfo(664, "pucture", 0, 60);
		assertEquals(jh.checkServiceTime("Stockport", "Bus Station 1", "Hayfield", "Bus Station 2", 600), rt);	
	}
	
	@Test
	public void testWithDelay2() {
		RealTimeInfo rt = new RealTimeInfo(1235, "delay sorry", 0, 300);
		assertEquals(jh.checkServiceTime("Romiley", "Train Station 2", "Marple", "Navigation Hotel 5", 1000), rt);	
	}

}
