package ch.esmeralda.notredame.unitHandling;

public interface WorkdayHandler {
	/**
	 * Handles incoming requests
	 * @param request a commandh from a Client
	 * @return a response to the given request
	 */
	public String getResponse(String request);
	
}
