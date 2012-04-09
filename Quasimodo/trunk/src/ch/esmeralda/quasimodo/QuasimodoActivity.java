/**
 * TODO: make an awesome icon.
 */

package ch.esmeralda.quasimodo;

import java.io.NotActiveException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import ch.esmeralda.quasimodo.net.QClient;
import ch.esmeralda.quasimodo.net.QClient.UnableToConnectException;
import ch.esmeralda.quasimodo.net.QClientImpl;
import ch.esmeralda.quasimodo.unitHandlingWrapper.WorkdayWrapperImpl;
import ch.esmeralda.DataExchange.*;

public class QuasimodoActivity extends Activity {
	
	// Various GUI Objects
	private ProgressDialog m_ProgressDialog;
	private Button addbutton;
	private Button connectbutton;
	private ListView lv_qtu;

	// Data objects
	private List<TaskUnit> m_qtus = null;
	private TUAdapter m_adapter;
	private SharedPreferences settings;

	// Connection specific values
	private String ip;
	private int port;

	// Public constants
	public static final String PREFS_NAME = "EsmeraldaPrefsFile";
	public static final String TU_NEW_KEY = "TUIFNEWTAGACTIVITY";
	public static final String TU_OBJECT_KEY = "TUSTATUSTOEDITACTIVITY";
	public static final String TU_DELETE_KEY = "TUTOBEDELETED"; 

	// Autoupdater
	ScheduledThreadPoolExecutor executor;
	Updater updater;
	private boolean updateable;

	// ---------- initial Handling

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		//---- android default
		super.onCreate(savedInstanceState);   
		setContentView(R.layout.main);

		//---- get ID's from various gui objects and initialize clickability
		addbutton = (Button) findViewById(R.id.addbtn);
		AddListenerClass addlistener = new AddListenerClass();
		addbutton.setOnClickListener(addlistener);

		connectbutton = (Button) findViewById(R.id.connectbtn);
		ConnectListenerClass connectlistener = new ConnectListenerClass();
		connectbutton.setOnClickListener(connectlistener);

		//---- load previous Settings.
		settings = getSharedPreferences(PREFS_NAME, 0);
		readSettings();

		//---- load List and its adapter
		lv_qtu = (ListView) findViewById(R.id.taskunitlist);
		lv_qtu.setEmptyView((TextView)findViewById(R.id.list_is_empty));
		m_qtus = new ArrayList<TaskUnit>();
		this.m_adapter = new TUAdapter(this, R.layout.row, m_qtus);
		lv_qtu.setAdapter(this.m_adapter);
		
		//---- AutoUpdater
		updater = new Updater();
		updateable = true;
		
		//---- Make sure, WIFI is turned on and connected.
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (!mWifi.isConnected()) {
			// wifi not connected, show info popup.
		    showDialog(1);
		    updateable = false;
		} else {
			// Make Connection
			connectbutton.performClick();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		updateable = true;
		executor = new ScheduledThreadPoolExecutor(1);
		executor.scheduleAtFixedRate(updater, 10, 10, TimeUnit.SECONDS);
	}
	
	@Override
	public void onPause() {
		executor.shutdownNow();
		updateable = false;
		super.onPause();
	}
	

	/**
	 * What happens when the "Add" button gets clicked?
	 * @author Marco
	 */
	private class AddListenerClass implements OnClickListener{
		public void onClick(View v) {
			startedit(0,0,true);
		}
	}

	/**
	 * What happens when the "Connect" button gets clicked?
	 * @author Marco
	 */
	private class ConnectListenerClass implements OnClickListener{
		public void onClick(View v) {

			// is the ip correct? if not, set it first in the settings
			if (!ip.matches("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$") || port < 1 || port > 65537) {
				Toast.makeText(getApplicationContext(), "Please set the right IP and the right port in the Settings before connecting.", Toast.LENGTH_LONG).show();
				return;
			}

			Thread connect = new net_DoStuff(false,1); // action 1 = get New Workday List.
			connect.start();
		}
	}

	/**
	 * Easy way to handle all the dialogs.
	 */
	protected Dialog onCreateDialog(int id) {
		if (id == 0) {
			// Progress Dialog.
			m_ProgressDialog = new ProgressDialog(this);
			m_ProgressDialog.setTitle("Please Wait...");
			m_ProgressDialog.setMessage("Retrieving Data");
			return m_ProgressDialog;
		} else if (id == 1) {
			// WIFI is disabled.
			AlertDialog WIFIdisable = new AlertDialog.Builder(this).create();
			WIFIdisable.setTitle("WIFI not connected.");
			WIFIdisable.setMessage("Quasimodo needs WIFI enabled and connected. Please enable it.");
			WIFIdisable.setButton("WIFI settings", new DialogInterface.OnClickListener() {
			   public void onClick(DialogInterface dialog, int which) {
					final Intent intent = new Intent(Intent.ACTION_MAIN, null);
					intent.addCategory(Intent.CATEGORY_LAUNCHER);
					final ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.wifi.WifiSettings");
					intent.setComponent(cn);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
			   }
			});
			WIFIdisable.setButton2("Dismiss", new DialogInterface.OnClickListener() {
				   public void onClick(DialogInterface dialog, int which) {
						// dont do shit.
				   }
				});
			WIFIdisable.setIcon(R.drawable.icon);
			return WIFIdisable;
		} else if (id == 2) {
			//---- RESET button pressed
			LayoutInflater factory = LayoutInflater.from(this);
	        final View tpdview = factory.inflate(R.layout.resettimepickerdialog, null);
	        TimePicker tp = (TimePicker)tpdview.findViewById(R.id.resetTimePicker);
	        tp.setCurrentHour(9);
	        tp.setCurrentMinute(0);
	        tp.setIs24HourView(true);
	        AlertDialog ResetDialog = new AlertDialog.Builder(QuasimodoActivity.this)
	            .setTitle("Set new Workday Starttime:")
	            .setView(tpdview)
	            .setPositiveButton("Reset!", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	                	TimePicker rtp = (TimePicker) tpdview.findViewById(R.id.resetTimePicker);
	                	ResetWorkday_init(rtp.getCurrentHour(),rtp.getCurrentMinute());
	                }
	            })
	            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	                		// Do nothing.
	                }
	            })
	            .create();
	        return ResetDialog;
		}
		return super.onCreateDialog(id);
	}
	
	/**
	 * Helper Function für einen Workday Reset ############################################################## TO BE TESTED!!!
	 */
	private void ResetWorkday_init(int hour, int minute) {
		Thread reset = new net_DoStuff(5,hour*60+minute); // eventuell anpassen für UTC
		reset.start();
	}

	// ------------------------- custom ArrayList Adapter class

	/**
	 * Adapter for the TaskUnit list.
	 * @author Marco
	 */
	private class TUAdapter extends ArrayAdapter<TaskUnit> implements OnClickListener{

		private List<TaskUnit> items;

		public TUAdapter(Context context, int textViewResourceId, List<TaskUnit> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}

		/**
		 * This gets called whenever a new ListItem is created from the m_orders set.
		 * @param position the position of the listitem at which it is created
		 * @param convertView the View object of the ListItem.
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.row, null);
			}
			TaskUnit o = items.get(position);
			if (o != null) {
				// fill View with info
				//Log.d("QAct","adding TU to list: "+o.toString());
				// Button
				Button btnRemove = (Button) v.findViewById(R.id.editbutton);
				TUTag tag = new TUTag(position, o.getKey());
				btnRemove.setTag(tag);
				btnRemove.setOnClickListener(this);
				// TextView und ImageView
				TextView tt = (TextView) v.findViewById(R.id.toptext);
				TextView bt = (TextView) v.findViewById(R.id.bottomtext);
				ImageView icon = (ImageView) v.findViewById(R.id.rowicon);
				// convert Time Data etc.
				Date starttime = o.getStarttime();
				Date endtime = new Date();
				endtime.setTime(starttime.getTime() + o.getDuration());
				String timestring = new String(String.format("%02d:%02d Uhr - %02d:%02d Uhr",starttime.getHours(),starttime.getMinutes(),endtime.getHours(),endtime.getMinutes()));  // String sch�n machen entsprechend 01:03 Uhr darstellen.
				// je nach pause/work einf�llen
				if (o.getStreamURL().trim().length() > 0) {  // checks if streamURL is not only whitespaces
					icon.setImageResource(R.drawable.pause);
					if (tt != null) { tt.setText(timestring); }
					if(bt != null) { 
						bt.setText(o.getStreamURL());
						bt.setVisibility(View.VISIBLE);
					}
				} else {
					icon.setImageResource(R.drawable.work);
					if (tt != null) { tt.setText(timestring); }
					if(bt != null) {
						bt.setText("");
						bt.setVisibility(View.GONE);
					}
				}
			}
			return v;
		}
		/**
		 * What happens on the edit buttons?
		 */
		public void onClick(View v) {
			TUTag tag = (TUTag) v.getTag();
			startedit(tag.position,tag.key,false);
		}
	}

	private class TUTag {
		public int position;
		public long key;
		public TUTag(int p, long k) {
			this.position = p; this.key = k;
		}
	}


	// ------------------------ Edit Screen related

	/**
	 * Function called when we want to edit or add a TaskUnit
	 * @param key	key of the TaskUnit to be edited.
	 * @param add	false if we edit, true if we add a new one TU
	 * @param index	position of the TU in the m_qtus
	 */
	private long modified_key;
	private void startedit(int index, long key ,boolean add) {
		TaskUnit orig;
		TaskUnit tu;
		final Intent i = new Intent(this, editActivity.class);
		if (!add) {
			orig = m_qtus.get(index);
			tu = new TaskUnit(orig.getStarttime(),orig.getDuration(),orig.getStreamURL());
			modified_key = key;
			i.putExtra(TU_OBJECT_KEY,tu);
		}
		i.putExtra(TU_NEW_KEY, add);
		startActivityForResult(i,0);
	}


	// 	------------------------ get results from subactivity

	/**
	 * RequestCode 	0: Back from Edit Activity.
	 *             	1: Back from Settings Activity
	 */ 
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		// back from edit activity.
		case 0:
			if (resultCode == Activity.RESULT_OK) {

				// get exras von intent
				final Bundle extras = data.getExtras();
				TaskUnit TUret = (TaskUnit) extras.get(TU_OBJECT_KEY);
				boolean delete = (Boolean) extras.get(TU_DELETE_KEY);
				boolean justnew = (Boolean) extras.get(TU_NEW_KEY);
				// loesche die aktuel modifizierende TU
				if (delete) {
					net_DoStuff trd = new net_DoStuff(2,modified_key);
					trd.start();
				}
				// wenn nicht gel�scht, dann erstelle ein neues TU! (und l�sche eventuell das alte)
				else {
					if (TUret == null) {Log.e("Qact connect","MAssive error: TUret = null obwohl es nicht sein d�rfte!"); break; }
					if (justnew) {
						net_DoStuff trd = new net_DoStuff(3,TUret.getStarttime(),TUret.getDuration(),TUret.getStreamURL());
						trd.start();
					} else {
						net_DoStuff trd = new net_DoStuff(4,modified_key,TUret.getStarttime(),TUret.getDuration(),TUret.getStreamURL());
						trd.start();
					}
				}
			}
			break;
			// Settings Activity
		case 1:
			switch (resultCode) {
			case Activity.RESULT_OK:
				readSettings();
			}
			break;
		default:
		}

	}   


	// ------------------------- All Networking

	/*
	 * Prinzipiell l�uft das ganze Networking so:
	 * Es gibt 4 Constructors:
	 * 		net_GetWorkday (connected, empf�ngt workday, disconnected)
	 * 		net_DeleteTU (connected, l�scht TU, disconnected)
	 * 		net_AddTU (connected, f�gt eine neue TU hinzu, disconnected)
	 * 		net_modTU (connected, l�scht die alte TU, f�gt die neue TU hinzu, disconnected)
	 */
	
	
	/**
	 * Stellt eine Verbinung zum Server her (port/ip);
	 * @return der verbundene QClient oder null falls die Verbindung nicht klappt.
	 */
	private QClient makeConn() {
		QClient ret = new QClientImpl();
		try {
			ret.connect(ip, port);
		} catch (UnableToConnectException e) {
			runOnUiThread(dispFailToast);
			return null;
		}
		return ret;
	}
	
	
	/**
	 * Autoupdater class.
	 * @author marco
	 */
	private class Updater implements Runnable {
		public void run() {
			if (!updateable) return;
			Thread connect = new net_DoStuff(true,1); // action 1 = get New Workday List.
			connect.start();
		}
	}
	
	
	/**
	 * Verbindet mit dem Server und macht eine Aktion und disconnected wieder.
	 * int Action: 1 fuer GetWorkday
	 *             2 fuer DeleteTU
	 *             3 fuer AddTU
	 *             4 fuer ModTU
	 *             5 fuer Reset
	 * @author Marco
	 */
	private class net_DoStuff extends Thread {
		
		private long key_to_delete;
		private int action;
		private Date starttime;
		private long duration;
		private String StreamURL;
		private boolean quietmode;
		private int minutes;
		
		// constructor f�r GetWorkday
		public net_DoStuff(boolean quiet,int action){
			super();
			this.action = action;
			this.quietmode = quiet;
		}
		
		// constructor f�r DeleteTU
		public net_DoStuff(int action, long key){
			super();
			this.action = action;
			this.key_to_delete = key;
		}

		// constructor f�r AddTU		
		public net_DoStuff(int action, Date starttime, long duration, String StreamURL){
			super();
			this.action = action;
			this.starttime = starttime;
			this.duration = duration;
			this.StreamURL = StreamURL;
		}
		
		// constructor f�r ModTU		
		public net_DoStuff(int action, long key, Date starttime, long duration, String StreamURL){
			super();
			this.action = action;
			this.key_to_delete = key;
			this.starttime = starttime;
			this.duration = duration;
			this.StreamURL = StreamURL;
		}
		
		// constructor fuer Reset
		public net_DoStuff(int action, int minutes){
			super();
			this.action = action;
			this.minutes = minutes;
		}
		
		public void run() {
			if (!quietmode) runOnUiThread(dispPleaseWaitmsg);
			
			QClient conn = makeConn();
			if (conn == null) {
				Log.e("QAct net","Connection could not be made to "+ip+":"+port);
				m_qtus.clear();
				runOnUiThread(renewList);
				if (!quietmode) runOnUiThread(dismissPleaseWaitmsg);
				updateable = false;
				return;
			}
			
			WorkdayWrapperImpl wrp;
			try {
				wrp = new WorkdayWrapperImpl(conn,m_qtus);
			} catch (NotActiveException e) {
				Log.e("QAct net","connection not active while creating new WorkdayWrapperImpl.");
				runOnUiThread(dispFailToast);
				if (!quietmode) runOnUiThread(dismissPleaseWaitmsg);
				updateable = false;
				return;
			}
			
			switch (action) {
			case 1: // Get New Workday
				GetWorkday(wrp);
				break;
			case 2: // Delete TU
				DeleteTU(wrp,key_to_delete);
				break;
			case 3: // Add TU
				AddTU(wrp, starttime, duration, StreamURL);
				break;
			case 4:	// delete and then add TU
				DeleteTU(wrp,key_to_delete);
				AddTU(wrp, starttime, duration, StreamURL);
				break;
			case 5:
				Reset(wrp, this.minutes);
				break;
			default:
				Log.e("Qact net","Thread created with the wrong action number!");
			}
			
			conn.disconnect();
			
			runOnUiThread(renewList);
			if (!quietmode) runOnUiThread(dismissPleaseWaitmsg);
		}
		
		private void GetWorkday(WorkdayWrapperImpl wrp){
			if (!wrp.getNewList()) {
				m_qtus.clear();
				Log.e("QAct net getNewList","wrapper could not get New List from Server.");
				runOnUiThread(dispFailToast);
				updateable = false;
			} else {
				updateable = true;
			}
		}
		
		private void DeleteTU(WorkdayWrapperImpl wrp, long key){
			if (!wrp.removeUnitByKey(key)) {
				m_qtus.clear();
				Log.e("QAct net deleteTU","wrapper could not delete the TU.");
				runOnUiThread(dispFailToast);
				updateable = false;
			} else {
				updateable = true;
			}
		}
		
		private void AddTU(WorkdayWrapperImpl wrp, Date strt, long dur, String URL) {
			if (!wrp.addUnit(strt, dur, URL)) {
				m_qtus.clear();
				Log.e("QAct net deleteTU","wrapper could not add a new TU.");
				runOnUiThread(dispFailToast);
				updateable = false;
			} else {
				updateable = true;
			}
		}
		
		private void Reset(WorkdayWrapperImpl wrp, int minutes) {
			if (!wrp.reset(minutes)) {
				m_qtus.clear();
				Log.e("QAct net Reset","wrapper could not Reset the Workday.");
				runOnUiThread(dispFailToast);
				updateable = false;
			} else {
				updateable = true;
			}
		}
	}
	

	// display the "Please Wait" circle thing.
	// UI THREAD!
	private Runnable dispPleaseWaitmsg = new Runnable(){
		public void run() {
			showDialog(0);
		}
	};

	// dismiss the "Please Wait" circle thing.
	// UI THREAD!
	private Runnable dismissPleaseWaitmsg = new Runnable(){
		public void run() {
			m_ProgressDialog.dismiss();
			removeDialog(0);
		}
	};
	
	// update the List and renew Adapter.
	// UI THREAD!
	private Runnable renewList = new Runnable(){
		public void run() {
			m_adapter = new TUAdapter(getApplicationContext(), R.layout.row, m_qtus);
			lv_qtu.setAdapter(m_adapter);
		}
	};
	
	// Display the connection failed Toast.
	// UI THREAD!
	private Runnable dispFailToast = new Runnable(){
		public void run() {
			dispFailToast();
		}
	};
	private void dispFailToast(){
		updateable = false;
		Toast.makeText(this.getApplicationContext(), "Error while talking to server...", Toast.LENGTH_LONG).show();
	}
	

	// ------------------------- Menu Funktionalit�t

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_exit:
			finish();
			break;
		case R.id.menu_settings:
			final Intent intent = new Intent(this,SettingsActivity.class);
			startActivityForResult(intent,1);
			break;
		case R.id.menu_reset:
			showDialog(2);
			break;
		default:
		}
		return true;
	}


	// ------------------------- Shared Preferences


	private void readSettings() {
		ip = settings.getString(SettingsActivity.SET_IP_KEY, "not set");
		port = settings.getInt(SettingsActivity.SET_PORT_KEY, 99999);
		Log.d("Settings","ip: "+ip);
		Log.d("Settings","Port: "+Integer.toString(port));
	}


}

