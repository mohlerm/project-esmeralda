package ch.esmeralda.quasimodo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.esmeralda.DataExchange.TaskUnit;

import android.app.Application;

public class QuasimodoApp extends Application {

	//---- saved vars
	private List<TaskUnit> workday; // a synchronized list to be used by all other classes!
	
	
	// public vars
	public String ip;
	public int port;
	
	public boolean updateBackground;
	public boolean serviceRunning;
	
	//---- get the workday
	public List<TaskUnit> getWD() {
		return this.workday;
	}
	
	
	//---- Lifecyclemethods
	
	@Override
	public void onCreate() {
		ArrayList<TaskUnit> tmp = new ArrayList<TaskUnit>();
		this.workday = Collections.synchronizedList(tmp);
	}
	
	@Override
	public void onTerminate() {
		workday = null;
	}
		
}
