package ch.esmeralda.notredame.net;

import java.util.*;
import java.io.*;

import java.util.List;
import java.net.*;
public class NServerImpl implements NServer {
	private static final boolean D=true;
	private int port;
	private boolean active = false;
	private ServerSocket serverSocket;
	private Listener listener;
	private ArrayList<Socket> socketList = new ArrayList<Socket>();

	public List getConnections() {
		return socketList;
	}

	public boolean isRunning() {
		return active;
	}

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
					System.out.print("N: client connected, blub...");
					socketList.add(socket);
					if(D) System.out.println("added socket");
					ClientHandler clientHandler = new ClientHandler(socket);
					clientHandler.start();
				} catch (Exception e) {}
			}
		}
	}
	
	private class ClientHandler extends Thread {
		private Socket socket;
		private PrintWriter out;
		private BufferedReader in;
		
		public ClientHandler(Socket socket) {
			if(D) System.out.println("new ClientHandler...");
			this.socket = socket;
		}
		public void run() {
			if(D) System.out.println("ClientHandler started!");
			try {
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);
				//test stuff
				System.out.print(in.readLine());
				//until here
			} catch (IOException e) {}
		}
	}
}
