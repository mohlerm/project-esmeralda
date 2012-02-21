package ch.esmeralda.notredame.jobs;

import javazoom.jl.player.Player;

public class StreamJobImpl extends StreamJob{
	private Player player;
	private boolean isRunning = false;

    Thread playSound = new Thread() {
        public void run() {
            try { player.play(); }
            catch (Exception e) { System.out.println(e); }
        }
    };
	
	@Override
	public void startStream(String url) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopStream() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run(){
		
	}
	
}
