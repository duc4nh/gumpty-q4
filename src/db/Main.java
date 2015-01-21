package db;

import java.util.ArrayList;

import org.joda.time.DateTime;

import ctrl.Journey;
import ctrl.JourneyHandler;
import ctrl.Notice;
import ctrl.RealTimeInfo;
import ctrl.RosterGenerator;

/*
 * A very simple application illustrating how to use the interface.
 * Prints the names of all the drivers in the database.
 * @author Ciprian Tomoiaga
 */
public class Main {

    /**
     * @param args the command line arguments
     */
	public static void main(String[] args) {
		if (database.busDatabase == null)
			database.openBusDatabase();

		/*// test for Dijkstra
		DateTime time = DateTime.now().withTimeAtStartOfDay().withDurationAdded(100 * 60, 100); // 100 min
		JourneyHandler jh = new JourneyHandler();
		Journey journey = jh.generate("ROM", "Corcoran Drive 2", "HAY", "Bus Station 2", time);
		*/
		//Test for issuing a new notice
		DateTime date = new DateTime();
		Notice testNotice = new Notice(123, date, 0, "puncture", 60);
		//testNotice.issue();
		
		RealTimeInfo realTimeInfo = JourneyHandler.checkServiceTime("Stockport", "Bus Station 1", "Hayfield", "Bus Station 2", 1000);
		
		//System.out.println("The earliest service coming:" + service[bestServiceIndex]);
		System.out.println("Arrive at:" + realTimeInfo.getTime());
		if (realTimeInfo.getType() == 2) System.out.println("Status: on time");
		else System.out.println("Status: delay for " + realTimeInfo.getDelayTime() + " minutes because: " + realTimeInfo.getReason());
		
		
		/*
		//Test for retrieving all notices
		ArrayList<Notice> notices = new ArrayList<Notice>();
		notices = NoticeInfo.getAllNotices();
		for (Notice n : notices)
		{
			System.out.println(n.getServiceId());
			System.out.println(n.getDate());
			System.out.println(n.getDelayTime());
			System.out.println(n.getReason());
			System.out.println(n.getType() + "\n");
		}
		
		
		//Test for retrieving a specific notice
		Notice testNotice2 = NoticeInfo.findNotice(1253);
		if (testNotice2 != null)
		{
			System.out.println(testNotice2.getServiceId());
			System.out.println(testNotice2.getDate());
			System.out.println(testNotice2.getDelayTime());
			System.out.println(testNotice2.getReason());
			System.out.println(testNotice2.getType() + "\n");
		}
		else
		{
			System.out.println("No notice for this service.\n");
		}*/
	}
}
