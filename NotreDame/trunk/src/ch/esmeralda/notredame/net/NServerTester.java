package ch.esmeralda.notredame.net;

import java.net.*;

public class NServerTester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		NServerImpl server = new NServerImpl(null);
		server.start(10002);
		while(true) {try {Thread.sleep(100);} catch (Exception e) {}}
	}

}
