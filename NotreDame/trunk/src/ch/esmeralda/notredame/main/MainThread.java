package ch.esmeralda.notredame.main;

import java.util.Date;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ch.esmeralda.notredame.jobs.AthmosStream;
import ch.esmeralda.notredame.jobs.StreamJob;
import ch.esmeralda.notredame.jobs.TimerJob;
import ch.esmeralda.notredame.jobs.TimerJobImpl;
import ch.esmeralda.notredame.unitHandling.TaskUnit;
import ch.esmeralda.notredame.unitHandling.Workday;
import ch.esmeralda.notredame.unitHandling.WorkdayImpl;

public class MainThread extends Thread{
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("starting...");
		
		ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
		
		StreamJob streamJob = new AthmosStream();

		Workday workday = new WorkdayImpl();
		
		if(true){//args.length>0){
			prefill(workday);
			System.out.println(workday.toString());
		}
		
		TimerJob timerJob = new TimerJobImpl(streamJob,workday);
		
		System.out.println("schedule jobs");
		executor.scheduleAtFixedRate(timerJob, 500, 1000, TimeUnit.MILLISECONDS);
		
		
		//Put here some UI Stuff if you like
		try {
			Thread.sleep(600000000);
		} catch (InterruptedException e) {}
		

		
		System.out.println("stopping jobs");
		executor.shutdownNow();
		System.out.println("...bye, bye");
	}
	
	private static void prefill(Workday workday){
		System.out.println("prefill a debug workday");
		long now = System.currentTimeMillis()+1000;
		TaskUnit task;
		for(int i=0;i<10;i++){
			if(i%2==0)	task = new TaskUnit(new Date(now+i*10000), 10000, "http://u11aw.di.fm:80/di_trance");
			else		task = new TaskUnit(new Date(now+i*10000), 10000, "");
			task.setDescription("debug "+i);
			workday.addUnit(task);
		}
	}
}
