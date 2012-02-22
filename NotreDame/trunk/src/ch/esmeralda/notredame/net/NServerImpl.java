package ch.esmeralda.notredame.net;

import java.util.List;
import java.net.*;
public class NServerImpl implements NServer {
	private int port;
	private boolean active = false;
	private ServerSocket serverSocket;
	private Listener listener;

	@Override
	public List getConnections() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isRunning() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void start(int port) {
		this.port = port;
		try {
			serverSocket = new ServerSocket(port);
			active = true;
			listener.start();
		} catch (Exception e) {}
	}

	@Override
	public void stop() {
		try {
			if (active) serverSocket.close();
			active = false;
		} catch (Exception e) {}
	}
	
	private class Listener extends Thread {
		public void run() {
			while(active) {
				try {
					/*
					 * todo:
					 * accept a connection and add socket to list
					 * call client handler
					 */
				} catch (Exception e) {}
			}
		}
	}
}
