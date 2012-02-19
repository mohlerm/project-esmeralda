package ch.esmeralda.notredame.main;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ch.esmeralda.notredame.jobs.*;

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
		SoundJob soundJob = new SoundJob();
		TimerJob timerJob = new TimerJobImpl(soundJob);
		executor.scheduleAtFixedRate(timerJob, 500, 1000, TimeUnit.MILLISECONDS);
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {}
		
		executor.shutdownNow();
		soundJob.stop();
		
	}

}
