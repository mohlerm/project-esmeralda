package ch.esmeralda.quasimodo.net;

public interface QClient {
	public class UnableToConnectException extends Exception{
		private static final long serialVersionUID = 4952945040581474032L;
		public UnableToConnectException(){}	
	};
	
	public Object sendRequest(Object request); //blocking

	public void connect(String ip, int port) throws UnableToConnectException;
	public void disconnect();
	public boolean isConnected();
}
