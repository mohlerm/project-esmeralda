package ch.esmeralda.notredame.main;

import java.io.IOException;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ch.esmeralda.DataExchange.TaskUnit;
import ch.esmeralda.notredame.jobs.*;
import ch.esmeralda.notredame.net.*;
import ch.esmeralda.notredame.unitHandling.*;

public class NotreDameInstance extends Thread{
		private static final int SERVERPORT = 10002;
		private static final String DI_TRANCE = "http://u11aw.di.fm:80/di_trance";
		
		private class Busy extends Thread{
			public boolean q = true;
			@Override
			public void run(){
				//nl();
				while(q){
					try {
						p("\rpress ENTER to exit   ");
						sleep(500);
						p("\rpress ENTER to exit.");
						sleep(500);
						p("\rpress ENTER to exit.. ");
						sleep(500);
						p("\rpress ENTER to exit...");
						sleep(500);
					} catch (InterruptedException e) {}
				}
			}
			public void quit(){
				q = false;
			}
		}
		
		
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

		public NotreDameInstance(){
			L = Constants.V;
			D = Constants.D;
			M = Constants.M;
			if(L){
				d("Welcome to the Notredame Server!");
			}
		}
		
		/**
		 * @param args
		 */
		@Override
		public void run() {
			if(L) System.out.println("running new instance...");
			executor = new ScheduledThreadPoolExecutor(1);
			
			if(L) d("create streamJob...");
			if(!M) streamJob = new AthmosStream();

			if(L) d("create workday...");
			workday = new WorkdayImpl();
			
			if(D){
				if(L) d("set a debug workday...");
				set_debug(workday);
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

			Busy busy = new Busy();
			
			busy.start();
			try {
				System.in.read();
			} catch (IOException e) {}
			busy.interrupt();
			busy.quit();
			try {
				busy.join(1000);
			} catch (InterruptedException e) {}
			
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
		
		private void set_debug(Workday workday){
			workday.reset();
			if(L) System.out.println("prefill a debug workday");
			long now = System.currentTimeMillis()+1000;
			TaskUnit task;
			for(int i=0;i<10;i++){
				if(i%2==0)	task = new TaskUnit(new Date(now+i*10000), 10000, DI_TRANCE);
				else		task = new TaskUnit(new Date(now+i*10000), 10000, "");
				task.setDescription("debug "+i);
				workday.addUnit(task);
			}
		}

		public boolean getCleanShutdownFlag(){
			return clean_shutdown;
		}	
}
