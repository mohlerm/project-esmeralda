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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class editActivity extends Activity implements OnClickListener{
	
	Button donebtn;
	Button deletebtn;
	
	TaskUnit tu = null;
    
    EditText nametxt;
    EditText statustxt;
    Spinner radiospinner;
    
    String name = new String("bla");
    String status = new String("bla2");
    
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
        radiospinner = (Spinner) findViewById(R.id.RadioStationSpinner);
        
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
        nametxt = (EditText) findViewById(R.id.editName);
        statustxt = (EditText) findViewById(R.id.editStatus);
        nametxt.setText(name);   // fill with info about task unit!
        statustxt.setText(status);
        
        
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
			name = nametxt.getText().toString();
			status = statustxt.getText().toString();
			backintent.putExtra(QuasimodoActivity.TU_DELETE_KEY, false);
		} else if (v.getId() == R.id.edit_deletebtn){
			// delete button
			backintent.putExtra(QuasimodoActivity.TU_DELETE_KEY, true);
		}
		backintent.putExtra("herpderp2",name);
		backintent.putExtra("herpderp",status);
		setResult(Activity.RESULT_OK, backintent);
		finish();
	}
}
