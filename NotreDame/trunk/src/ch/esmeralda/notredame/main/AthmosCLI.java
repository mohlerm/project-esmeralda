package ch.esmeralda.notredame.main;

import java.net.NetworkInterface;
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
import ch.esmeralda.notredame.utils.Minions;

public class AthmosCLI extends Thread{
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
	
	private Scanner in;

	public AthmosCLI(){
		L = Constants.V;
		D = Constants.D;
		M = Constants.M;
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
			d("    )                                  v" + Constants.VERSION);
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
			Minions.set_debug(workday);
			if(L) System.out.println(workday.toString());
			if(L) System.out.println(workday.getList().size());
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
		server.start(Constants.SERVERPORT);
		
		if(L) d("start io...");
		in = new Scanner(System.in);
		String msg;
		boolean quit = false;
		/*
		try {
			wait(100);	//give the server some time to get ready
		} catch (InterruptedException e1) {	}
		*/
		
		 while(!quit){
	         msg = in.nextLine();
	         if(msg.equals("help")){
	        	 help();
	         }else if(msg.equals("set default")){
	        	try{
	        		p("starting hour: ");
		        	int hour = in.nextInt();
		        	while(hour<0||hour>23){	
		        		hour = in.nextInt();
		        	}
		        	p("starting minutes: ");
		        	int minutes = in.nextInt();
		        	while(minutes<0||minutes>59){	
		        		hour = in.nextInt();
		        	}
		        	Minions.set_default(workday,hour,minutes);
	        	}catch(Exception e){
	        		if(Constants.V) d("Parse Exception");
	        	}
	         }else if(msg.equals("show")){
	            p(workday.toString());
	         }else if(msg.equals("about")){
		        about();
		     }else if(msg.equals("set debug")){
		        Minions.set_debug(workday);
		     }else if(msg.equals("quit")){
	            quit = true;
	         }else if(msg.equals("active")||msg.equals("show stream")){
	            active(workday);
	         }else if(msg.equals("reset")){
		        workday.reset();
	         }else if(msg.equals("remove")){
	        	 remove();
	         }else if(msg.equals("add")){
	             add();
	         }else if(msg.equals("status")){
	        	 status();
	         }else{
	        	 if(L) d("unknown CLI command: '" + msg + "'");
	         }
	      }
		
		
		 if(L) System.out.println("stopping jobs");
		executor.shutdownNow();
		if(L) System.out.println("shutdown server");
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
	
	/**
	 * von Eppi
	 */
	private void add() {
		String msg;
		int minutes;
		long duration;
		int temp;
		String URL;
		while (true) {
			try {
				p("New Starttime? (+/- minutes from now?): ");
				msg = in.nextLine();
				if (msg.toLowerCase().equals("cancel") || msg.toLowerCase().equals("exit")) {
					return;
				}
				minutes = Integer.parseInt(msg);
				
				p("New Duration? (minutes): ");
				msg = in.nextLine();
				if (msg.toLowerCase().equals("cancel") || msg.toLowerCase().equals("exit")) {
					return;
				}
				temp = Integer.parseInt(msg);
				duration = temp*60000;
				
				p("StreamURL? (empty for Worktime): ");
				msg = in.nextLine();
				if (msg.toLowerCase().equals("cancel") || msg.toLowerCase().equals("exit")) {
					return;
				}
				URL = msg;
				break;
			} catch (Exception e) {
				d(" Try again! (or type cancel)");
				continue;
			}
		}
		Date start = new Date(System.currentTimeMillis()+minutes*60000);
		TaskUnit unit = new TaskUnit(start,duration,URL);
		workday.addUnit(unit);
		d("TU added.");
	}
	
	
	/**
	 * von Eppi
	 */
	private void remove() {
		String msg;
		int key;
		while (true) {
			p("Enter ID: ");
			msg = in.nextLine();
			if (msg.toLowerCase().equals("cancel") || msg.toLowerCase().equals("exit")) {
				return;
			}
			try {
				key = Integer.parseInt(msg);
				break;
			} catch (Exception e) {
				d(" Try again! (or type cancel)");
				continue;
			}
		}
		workday.removeUnitByKey(key);
	}
	
	private void active(Workday workday){
		TaskUnit unit = workday.getActiveUnit(new Date());
		d(unit.toString());
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
        d("about  - - - Displays infos about this program");
	}
	
	private void about(){
		nl();
        d("NotreDame, Version: " + Constants.VERSION);
        d("                 Authors:                ");
        d("                ~~~~~~~~~~               ");
        d("              Thomas Richner             ");
        d("       Main,CLI,Jobs & UnitHandling      ");
        nl();
        d("               Sandro Sgier              "); 
        d("      Connection Management & Stream     ");
        nl();
        d("             Marco Eppenberger           ");
        d("       Communication & UnitHandling      ");
        nl();
        d("               Stefan Mach               ");
        d("              Communication              ");
        d("                ~~~~~~~~~~               ");
	}
	
	private void status(){
	   	 d("Timerstatus:  \t" + (executor.isTerminated() ? "terminated" : "running") + (M ? "  MUTE":""));
	   	 d("Serverstatus: \t" + (server.isRunning() ? ("running on port " + server.getPort()): "down"));
	   	 listInterfaces();
	   	 List<Socket> connections = server.getConnections();
	   	 d("|->Connections: " + connections.size());
	   	for(Socket socket : connections){
	   		d(" |->" + socket.getRemoteSocketAddress()+ ":"+socket.getPort());
	   	}  
	}
	
	private void listInterfaces(){
	  try {
        String wlan = "wlan0";
        String eth = "eth0";
        NetworkInterface Iwlan = NetworkInterface.getByName(wlan);
        NetworkInterface Ieth  = NetworkInterface.getByName(eth);
        
        if(Constants.V) d("fetched Interfaces " + wlan + ", " + eth);
        
        if(Iwlan != null && Iwlan.getInterfaceAddresses().size()>=2) 
        	d(wlan+ "\t " + Iwlan.getInterfaceAddresses().get(1).getAddress().getHostAddress());
        else
        	d(wlan + " not found");
        
        if(Ieth != null && Ieth.getInterfaceAddresses().size()>=2)
        	d(eth + "\t " + Ieth.getInterfaceAddresses().get(1).getAddress().getHostAddress());
        else
        	d(eth + " not found");
	  }
	  catch (Exception e) {
	     if(Constants.V){
	    	 System.err.println(e.getMessage());     
		     e.printStackTrace(System.err);
	     }
	  }
	}
	
	
	public boolean getCleanShutdownFlag(){
		return clean_shutdown;
	}
	
}
