package ch.esmeralda.quasimodo.svc;

import java.io.NotActiveException;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ch.esmeralda.DataExchange.TaskUnit;
import ch.esmeralda.quasimodo.QuasimodoApp;
import ch.esmeralda.quasimodo.net.QClient;
import ch.esmeralda.quasimodo.net.QClientImpl;
import ch.esmeralda.quasimodo.net.QClient.UnableToConnectException;
import ch.esmeralda.quasimodo.unitHandlingWrapper.WorkdayWrapperImpl;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class QNetSvc extends Service {
	
	private ScheduledThreadPoolExecutor exec;
	private Updater upd;
	private List<TaskUnit> QTUlist;

	
	private QuasimodoApp app;

	// dont use this
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	//---- Lifecycle methods
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		app = (QuasimodoApp) this.getApplicationContext();
		
		
		// updater related
		QTUlist = app.getWD();
		upd = new  Updater();
		exec = new ScheduledThreadPoolExecutor(1);
		exec.scheduleAtFixedRate(upd, 180, 300, TimeUnit.SECONDS);
		
		// notif related

	}

	@Override
	public void onDestroy() {
		exec.shutdown();
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flag,int startId) {
		super.onStartCommand(intent, flag, startId);
		return Service.START_STICKY;
	}
	// -----------------------------------------
	// ---- Notification Handler
	// -----------------------------------------
	
	
	
	// -----------------------------------------
	// ---- Net update thread
	// ----------------------------------------
	private class Updater implements Runnable {
		public void run() {
			if (!app.updateBackground) return; // exit falls updateflag nicht gesetzt.
			Thread connect = new net_UpdateWD();
			connect.start();
		}
	}
	
	private class net_UpdateWD extends Thread {		
		public void run() {
			QClient conn = makeConn();
			if (conn == null) {
				Log.e("Background Svc","Connection could not be made to "+app.ip+":"+app.port);
				return;
			}
			
			WorkdayWrapperImpl wrp;
			try {
				wrp = new WorkdayWrapperImpl(conn,QTUlist);
			} catch (NotActiveException e) {
				Log.e("Background Svc","connection not active while creating new WorkdayWrapperImpl.");
				return;
			}
			
			if (!wrp.getNewList()) {
				Log.e("Background Svc","wrapper could not get New List from Server.");
			}
			conn.disconnect();
		}
		
		/**
		 * Stellt eine Verbinung zum Server her (port/ip);
		 * @return der verbundene QClient oder null falls die Verbindung nicht klappt.
		 */
		private QClient makeConn() {
			QClient ret = new QClientImpl();
			try {
				ret.connect(app.ip, app.port);
			} catch (UnableToConnectException e) {
				return null;
			}
			return ret;
		}
	}

}
