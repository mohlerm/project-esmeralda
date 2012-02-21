package time;

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
public class WorkDay extends TimeUnit{
	//contains the list of all the TimeUnit sorted by starttime
	Vector<TimeUnit> daylist;
	
	public WorkDay(){
		super(null, null, null,"");
		this.daylist = new Vector<TimeUnit>(30);
	}
	
	public WorkDay(Timestamp start, Time duration, String com){
		super(start, duration, null,com);
		if(this.start==null) start = new Timestamp(0);
		this.daylist = new Vector<TimeUnit>(30);
	}
	
	/**
	 * Doesn't care if it's older than the duration!!! (coming soon...)
	 * @param TimeStamp timestamp actual time
	 * @return TimeUnit
	 */
	public TimeUnit getActiveUnit(Timestamp time){
	   if(daylist.size()==0||daylist==null) return null;
	   
	   if(time.before(this.start)) return null; //not started yet
	   
		for(int j=0;j<daylist.size()-1;j++){
			if(daylist.get(j+1).getStart().after(time)) //ENDTIME!!!
				return daylist.get(j);
		}
		return daylist.lastElement();
	}
	/**
	 * adds a new TimeUnit, sorts it, checks for overlapping
	 * 
	 * @param timeunit
	 * @throws OverlappingTimeUnitException
	 */
	public void addUnit (TimeUnit unit)throws OverlappingTimeUnitException{
		daylist.add(unit);
		sortVec();
		this.start = daylist.firstElement().getStart();
	}
	
	private void sortVec() throws OverlappingTimeUnitException {
		Collections.sort(daylist, new Comparator<TimeUnit>() {
			   public int compare(TimeUnit s1, TimeUnit s2){
			      return s1.getStart().compareTo(s2.getStart());
			   }
		});
		
		//Time overlapping? Doesn't seem to work....
		for(int i=0;i<(daylist.size()-1);i++){
		   if((daylist.get(i).start.getTime() + daylist.get(i).duration.getTime())>daylist.get(i+1).start.getTime()&&
				   (daylist.get(i).start.getTime()<daylist.get(i+1).getStart().getTime())){
			   throw new OverlappingTimeUnitException();
		   }
		   if((daylist.get(i+1).start.getTime() + daylist.get(1+i).duration.getTime())>daylist.get(i).start.getTime()&&
				   (daylist.get(i+1).start.getTime()<daylist.get(i).getStart().getTime())){
			   throw new OverlappingTimeUnitException();
		   }
		}

	}
	/**
	 * Removes a TimeUnit at an given index.
	 * @param index index of the Unit which should be removed
	 * @throws OverlappingTimeUnitException
	 */
	public void removeUnit (int index){
		try{
			daylist.remove(index);
		}catch (ArrayIndexOutOfBoundsException e){
			
		}
		if(daylist.size()>0)	this.start = daylist.firstElement().getStart();
		else this.start = new Timestamp(0);
	}
	
	/**
	 * Removes a given TimeUnit.
	 * @param unit TimeUnit wich should be removed
	 * @throws OverlappingTimeUnitException
	 */
	public void removeUnit (TimeUnit unit){
		daylist.remove(unit);
		if(daylist.size()>0)  this.start = daylist.firstElement().getStart();
      else this.start = new Timestamp(0);
	}
	/**
	 * Calculates the duration from the
	 * first unit to te end of the last unit
	 * @return the duration as a Time Object
	 */
	public Time getDuration(){
	   if(daylist==null||daylist.size()==0) return new Time(0, 0, 0);
	   long end = daylist.get(daylist.size()-1).getStart().getTime()+daylist.get(daylist.size()-1).getDuration().getTime();
	   long start = daylist.get(0).getStart().getTime();
	   return new Time(end-start);
	}
	/**
	 * Represents a Workday in a String
	 * @return string representation of the Workday
	 */
	public String toString(){
		StringBuffer str = new StringBuffer();
		String start = "unknown",duration=this.getDuration().toString();
		if(this.daylist.size()>0){
		   start = this.daylist.get(0).getStart().toString();
		}
		str.append("Workday Starts at: " + start + "  Duration: "+ duration + '\n');
		str.append("-----------------------------------------------\n");
		str.append("start \t\t\tduration \tcomment\n");
		for(int i=0;i<daylist.size();i++){
			str.append(daylist.get(i).getStart().toString()+" \t");
			str.append(daylist.get(i).getDuration().toString()+" \t");
			str.append(daylist.get(i).comment + '\n');
		}
		str.append('\n');
		return str.toString();
	}
	/**
	 * Get the TimeUnit at 'index', starting with 0.
	 * 
	 * @param index index of the TimeUnit
	 * @return TimeUnit at the given index
	 * @throws IndexOutOfBoundsException
	 */
	public TimeUnit getUnit(int index) throws IndexOutOfBoundsException {
	   if(index<daylist.size())
	      return daylist.get(index);
	   else
	      throw new IndexOutOfBoundsException();
	}
	
	/**
	 * Gives back the number of TimeUnit elements
	 * @return number of TimeUnit elements
	 */
	public int size(){
	   return daylist.size();
	}
	
}
