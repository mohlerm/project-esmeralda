package ch.esmeralda.quasimodo.net;

import java.io.*;
import java.net.*;

import android.util.Log;

public class QClientImpl implements QClient {
	private Socket socket;
	private boolean connected = false;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	
	public void connect(String ip, int port) throws UnableToConnectException {
		try {
			socket = new Socket(ip, port);
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
			connected = true;
		} catch (Exception e) {throw new UnableToConnectException();}
	}

	public void disconnect() {
		try {
			socket.close();
		} catch (Exception e) {}
	}

	public boolean isConnected() {
		return connected;
	}

	public Object sendRequest(Object request) {
		try {
			Log.d("connection", "writing object:");
			out.writeObject(request);
			Log.d("connection", "writing object done, now flushing");
			out.flush();
			Log.d("connection", "flushing done, now reading");
			Object o = in.readObject();
			Log.d("connection", "reading done!");
			return o;
		} catch (Exception e) {
			Log.e("connection","Could not complete sendRequest: " + e.getMessage());
		}
		return null;
	}
}
