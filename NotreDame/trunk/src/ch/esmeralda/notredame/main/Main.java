package ch.esmeralda.notredame.main;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ch.esmeralda.notredame.jobs.*;
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
		// TODO Auto-generated method stub
		ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2);
		
		StreamJob streamJob = new StreamJobImpl();
		Workday workday = new WorkdayImpl();
		//fill the workday with stuff
		TimerJob timerJob = new TimerJobImpl(streamJob,workday);
		
		executor.scheduleAtFixedRate(timerJob, 500, 1000, TimeUnit.MILLISECONDS);
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {}
		
		executor.shutdownNow();
		streamJob.stop(); // better way??
		
	}

}
