package ch.esmeralda.notredame.main;

import java.util.Date;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ch.esmeralda.notredame.jobs.*;
import ch.esmeralda.notredame.unitHandling.TaskUnit;
import ch.esmeralda.notredame.unitHandling.Workday;
import ch.esmeralda.notredame.unitHandling.WorkdayImpl;

/**
 * The actual Frontend.
 * @author Thomas Richner
 *
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("starting...");
		// TODO Auto-generated method stub
		ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2);
		
		StreamJob streamJob = new StreamJobImpl();
		
		Workday workday = new WorkdayImpl();
		
		if(args.length>0){
			prefill(workday);
			System.out.println(workday.toString());
		}
		
		TimerJob timerJob = new TimerJobImpl(streamJob,workday);
		
		System.out.println("schedule jobs");
		executor.scheduleAtFixedRate(timerJob, 500, 1000, TimeUnit.MILLISECONDS);
		streamJob.start();
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {}
		
		System.out.println("stopping jobs");
		executor.shutdownNow();
		streamJob.stop(); // better way??
		
		
		System.out.println("...bye, bye");
	}
	
	private static void prefill(Workday workday){
		long now = System.currentTimeMillis()+10000;
		TaskUnit task;
		for(int i=0;i<10;i++){
			task = new TaskUnit(new Date(now+i*10000), 10000, "hallo");
			workday.addUnit(task);
		}
	}

}
