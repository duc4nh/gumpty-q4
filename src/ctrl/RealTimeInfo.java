package ctrl;

public class RealTimeInfo {

	// Actual info including: actual time + (reason + apologise) + (delay OR
	// cancel);
	private int actualTime;
	private String reason;
	private int type; // 0 means delay, 1 means cancel, 2 is on time
	private int delayTime;

	// constructor
	public RealTimeInfo(int actualTime, String reason, int type, int delayTime) {
		this.actualTime = actualTime;
		this.reason = reason;
		this.type = type;
		this.delayTime = delayTime;
	}
	
	/**
	 * Gets the actualTime
	 * @return actualTime
	 */
	public int getTime()
	{
		return actualTime;
	}
	
	/**
	 * Gets the reason
	 * @return reason
	 */
	public String getReason()
	{
		return reason;
	}
	/**
	 * Gets the type
	 * @return type
	 */
	public int getType()
	{
		return type;
	}
	/**
	 * Gets the delayTime
	 * @return delayTime
	 */
	public int getDelayTime()
	{
		return delayTime;
	}
}
