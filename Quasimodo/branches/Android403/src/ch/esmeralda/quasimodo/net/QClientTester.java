package ch.esmeralda.quasimodo.net;


public class QClientTester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			QClientImpl client = new QClientImpl();
			client.connect("127.0.0.1", 10002);
			System.out.print((String)client.sendRequest("lollll"));
		} catch (Exception e) {System.out.print("Q: Something failed");}
	}
}
