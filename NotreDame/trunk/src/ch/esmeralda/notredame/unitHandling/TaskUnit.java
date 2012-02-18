package ch.esmeralda.notredame.unitHandling;

import java.util.Date;

public class TaskUnit {
	private Date starttime;
	private long duration;
	private String streamURL;
	
	public TaskUnit(Date starttime,long duration,String streamURL){
		this.starttime = starttime;
		this.duration = duration;
		this.streamURL = streamURL;
	}
	
	public TaskUnit(Date starttime,long duration){
		this.starttime = starttime;
		this.duration = duration;
		this.streamURL = "";
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

}
