package ch.esmeralda.quasimodo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import ch.esmeralda.DataExchange.TaskUnit;

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
	 * Ergibt eine Endlosschleife!
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
			// Warte für 1 Sekunde. #################################################################
			semaphore = 0;
		}
	};
	
	
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
}
