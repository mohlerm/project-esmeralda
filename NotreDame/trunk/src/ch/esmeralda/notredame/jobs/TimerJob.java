package ch.esmeralda.notredame.jobs;

import java.util.concurrent.Callable;

import ch.esmeralda.notredame.unitHandling.TaskUnit;
/**
 * TimerJob starts and stops the SoundStream depending on the active
 * Unit in the current Workday.
 * @author Athmos
 *
 */
public abstract class TimerJob implements Runnable{
	protected SoundJob soundJob;
	protected TaskUnit activeTask;
	/*
	public class InvalidSoundJobExeption extends Exception{
		private static final long serialVersionUID = -216773824792544312L;
		public InvalidSoundJobExeption(String msg){
			super(msg);
		}
	}*/
	
	/**
	 * Creates a new TimerJob which should be used
	 * in combination with a ScheduledThreadPoolExecutor
	 * @param soundJob a SoundJob which handles the URL Streams
	 */
	public TimerJob(SoundJob soundJob){
		this.soundJob = soundJob;
	}
	/**
	 * implements Runnable, for use with a
	 * ThreadPoolExecutor
	 * @return 
	 */
	@Override
	public abstract void run();
}
