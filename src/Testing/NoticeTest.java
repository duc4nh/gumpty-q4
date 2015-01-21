package Testing;

import static org.junit.Assert.*;

import org.joda.time.DateTime;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import ctrl.Notice;
import db.NoticeInfo;
import db.database;

public class NoticeTest {

	@BeforeClass
	public static void initDb() {
		database.openBusDatabase();    // open database before all tests
	}
	
	@AfterClass
	public static void closeDb() {
		database.busDatabase.close();  // close database after finishing tests
	}
	
	@Test
	public void testFindNotice() {
		//NoticeInfo.setNewNotice(6194, DateTime.parse("2014-05-09T00:00:00+00:00"), 0, "pucture", 60);
		Notice n = new Notice(6194, DateTime.parse("2014-05-09T00:00:00+00:00"), 0, "pucture", 60);
		assertEquals(NoticeInfo.findNotice(6194), n);	
	}

	@Test
	public void testFindAllNotice() {
		assertTrue(NoticeInfo.getAllNotices() != null);	
	}
	
	@Test
	public void testSetNotice() {
		NoticeInfo.setNewNotice(6197, DateTime.parse("2014-05-09T00:00:00+00:00"), 12, "broken door", 0);
		Notice n = new Notice(6197, DateTime.parse("2014-05-09T00:00:00+00:00"), 12, "broken door", 0);
		assertEquals(NoticeInfo.findNotice(6197), n);	
	}
	
	@Test
	public void testCancellation() {
		NoticeInfo.setNewNotice(6200, DateTime.parse("2014-05-09T00:00:00+00:00"), 0, "broken wheel", 1);
		Notice n = new Notice(6200, DateTime.parse("2014-05-09T00:00:00+00:00"), 0, "broken wheel", 1);
		assertEquals(NoticeInfo.findNotice(6200), n);	
	}
}
