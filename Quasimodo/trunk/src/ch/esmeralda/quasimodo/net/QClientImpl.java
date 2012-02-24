package ch.esmeralda.quasimodo.net;

import java.io.*;
import java.net.*;

public class QClientImpl implements QClient {
	private Socket socket;
	private boolean connected = false;
	private BufferedReader in;
	private PrintWriter out;
	@Override
	public void connect(String ip, int port) throws UnableToConnectException {
		try {
			socket = new Socket(ip, port);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
			connected = true;
			
			out.println("asdlkfsalkdks");
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
	public String sendRequest(String request) {
		// TODO Auto-generated method stub
		return null;
	}

}
