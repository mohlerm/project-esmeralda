package ch.esmeralda.notredame.unitHandling;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * class WorkDay
 * 
 * implements a sorted List of TimeUnits
 * 
 * @author ThomasR
 * @version 0.7
 * @since 0.1
 * @see TimeUnit
 */
public interface Workday {
	
	//public Workday();
	
	//public Workday(Timestamp start, Time duration, String com);
	
	/**
	 * Doesn't care if it's older than the duration!!! (coming soon...)
	 * @param TimeStamp timestamp actual time
	 * @return TimeUnit
	 */
	public TimeUnit getActiveUnit(Timestamp time);
	/**
	 * adds a new TimeUnit, sorts it, checks for overlapping
	 * 
	 * @param timeunit
	 * @throws OverlappingTimeUnitException
	 */
	public void addUnit (TimeUnit unit);

	/**
	 * Removes a TimeUnit at an given index.
	 * @param index index of the Unit which should be removed
	 * @throws OverlappingTimeUnitException
	 */
	public void removeUnit (int index);

	/**
	 * Represents a Workday in a String
	 * @return string representation of the Workday
	 */
	public String toString();
	/**
	 * Get the TimeUnit at 'index', starting with 0.
	 * 
	 * @param index index of the TimeUnit
	 * @return TimeUnit at the given index
	 * @throws IndexOutOfBoundsException
	 */
	public TimeUnit getUnit(int index) throws IndexOutOfBoundsException;
	
	/**
	 * Gives back the number of TimeUnit elements
	 * @return number of TimeUnit elements
	 */
	public int size();
}






