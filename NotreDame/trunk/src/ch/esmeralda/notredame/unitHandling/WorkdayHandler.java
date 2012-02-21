package ch.esmeralda.notredame.unitHandling;

public interface WorkdayHandler {
	/**
	 * Handles incomming requests
	 * @param request a request from a Client
	 * @return a response to the given request
	 */
	public String getResponse(String request);
	
}
