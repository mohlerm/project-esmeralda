package ch.esmeralda.notredame.jobs;

public interface StreamJob {
	/**
	 * plays a stream from the given url or
	 * plays the alarm if url is NULL
	 * @param url an url of a stream
	 */
	public void startStream(String url);
	public void stopStream();
	public boolean isPerforming();
}
