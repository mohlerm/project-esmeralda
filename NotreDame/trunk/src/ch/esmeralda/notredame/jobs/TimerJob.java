package ch.esmeralda.notredame.jobs;

import ch.esmeralda.DataExchange.TaskUnit;
import ch.esmeralda.notredame.unitHandling.Workday;
/**
 * TimerJob starts and stops the SoundStream depending on the active
 * Unit in the current Workday.
 * @author Athmos
 *
 */
public abstract class TimerJob implements Runnable{
	protected StreamJob streamJob = null;
	protected TaskUnit activeTask = null;
	protected Workday workday = null;
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
	public TimerJob(StreamJob streamJob,Workday workday){
		this.streamJob = streamJob;
		this.workday = workday;
	}
	/**
	 * implements Runnable, for use with a
	 * ThreadPoolExecutor
	 * @return 
	 */
	@Override
	public abstract void run();
}
