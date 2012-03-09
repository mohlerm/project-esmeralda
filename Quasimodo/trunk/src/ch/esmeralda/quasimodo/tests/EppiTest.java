package ch.esmeralda.quasimodo.tests;

import java.util.Date;

import ch.esmeralda.quasimodo.unitHandlingWrapper.QTaskUnit;

public class EppiTest {

	public static void main(String[] args) {
		Date date = new Date(56546540);
		QTaskUnit qt = new QTaskUnit(date , 3600000);
		System.out.println(qt.getDurationAsString());
		System.out.println(qt.getStarttimeAsString(0));
	}

}
