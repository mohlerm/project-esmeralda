package ch.esmeralda.quasimodo;

import java.util.ArrayList;

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

import ch.esmeralda.quasimodo.unitHandlingWrapper.TaskUnit;

public class QuasimodoActivity extends Activity {
	
	// Various GUI Objects
    private ProgressDialog m_ProgressDialog = null; // -
    private ArrayList<TaskUnit> m_qtus = null;
    private QTUAdapter m_adapter;
    private Runnable viewOrders;  // -
    private Button addbutton;
    private ListView lv_qtu;
    private SharedPreferences settings;
    
    // Connection specific values
    private String ip;
    private int port;
    
    // Public constants
    public static final String PREFS_NAME = "EsmeraldaPrefsFile";
    public static final String TU_NEW_KEY = "TUIFNEWTAGACTIVITY";
    public static final String TU_OBJECT_KEY = "TUSTATUSTOEDITACTIVITY";
    public static final String TU_DELETE_KEY = "TUTOBEDELETED"; 
	
    
    
    // ---------- initial Handling
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);   
        setContentView(R.layout.main);
        
        // get ID's from various gui objects
        addbutton = (Button) findViewById(R.id.addbtn);			// define GUI objects
        AddListenerClass addlistener = new AddListenerClass();
        addbutton.setOnClickListener(addlistener);
        lv_qtu = (ListView) findViewById(R.id.taskunitlist);
        
        settings = getSharedPreferences(PREFS_NAME, 0);			// read settings
        readSettings();
        
        m_qtus = new ArrayList<TaskUnit>();						// initialize orders and set adapter
        this.m_adapter = new QTUAdapter(this, R.layout.row, m_qtus);
        lv_qtu.setAdapter(this.m_adapter);
        
        getDataFromNet();
        
    }

	private class AddListenerClass implements OnClickListener{
		public void onClick(View v) {
			startedit(m_qtus.size()-1,1);
		}
    }
	
	
	// ------------------------- custom ArrayList Adapter class
	
    private class QTUAdapter extends ArrayAdapter<TaskUnit> implements OnClickListener{

        private ArrayList<TaskUnit> items;

        public QTUAdapter(Context context, int textViewResourceId, ArrayList<TaskUnit> items) {
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
                	Button btnRemove = (Button) v.findViewById(R.id.removebutton);
                    btnRemove.setFocusableInTouchMode(false);
                    btnRemove.setFocusable(false);
                    btnRemove.setTag(position);
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
	
    private void updateListView() {
    	// update the listview and notify adapter etc.
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
    
    private int beingmodified;
    private void startedit(int o, int code) {			// code = 1 if we just added a new one.
    	beingmodified = o;
    	final Intent i = new Intent(this, editActivity.class);
    	// put extra object stuff
    	i.putExtra(TU_NEW_KEY, code);
    	startActivityForResult(i,0);
    }
    
    
	// ------------------------- All Networking
	
	private void getDataFromNet() {
		m_ProgressDialog = ProgressDialog.show(this, "Please wait...", "Retrieving data ...", true);
		// fill data from network. Use Task or something
	}	
	
	
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
    		final Intent intent = new Intent(this,SettingsActivity.class);
    		startActivityForResult(intent,1);
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

