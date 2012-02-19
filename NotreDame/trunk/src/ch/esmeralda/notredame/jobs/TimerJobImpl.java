package ch.esmeralda.notredame.jobs;

public class TimerJobImpl extends TimerJob{
	
	public TimerJobImpl(SoundJob soundJob) {
		super(soundJob);
	}

	@Override
	public void run(){
		//Valid soundJob?
		if(soundJob==null||soundJob.getState()==Thread.State.TERMINATED) return;
		//not yet started?
		if(soundJob.getState()==Thread.State.NEW) soundJob.start();
		
		
		
	}

}
