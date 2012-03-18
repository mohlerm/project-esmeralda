package ch.esmeralda.quasimodo;

import ch.esmeralda.quasimodo.unitHandlingWrapper.TaskUnit;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class editActivity extends Activity implements OnClickListener{
	
	Button donebtn;
	Button deletebtn;
	
	TaskUnit tu = null;
    
    EditText nametxt;
    EditText statustxt;
    
    String name = new String("bla");
    String status = new String("bla2");
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.editscreen);
        
        donebtn = (Button) findViewById(R.id.edit_donebtn);
        deletebtn = (Button) findViewById(R.id.edit_deletebtn);
        
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
        
        donebtn.setOnClickListener(this);
        deletebtn.setOnClickListener(this);
        
        if (justnew != 0){
        	TextView title = (TextView) findViewById(R.id.titlebar);
        	title.setText("Add TaskUnit");
        	deletebtn.setTextColor(Color.GRAY);
        	deletebtn.setClickable(false);
        }
	}

	public void onClick(View v) {
		Intent backintent = new Intent();
		if (v.getId() == R.id.edit_donebtn){
			name = nametxt.getText().toString();
			status = statustxt.getText().toString();
			backintent.putExtra(QuasimodoActivity.TU_DELETE_KEY, false);
		} else if (v.getId() == R.id.edit_deletebtn){
			backintent.putExtra(QuasimodoActivity.TU_DELETE_KEY, true);
		}
		backintent.putExtra("herpderp2",name);
		backintent.putExtra("herpderp",status);
		setResult(Activity.RESULT_OK, backintent);
		finish();
	}
}
