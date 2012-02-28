package ch.esmeralda.quasimodo;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SettingsActivity extends Activity implements OnClickListener{

	public final static String SET_IP_KEY = "settingsipkey";
	public final static String SET_PORT_KEY = "settingsportkey";
	
	public final static String DefaultIP = "192.168.1.15";
	public final static int DefaultPORT = 15000;
	
	private Button defaultbtn;
	private Button savebtn;
	private EditText editip;
	private EditText editport;
	
	private SharedPreferences settings;
	private String ip;
	private int port;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        
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
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.set_defaultbtn:
			ip = DefaultIP;
			port = DefaultPORT;
			break;
		case R.id.set_savebtn:
			ip = editip.getText().toString();
			port = Integer.parseInt(editport.getText().toString());
			break;
		default:
		}
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(SET_IP_KEY,ip);
		editor.putInt(SET_PORT_KEY,port);
		editor.commit();
		setResult(Activity.RESULT_OK, null);
		finish();
	}
	
}
