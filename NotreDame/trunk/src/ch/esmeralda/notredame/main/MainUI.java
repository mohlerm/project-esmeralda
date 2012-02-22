package ch.esmeralda.notredame.main;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ch.esmeralda.notredame.jobs.AthmosStream;
import ch.esmeralda.notredame.jobs.StreamJob;
import ch.esmeralda.notredame.jobs.TimerJob;
import ch.esmeralda.notredame.jobs.TimerJobImpl;
import ch.esmeralda.notredame.unitHandling.TaskUnit;
import ch.esmeralda.notredame.unitHandling.Workday;
import ch.esmeralda.notredame.unitHandling.WorkdayImpl;

public class MainUI{
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("starting...");
		
		ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
		
		StreamJob streamJob = new AthmosStream();

		Workday workday = new WorkdayImpl();
		
		if(true){//args.length>0){
			workday = set_debug();
			System.out.println(workday.toString());
		}
		
		TimerJob timerJob = new TimerJobImpl(streamJob,workday);
		
		System.out.println("schedule jobs");
		executor.scheduleAtFixedRate(timerJob, 500, 1000, TimeUnit.MILLISECONDS);
		
		Scanner in = new Scanner(System.in);
		String msg;
		boolean quit = false;
		 while(!quit){
	         msg = in.nextLine();
	         //d(msg.charAt(msg.length()-1));
	         
	         if(msg.equals("help")){
	            //help();            
	         }else if(msg.equals("set default")){
	        	p("starting hour: ");
	        	int hour = in.nextInt();
	        	while(hour<0||hour>23){	
	        		hour = in.nextInt();
	        	}
	        	p("my int is: "+hour);
	            
	        	set_default(workday,hour);
	         }else if(msg.equals("show")){
	            p(workday.toString());
	         }else if(msg.equals("quit")){
	            quit = true;
	         }else if(msg.equals("active")){
	            active(workday);
	         }else if(msg.equals("remove")){
	            //remove();
	         }else if(msg.equals("add")){
	            //add();
	         }else if(msg.equals("show sounds")){
	            //show_sounds();
	         }
	         else if(msg.equals("skip sound")){
	            //skip_sound();
	         }else{
	            //d("Unknown command!");
	            //help();
	         }
	      }
		
		
		System.out.println("stopping jobs");
		executor.shutdownNow();
		System.out.println("...bye, bye");
	}
	
	//helper
	private static void d(Object msg){
		System.out.println(msg);
	}
	private static void p(Object msg){
	    System.out.print(msg);
	}
	private static void nl(){
	    System.out.println();
	}
	
	
	private static Workday set_debug(){
		Workday workday = new WorkdayImpl();
		System.out.println("prefill a debug workday");
		long now = System.currentTimeMillis()+1000;
		TaskUnit task;
		for(int i=0;i<10;i++){
			if(i%2==0)	task = new TaskUnit(new Date(now+i*10000), 10000, "http://u11aw.di.fm:80/di_trance");
			else		task = new TaskUnit(new Date(now+i*10000), 10000, "");
			task.setDescription("debug "+i);
			workday.addUnit(task);
		}
		return workday;
	}
	
	private static void set_default(Workday workday,int hour){
		//System.out.println("prefill a debug workday");
		workday.reset();
		//today at midnight
		long a = System.currentTimeMillis();
		a = a-a%(1000*3600*24);
		long start = a + hour*3600*1000;
		
		
		
		//Calendar cal = new GregorianCalendar();
		
		TaskUnit task;
		
		task = new TaskUnit(new Date(start), 75*60*1000, "");
		task.setDescription("Work1");
		workday.addUnit(task);
		start += 75*60*1000;
		
		task = new TaskUnit(new Date(start), 15*60*1000, "http://u11aw.di.fm:80/di_trance");		
		task.setDescription("break1");
		workday.addUnit(task);
		start += 15*60*1000;
		
		task = new TaskUnit(new Date(start), 60*60*1000, "");
		task.setDescription("Work2");
		workday.addUnit(task);
		start += 60*60*1000;
		
		task = new TaskUnit(new Date(start), 15*60*1000, "http://u11aw.di.fm:80/di_trance");		
		task.setDescription("break2");
		workday.addUnit(task);
		start += 15*60*1000;
		
		task = new TaskUnit(new Date(start), 45*60*1000, "");
		task.setDescription("Work2");
		workday.addUnit(task);
		start += 45*60*1000;
		
		task = new TaskUnit(new Date(start), 60*60*1000, "http://u11aw.di.fm:80/di_trance");		
		task.setDescription("supper");
		workday.addUnit(task);
		start += 60*60*1000;
		
		task = new TaskUnit(new Date(start), 75*60*1000, "");
		task.setDescription("Work1");
		workday.addUnit(task);
		start += 75*60*1000;
		
		task = new TaskUnit(new Date(start), 15*60*1000, "http://u11aw.di.fm:80/di_trance");		
		task.setDescription("break1");
		workday.addUnit(task);
		start += 15*60*1000;
		
		task = new TaskUnit(new Date(start), 60*60*1000, "");
		task.setDescription("Work2");
		workday.addUnit(task);
		start += 60*60*1000;
		
		task = new TaskUnit(new Date(start), 15*60*1000, "http://u11aw.di.fm:80/di_trance");		
		task.setDescription("break2");
		workday.addUnit(task);
		start += 15*60*1000;
		
		task = new TaskUnit(new Date(start), 45*60*1000, "");
		task.setDescription("Work3");
		workday.addUnit(task);
		start += 45*60*1000;
		
		task = new TaskUnit(new Date(start), 10*60*1000, "http://u11aw.di.fm:80/di_trance");		
		task.setDescription("end of day");
		workday.addUnit(task);
		//start += 15*60*1000;
			
	}
	
	private static void active(Workday workday){
		workday.getActiveUnit(new Date());
	}
}
