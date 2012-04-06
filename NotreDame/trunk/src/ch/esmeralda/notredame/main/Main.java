package ch.esmeralda.notredame.main;

import java.util.Date;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ch.esmeralda.notredame.jobs.*;
import ch.esmeralda.DataExchange.TaskUnit;
import ch.esmeralda.notredame.unitHandling.Workday;
import ch.esmeralda.notredame.unitHandling.WorkdayImpl;

/**
 * The actual Frontend.
 * Notes:
 * -http://www.javazoom.net/javalayer/sources.html
 * 
 * %bin%/java -cp . ch.esmeralda.notredame.main.Main
 * 
 * @author Thomas Richner
 *
 */
public class Main {
	public static final boolean DEBUG_FLAG = false;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		boolean verbose = false;
		boolean debug = false;
		boolean mute = false;
		if(args.length==0){
			System.out.println("fyi, there are program options:");
			System.out.println("[-v] verbose [-d] debug [-m] mute");
		}else{
			for(String arg : args){
				System.out.println(arg);
				try{
					char c = arg.toCharArray()[1];
					
					switch(c){
						case 'v': verbose = true; break;
						case 'd': debug   = true; break;
						case 'm': mute    = true; break;
						default:
							if(DEBUG_FLAG) System.out.println("Unknown option '" + arg +"'");
					}
				}catch(Exception e){
					if(DEBUG_FLAG) System.out.println("unable to parse option '"+arg+"'");
				}
			}
		}
		
		if(verbose) System.out.println("starting CLI, Mode: " + (debug ? "DEBUG ":" ") + (verbose ? "VERBOSE ":" ") + (mute ? "MUTE ":""));
		AthmosCLI cli = new AthmosCLI(verbose,debug,mute);
		cli.start();
		
		try {
			cli.join();
		} catch (InterruptedException e) {
			System.out.println("InterruptedException caught, terminating...");
			System.exit(0);
		}
		
		if(verbose) System.out.println("exiting...");
		
		if(!cli.getCleanShutdownFlag()){
			System.out.println("CLI hasn't terminated controlled, something went wrong...");
			System.exit(0);
		}
	}
}
