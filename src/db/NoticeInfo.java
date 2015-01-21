package db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.joda.time.DateTime;

import ctrl.Notice;

/**
 * A class providing information about Notices. 
 * The methods contain checks for invalid queries, which will result in
 * InvalidQueryExceptions being thrown, but it does not enforce "business
 * rules" such as checking for dates in the past.
 */
public class NoticeInfo
{
	// This class is not intended to be instantiated.
	private NoticeInfo() 
	{
	}
	
	/**
	 * Saves a new notice into the database
	 * @param id
	 * @param date
	 * @param delay
	 * @param reason
	 * @param type
	 */
	public static void setNewNotice(int id, DateTime date, int delay, String reason, int type)
	{
		database.busDatabase.new_record("notice", new Object[][]{{"service_id", id}, {"day", date.toDate()}, {"delay_time", delay}, {"reason", reason}, {"type", type}});
	}
	
	/**
	 * 
	 * @return Returns an ArrayList of every notice saved in the database
	 */
	public static ArrayList<Notice> getAllNotices()
	{
		ArrayList<Notice> notices = new ArrayList<Notice>();
		//Retrieve the data from the database
		database.busDatabase.select("*","notice", "", "");
		ResultSet noticeResults = database.busDatabase.results;
		
		//Extract data from result set
	    try {
			while(noticeResults.next())
			{
				Notice notice = new Notice(noticeResults.getInt("service_id"),
											new DateTime(noticeResults.getDate("day")),
											noticeResults.getInt("type"),
											noticeResults.getString("reason"),
											noticeResults.getInt("delay_time"));
			 	notices.add(notice);
			  }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return notices;
	}
	
	/**
	 * Searches if there is a notice for a given service There should only be one notice for a service
	 * @param serviceNumber
	 * @return Returns the notice if one exists or null if one does not
	 */
	public static Notice findNotice(int serviceNumber)
	{
		Notice notice = null;
		
		Date today = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String date = dateFormat.format(today); 
		
		//Retrieve the data from the database
		database.busDatabase.select("*","notice", "service_id = " + serviceNumber  + " AND day = '" + date + "'", "");
		ResultSet noticeResults = database.busDatabase.results;
		
		//Extract data from result set
	    try {
			noticeResults.next();
			notice = new Notice(noticeResults.getInt("service_id"),
								new DateTime(noticeResults.getDate("day")),
								noticeResults.getInt("type"),
								noticeResults.getString("reason"),
								noticeResults.getInt("delay_time"));
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			
			//Return null if there is no notice
			return null;
		}
		return notice;
	}
	
}
