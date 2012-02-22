package ch.esmeralda.notredame.unitHandling;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * class WorkDay
 * 
 * implements a sorted List of TaskUnits
 * 
 * @author ThomasR
 * @version 0.7
 * @since 0.1
 * @see TaskUnit
 */
public interface Workday {
	
	//public Workday();
	
	//public Workday(Timestamp start, Time duration, String com);
	
	/**
	 * Doesn't care if it's older than the duration!!! (coming soon...)
	 * @param TimeStamp timestamp actual time
	 * @return TaskUnit
	 */
	public TaskUnit getActiveUnit(Timestamp time);
	/**
	 * adds a new TaskUnit, sorts it, checks for overlapping
	 * 
	 * @param TaskUnit
	 * @throws OverlappingTaskUnitException
	 */
	public void addUnit (TaskUnit unit);

	/**
	 * Removes a TaskUnit at an given index.
	 * @param index index of the Unit which should be removed
	 * @throws OverlappingTaskUnitException
	 */
	public void removeUnit (int index);

	/**
	 * Represents a Workday in a String
	 * @return string representation of the Workday
	 */
	public String toString();
	/**
	 * Get the TaskUnit at 'index', starting with 0.
	 * 
	 * @param index index of the TaskUnit
	 * @return TaskUnit at the given index
	 * @throws IndexOutOfBoundsException
	 */
	public TaskUnit getUnit(int index);
	
	/**
	 * Gives back the number of TaskUnit elements
	 * @return number of TaskUnit elements
	 */
	public int size();
	/**
	 * Provides the internal list of TaskUnits
	 * @return a sorted list of all TaskUnits
	 */
	public List<TaskUnit> getList();
}






