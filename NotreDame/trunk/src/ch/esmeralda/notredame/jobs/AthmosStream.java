
package ch.esmeralda.notredame.jobs;

import java.net.URL;

import javazoom.jl.player.Player;

public class AthmosStream implements StreamJob{
    private class PlaySound extends Thread{
    	private String urlString;
    	public PlaySound(String urlString){
    		this.urlString = urlString;
    	}
        public void run() {
            try {
            	URL url = new URL(urlString);
                player = new Player(url.openStream());
                player.play(); }
            catch (Exception e) {}
        }
    }
    
	private Player player;
	private PlaySound playSound;
	
	
	public void startStream(String urlString) {
		playSound = new PlaySound(urlString);
        playSound.start(); //du chasch en thread nur 1 starte!!!!
	}

	public void stopStream() {
		player.close();
		try {
			playSound.join();
		} catch (InterruptedException e) {}
	}

	public boolean isPerforming()
	{
		return playSound.isAlive();
	}
}
