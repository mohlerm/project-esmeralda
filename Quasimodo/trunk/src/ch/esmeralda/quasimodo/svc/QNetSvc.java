package ch.esmeralda.quasimodo.svc;

import java.io.NotActiveException;
import java.util.ArrayList;
import java.util.Date;
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
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class QNetSvc extends Service {
	
	// public statics
	public static final int NOTIF_UPDATE_TIME = 60;
	
	// private statics
	private static final String TAG = "Background Service";
	private static final int MSG_NOTIF_ID = R.id.notifications_tbtn;
	
	// Updater stuffs
	private ScheduledThreadPoolExecutor exec_upd;
	private Updater upd;
	private List<TaskUnit> QTUlist;
	
	// Notifier stuffs
	private ScheduledThreadPoolExecutor exec_ntf;
	private Notifier notify;
	NotificationManager mNotificationManager;
	private List<Long> allready_notified;
	private NotifPoster notifposter;
	
	// Application
	private QuasimodoApp app;
	
	//---- Lifecycle methods
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		Log.d(TAG, "Service created.");
		
		app = (QuasimodoApp) this.getApplicationContext();
		
		// updater related
		QTUlist = app.getWD();
		upd = new  Updater();
		
		// notifier
		notify = new Notifier();
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		allready_notified = (List<Long>) new ArrayList<Long>();
		
		notifposter = new NotifPoster();
		
	}

	@Override
	public int onStartCommand(Intent intent, int flag,int startId) {
		super.onStartCommand(intent, flag, startId);
		
		// debug
		Toast toast = Toast.makeText(this, "Notifications enabled", Toast.LENGTH_SHORT);
		toast.show();
		Log.d(TAG, "Service started.");
		
		upd = new Updater();
		exec_upd = new ScheduledThreadPoolExecutor(1);
		exec_upd.scheduleAtFixedRate(upd, 180, 300, TimeUnit.SECONDS);
		
		notify = new Notifier();
		exec_ntf = new ScheduledThreadPoolExecutor(1);
		exec_ntf.scheduleAtFixedRate(notify, 0, NOTIF_UPDATE_TIME, TimeUnit.SECONDS);
		
		app.serviceRunning = true;
		return Service.START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		// debug
		Toast toast = Toast.makeText(this, "Notifications disabled", Toast.LENGTH_SHORT);
		toast.show();
		Log.d(TAG, "Service shutdown.");
		
		exec_upd.shutdown();
		exec_ntf.shutdown();
		app.serviceRunning = false;
		super.onDestroy();
	}
	
	// -----------------------------------------
	// ---- Notification Handler
	// -----------------------------------------
	
	private class NotifPoster extends Handler {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == MSG_NOTIF_ID) {
				mNotificationManager.notify(0, (Notification)msg.obj);
			} else {
				super.handleMessage(msg);
			}
		}
	}
	
	private class Notifier implements Runnable {
		public void run() {
			TaskUnit current = null;
			Date startTime = new Date(System.currentTimeMillis());
			synchronized (QTUlist) {
				for (TaskUnit tu : QTUlist) {
					if(isWithinRange(tu.getStarttime().getTime(), startTime.getTime(), NOTIF_UPDATE_TIME*1000)) {
						current=tu;
						break;
					}
				}
			}
			
			if (startTime.getHours() >= 23 && startTime.getMinutes() >= 55) {
				Log.d(TAG,"Service stopping, its the end of the day.");
				QTUlist.clear();
				QNetSvc.this.stopSelf();
			}
			
			if (current == null) {
				Log.d(TAG,"No Notifications comming up...");
				return;  // Keine TU ist innerhalb der nächsten Minute geplant.
			}
			
			// ---- Notification.			

			// datum an heute anpassen.
			Date notifDate = setHoursMinsToday(current.getStarttime(),startTime);
			
			// check ob die Notification in der Vergangenheit liegt
			if (notifDate.before(startTime)) {
				Log.d(TAG,"Got a new Date, but it is in the past.");
				return;
			}
			
			// check ob wir den gefundenen schon gezeigt haben.
			if (allready_notified.contains(current.getKey())) {
				Log.d(TAG,"The Date to be notified was allready shown!");
				return;
			} else {
				allready_notified.add(current.getKey());
			}
			
			// wenn alles gut ist: baue notification und schicke sie los.
			
			Log.d(TAG, "Sending Notifications.");
			
			String tickerText;
			String titleText;
			String bodyText;
			
			if (current.getStreamURL().trim().length() > 0) // pause
			{
				tickerText = "Pause faengt an!";
				titleText = "Pause faengt an!";
				bodyText = "Stream: "+current.getStreamURL();
			} else {
				tickerText = "An die Arbeit!";
				titleText = "Arbeit, Arbeit...";
				bodyText = "Viel Erfolg!";
			}
			
			int icon = R.drawable.icon;
			Notification notification = new Notification(icon, tickerText, notifDate.getTime());
			
			Context context = getApplicationContext();
			Intent notificationIntent = new Intent(QNetSvc.this, QuasimodoActivity.class);
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);  // damit die Activity nicht ein zweites Mal gestartet wird.
			PendingIntent contentIntent = PendingIntent.getActivity(QNetSvc.this, 0, notificationIntent, 0);
			notification.setLatestEventInfo(context, titleText, bodyText, contentIntent);
			notification.defaults = Notification.DEFAULT_ALL;
			notification.flags |= Notification.FLAG_AUTO_CANCEL;  // damit beim klicken auf die Notification die auch verschwindet.

			Message msg = Message.obtain(notifposter,MSG_NOTIF_ID,notification);
			long delay = notifDate.getTime()-startTime.getTime();
			notifposter.sendMessageDelayed(msg,delay);
		}
	}
	
	private boolean isWithinRange(long current, long startTime, long duration) {
		return ((startTime <= current) && (current < startTime+duration));
	}
	
	private Date setHoursMinsToday(Date input, Date today) {
		Date temp_when = new Date(today.getTime());
		temp_when.setHours(input.getHours());
		temp_when.setMinutes(input.getMinutes());
		temp_when.setSeconds(input.getSeconds());
		return temp_when;
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
				Log.e(TAG,"Connection could not be made to "+app.ip+":"+app.port);
				return;
			}
			
			WorkdayWrapperImpl wrp;
			try {
				wrp = new WorkdayWrapperImpl(conn,QTUlist);
			} catch (NotActiveException e) {
				Log.e(TAG,"connection not active while creating new WorkdayWrapperImpl.");
				return;
			}
			
			if (!wrp.getNewList()) {
				Log.e(TAG,"wrapper could not get New List from Server.");
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
	
	// dont use this
		@Override
		public IBinder onBind(Intent arg0) {
			return null;
		}

}
