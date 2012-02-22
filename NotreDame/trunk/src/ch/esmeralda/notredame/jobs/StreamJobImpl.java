package ch.esmeralda.notredame.jobs;

import java.net.URL;

import javazoom.jl.player.Player;

public class StreamJobImpl implements StreamJob{
	private Player player;
	private String urlString;
	private boolean performing = false;

    Thread playSound = new Thread() {
        public void run() {
            try {
            	URL url = new URL(urlString);
                player = new Player(url.openStream());
                player.play(); }
            catch (Exception e) {}
            performing = false;
        }
    };
	
	public void startStream(String urlString) {
		this.urlString = urlString;
        performing = true;
        playSound.start(); //du chasch en thread nur 1 starte!!!!
	}

	public void stopStream() {
		player.close();
		performing = false;
	}

	public boolean isPerforming()
	{
		return performing; //warum nid eifach playSound.isRunning() ???
	}
}
