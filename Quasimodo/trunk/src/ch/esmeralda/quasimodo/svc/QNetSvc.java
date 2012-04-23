package ch.esmeralda.quasimodo.svc;

import java.io.NotActiveException;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ch.esmeralda.DataExchange.TaskUnit;
import ch.esmeralda.quasimodo.QuasimodoActivity;
import ch.esmeralda.quasimodo.QuasimodoApp;
import ch.esmeralda.quasimodo.R;
import ch.esmeralda.quasimodo.net.QClient;
import ch.esmeralda.quasimodo.net.QClientImpl;
import ch.esmeralda.quasimodo.net.QClient.UnableToConnectException;
import ch.esmeralda.quasimodo.unitHandlingWrapper.WorkdayWrapperImpl;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class QNetSvc extends Service {
	
	public static final int NOTIF_UPDATE_TIME = 60;
	
	private ScheduledThreadPoolExecutor exec_upd;
	private Updater upd;
	private List<TaskUnit> QTUlist;
	
	private ScheduledThreadPoolExecutor exec_ntf;
	private Notifier notify;
	NotificationManager mNotificationManager;
	
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
		
		// notifier
		notify = new Notifier();
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	}

	@Override
	public void onDestroy() {
		// debug
		Toast toast = Toast.makeText(this, "Service stopped", Toast.LENGTH_SHORT);
		toast.show();
		
		exec_upd.shutdown();
		app.serviceRunning = false;
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flag,int startId) {
		super.onStartCommand(intent, flag, startId);
		
		// debug
		Toast toast = Toast.makeText(this, "Service started", Toast.LENGTH_SHORT);
		toast.show();
		
		upd = new Updater();
		exec_upd = new ScheduledThreadPoolExecutor(1);
		exec_upd.scheduleAtFixedRate(upd, 180, 300, TimeUnit.SECONDS);
		
		notify = new Notifier();
		exec_ntf = new ScheduledThreadPoolExecutor(1);
		exec_ntf.scheduleAtFixedRate(notify, 0, NOTIF_UPDATE_TIME, TimeUnit.SECONDS);
		
		app.serviceRunning = true;
		return Service.START_STICKY;
	}
	
	// -----------------------------------------
	// ---- Notification Handler
	// -----------------------------------------
	
	private class Notifier implements Runnable {
		public void run() {
			Thread ntftrd = new NotifScheduler();
			ntftrd.start();
		}
	}
	
	private class NotifScheduler extends Thread {
		public void run() {
			TaskUnit current = null;
			synchronized (QTUlist) {
				long startTime = System.currentTimeMillis();
				for (TaskUnit tu : QTUlist) {
					if(isWithinRange(tu, startTime, NOTIF_UPDATE_TIME*1000)) {
						current=tu;
						break;
					}
				}
			}
			
			if (current == null) return;  // Keine TU ist innerhalb der nächsten Minute geplant.
			
			// ---- Notification.			
			String tickerText;
			String titleText;
			String bodyText;
			if (current.getStreamURL().trim().length() > 0) // pause
			{
				tickerText = "Pause fängt an!";
				titleText = "Quasimodo";
				bodyText = "Pause fängt an. Stream: "+current.getStreamURL();
			} else {
				tickerText = "An die Arbeit!";
				titleText = "Quasimodo";
				bodyText = "Arbeit, Arbeit...";
			}

			long when = current.getStarttime().getTime();
			int icon = R.drawable.icon;
			Notification notification = new Notification(icon, tickerText, when);
			
			Context context = getApplicationContext();
			Intent notificationIntent = new Intent(QNetSvc.this, QuasimodoActivity.class);
			PendingIntent contentIntent = PendingIntent.getActivity(QNetSvc.this, 0, notificationIntent, 0);
			notification.setLatestEventInfo(context, titleText, bodyText, contentIntent);
			notification.defaults = Notification.DEFAULT_ALL;

			mNotificationManager.notify(0, notification);
		}
	}
	
	private boolean isWithinRange(TaskUnit tu, long startTime, long duration) {
		   return ((startTime <= tu.getStarttime().getTime()) && (tu.getStarttime().getTime() > startTime+duration));
		}
	
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
