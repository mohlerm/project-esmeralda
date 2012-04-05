package ch.esmeralda.notredame.net;

import java.util.*;
import ch.esmeralda.notredame.unitHandling.*;
import java.io.*;
import java.util.List;
import java.net.*;

public class NServerImplEppi implements NServer {
	private int port;
	private boolean active = false;
	private ServerSocket serverSocket;
	private Listener listener;
	private ArrayList<Socket> socketList = new ArrayList<Socket>();
	private WorkdayHandler workdayHandler = null;

	public NServerImplEppi(WorkdayHandler workdayHandler) {
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
			while (active) {
				try {
					Object o = in.readObject();
					out.writeObject(workdayHandler.getResponse(o));
					out.flush();
				} catch (IOException e) {
					System.err.println("IOException: "+e.getMessage());
					break;
				} catch (ClassNotFoundException e) {
					System.err.println("ClassNotFound: "+e.getMessage());
					break;
				} catch (Exception e) {
					System.err.println("Error Transmitting: "+e.getMessage());
					break;
				}
			}
		}
	}
}
