package ch.esmeralda.notredame.jobs;

import java.sql.Timestamp;

import ch.esmeralda.notredame.unitHandling.TaskUnit;
import ch.esmeralda.notredame.unitHandling.Workday;

public class TimerJobImpl extends TimerJob{
	
	public TimerJobImpl(StreamJob streamJob,Workday workday) {
		super(streamJob,workday);
	}

	@Override
	public void run(){
		
		if(workday==null||streamJob==null) return;
		//Valid soundJob?
		if(streamJob.getState()==Thread.State.TERMINATED) return;
		//not yet started?
		if(streamJob.getState()==Thread.State.NEW) streamJob.start();
		
		TaskUnit nextTask = workday.getActiveUnit(new Timestamp(System.currentTimeMillis()));
		if(nextTask.equals(activeTask)) return;
		
		streamJob.stopStream();
		
		if(nextTask!=null)	streamJob.startStream(nextTask.getStreamURL());
		
		activeTask = nextTask;

	}

}
