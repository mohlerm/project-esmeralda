
package ch.esmeralda.notredame.jobs;

import java.net.URL;
import javazoom.jl.player.Player;
/**
 * Another Implementation of StreamJob
 * 
 * @author Thomas Richner
 * @version 0.1
 *
 */
public class AthmosStream implements StreamJob{
	//internal thread that handles the player, because player is blocking
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
    
	private Player player;						//the StreamPlayer
	private PlaySound playSound;				//the Player Thread
	
	public AthmosStream(){
		System.out.println("Created new AthmosStream");
	}
	
	public void startStream(String urlString) {
		System.out.println("starting Stream");
		playSound = new PlaySound(urlString);	//create a new Player thread
        playSound.start();						//start the thread
	}

	public void stopStream() {
		System.out.println("stopping Stream");
		if(player==null||playSound==null)		//stopping before starting?
			return;
		player.close();							//close the player
		try {
			playSound.join();					//wait for the Thread to exit clean, be aware: possible block
		} catch (InterruptedException e) {}
	}

	public boolean isPerforming()
	{
		return playSound!=null && playSound.isAlive();				//any stream playing right now?
	}
}
