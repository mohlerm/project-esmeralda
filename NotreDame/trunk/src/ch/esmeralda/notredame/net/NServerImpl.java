package ch.esmeralda.notredame.net;

import java.util.*;

import java.util.List;
import java.net.*;
public class NServerImpl implements NServer {
	private int port;
	private boolean active = false;
	private ServerSocket serverSocket;
	private Listener listener;
	private ArrayList<Socket> socketList;

	@Override
	public List getConnections() {
		// TODO Auto-generated method stub
		return socketList;
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
			System.out.print("listening on port "+port+"\n");
			serverSocket = new ServerSocket(port);
			active = true;
			listener = new Listener();
			listener.start();
		} catch (Exception e) {System.out.print("Something failed");}
	}

	@Override
	public void stop() {
		try {
			if (active) serverSocket.close();
			active = false;
		} catch (Exception e) {}
	}
	
	private class Listener extends Thread {
		private Socket socket;
		public void run() {
			while(active) {
				try {
					/*
					 * todo:
					 * accept a connection and add socket to list
					 * call client handler
					 */
					socket = serverSocket.accept();
					socketList.add(socket);
					ClientHandler clientHandler = new ClientHandler(socket);
				} catch (Exception e) {}
			}
		}
	}
	
	private class ClientHandler extends Thread {
		private Socket socket;
		public ClientHandler(Socket socket) {
			this.socket = socket;
		}
		public void run() {

		}
	}
}
