package ch.esmeralda.notredame.main;

/**
 * The actual Main class of the whole Server.
 * Notes:
 * -http://www.javazoom.net/javalayer/sources.html
 * 
 * running in console:
 * %bin%/java -cp . ch.esmeralda.notredame.main.Main -i -m
 * 
 * @author Thomas Richner
 *
 */
public class Main {
	public static final boolean DEBUG_FLAG = false;
	
	public static final String VERSION = "0.9";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		boolean verbose = false;
		boolean debug = false;
		boolean mute = false;
		boolean clinterface = false;
		
		if(args.length==0){								//if no command line options given, display the possible ones
			System.out.println("fyi, there are program options:");
			System.out.println("verbose, debug, mute, commandline interface");
			System.out.println("Usage: [-v] [-d] [-m] [-i] ");
		}else{											//parse the command line options
			for(String arg : args){
				if(DEBUG_FLAG) System.out.println(arg);
				try{
					char c = arg.toCharArray()[1];		//might be OutOfBounds -> catch
					
					switch(c){
						case 'v': verbose = true; break;
						case 'd': debug   = true; break;
						case 'm': mute    = true; break;
						case 'i': clinterface    = true; break;
						default:
							if(DEBUG_FLAG) System.out.println("Unknown option '" + arg +"'");
					}
				}catch(Exception e){
					if(DEBUG_FLAG) System.out.println("unable to parse option '"+arg+"'");	//probably OutOfBounds
				}
			}
		}
		
		if(verbose){
			System.out.println("NotreDame, Version: " + VERSION);
			System.out.println("started in Mode: " + (debug ? "DEBUG ":" ") + (verbose ? "VERBOSE ":" ") + (mute ? "MUTE ":""));
		}
		
		if(clinterface){
			AthmosCLI cli = new AthmosCLI(verbose,debug,mute);
			cli.start();
			
			try {
				cli.join();
			} catch (InterruptedException e) {
				System.out.println("InterruptedException caught, terminating...");
			}
			
			if(verbose) System.out.println("exiting...");
			
			if(!cli.getCleanShutdownFlag()){
				System.out.println("CLI hasn't terminated clean, something went wrong...");
				System.exit(0);
			}
		}else{
			NotreDameInstance ndi = new NotreDameInstance(verbose, debug, mute);
			ndi.start();
			try {
				ndi.join();
			} catch (InterruptedException e) {
				System.out.println("InterruptedException caught, terminating...");
			}
			
			if(verbose) System.out.println("exiting...");
			
			if(!ndi.getCleanShutdownFlag()){
				System.out.println("CLI hasn't terminated clean, something went wrong...");
				System.exit(0);
			}
		}
	}
}
