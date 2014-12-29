
package ch.esmeralda.notredame.jobs;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;

import ch.esmeralda.notredame.main.Constants;
import javazoom.jl.player.Player;
/**
 * Another Implementation of StreamJob
 * 
 * @author Thomas Richner
 * @version 0.1
 *
 */
public class AthmosStream implements StreamJob{
	private static final String STARTSOUND_PATH = "Soviet_Anthem_Instrumental_1955.mp3";
	//internal thread that handles the player, because player is blocking
    private class PlaySound extends Thread{
    	private String urlString;
    	public PlaySound(String urlString){
    		this.urlString = urlString;
    	}
        public void run() {
            try {
            	
            	if(urlString.equals("")){
	    			try {
	    				BufferedInputStream bis = new BufferedInputStream(new FileInputStream(STARTSOUND_PATH));
	    				player = new Player(bis);
	    			} catch (FileNotFoundException e) {
	    				// TODO Auto-generated catch block
	    				e.printStackTrace();
	    			}
            	}else{
            		URL url = new URL(urlString);
            		player = new Player(url.openStream());
            	}
            	
                
                player.play(); }
            catch (Exception e) {}
        }
    }
    
	private Player player;						//the StreamPlayer
	private PlaySound playSound;				//the Player Thread
	
	public AthmosStream(){
		if(Constants.V) System.out.println("Created new AthmosStream");
	}
	
	public void startStream(String urlString) {
		if(Constants.V) System.out.println("starting Stream " + urlString);
		
		playSound = new PlaySound(urlString);	//create a new Player thread

        playSound.start();						//start the thread
	}

	public void stopStream() {
		if(Constants.V) System.out.println("stopping Stream");
		if(player==null||playSound==null)		//stopping before starting?
			return;
		player.close();							//close the player
		try {
			playSound.join(5000);					//wait for the Thread to exit clean, be aware: possible block
		} catch (InterruptedException e) {}
	}

	public boolean isPerforming()
	{
		return playSound!=null && playSound.isAlive();				//any stream playing right now?
	}
}
