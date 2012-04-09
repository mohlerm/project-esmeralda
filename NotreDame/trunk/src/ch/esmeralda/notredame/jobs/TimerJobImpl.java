package ch.esmeralda.notredame.jobs;

import java.sql.Timestamp;

import ch.esmeralda.DataExchange.TaskUnit;
import ch.esmeralda.notredame.main.Constants;
import ch.esmeralda.notredame.unitHandling.Workday;

public class TimerJobImpl extends TimerJob{
	public TimerJobImpl(StreamJob streamJob,Workday workday) {
		super(streamJob,workday);
	}

	@Override
	public void run(){
		if(false) System.out.println("running Timer...");
		if(workday==null) return;
		//if(D) System.out.println("nothing is null");
		/*
		//Valid soundJob?
		if(streamJob.getState()==Thread.State.TERMINATED) return;
		//not yet started?
		if(streamJob.getState()==Thread.State.NEW) streamJob.start();*/
		
		TaskUnit nextTask = workday.getActiveUnit(new Timestamp(System.currentTimeMillis()));
		if(nextTask!=null&&!nextTask.equals(activeTask)){
			if(streamJob!=null){
				streamJob.stopStream();
				if(nextTask!=null)	streamJob.startStream(nextTask.getStreamURL());
			}else{
				System.out.println("sound changed, but muted (StreamJob is null)");
			}
			activeTask = nextTask;
		}
		//System.out.println("  end of run");
	}

}
