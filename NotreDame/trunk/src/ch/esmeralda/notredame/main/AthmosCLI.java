package ch.esmeralda.notredame.main;

import java.net.Socket;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ch.esmeralda.DataExchange.TaskUnit;
import ch.esmeralda.notredame.jobs.*;
import ch.esmeralda.notredame.net.*;
import ch.esmeralda.notredame.unitHandling.*;

public class AthmosCLI extends Thread{
	private static final int SERVERPORT = 10002;
	private static final String DI_TRANCE = "http://u11aw.di.fm:80/di_trance";
	
	private static final int TimeOffset = -2;
	
	private boolean L = false;	// verbose flag
	private boolean D = false;  // debug flag
	private boolean M = false;  // mute flag
	
	private boolean clean_shutdown = false;
	
	private StreamJob streamJob = null;
	private Workday workday = null;
	private WorkdayHandler workdayHandler = null;
	private TimerJob timerJob = null;
	private ScheduledThreadPoolExecutor executor = null;
	private NServer server = null;
	
	public AthmosCLI(){

	}
	public AthmosCLI(boolean verbose,boolean debug,boolean mute){
		L = verbose;
		D = debug;
		M = mute;
		if(L){
			d("Welcome to the Notredame Server CLInterface!");
			d("created new CLI!");
		}
	}
	
	/**
	 * @param args
	 */
	@Override
	public void run() {
		if(L) System.out.println("running CLI...");
		
		// ---- Welcome banner
			d("    )                                  v0.9      ");
			d(" ( /(        )         (                     	");
			d(" )\\())    ( /((     (  )\\ )   )    )     (   	");
			d("((_)\\  (  )\\())(   ))\\(()/(( /(   (     ))\\  	");
			d(" _((_) )\\(_))(()\\ /((_)((_))(_))  )\\  '/((_) 	");
			d("| \\| |((_) |_ ((_|_))  _| ((_)_ _((_))(_))   	");
			d("| .` / _ \\  _| '_/ -_) _` / _` | '  \\() -_)  	");
			d("|_|\\_\\___/\\__|_| \\___\\__,_\\__,_|_|_|_|\\___|		");
			d("2012             this CLI is brought to you by TR");
		// ----
		
		executor = new ScheduledThreadPoolExecutor(1);
		
		if(L) d("create streamJob...");
		if(!M) streamJob = new AthmosStream();

		if(L) d("create workday...");
		workday = new WorkdayImpl();
		
		if(D){
			if(L) d("set a debug workday...");
			workday = set_debug();
			System.out.println(workday.toString());
			System.out.println(workday.getList().size());
		}
		
		if(L) d("create workdayHandler...");
		workdayHandler = new WorkdayHandlerImpl(workday);
		
		if(L) d("init timer...");
		timerJob = new TimerJobImpl(streamJob,workday);
		
		if(L) d("schedule jobs...");
		executor.scheduleAtFixedRate(timerJob, 500, 1000, TimeUnit.MILLISECONDS);
		
		if(L) d("init server...");
		server = NServerFactory.createNewInstance(workdayHandler);
		if(L) d("start server...");
		server.start(SERVERPORT);
		
		if(L) d("start io...");
		Scanner in = new Scanner(System.in);
		String msg;
		boolean quit = false;
		 while(!quit){
	         msg = in.nextLine();
	         if(msg.equals("help")){
	        	 help();
	         }else if(msg.equals("set default")){
	        	p("starting hour: ");
	        	try{
		        	int hour = in.nextInt();
		        	while(hour<0||hour>23){	
		        		hour = in.nextInt();
		        	}
		        	set_default(workday,hour);
	        	}catch(Exception e){
	        		d("Parse Exception");
	        	}
	         }else if(msg.equals("show")){
	            p(workday.toString());
	         }else if(msg.equals("quit")){
	            quit = true;
	         }else if(msg.equals("active")){
	            active(workday);
	         }else if(msg.equals("remove")){
	        	 d("sorry, not yet implemented in CLI");
	            //remove();
	         }else if(msg.equals("add")){
	        	 d("sorry, not yet implemented in CLI");
	            //add();
	         }else if(msg.equals("show stream")){
	        	 d("sorry, not yet implemented in CLI");
	            //show_sounds();
	         }else if(msg.equals("status")){
	        	 status();
	         }else{
	        	 if(L) d("unknown input: '" + msg + "'");
	         }
	      }
		
		
		 if(L) System.out.println("stopping jobs");
		executor.shutdownNow();
		server.stop();
		if(L) System.out.println("...bye, bye");
		clean_shutdown = true;
	}

	//helper
	private static final void d(Object msg){
		System.out.println(msg);
	}
	private static final void p(Object msg){
	    System.out.print(msg);
	}
	private static final void nl(){
	    System.out.println();
	}
	
	private Workday set_debug(){
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
	
	private void set_default(Workday workday,int hour){
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
	
	private void active(Workday workday){
		workday.getActiveUnit(new Date());
	}
	
	private void help(){
        d("help - - - - Displays this Text");
        d("set default  Sets a set of predefined units");
        d("show - - - - Shows the whole workday");
        d("quit - - - - Shuts down the whole server");
        d("active - - - Displays the active unit");
        d("remove - - - Removes a unit from the workday");
        d("add  - - - - Adds a unit to the workday");
        d("show stream  Displays the name of the active stream");
        d("status - - - Displays the current status of the server");
	}
	
	private void status(){
	   	 d("Timerstatus:  \t" + (executor.isTerminated() ? "terminated" : "running") + (M ? "  MUTE":""));
	   	 d("Serverstatus: \t" + (server.isRunning() ? "running" : "down"));
	   	 List<Socket> connections= server.getConnections();
	   	 d("|->Connections: " + connections.size());
	   	for(Socket socket : connections){
	   		d(" |->" + socket.getRemoteSocketAddress()+ ":"+socket.getPort());
	   	}  
	}
	
	public boolean getCleanShutdownFlag(){
		return clean_shutdown;
	}
	
}
