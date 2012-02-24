package ch.esmeralda.quasimodo.net;

import java.net.*;

public class QClientImpl implements QClient {
	private Socket socket;
	@Override
	public void connect(String ip, int port) throws UnableToConnectException {
		try {
			socket = new Socket(ip, port);
		} catch (Exception e) {throw new UnableToConnectException();}

	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isConnected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String sendRequest(String request) {
		// TODO Auto-generated method stub
		return null;
	}

}
