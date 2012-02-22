package ch.esmeralda.notredame.net;

import java.util.List;
import java.net.*;
public class NServerImpl implements NServer {
	private int port;
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
		listener.start();
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
	}
	
	private class Listener extends Thread {
		public void run() {
			
		}
	}
}
