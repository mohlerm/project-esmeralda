package ch.esmeralda.notredame.net;

import java.net.*;

public class NServerTester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		NServerImpl server = new NServerImpl();
		server.start(1241);
		while(true) {try {Thread.sleep(100);} catch (Exception e) {}}
	}

}
