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

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		boolean verbose = false;
		boolean debug = false;
		if(args.length>0){
			verbose = args[0].equals("-v");
			debug   = args[0].equals("-d");
		}
		if(args.length>1){
			verbose = args[1].equals("-v");
			debug   = args[1].equals("-d");		
		}
		
		if(verbose) System.out.println("starting CLI" + (debug ? "in debug mode...":"..."));
		AthmosCLI cli = new AthmosCLI(verbose,debug);
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
