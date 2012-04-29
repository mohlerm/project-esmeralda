package ch.esmeralda.quasimodo.tests;

import java.util.Date;


import ch.esmeralda.DataExchange.*;

public class AthmosTester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TaskUnit unit = new TaskUnit(new Date(), 40);
		System.out.println(unit.getStarttimeAsString(0));
		System.out.println(unit.getDurationAsString());
	}

}
