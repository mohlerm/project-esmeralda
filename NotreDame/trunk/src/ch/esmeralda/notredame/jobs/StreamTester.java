package ch.esmeralda.notredame.jobs;

public class StreamTester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		StreamJobImpl sji = new StreamJobImpl();
		sji.startStream("http://u11aw.di.fm:80/di_spacemusic");
		System.out.print("blabedi blub");
	}

}
