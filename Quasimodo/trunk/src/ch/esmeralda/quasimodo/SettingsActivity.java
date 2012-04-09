package ch.esmeralda.quasimodo;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import ch.esmeralda.quasimodo.RadioStation;


public class SettingsActivity extends Activity implements OnClickListener{
	
	// public static values
	public final static String SET_IP_KEY = "settingsipkey";
	public final static String SET_PORT_KEY = "settingsportkey";
	
	public final static String DefaultIP = "192.168.1.15";
	public final static int DefaultPORT = 10002;
	
	public final static String RadioListFilename = "RadioList.db";
	
	// Buttons and Texts
	private Button creditsbtn;
	private Button defaultbtn;
	private Button savebtn;
	private EditText editip;
	private EditText editport;
	
	// Shared preferences
	private SharedPreferences settings;
	private String ip;
	private int port;
	
	// Radio ListView / Arrayliste und adapter
	private ListView radioLV;
	private RadioAdapter radioadap;
	private ArrayList<RadioStation> RADIO_LIST;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        
        creditsbtn = (Button) findViewById(R.id.creditsbtn);
        creditsbtn.setOnClickListener(this);
        
        settings = getSharedPreferences(QuasimodoActivity.PREFS_NAME, 0);
        ip = settings.getString(SET_IP_KEY, "yet unset");
        port = settings.getInt(SET_PORT_KEY, 0);
        
        defaultbtn = (Button) findViewById(R.id.set_defaultbtn);
        savebtn = (Button) findViewById(R.id.set_savebtn);
        defaultbtn.setOnClickListener(this);
        savebtn.setOnClickListener(this);
        
        editip = (EditText) findViewById(R.id.setText_IP);
        editport = (EditText) findViewById(R.id.setText_PORT);
        editip.setText(ip);
        editport.setText(Integer.toString(port));
        
        // Radio List
        RADIO_LIST = new ArrayList<RadioStation>();
        QFileIO.loadRadioList(this, RADIO_LIST, RadioListFilename);
        // add the NEW button
     		RadioStation adder = new RadioStation("","New Radio...");
     		adder.newtag = true;
     		RADIO_LIST.add(adder);
        radioLV = (ListView) findViewById(R.id.RadioStationsLV);
        reinit_RadioList();
        
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.set_defaultbtn:
			// default ip and port
				ip = DefaultIP;
				port = DefaultPORT;
				editip.setText(ip);
		        editport.setText(Integer.toString(port));
	        // clear Radio List
		        RADIO_LIST.clear();
		        RADIO_LIST.add(new RadioStation("DI Trance","http://u11aw.di.fm:80/di_trance"));
		        RADIO_LIST.add(new RadioStation("DI Eurodance","http://u11aw.di.fm:80/di_eurodance"));
		        // add the last button
			        RadioStation adder = new RadioStation("","New Radio...");
		     		adder.newtag = true;
		     		RADIO_LIST.add(adder);
	     		reinit_RadioList();
	        return;
		case R.id.set_savebtn:
			// Pr�fe ob IP richtig eingegeben.
				if (!editip.getText().toString().matches("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$")){
					Toast.makeText(getApplicationContext(), "Die IP hat keine korrekte IPv4 Form!", Toast.LENGTH_LONG).show();
					return;
				}
				ip = editip.getText().toString();
				
			// Pr�fe ob Port richtig eingegeben.
				int tempport = Integer.parseInt(editport.getText().toString());
				if (tempport < 0 || tempport > 65536) {
					Toast.makeText(getApplicationContext(), "Der Port ist nicht korrekt gesetzt!", Toast.LENGTH_LONG).show();
					return;
				}
				port = tempport;
			
			// Alles ok, schreibe die Einstellungen und die RadioListe
				RADIO_LIST.remove(RADIO_LIST.size()-1);
				QFileIO.writeRadioList(this, RADIO_LIST, RadioListFilename);
				
				SharedPreferences.Editor editor = settings.edit();
				editor.putString(SET_IP_KEY,ip);
				editor.putInt(SET_PORT_KEY,port);
				editor.commit();
				setResult(Activity.RESULT_OK, null);
				finish();
			return;
		case R.id.creditsbtn:
			showDialog(1);
			break;
		default:
		}
	}
	
	// ************************************************* popup
	
	@Override
    protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 0:
			LayoutInflater factory = LayoutInflater.from(this);
	        final View textEntryView = factory.inflate(R.layout.newradioalertdialog, null);
	        AlertDialog NewRadio = new AlertDialog.Builder(SettingsActivity.this)
	            .setTitle("New Radio entry:")
	            .setView(textEntryView)
	            .setPositiveButton("Done", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	                	EditText name_edit = (EditText) textEntryView.findViewById(R.id.radioname_edit);
	                	EditText url_edit = (EditText) textEntryView.findViewById(R.id.radiourl_edit);
	                	RADIO_LIST.add(RADIO_LIST.size()-1, new RadioStation(name_edit.getText().toString(), url_edit.getText().toString()));
	                	reinit_RadioList();
	                	// l�sst Tastatur verschwinden.
		                	InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE); 
		        	        if (name_edit != null) 
		        	        	inputManager.hideSoftInputFromWindow(name_edit.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	                }
	            })
	            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	                		// Do nothing.
	                }
	            })
	            .create();
	        return NewRadio;
		case 1:
			// show credits;
			AlertDialog Credits = new AlertDialog.Builder(this).create();
			Credits.setTitle("Quasimodo,\nthe Hunchback of NotreDame");
			Credits.setMessage(getResources().getText(R.string.credits_string));
			Credits.setIcon(R.drawable.icon);
			Credits.setButton("Done", new DialogInterface.OnClickListener() {
			   public void onClick(DialogInterface dialog, int which) {
					// do nothing.
			   }
			});
			return Credits;
		default:
			break;
		}
		LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.newradioalertdialog, null);
        return new AlertDialog.Builder(SettingsActivity.this)
            .setTitle("New Radio entry:")
            .setView(textEntryView)
            .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	EditText name_edit = (EditText) textEntryView.findViewById(R.id.radioname_edit);
                	EditText url_edit = (EditText) textEntryView.findViewById(R.id.radiourl_edit);
                	RADIO_LIST.add(RADIO_LIST.size()-1, new RadioStation(name_edit.getText().toString(), url_edit.getText().toString()));
                	reinit_RadioList();
                	// l�sst Tastatur verschwinden.
	                	InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE); 
	        	        if (name_edit != null) 
	        	        	inputManager.hideSoftInputFromWindow(name_edit.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            })
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                		// Do nothing.
                }
            })
            .create();
	}
	
	
	
	// ************************************************* Radio Station Liste etc.
	
	
	private void reinit_RadioList() {   											// eventuell mit notifyAdapterDataSetChanged() machen!
		radioadap = new RadioAdapter(this, R.layout.radiorow, RADIO_LIST);
        radioLV.setAdapter(radioadap);
        radioLV.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
				if (position == RADIO_LIST.size()-1) {
					showDialog(0);
				}
			}
        });
        // l�sst Tastatur verschwinden.
	        InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE); 
	        View v = this.getCurrentFocus();
	        if (v != null) 
	        	inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}
	
	private class RadioAdapter extends ArrayAdapter<RadioStation> implements OnClickListener {
		
		 private ArrayList<RadioStation> radiostations;

	        public RadioAdapter(Context context, int textViewResourceId, ArrayList<RadioStation> items) {
	                super(context, textViewResourceId, items);
	                this.radiostations = items;
	        }
	        
	        @Override
	        public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.radiorow, null);
                }
                RadioStation stat = radiostations.get(position);
                if (stat != null) {
                	TextView stationname = (TextView) v.findViewById(R.id.radiostationname);
                	TextView stationurl = (TextView) v.findViewById(R.id.radiostationurl);
                	Button rembutton = (Button) v.findViewById(R.id.removeradiobutton);
                	stationname.setText(stat.name);
                	stationurl.setText(stat.url);
                	rembutton.setTag(position);
                	rembutton.setOnClickListener(this);
                	// behandeln des letzten Eintrages
                	if (stat.newtag) {
                		rembutton.setVisibility(View.GONE);
                		v.setFocusable(false);
                	} else {
                    	rembutton.setVisibility(View.VISIBLE);
                    	v.setFocusable(true);
                	}
                }
                return v;
        }

			public void onClick(View v) {
				Button in = (Button) v;
				int pos = (Integer) in.getTag();
				RADIO_LIST.remove(pos);
				reinit_RadioList();
			}
		
			
	}
	
	
}
