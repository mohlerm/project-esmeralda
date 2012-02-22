package ch.esmeralda.notredame.unitHandling;

import java.util.Date;
/**
 * TaskUnit represents either a Break- or a Workshift. Depending
 * on the streamURL. 
 * @author Thomas Richner
 * @version 0.8
 * @since 0.1 (former TimeUnit)
 *
 */
public class TaskUnit {
	private Date starttime;
	private long duration;
	private String streamURL;
	String description;
	/**
	 * Creates a new Object and initializes everything.
	 * After the creation, nothing can be changed.
	 * @param starttime When does it start? be aware, this ist probably UTC!
	 * @param duration How long does it take?
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
	 * @param duration How long does it take?
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
	public Date getStarttime() {
		return starttime;
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
	

}
