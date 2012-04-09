package ch.esmeralda.notredame.unitHandling;

import java.util.*;
import java.sql.Timestamp;

import ch.esmeralda.DataExchange.TaskUnit;

/**
 * class WorkdayImpl
 * 
 * implements a sorted List of TaskUnits
 * 
 * @author ThomasR
 * @version 0.8
 * @since 0.1
 * @see TaskUnit
 */
public class WorkdayImpl implements Workday{
	//contains the list of all the TaskUnit sorted by starttime
	List<TaskUnit> daylist;
	long keycount = 0;
	
	public WorkdayImpl(){
		//TODO
		this.daylist = new ArrayList<TaskUnit>(30);
	}
	
	/**
	 * Doesn't care if it's older than the duration!!! (coming soon...)
	 * @param TimeStamp timestamp actual time
	 * @return TaskUnit
	 */
	public TaskUnit getActiveUnit(Date date){
	   if(daylist.size()==0||daylist==null) return null;
	   
	   TaskUnit active = null;
		for(int j=0;j<daylist.size();j++){
			if(daylist.get(j).getStarttime().before(date))
				active = daylist.get(j);
		}
		return active;
	}
	/**
	 * adds a new TaskUnit, sorts it, checks for overlapping
	 * 
	 * @param TaskUnit
	 * @throws OverlappingTaskUnitException
	 */
	@Override
	public void addUnit (TaskUnit unit){
		daylist.add(unit);
		this.sortVec();
		keycount++;
		unit.setKey(keycount);
	}
	
	private void sortVec(){
		Collections.sort(daylist, new Comparator<TaskUnit>() {
			   public int compare(TaskUnit s1, TaskUnit s2){
			      return s1.getStarttime().compareTo(s2.getStarttime());
			   }
		});
		
		//Time overlapping? Doesn't seem to work....
		/*
		for(int i=0;i<(daylist.size()-1);i++){
		   if((daylist.get(i).starttime.getTime() + daylist.get(i).duration.getTime())>daylist.get(i+1).starttime.getTime()&&
				   (daylist.get(i).starttime.getTime()<daylist.get(i+1).getStarttime().getTime())){
			   throw new OverlappingTaskUnitException();
		   }
		   if((daylist.get(i+1).starttime.getTime() + daylist.get(1+i).duration.getTime())>daylist.get(i).starttime.getTime()&&
				   (daylist.get(i+1).starttime.getTime()<daylist.get(i).getStarttime().getTime())){
			   throw new OverlappingTaskUnitException();
		   }
		}
		*/

	}
	/**
	 * Removes a TaskUnit at an given index.
	 * @param index index of the Unit which should be removed
	 * @throws OverlappingTaskUnitException
	 */
	@Override
	public void removeUnitByIndex(int index){
		try{
			daylist.remove(index);
		}catch (ArrayIndexOutOfBoundsException e){
			System.out.println("Index out of bounds. Can't remove.");
		}
	}
	
	/**
	 * Removes a given TaskUnit.
	 * @param unit TaskUnit wich should be removed
	 * @throws OverlappingTaskUnitException
	 */
	@Override
	public void removeUnitByKey(int key){
		for(TaskUnit unit : daylist){
			if(unit.getKey()==key){
				daylist.remove(unit);
				break;
			}
		}
	}
	/*
	/**
	 * Calculates the duration from the
	 * first unit to te end of the last unit
	 * @return the duration as a Time Object
	 *//*
	public Time getDuration(){
	   if(daylist==null||daylist.size()==0) return new Time(0, 0, 0);
	   long end = daylist.get(daylist.size()-1).getStarttime().getTime()+daylist.get(daylist.size()-1).getDuration();
	   long start = daylist.get(0).getStarttime().getTime();
	   return new Time(end-start);
	}*/
	/**
	 * Represents a WorkdayImpl in a String
	 * @return string representation of the WorkdayImpl
	 */
	public String toString(){
		StringBuffer str = new StringBuffer();
		String starttime = "";
		if(!this.daylist.isEmpty()){
		   starttime = daylist.get(0).getStarttime().toString();
		}
		str.append("Workday Starts at: " + starttime + '\n');
		str.append("-----------------------------------------------\n");
		str.append("start \t\t\t\tduration\t stream \tkey \tcomment\n");
		for(TaskUnit unit:daylist){
			str.append(unit.toString() + '\n');
//			str.append(unit.getStarttime().toString()+" \t");
//			str.append(unit.getDuration()+" \t");
//			str.append(unit.getStreamURL() + '\t');
//			str.append(unit.getKey()+" \t");
//			str.append(unit.getDescription() + '\n');
		}
		str.append('\n');
		return str.toString();
	}
	/**
	 * Get the TaskUnit at 'index', starting with 0.
	 * 
	 * @param index index of the TaskUnit
	 * @return TaskUnit at the given index
	 * @throws IndexOutOfBoundsException
	 */
	public TaskUnit getUnit(int index) throws IndexOutOfBoundsException {
	      return daylist.get(index);
	}
	
	/**
	 * Gives back the number of TaskUnit elements
	 * @return number of TaskUnit elements
	 */
	public int size(){
	   return daylist.size();
	}

	@Override
	public List<TaskUnit> getList() {
		return daylist;
	}

	@Override
	public void reset() {
		daylist.clear();
		keycount = 0;
	}
	
}
