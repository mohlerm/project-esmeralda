package ch.esmeralda.notredame.net;

import java.util.List;

public interface NServer {
	/*
	interrupt {
		response = wdh.verarbeite(request);
		send(response)
	}
	*/
	public void start();
	public void stop();
	public boolean isRunning();
	public List getConnections();
}
