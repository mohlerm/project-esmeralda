package ch.esmeralda.quasimodo.unitHandlingWrapper;

import java.util.Date;
import java.util.List;

import ch.esmeralda.DataExchange.*;

/**
 * class WorkDay
 * 
 * implements a sorted List of TaskUnits
 * 
 * @author Thomas Richner
 * @version 0.1
 * @see QTaskUnit
 */
public interface WorkdayWrapper {
	
	/**
	 * Doesn't care if it's older than the duration!!! (coming soon...)
	 * @param TimeStamp timestamp actual time
	 * @return TaskUnit
	 */
	public TaskUnit getActiveUnit();
	/**
	 * adds a new TaskUnit, sorts it, checks for overlapping
	 * 
	 * @param QTaskUnit
	 */
	public boolean addUnit (Date starttime,long duration,String streamURL);

	/**
	 * Removes a TaskUnit at an given index.
	 * @param index index of the Unit which should be removed
	 */
	public boolean removeUnitByIndex (int index);

	/**
	 * Removes a TaskUnit with a given key.
	 * @param modified_key key of the Unit which should be removed
	 */
	public boolean removeUnitByKey (long modified_key);
	/**
	 * Represents a Workday in a String
	 * @return string representation of the Workday
	 */
	public String toString();
	
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
	/**
	 * Resets a workday and removes everything
	 */
	public boolean reset(int hour);
}






