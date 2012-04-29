package ch.esmeralda.quasimodo.tests;

import java.util.ArrayList;

public class TestClass1 {

	ArrayList<String> liste;
	
	public TestClass1(ArrayList<String> in){
		this.liste = in;
	}
	
	public void changelist() {
		liste.remove(liste.size()-1);
		liste.add("Liste wurde geändert!");
	}
}
