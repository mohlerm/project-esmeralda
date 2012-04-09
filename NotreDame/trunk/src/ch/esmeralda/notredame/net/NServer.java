package ch.esmeralda.notredame.net;

import java.util.ArrayList;
import java.util.List;
import java.net.*;

public interface NServer {
	/*
	interrupt {
		response = wdh.verarbeite(request);
		send(response)
	}
	*/
	public void start(int port);
	public void stop();
	public boolean isRunning();
	public List<Socket> getConnections();
	public int getPort();
}
