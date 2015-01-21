package ctrl;

import org.joda.time.DateTime;

import db.NoticeInfo;

public class Notice {

	private int serviceID;
	private DateTime date;
	private int type;
	private String reason; // 0 delay, 1 cancel
	private int delayTime;
	
	//constructor
	public Notice(int serviceID, DateTime date, int type, String reason, int delayTime) {
		this.serviceID = serviceID;
		this.date = date;
		this.type = type;
		this.reason = reason;
		this.delayTime = delayTime;
	}

	/**
	 * to CREATE a new entry in database
	 */
	public void issue() {
		NoticeInfo.setNewNotice(serviceID, date, delayTime, reason, type);
	}
	
	public int getServiceId()
	{
		return this.serviceID;
	}
	
	public DateTime getDate()
	{
		return this.date;
	}
	
	public int getType()
	{
		return this.type;
	}
	
	public String getReason()
	{
		return this.reason;
	}
	
	public int getDelayTime()
	{
		return this.delayTime;
	}
}
