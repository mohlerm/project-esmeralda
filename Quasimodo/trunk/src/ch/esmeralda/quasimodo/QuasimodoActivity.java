package ch.esmeralda.quasimodo;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import ch.esmeralda.quasimodo.net.QClient;
import ch.esmeralda.quasimodo.net.QClient.UnableToConnectException;
import ch.esmeralda.quasimodo.net.QClientImpl;
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
        addbutton = (Button) findViewById(R.id.addbtn);			// define GUI objects
        AddListenerClass addlistener = new AddListenerClass();
        addbutton.setOnClickListener(addlistener);
        
        connectbutton = (Button) findViewById(R.id.connectbtn);			// define GUI objects
        ConnectListenerClass connectlistener = new ConnectListenerClass();
        connectbutton.setOnClickListener(connectlistener);
        
        // load previous Settings.
        settings = getSharedPreferences(PREFS_NAME, 0);			// read settings
        readSettings();
        
        // load List and its adapter
        lv_qtu = (ListView) findViewById(R.id.taskunitlist);
        m_qtus = new ArrayList<TaskUnit>();						// initialize orders and set adapter
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
			startedit(-1,1);
		}
    }
	
	/**
	 * What happens when the "Connect" button gets clicked?
	 * @author Marco
	 */
	private class ConnectListenerClass implements OnClickListener{
		public void onClick(View v) {
			if (ip.matches("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$") && port > 0 && port < 65537) {
				try {
					connection.connect(ip, port);
				} catch (UnableToConnectException e) {
					Log.e("connection", "UnableToConnect Exception thrown, could not connect to server.");
					Toast.makeText(getApplicationContext(), "Error communicating with server.", Toast.LENGTH_LONG).show();
				}
				getWorkdayFromNet();
			} else {
				Toast.makeText(v.getContext(), "Please set the right IP and PORT in the settings.", Toast.LENGTH_LONG).show();
			}
		}
	}
	
	/**
	 * This gets called when the activity shuts down.
	 */
	protected void onDestroy() {
		try {
			connection.disconnect();
		} catch (Exception e) { }
		
		Toast.makeText(this.getApplicationContext(), "disconnected", Toast.LENGTH_SHORT).show();
		super.onDestroy();
	}
	
	// ------------------------- custom ArrayList Adapter class
	
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
                int curkey = (int) o.getKey();
                if (o != null) {
                	Button btnRemove = (Button) v.findViewById(R.id.removebutton);
                    btnRemove.setFocusableInTouchMode(false);
                    btnRemove.setFocusable(false);
                    btnRemove.setTag(curkey);
                    btnRemove.setOnClickListener(this);     
                	TextView tt = (TextView) v.findViewById(R.id.toptext);
                    TextView bt = (TextView) v.findViewById(R.id.bottomtext);
                    if (tt != null) {
                          // Set Value of upper text to something
                    }
                    if(bt != null){
                          // set Value of lower text to something
                    }
                }
                return v;
        }
        /**
         * What happens on the edit buttons?
         */
		public void onClick(View v) {
			 startedit((Integer) v.getTag(),0);
		}
    }
    
    
    // ------------------------ get results from subactivity
    
   
    /**
     * RequestCode 0: Back from Edit Activity.
     *             1: Back from Settings Activity
     */
   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    switch (requestCode) {
	    case 0:
	    	// edit values come back by intent extras!
	    	// store data to server
		    break;
	    case 1:
	    	switch (resultCode) {
	    	case Activity.RESULT_OK:
				readSettings();
	    	}
		    break;
		default:
	    }
   		
   }
 
    
    
    // ------------------------ Edit Screen related
    
   /**
    * Function called when we want to edit or add a TaskUnit
    * @param key	key of the TaskUnit to be edited.
    * @param code	0 for editing. 1 for adding.
    */
    private void startedit(int key, int code) {
    	final Intent i = new Intent(this, editActivity.class);
    	// put extra object stuff
    	i.putExtra(TU_NEW_KEY, code);
    	startActivityForResult(i,0);
    }
    
    
	// ------------------------- All Networking
	
    /**
     * Gets a whole workday update.
     * call code = 0
     */
	private void getWorkdayFromNet() {
		if (!connection.isConnected()) return;
		m_ProgressDialog = ProgressDialog.show(QuasimodoActivity.this, "Please wait...", "Retrieving data ...", true);
		Thread conn_thrd =  new Thread(null, GetWorkday, "GetDatafromServer");
        conn_thrd.start();
	}	
	
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
				Toast.makeText(m_ProgressDialog.getContext(), "Error communicating with server.", Toast.LENGTH_LONG).show();
				ans = null;
			}
			
			if (ans == null) {
				Log.e("connection","ans is null");
			} else {
				if (ans.getworkday() == null) Log.e("connection","workday is null");
			}
			
			if (ans != null && ans.getworkday() != null){
				Log.d("connection", "workdaysize: "+Integer.toString(ans.getworkday().size()));
				m_qtus.clear();
				m_qtus.addAll(ans.getworkday());
				runOnUiThread(UpdateData);
			}
				

		}
	};
	
	private Runnable UpdateData = new Runnable() {
		public void run() {
			m_adapter = new TUAdapter(getApplicationContext(), R.layout.row, m_qtus);
			lv_qtu.setAdapter(m_adapter);
			m_ProgressDialog.dismiss();
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

