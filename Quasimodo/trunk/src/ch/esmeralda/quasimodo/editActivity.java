package ch.esmeralda.quasimodo;

import java.util.ArrayList;
import java.util.List;

import ch.esmeralda.DataExchange.*;

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

public class editActivity extends Activity implements OnClickListener{
	
	Button donebtn;
	Button deletebtn;
	
	TaskUnit tu = null;
    
	TimePicker StartTimePicker;
	TimePicker EndTimePicker;
    Spinner radiospinner;
    
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
        
        // fill the Radio Station spinner
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
        
        // get Data from Intent and fill Views
        final Bundle extras = getIntent().getExtras();
        int justnew = 0;
        if (extras != null)
        {
        	tu = (TaskUnit) extras.getSerializable(QuasimodoActivity.TU_OBJECT_KEY);
        	justnew = extras.getInt(QuasimodoActivity.TU_NEW_KEY);
        }
        StartTimePicker = (TimePicker) findViewById(R.id.TimePicker_Start);
        EndTimePicker = (TimePicker) findViewById(R.id.TimePicker_End);
        
        // fill with info about task unit!
        
        
        
        // We just added a new TU, grey out delete button.
        if (justnew != 0){
        	TextView title = (TextView) findViewById(R.id.titlebar);
        	title.setText("Add TaskUnit");
        	deletebtn.setTextColor(Color.GRAY);
        	deletebtn.setClickable(false);
        }
        
        // ********************DEBUG!!
        for (RadioStation rs : Radio_List){
        	Log.d("EditActOnCreate", "Radio Station: "+rs.name+" has url: "+rs.url);
        }
	}

	
	/**
	 * OnClick listener function for the 2 buttons.
	 */
	public void onClick(View v) {
		Intent backintent = new Intent();
		if (v.getId() == R.id.edit_donebtn){
			// done button
			// get time from timepicker
			backintent.putExtra(QuasimodoActivity.TU_DELETE_KEY, false);
		} else if (v.getId() == R.id.edit_deletebtn){
			// delete button
			backintent.putExtra(QuasimodoActivity.TU_DELETE_KEY, true);
		}
		setResult(Activity.RESULT_OK, backintent);
		finish();
	}
	

}
