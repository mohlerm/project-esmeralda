package ch.esmeralda.notredame.main;

import java.net.Socket;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ch.esmeralda.notredame.jobs.*;
import ch.esmeralda.notredame.net.NServer;
//import ch.esmeralda.notredame.net.NServerImpl;
import ch.esmeralda.notredame.net.NServerImplEppi;
import ch.esmeralda.DataExchange.TaskUnit;
import ch.esmeralda.notredame.unitHandling.Workday;
import ch.esmeralda.notredame.unitHandling.WorkdayHandler;
import ch.esmeralda.notredame.unitHandling.WorkdayHandlerImpl;
import ch.esmeralda.notredame.unitHandling.WorkdayImpl;

public class MainUI{
	private static final int SERVERPORT = 10002;
	private static final boolean L = true;
	private static final String DI_TRANCE = "http://u11aw.di.fm:80/di_trance";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if(L) System.out.println("starting...");
		
		ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
		
		StreamJob streamJob = new AthmosStream();

		Workday workday = new WorkdayImpl();
		
		if(true){//args.length>0){
			workday = set_debug();
			System.out.println(workday.toString());
			System.out.println(workday.getList().size());
		}
		
		WorkdayHandler workdayHandler = new WorkdayHandlerImpl(workday);
		
		
		TimerJob timerJob = new TimerJobImpl(streamJob,workday);
		
		if(L) System.out.println("schedule jobs");
		executor.scheduleAtFixedRate(timerJob, 500, 1000, TimeUnit.MILLISECONDS);
		NServer server = new NServerImplEppi(workdayHandler);
		server.start(SERVERPORT);
		
		Scanner in = new Scanner(System.in);
		String msg;
		boolean quit = false;
		 while(!quit){
	         msg = in.nextLine();
	         //d(msg.charAt(msg.length()-1));
	         
	         if(msg.equals("help")){
	            //help();
	        	 d("your lost...");
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
	         else if(msg.equals("status")){
	        	 d("Timerstatus:  \t" + (executor.isTerminated() ? "terminated" : "running"));
	        	 d("Serverstatus: \t" + (server.isRunning() ? "running" : "down"));
	        	 List<Socket> connections= server.getConnections();
	        	 d(" Connections: " + connections.size());
	        	for(Socket socket : connections){
	        		d(" " + socket.getRemoteSocketAddress()+ ":"+socket.getPort());
	        	}
	        	
	            
	         }else{
	            //d("Unknown command!");
	            //help();
	         }
	      }
		
		
		 if(L) System.out.println("stopping jobs");
		executor.shutdownNow();
		server.stop();
		if(L) System.out.println("...bye, bye");
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
		if(L) System.out.println("prefill a debug workday");
		long now = System.currentTimeMillis()+1000;
		TaskUnit task;
		for(int i=0;i<10;i++){
			if(i%2==0)	task = new TaskUnit(new Date(now+i*10000), 10000, DI_TRANCE);
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
		//a = a-a%(1000*3600*24);
		//long start = a + hour*3600*1000;
		task = new TaskUnit(new Date(start), 15*60*1000, DI_TRANCE);		
		task.setDescription("break1");
		workday.addUnit(task);
		start += 15*60*1000;
		
		task = new TaskUnit(new Date(start), 60*60*1000, "");
		task.setDescription("Work2");
		workday.addUnit(task);
		start += 60*60*1000;
		
		task = new TaskUnit(new Date(start), 15*60*1000, DI_TRANCE);		
		task.setDescription("break2");
		workday.addUnit(task);
		start += 15*60*1000;
		
		task = new TaskUnit(new Date(start), 45*60*1000, "");
		task.setDescription("Work2");
		workday.addUnit(task);
		start += 45*60*1000;
		
		task = new TaskUnit(new Date(start), 60*60*1000, DI_TRANCE);		
		task.setDescription("supper");
		workday.addUnit(task);
		start += 60*60*1000;
		
		task = new TaskUnit(new Date(start), 75*60*1000, "");
		task.setDescription("Work1");
		workday.addUnit(task);
		start += 75*60*1000;
		
		task = new TaskUnit(new Date(start), 15*60*1000, DI_TRANCE);		
		task.setDescription("break1");
		workday.addUnit(task);
		start += 15*60*1000;
		
		task = new TaskUnit(new Date(start), 60*60*1000, "");
		task.setDescription("Work2");
		workday.addUnit(task);
		start += 60*60*1000;
		
		task = new TaskUnit(new Date(start), 15*60*1000, DI_TRANCE);		
		task.setDescription("break2");
		workday.addUnit(task);
		start += 15*60*1000;
		
		task = new TaskUnit(new Date(start), 45*60*1000, "");
		task.setDescription("Work3");
		workday.addUnit(task);
		start += 45*60*1000;
		
		task = new TaskUnit(new Date(start), 10*60*1000, DI_TRANCE);		
		task.setDescription("end of day");
		workday.addUnit(task);
		//start += 15*60*1000;
			
	}
	
	private static void active(Workday workday){
		workday.getActiveUnit(new Date());
	}
}
