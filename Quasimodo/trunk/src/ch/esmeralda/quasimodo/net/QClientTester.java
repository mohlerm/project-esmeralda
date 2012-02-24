package ch.esmeralda.quasimodo.net;

import java.net.*;

public class QClientTester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			QClientImpl client = new QClientImpl();
			client.connect("127.0.0.1", 1241);
			System.out.print("Connected!");
		} catch (Exception e) {System.out.print("Q: Something failed");}

	}

}
