package ch.esmeralda.quasimodo.tests;

import java.util.ArrayList;

public class EppiTest {

	public static void main(String[] args) {
		ArrayList<String> liste = new ArrayList<String>();
		liste.add("Hallo, Liste wurde erstellt.");
		TestClass1 test1 = new TestClass1(liste);
		test1.changelist();
		test1 = null;
		System.out.println(liste.toString());  // WORKS!!
	}

}
