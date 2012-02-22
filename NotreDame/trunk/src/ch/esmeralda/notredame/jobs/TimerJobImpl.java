package ch.esmeralda.notredame.jobs;

import java.sql.Timestamp;

import ch.esmeralda.notredame.unitHandling.TaskUnit;
import ch.esmeralda.notredame.unitHandling.Workday;

public class TimerJobImpl extends TimerJob{
	private static boolean D = true;
	public TimerJobImpl(StreamJob streamJob,Workday workday) {
		super(streamJob,workday);
	}

	@Override
	public void run(){
		if(D) System.out.print("running Timer...");
		if(workday==null||streamJob==null) return;
		if(D) System.out.println("nothing is null");
		/*
		//Valid soundJob?
		if(streamJob.getState()==Thread.State.TERMINATED) return;
		//not yet started?
		if(streamJob.getState()==Thread.State.NEW) streamJob.start();*/
		
		TaskUnit nextTask = workday.getActiveUnit(new Timestamp(System.currentTimeMillis()));
		if(nextTask!=null&&!nextTask.equals(activeTask)){

		streamJob.stopStream();
		
		if(nextTask!=null)	streamJob.startStream(nextTask.getStreamURL());
		
		activeTask = nextTask;
		}
		System.out.println("  end of run");
	}

}
