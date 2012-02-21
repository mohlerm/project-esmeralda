package ch.esmeralda.notredame.jobs;


public abstract class StreamJob extends Thread {
	/**
	 * plays a stream from the given url or
	 * plays the alarm if url is NULL
	 * @param url an url of a stream
	 */
	public abstract void startStream(String url);
	public abstract void stopStream();
	
}
