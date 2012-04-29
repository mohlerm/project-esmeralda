package ch.esmeralda.quasimodo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import ch.esmeralda.DataExchange.TaskUnit;
import ch.esmeralda.quasimodo.radiostation.QFileIO;
import ch.esmeralda.quasimodo.radiostation.RadioStation;

public class editActivity extends Activity implements OnClickListener{
	
	Button donebtn;
	Button deletebtn;
	
	Button workbtn;
	Button breakbtn;
	
	boolean isworkTU;
	boolean justnew;
	int semaphore;
	
	TaskUnit tu = null;
    
	TimePicker StartTP;
	TimePicker EndTP;
    Spinner radiospinner;
    
    TextView RadioSpinnerTitle;
    int defaultRadioSpinnerTitleColor;
    
    // add data classes for the start time and end time
    
    public  ArrayList<RadioStation> Radio_List;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.editscreen);
        
        // Radio Liste laden
	        Radio_List = new ArrayList<RadioStation>();
	        QFileIO.loadRadioList(this, (List<RadioStation>)Radio_List, SettingsActivity.RadioListFilename);
	        
        // find Views and make objects.
	        donebtn = (Button) findViewById(R.id.edit_donebtn);
	        deletebtn = (Button) findViewById(R.id.edit_deletebtn);
	        workbtn = (Button) findViewById(R.id.edit_workTU);
	        breakbtn = (Button) findViewById(R.id.edit_breakTU);
        
        // fill the Radio Station spinner
	        RadioSpinnerTitle = (TextView) findViewById(R.id.RadioSpinnerTitle);
	        defaultRadioSpinnerTitleColor = RadioSpinnerTitle.getTextColors().getDefaultColor();
	        radiospinner = (Spinner) findViewById(R.id.RadioStationSpinner);
	        ArrayList<String> RadioStationNames = new ArrayList<String>();
	        for (RadioStation o : Radio_List) {
	        	RadioStationNames.add(o.name);
	        }
	        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, RadioStationNames);
	        radiospinner.setAdapter(spinnerArrayAdapter);
        
        // set further View settings.
	        donebtn.setOnClickListener(this);
	        deletebtn.setOnClickListener(this);
	        workbtn.setOnClickListener(this);
	        breakbtn.setOnClickListener(this);
        
        // get Data from Intent and chose if work or break.
	        final Bundle extras = getIntent().getExtras();
	        justnew = false;
	        if (extras != null)
	        {
	        	tu = (TaskUnit) extras.getSerializable(QuasimodoActivity.TU_OBJECT_KEY);
	        	justnew = extras.getBoolean(QuasimodoActivity.TU_NEW_KEY);
	        } else {
	        	Log.e("EditAct","received a null TU with the intent!");
	        }
	        if (!justnew)
		        if (tu.getStreamURL().trim().length() > 0) {
		        	// check for the right radio Station and set spinner to it.
		        	// else add new RadioStation to the Settings File.
		        	boolean RS_exists = false;
		        	int i;
		        	for (i = 0; i < Radio_List.size(); i++) {
		        		if (tu.getStreamURL().equals(Radio_List.get(i).url)) {
		        			RS_exists = true;
		        			break;
		        		}
			        }
		        	// Wenn der Radio schon existiert, setze Spinner auf ihn.
		        	// Wenn nicht: f�ge die Radio Station der Liste hinzu.
		        	if (RS_exists) {
		        		radiospinner.setSelection(i);
		        	} else {
		        		RadioStation adder = new RadioStation(getStreamName(tu.getStreamURL()),tu.getStreamURL());
		        		Radio_List.add(adder);
		        		QFileIO.writeRadioList(this, Radio_List, SettingsActivity.RadioListFilename);
		        		// and redo all the important parts again from general initialization
		    	        Radio_List.clear();
		    	        QFileIO.loadRadioList(this, (List<RadioStation>)Radio_List, SettingsActivity.RadioListFilename);
		    	        RadioStationNames = new ArrayList<String>();
		    	        for (RadioStation o : Radio_List) {
		    	        	RadioStationNames.add(o.name);
		    	        }
		    	        spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, RadioStationNames);
		    	        radiospinner.setAdapter(spinnerArrayAdapter);
		    	        // and finally set the correct selection
		        		radiospinner.setSelection(Radio_List.size()-1);
		        	}
		        	setToBreak();
		        } else {
		        	setToWork();
		        }
        
        // Fill the TimePickers
	        Date starttime;
	        Date endtime;
	        if (!justnew) {
	        	starttime = tu.getStarttime();
		        endtime = new Date(starttime.getTime() + tu.getDuration());
	        } else {
	        	starttime = new Date(System.currentTimeMillis());
	        	endtime = new Date();
	        	endtime = new Date(starttime.getTime() + 45*60000);
	        }
	        
	        
	        StartTP = (TimePicker) findViewById(R.id.TimePicker_Start);
	        EndTP = (TimePicker) findViewById(R.id.TimePicker_End);
	        
	        StartTP.setIs24HourView(true);
	        EndTP.setIs24HourView(true);
	        
	        TPlistener tpl = new TPlistener();
	        StartTP.setOnTimeChangedListener(tpl);
	        EndTP.setOnTimeChangedListener(tpl);
	        
	        StartTP.setCurrentHour(starttime.getHours()); 	StartTP.setCurrentMinute(starttime.getMinutes());
	        EndTP.setCurrentHour(endtime.getHours()); 		EndTP.setCurrentMinute(endtime.getMinutes());
        
        
        // We just added a new TU, grey out delete button and set the edit mode to work.
	        if (justnew){
	        	TextView title = (TextView) findViewById(R.id.titlebar);
	        	title.setText("Add TaskUnit");
	        	deletebtn.setTextColor(Color.GRAY);
	        	deletebtn.setClickable(false);
	        	setToWork();
	        }
	}

	
	/**
	 * OnClick listener function for the 4 buttons.
	 * edit und delete button closen diese activity und geben Resultate an Quasimodo activity zur�ck.
	 */
	public void onClick(View v) {
		if (v.getId() == R.id.edit_donebtn){
			// neue Date objekte erstellen und duration berechnen
				Date start = makeTodayDate(StartTP.getCurrentHour(),StartTP.getCurrentMinute());
				Date end = makeTodayDate(EndTP.getCurrentHour(),EndTP.getCurrentMinute());
				long duration = end.getTime()-start.getTime();
			//  return TU ausf�llen
				String streamURL = "lol";
				if (isworkTU) {
					streamURL = "";
				} else {
					streamURL = Radio_List.get(radiospinner.getSelectedItemPosition()).url;
				}
				TaskUnit retTU = new TaskUnit(start,duration,streamURL);
			// Intent erstellen und zur�ckgeben und activity beenden.
				Intent backintent = new Intent();
				backintent.putExtra(QuasimodoActivity.TU_NEW_KEY, justnew);
				backintent.putExtra(QuasimodoActivity.TU_OBJECT_KEY, retTU);
				backintent.putExtra(QuasimodoActivity.TU_DELETE_KEY, false);
				setResult(Activity.RESULT_OK, backintent);
				finish();
		} else if (v.getId() == R.id.edit_deletebtn){
			// Delete button, einfach delete key setzen und zur�ckgeben.
				Intent backintent = new Intent();
				backintent.putExtra(QuasimodoActivity.TU_DELETE_KEY, true);
				backintent.putExtra(QuasimodoActivity.TU_NEW_KEY, justnew);
				setResult(Activity.RESULT_OK, backintent);
				finish();
		} else if (v.getId() == R.id.edit_workTU) {
			setToWork();
		} else if (v.getId() == R.id.edit_breakTU) {
			setToBreak();
		}
	}
	
	
	/**
	 * Existiert um effektiv daf�r zu sorgen, dass das Ende des TU nicht vor dem Start liegt.
	 * @author Marco
	 */
	private class TPlistener implements TimePicker.OnTimeChangedListener {
		public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
			Date start = makeTodayDate(StartTP.getCurrentHour(),StartTP.getCurrentMinute());
			Date end = makeTodayDate(EndTP.getCurrentHour(),EndTP.getCurrentMinute());
			if (view.getId() == R.id.TimePicker_Start) {
				// End Timepicker so anpassen das er nach dem Start Time picker liegt.
				if (start.after(end) && semaphore == 0) {
					// Locke die Semaphore
						semaphore = 1;
						Thread trd = new Thread(null, TPTimeout, "SemWait");
				        trd.start();
					end = new Date(start.getTime()+120000);
					EndTP.setCurrentHour(end.getHours());
					EndTP.setCurrentMinute(end.getMinutes());
				}
			} else if (view.getId() == R.id.TimePicker_End) {
				// genau das umgekehrte wie oben.
				if (end.before(start) && semaphore == 0) {
					// Locke die Semaphore
						semaphore = 1;
						Thread trd = new Thread(null, TPTimeout, "SemWait");
				        trd.start();
					start = new Date(end.getTime()-120000);
					StartTP.setCurrentHour(start.getHours());
					StartTP.setCurrentMinute(start.getMinutes());
				}
			}
		}
	};
	
	Runnable TPTimeout = new  Runnable() {
		public void run() {
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {}
			semaphore = 0;
		}
	};
	
	// ------------------------- Menu Funktionalit�t

		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.editmenu, menu);
			return true;
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			switch (item.getItemId()) {
			case R.id.editmenu_delete:
				//delete()
				break;
			case R.id.editmenu_done:
				//done()
				break;
			default:
			}
			return true;
		}
	
	
	/**
	 * Diese Funktionen �bernehmen die Darstellungs�nderungen beim Wechsel zwischen Work und Pausenmodus
	 */
	private void setToBreak(){
		breakbtn.setTextAppearance(this, R.style.BoldBlueText);
		workbtn.setTextAppearance(this, R.style.NormalBlackText);
		radiospinner.setEnabled(true);
		RadioSpinnerTitle.setTextColor(defaultRadioSpinnerTitleColor);
		isworkTU = false;
	}
	
	private void setToWork() {
		workbtn.setTextAppearance(this, R.style.BoldBlueText);
		breakbtn.setTextAppearance(this, R.style.NormalBlackText);
		radiospinner.setEnabled(false);
		RadioSpinnerTitle.setTextColor(Color.GRAY);
		isworkTU = true;
	}
	
	/**
	 * Hilfsfunktion zur erstellung von DateObjekten von Heute aber mit der vorgegebenen Zeit
	 */
	private Date makeTodayDate(int hours, int minutes) {
		Date ret = new Date(System.currentTimeMillis());
		ret.setMinutes(minutes);
		ret.setHours(hours);
		return ret;
	}
	
	
	/**
	 * Returns the StreamName (last part of url)
	 * @param url
	 * @return the last part of the url
	 */
	private String getStreamName(String url) {
		int lastslash = url.lastIndexOf("/");
		if (lastslash >= url.length()-2)
			return "Stream";
		return url.substring(lastslash+1);
	}
}
