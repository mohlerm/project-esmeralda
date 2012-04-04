package ch.esmeralda.quasimodo;

import java.io.NotActiveException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import ch.esmeralda.quasimodo.net.QClient;
import ch.esmeralda.quasimodo.net.QClient.UnableToConnectException;
import ch.esmeralda.quasimodo.net.QClientImpl;
import ch.esmeralda.quasimodo.unitHandlingWrapper.WorkdayWrapper;
import ch.esmeralda.quasimodo.unitHandlingWrapper.WorkdayWrapperImpl;
import ch.esmeralda.DataExchange.*;

public class QuasimodoActivity extends Activity {
	
	// Various GUI Objects
    private ProgressDialog m_ProgressDialog = null;
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
    private QClient connection;
    private WorkdayWrapper wrapper;
    
    // Public constants
    public static final String PREFS_NAME = "EsmeraldaPrefsFile";
    public static final String TU_NEW_KEY = "TUIFNEWTAGACTIVITY";
    public static final String TU_OBJECT_KEY = "TUSTATUSTOEDITACTIVITY";
    public static final String TU_DELETE_KEY = "TUTOBEDELETED"; 
	
    
    
    // ---------- initial Handling
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	// android default
        super.onCreate(savedInstanceState);   
        setContentView(R.layout.main);
        
        // get ID's from various gui objects and initialize clickability
        addbutton = (Button) findViewById(R.id.addbtn);
        AddListenerClass addlistener = new AddListenerClass();
        addbutton.setOnClickListener(addlistener);
        
        connectbutton = (Button) findViewById(R.id.connectbtn);
        ConnectListenerClass connectlistener = new ConnectListenerClass();
        connectbutton.setOnClickListener(connectlistener);
        
        // load previous Settings.
        settings = getSharedPreferences(PREFS_NAME, 0);
        readSettings();
        
        // load List and its adapter
        lv_qtu = (ListView) findViewById(R.id.taskunitlist);
        m_qtus = new ArrayList<TaskUnit>();
        this.m_adapter = new TUAdapter(this, R.layout.row, m_qtus);
        lv_qtu.setAdapter(this.m_adapter);
        
        // Make Connection
		connection = (QClient) new QClientImpl();
        connectbutton.performClick();
        
        
    }

    /**
     * What happens when the "Add" button gets clicked?
     * @author Marco
     */
	private class AddListenerClass implements OnClickListener{
		public void onClick(View v) {
			if (connection.isConnected()) {
				startedit(0,0,true);
			}
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
			
			// display progress dialog
			runOnUiThread(connectprogress);
			
			// Make Connection thread
	        Runnable connect = new Runnable(){
	        	public void run() {
					try {
						connection.connect(ip, port);
					} catch (UnableToConnectException e) {
						Log.e("connection", "UnableToConnect Exception thrown, could not connect to server.");
					}
					if (connection.isConnected()) {
						// were connected, initialize the workday wrapper!
						try	{
							wrapper = (WorkdayWrapper) new WorkdayWrapperImpl(connection,m_qtus);
						} catch (NotActiveException e) {
							Log.e("Qact connection","connection not set while making the workdaywrapper!");
							e.printStackTrace();
						}
						// were connected, start the reading thread:
						Thread net_thrd =  new Thread(null, GetWorkday, "GetDatafromServer");
				        net_thrd.start();
					} else {
						// just dismiss the waiting circle and display error toast
						runOnUiThread(connectionreturn);
					}
				}
		        
	        };
	        
	        // Start the thread
	        Thread trd = new Thread(null, connect, "connecting Thread");
	        trd.start();
		}
	}
	
	
	
	/**
	 * This gets called when the activity shuts down.
	 */
	protected void onDestroy() {
		try {
			connection.disconnect();
		} catch (Exception e) { }
		super.onDestroy();
	}
	
	/**
	 * Easy way to handle all the dialogs.
	 */
	protected Dialog onCreateDialog(int id) {
		if (id == 0) {
			m_ProgressDialog = new ProgressDialog(this);
			m_ProgressDialog.setTitle("Please Wait...");
			m_ProgressDialog.setMessage("Retrieving Data");
			return m_ProgressDialog;
		}
		return super.onCreateDialog(id);
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
	                	Log.d("QAct","adding TU to list: "+o.toString());
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
	                    String timestring = new String(starttime.getHours()+":"+starttime.getMinutes()+" - "+endtime.getHours()+":"+endtime.getMinutes());
		            // je nach pause/work einfüllen
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
    
    private void notifyadapter() {
    	m_adapter = new TUAdapter(getApplicationContext(), R.layout.row, m_qtus);
		lv_qtu.setAdapter(m_adapter);
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
		    	// lösche die aktuel modifizierende TU
			    	if (delete) {
			    		if (wrapper.removeUnitByKey(modified_key)) {
			    			Log.d("Qact connect","removed a TU by key!");	    
			    		} else {
			    			Log.e("Qact connect","could not remove TU by key!");
			    		}
			    		notifyadapter();
			    	}
		    	// wenn nicht gelöscht, dann erstelle ein neues TU! (und lösche eventuell das alte)
			    	else {
			    		boolean res;
			    		if (!justnew) {
			    			// Da wir kein neues hinzugefügt haben, müssen wir da alte löschen!
			    			res = wrapper.removeUnitByKey(modified_key);
			    			Log.d("Qact connect","altes TU löschen vor neuem TU machen:"+res);
			    		}
			    		if (TUret == null) {Log.e("Qact connect","MAssive error: TUret = null obwohl es nicht sein dürfte!"); break; }
			    		res = wrapper.addUnit(TUret.getStarttime(), TUret.getDuration(), TUret.getStreamURL());
			    		Log.d("Qact connect","resultat von wrapper.addunit(3): "+res);
			    		if (!res) {
			    			connection.disconnect();
			    			runOnUiThread(connectionreturn);
			    			m_qtus.clear();
			    			notifyadapter();
			    		} else {
			    			notifyadapter();
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
	
	private Runnable GetWorkday = new Runnable(){
		public void run() {
			
			QueryDataPkg req = new QueryDataPkg(0, null);
			AnsDataPkg ans;
			
			try {
				Log.d("connection","starting the sendrequest.");
				ans = (AnsDataPkg) connection.sendRequest((Object)req);
				Log.d("connection","got the ans package!");
			} catch (Exception e) {
				Log.e("connection", "Server sent not an AnsDataPkg as object.");
				ans = null;
				connection.disconnect();
				runOnUiThread(connectionreturn);
			}
			
			// debug
				if (ans == null) {
					Log.e("connection","ans is null");
				} else {
					if (ans.getworkday() == null) Log.e("connection","workday is null");
				}
			
			if (ans != null && ans.getworkday() != null){
				Log.d("connection", "workdaysize: "+Integer.toString(ans.getworkday().size()));
				m_qtus.clear();
				m_qtus.addAll(ans.getworkday());
				runOnUiThread(updateList);
			} else {
				connection.disconnect();
			}
			runOnUiThread(connectionreturn);
		}
	};
	private Runnable updateList = new Runnable(){
		public void run() {
			notifyadapter();
		}
	};
	
	
	// UI Thread für ProgressDialog
	private Runnable connectprogress = new Runnable(){
		public void run() {
			showDialog(0);
		}
	};
	
	// Benötigt damit der Thread wieder auf das UI zugreifen darf.
	private Runnable connectionreturn = new Runnable(){
		public void run() {
			m_ProgressDialog.dismiss();
			removeDialog(0);
			if (!connection.isConnected())  {
				Toast.makeText(getApplicationContext(), "Error communicating with server.", Toast.LENGTH_LONG).show();
				m_qtus.clear();
				notifyadapter();
			}
		}
    };
	
	
	// ------------------------- Menu Funktionalität
	
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
    		connection.disconnect();
    		Toast.makeText(this.getApplicationContext(), "disconnected", Toast.LENGTH_SHORT).show();
    		final Intent intent = new Intent(this,SettingsActivity.class);
    		startActivityForResult(intent,1);
    		break;
    	case R.id.menu_disconnect:
    		connection.disconnect();
    		Toast.makeText(this.getApplicationContext(), "disconnected", Toast.LENGTH_SHORT).show();
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

