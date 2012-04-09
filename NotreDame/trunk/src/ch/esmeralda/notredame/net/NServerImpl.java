package ch.esmeralda.notredame.net;

import java.util.*;
import ch.esmeralda.notredame.unitHandling.*;
import java.io.*;
import java.util.List;
import java.net.*;

public class NServerImpl implements NServer {
	private int port;
	private boolean active = false;
	private ServerSocket serverSocket;
	private Listener listener;
	private ArrayList<Socket> socketList = new ArrayList<Socket>();
	private WorkdayHandler workdayHandler = null;

	public NServerImpl(WorkdayHandler workdayHandler) {
		this.workdayHandler = workdayHandler;
	}
	
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
		} catch (Exception e) {}
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
					socket = serverSocket.accept();
					socketList.add(socket);
					ClientHandler clientHandler = new ClientHandler(socket);
					clientHandler.start();
				} catch (Exception e) {}
			}
		}
	}
	
	private class ClientHandler extends Thread {
		private Socket socket;
		private ObjectOutputStream out;
		private ObjectInputStream in;
		
		public ClientHandler(Socket socket) {
			this.socket = socket;
			try {
				out = new ObjectOutputStream(socket.getOutputStream());
				in = new ObjectInputStream(socket.getInputStream());
			} catch (Exception e) {}
		}
		public void run() {
			while(true){
				try {
					out.writeObject(workdayHandler.getResponse(in.readObject()));
					out.flush();
				} catch (Exception e) {
					System.out.println("Connection lost");
					break;
				}
			}
		}
	}

	@Override
	public int getPort() {
		// TODO Auto-generated method stubd
		return 0;
	}
}
