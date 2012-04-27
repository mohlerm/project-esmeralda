package ch.esmeralda.quasimodo;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ToggleButton;

import ch.esmeralda.quasimodo.radiostation.QFileIO;
import ch.esmeralda.quasimodo.radiostation.RadioStation;
import ch.esmeralda.quasimodo.svc.QNetSvc;


public class SettingsActivity extends Activity{
	
	// public static values
	public final static String SET_IP_KEY = "settingsipkey";
	public final static String SET_PORT_KEY = "settingsportkey";
	public final static String DefaultIP = "192.168.1.15";
	public final static int DefaultPORT = 10002;
	public final static String RadioListFilename = "RadioList.db";
	
	// private static values
	private final static int DIALOG_NEW_RADIO = 0;
	private final static int DIALOG_EDIT_RADIO = 1;
	private final static int DIALOG_CREDITS = 2;
	private final static int DIALOG_ASK_DEFAULT = 3;
	private final static int DIALOG_INFO_NOTIF = 4;
	private final static String SET_FIRSTTIME = "kadjhfiahjsdgfikajhdsgfiaezfbakcfhjv"; 
	
	// App
	private QuasimodoApp app; 
	
	// Buttons and Texts
	private EditText editip;
	private EditText editport;
	private ToggleButton notiftgl;
	
	// Shared preferences
	private SharedPreferences settings;
	private boolean firsttime;
	
	// Radio ListView / Arrayliste und adapter
	private ListView radioLV;
	private RadioAdapter radioadap;
	private ArrayList<RadioStation> RADIO_LIST;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        
        // ---- app class
        app = (QuasimodoApp) getApplicationContext();
        
        // ---- shared prefs
        settings = getSharedPreferences(QuasimodoActivity.PREFS_NAME, 0);
        app.ip = settings.getString(SET_IP_KEY, "yet unset");
        app.port = settings.getInt(SET_PORT_KEY, 0);
        firsttime = settings.getBoolean(SET_FIRSTTIME, true);
        
        // ---- Find IP and Port View
        editip = (EditText) findViewById(R.id.setText_IP);
        editport = (EditText) findViewById(R.id.setText_PORT);
        editip.setText(app.ip);
        editport.setText(Integer.toString(app.port));
        
        // ---- ToggleButton
        notiftgl = (ToggleButton) findViewById(R.id.notifications_tbtn);
        notiftgl.setChecked(app.serviceRunning);
        
        // ---- Radio List
        RADIO_LIST = new ArrayList<RadioStation>();
        QFileIO.loadRadioList(this, RADIO_LIST, RadioListFilename);
        // add the NEW button
     		RadioStation adder = new RadioStation("","New Radio...");
     		adder.newtag = true;
     		RADIO_LIST.add(adder);
        radioLV = (ListView) findViewById(R.id.RadioStationsLV);
        
        // set default values after installation
        if (app.port == 0) {
        	setDefault();
        	editip.setText("your server IP");
        }
        
        reinit_RadioList();
        
	}
	
	// ---- Button click Functions / Handlers
	
	public void DefaultButtonClick(View v) {
		showDialog(DIALOG_ASK_DEFAULT);
	}
	
	public void SaveButtonClick(View v) {
		// Service starten oder stoppen:
		if (app.serviceRunning != notiftgl.isChecked()) {
			if (notiftgl.isChecked()) {
				// start service und zeige Info Dialog
				startService(new Intent(this, QNetSvc.class));
			} else {
				// stop service
				stopService(new Intent(this, QNetSvc.class));
			}
			if (firsttime) {
				showDialog(DIALOG_INFO_NOTIF);
				firsttime = false;
				return;
			}
		}
		
		// Pr�fe ob IP richtig eingegeben.
		if (!editip.getText().toString().matches("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$")){
			Toast.makeText(getApplicationContext(), "Please enter a correct IP address.", Toast.LENGTH_LONG).show();
			return;
		}
		app.ip = editip.getText().toString();
		
	// Pr�fe ob Port richtig eingegeben.
		int tempport = Integer.parseInt(editport.getText().toString());
		if (tempport < 0 || tempport > 65536) {
			Toast.makeText(getApplicationContext(), "Please set a valid port.", Toast.LENGTH_LONG).show();
			return;
		}
		app.port = tempport;
	
	// Alles ok, schreibe die Einstellungen und die RadioListe
		RADIO_LIST.remove(RADIO_LIST.size()-1); // remove "add" entry
		QFileIO.writeRadioList(this, RADIO_LIST, RadioListFilename);
		
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(SET_IP_KEY,app.ip);
		editor.putInt(SET_PORT_KEY,app.port);
		editor.putBoolean(SET_FIRSTTIME, false);
		editor.commit();
		setResult(Activity.RESULT_OK, null);
		finish();
	}
	
	public void CreditsButtonClick(View v) {
		showDialog(DIALOG_CREDITS);
	}
	
	// ************************************************* popup
	
	@Override
    protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_NEW_RADIO:
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
	                	EditText temp = (EditText) textEntryView.findViewById(R.id.radioname_edit);
	                	hidekeyboard(temp);
	                }
	            })
	            .create();
	        return NewRadio;
		case DIALOG_CREDITS:
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
		//---- edit radio station.
		case DIALOG_EDIT_RADIO:
			LayoutInflater factory1 = LayoutInflater.from(this);
	        final View textEntryView1 = factory1.inflate(R.layout.newradioalertdialog, null);
	        return new AlertDialog.Builder(SettingsActivity.this)
	            .setTitle("Edit Radio entry:")
	            .setView(textEntryView1)
	            .setPositiveButton("Done", new DialogInterface.OnClickListener() {
	            	//--- on click listener for positive button.
	                public void onClick(DialogInterface dialog, int whichButton) {
	                	EditText name_edit = (EditText) textEntryView1.findViewById(R.id.radioname_edit);
	                	EditText url_edit = (EditText) textEntryView1.findViewById(R.id.radiourl_edit);
	                	RADIO_LIST.remove(rs_mod_index);
	                	RADIO_LIST.add(rs_mod_index, new RadioStation(name_edit.getText().toString(), url_edit.getText().toString()));
	                	reinit_RadioList();
	                	// laesst Tastatur verschwinden.
		                hidekeyboard(name_edit);
	                }
	            })
	            .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	                		RADIO_LIST.remove(rs_mod_index);
	                		reinit_RadioList();
	                }
	            })
	            .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	                	EditText name_edit = (EditText) textEntryView1.findViewById(R.id.radioname_edit);
                		hidekeyboard(name_edit);
	                }
                })
	            .create();
		case DIALOG_ASK_DEFAULT:
			//---- really sure if you want to default all settings?
			return new AlertDialog.Builder(SettingsActivity.this)
            .setTitle("Set default?")
            .setPositiveButton("Yes!", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
        			setDefault();
    	     		reinit_RadioList();
                }
            })
            .setNegativeButton("Nah...", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	// do nothing...
                }
            })
            .create();
		case DIALOG_INFO_NOTIF:
			return new AlertDialog.Builder(SettingsActivity.this)
			.setTitle("Notifications")
			.setMessage(R.string.notifinfo)
			.setIcon(R.drawable.icon)
			.setNeutralButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// dont do shit
				}
			}).create();
		default:
			return super.onCreateDialog(id);
		}
	}
	
	//---- fill 
	@Override
    protected void onPrepareDialog (int id, Dialog dialog){ 
         if(id == DIALOG_EDIT_RADIO){ 
            AlertDialog adlg = (AlertDialog)dialog;
            EditText name_edit = (EditText) adlg.findViewById(R.id.radioname_edit);
        	EditText url_edit = (EditText) adlg.findViewById(R.id.radiourl_edit);
            name_edit.setText(RADIO_LIST.get(rs_mod_index).name);
            url_edit.setText(RADIO_LIST.get(rs_mod_index).url);
         } else {
            super.onPrepareDialog(id, dialog);
         }             
    }
	
	
	
	// ************************************************* Radio Station Liste etc.
	
	
	private void reinit_RadioList() {   											// eventuell mit notifyAdapterDataSetChanged() machen!
		radioadap = new RadioAdapter(this, R.layout.radiorow, RADIO_LIST);
        radioLV.setAdapter(radioadap);
        radioLV.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
				if (position == RADIO_LIST.size()-1) {
					showDialog(DIALOG_NEW_RADIO);
				}
			}
        });
        // l�sst Tastatur verschwinden.
	        InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE); 
	        View v = this.getCurrentFocus();
	        if (v != null) 
	        	inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}
	
	private int rs_mod_index;
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
				rs_mod_index = (Integer) in.getTag();
				showDialog(DIALOG_EDIT_RADIO);
			}
		
			
	}
	
	// ----  Helpers
	
	private void hidekeyboard(View v) {
		InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE); 
        if (v != null) 
        	inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}
	
	public void setDefault() {
		// default ip and port
		app.ip = DefaultIP;
		app.port = DefaultPORT;
		editip.setText(app.ip);
        editport.setText(Integer.toString(app.port));
        notiftgl.setChecked(false);
    // clear Radio List
        RADIO_LIST.clear();
        RADIO_LIST.add(new RadioStation("DI Trance","http://u11aw.di.fm:80/di_trance"));
        RADIO_LIST.add(new RadioStation("DI Eurodance","http://u11aw.di.fm:80/di_eurodance"));
        RADIO_LIST.add(new RadioStation("DI Vocal Trance","http://u11aw.di.fm:80/di_vocaltrance"));
        RADIO_LIST.add(new RadioStation("DI Chillout","http://u11aw.di.fm:80/di_chillout"));
        RADIO_LIST.add(new RadioStation("DI Lounge","http://u11aw.di.fm:80/di_lounge"));
        RADIO_LIST.add(new RadioStation("DI Classic Eurodance","http://u11aw.di.fm:80/di_classiceurodance"));
        RADIO_LIST.add(new RadioStation("DI Disco House","http://u11aw.di.fm:80/di_discohouse"));
        RADIO_LIST.add(new RadioStation("DI Drum n Bass","http://u11aw.di.fm:80/di_drumandbass"));
    // add the last button
        RadioStation adder = new RadioStation("","New Radio...");
 		adder.newtag = true;
 		RADIO_LIST.add(adder);
	}
	
}
