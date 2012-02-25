package ch.esmeralda.quasimodo.net;

import java.io.*;
import java.net.*;

public class QClientImpl implements QClient {
	private Socket socket;
	private boolean connected = false;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	
	@Override
	public void connect(String ip, int port) throws UnableToConnectException {
		try {
			socket = new Socket(ip, port);
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
			connected = true;
		} catch (Exception e) {throw new UnableToConnectException();}
	}

	@Override
	public void disconnect() {
		try {
			socket.close();
		} catch (Exception e) {}
	}

	@Override
	public boolean isConnected() {
		return connected;
	}

	@Override
	public Object sendRequest(Object request) {
		try {
			out.writeObject(request);
			out.flush();
			return in.readObject();
		} catch (Exception e) {}
		return null;
	}
}
