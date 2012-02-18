package ch.esmeralda.notredame.jobs;

public abstract class TimerJob implements Runnable{
	protected SoundJob soundJob;
	public TimerJob(SoundJob soundJob){
		this.soundJob = soundJob;
	}
	
	@Override
	public abstract void run();
}
