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
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
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
						case 'v': Constants.V = true; break;
						case 'd': Constants.D = true; break;
						case 'm': Constants.M = true; break;
						case 'i': Constants.I = true; break;
						default:
							if(DEBUG_FLAG) System.out.println("Unknown option '" + c +"'");
					}
				}catch(Exception e){					// we don't care about 
					if(DEBUG_FLAG) System.out.println("unable to parse option '"+arg+"'");	//probably OutOfBounds
				}
			}
		}
		
		if(Constants.V){
			System.out.println("NotreDame, Version: " + Constants.VERSION);
			System.out.println("started in Mode: " + (Constants.D ? "DEBUG ":" ") + (Constants.V ? "VERBOSE ":" ") + (Constants.M ? "MUTE ":""));
		}
		
		if(Constants.I){
			AthmosCLI cli = new AthmosCLI();
			cli.start();
			
			try {
				cli.join(5000);					// Timeout, kill if it doesn't stop properly
			} catch (InterruptedException e) {
				System.out.println("InterruptedException caught, terminating...");
			}
		}else{
			NotreDameInstance ndi = new NotreDameInstance();
			ndi.start();
			try {
				ndi.join();
			} catch (InterruptedException e) {
				System.out.println("InterruptedException caught, terminating...");
			}
			
			if(Constants.V) System.out.println("exiting...");
			
			if(!ndi.getCleanShutdownFlag()){	
				System.out.println("CLI hasn't terminated clean, something went wrong...");
				System.exit(0);
			}else{
				System.exit(0);  // just for safety
			}
		}
		
	}
}
