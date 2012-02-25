package ch.esmeralda.notredame.unitHandling;

public interface WorkdayHandler {	
	/**
	 * Handles incoming requests
	 * @param request a command from a Client
	 * @return a response to the given request
	 */
	public Object getResponse(Object request);
}
