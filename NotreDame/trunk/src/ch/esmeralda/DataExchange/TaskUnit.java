package ch.esmeralda.DataExchange;

import java.io.Serializable;
import java.util.Date;

/**
 * TaskUnit represents either a Break- or a Workshift. Depending
 * on the streamURL. 
 * @author Thomas Richner
 * @version 0.1
 *
 */
public class TaskUnit implements Serializable {
	
	private static final long serialVersionUID = 5275585922124992816L;
	private Date starttime;
	private long duration;
	private String streamURL;
	private String description;
	private long key = 0;
	/**
	 * Creates a new Object and initializes everything.
	 * After the creation, nothing can be changed.
	 * @param starttime When does it start? be aware, this ist probably UTC!
	 * @param duration How long does it take? [ms]
	 * @param streamURL URL of a Radiostream like DI
	 */
	public TaskUnit(Date starttime,long duration,String streamURL){
		this.starttime = starttime;
		this.duration = duration;
		this.streamURL = streamURL;
	}
	/**
	 * Creates a new TaskUnit Object as a 'Workshift', since
	 * there is no stream given.
	 * @param starttime	When does it start? be aware, this ist probably UTC!
	 * @param duration How long does it take? [ms]
	 */
	public TaskUnit(Date starttime,long duration){
		this.starttime = starttime;
		this.duration = duration;
		this.streamURL = "";
	}
	/**
	 * @return a String representation
	 */
	public String toString(){
		StringBuffer str = new StringBuffer();
		long mins = duration/(60000);
		str.append(starttime.toString() + "  " +mins+ "  "+ streamURL + "  " + description);
		return str.toString();
	}

	//---- Getters
	/**
	 * Returns the Starttime as a string like '9:00'
	 * @param timeshift
	 * @return
	 */
	public String getStarttimeAsString(int timeshift) {
		int hour=starttime.getHours()+timeshift;
		int minutes = starttime.getMinutes(); 
		return String.format("%02d:%02d", hour,minutes);
	}
	
	public Date getStarttime() {
		return starttime;
	}
	/**
	 * Returns the time in mins.
	 * Be aware, this functions is very unrelyable because
	 * the unit of the time isn't always clear.
	 * @return duration of the unit in minutes
	 */
	public String getDurationAsString() {
		long mins = duration/(60000);
		return mins+"min";
	}
	
	public long getDuration() {
		return duration;
	}

	public String getStreamURL() {
		return streamURL;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setKey(long keycount){
		this.key = keycount;
	}
	public long getKey(){
		return key;
	}

}
