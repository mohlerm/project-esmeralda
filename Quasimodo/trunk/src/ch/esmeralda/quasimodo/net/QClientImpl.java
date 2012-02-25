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
			System.out.print("Q: connecting...");
			socket = new Socket(ip, port);
			//in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			//out = new PrintWriter(socket.getOutputStream(), true);
			ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
			connected = true;
			System.out.print("Q: reading obj...");
			out.println((String)is.readObject());
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
