package ch.esmeralda.notredame.unitHandling;

import java.util.*;
import java.sql.Timestamp;

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
	
	public WorkdayImpl(){
		//TODO
		this.daylist = new ArrayList<TaskUnit>(30);
	}
	
	/**
	 * Doesn't care if it's older than the duration!!! (coming soon...)
	 * @param TimeStamp timestamp actual time
	 * @return TaskUnit
	 */
	public TaskUnit getActiveUnit(Timestamp time){
	   if(daylist.size()==0||daylist==null) return null;
	   
	   TaskUnit active = null;
		for(int j=0;j<daylist.size();j++){
			if(daylist.get(j).getStarttime().before(time))
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
	public void addUnit (TaskUnit unit){
		daylist.add(unit);
		this.sortVec();
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
	public void removeUnit (int index){
		try{
			daylist.remove(index);
		}catch (ArrayIndexOutOfBoundsException e){
			
		}
	}
	
	/**
	 * Removes a given TaskUnit.
	 * @param unit TaskUnit wich should be removed
	 * @throws OverlappingTaskUnitException
	 */
	public void removeUnit (TaskUnit unit){
		daylist.remove(unit);
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
		str.append("start \t\t\tduration \tcomment\n");
		for(int i=0;i<daylist.size();i++){
			str.append(daylist.get(i).getStarttime().toString()+" \t");
			str.append(daylist.get(i).getDuration()+" \t");
			str.append(daylist.get(i).getDescription() + '\n');
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
	
}
