package ch.esmeralda.notredame.utils;

import java.util.Date;

import ch.esmeralda.DataExchange.TaskUnit;
import ch.esmeralda.notredame.main.Constants;
import ch.esmeralda.notredame.unitHandling.Workday;

public class Minions {
	public static void set_default(Workday workday,int hour,int minutes){
		set_default(workday,hour*60+minutes);
	}
	public static void set_default(Workday workday,int minutes){
		//System.out.println("prefill a debug workday");
		workday.reset();
		//today at midnight
		long a = System.currentTimeMillis();
		a = a-a%(1000*3600*24);
		long start = a + minutes*60*1000;
		
		
		
		//Calendar cal = new GregorianCalendar();
		
		TaskUnit task;
		
		task = new TaskUnit(new Date(start), 75*60*1000, "");
		task.setDescription("Work1");
		workday.addUnit(task);
		start += 75*60*1000;
		//a = a-a%(1000*3600*24);
		//long start = a + hour*3600*1000;
		task = new TaskUnit(new Date(start), 15*60*1000, Constants.DI_TRANCE);		
		task.setDescription("break1");
		workday.addUnit(task);
		start += 15*60*1000;
		
		task = new TaskUnit(new Date(start), 60*60*1000, "");
		task.setDescription("Work2");
		workday.addUnit(task);
		start += 60*60*1000;
		
		task = new TaskUnit(new Date(start), 15*60*1000, Constants.DI_TRANCE);		
		task.setDescription("break2");
		workday.addUnit(task);
		start += 15*60*1000;
		
		task = new TaskUnit(new Date(start), 45*60*1000, "");
		task.setDescription("Work2");
		workday.addUnit(task);
		start += 45*60*1000;
		
		task = new TaskUnit(new Date(start), 60*60*1000, Constants.DI_TRANCE);		
		task.setDescription("supper");
		workday.addUnit(task);
		start += 60*60*1000;
		
		task = new TaskUnit(new Date(start), 75*60*1000, "");
		task.setDescription("Work1");
		workday.addUnit(task);
		start += 75*60*1000;
		
		task = new TaskUnit(new Date(start), 15*60*1000, Constants.DI_TRANCE);		
		task.setDescription("break1");
		workday.addUnit(task);
		start += 15*60*1000;
		
		task = new TaskUnit(new Date(start), 60*60*1000, "");
		task.setDescription("Work2");
		workday.addUnit(task);
		start += 60*60*1000;
		
		task = new TaskUnit(new Date(start), 15*60*1000, Constants.DI_TRANCE);		
		task.setDescription("break2");
		workday.addUnit(task);
		start += 15*60*1000;
		
		task = new TaskUnit(new Date(start), 45*60*1000, "");
		task.setDescription("Work3");
		workday.addUnit(task);
		start += 45*60*1000;
		
		task = new TaskUnit(new Date(start), 10*60*1000, Constants.DI_TRANCE);		
		task.setDescription("end of day");
		workday.addUnit(task);	
	}
	
	public static void set_debug(Workday workday){
		workday.reset();
		if(Constants.V) System.out.println("prefill a debug workday");
		long now = System.currentTimeMillis()+1000;
		TaskUnit task;
		for(int i=0;i<10;i++){
			if(i%2==0)	task = new TaskUnit(new Date(now+i*10000), 10000, Constants.DI_TRANCE);
			else		task = new TaskUnit(new Date(now+i*10000), 10000, "no stream          ");
			task.setDescription("debug "+i);
			workday.addUnit(task);
		}
	}
}
