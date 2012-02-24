package ch.esmeralda.quasimodo.tests;

import java.util.Date;

import ch.esmeralda.quasimodo.unitHandlingWrapper.QTaskUnit;

public class AthmosTester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		QTaskUnit unit = new QTaskUnit(new Date(), 40);
		System.out.println(unit.getStarttimeAsString(0));
		System.out.println(unit.getDurationAsString());
	}

}
