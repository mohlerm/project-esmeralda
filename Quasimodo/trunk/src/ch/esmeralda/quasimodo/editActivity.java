package ch.esmeralda.quasimodo;

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
	
	String name = new String();
    String status = new String();
    
    EditText nametxt;
    EditText statustxt;
	
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
        	name = extras.getString(AndroidlistviewtestActivity.ORDER_NAME_KEY);
        	status = extras.getString(AndroidlistviewtestActivity.ORDER_STATUS_KEY);
        	justnew = extras.getInt(AndroidlistviewtestActivity.ORDER_NEW_KEY);
        }
        nametxt = (EditText) findViewById(R.id.editName);
        statustxt = (EditText) findViewById(R.id.editStatus);
        nametxt.setText(name);
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
			backintent.putExtra(AndroidlistviewtestActivity.ORDER_DELETE_KEY, false);
		} else if (v.getId() == R.id.edit_deletebtn){
			backintent.putExtra(AndroidlistviewtestActivity.ORDER_DELETE_KEY, true);
		}
		backintent.putExtra(AndroidlistviewtestActivity.ORDER_NAME_KEY,name);
		backintent.putExtra(AndroidlistviewtestActivity.ORDER_STATUS_KEY,status);
		setResult(Activity.RESULT_OK, backintent);
		finish();
	}
}
