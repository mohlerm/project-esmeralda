package ch.esmeralda.quasimodo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class QuasimodoActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);   
        setContentView(R.layout.main);
        
        // Put other important stuff here liek networking and stuff.
        
        OnItemClickListener itemclick = new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Toast toast = Toast.makeText(getBaseContext(), (CharSequence) ((ListView)arg0).getItemAtPosition(position), Toast.LENGTH_SHORT);
				toast.show();
			}
        };
        
        OnClickListener addclick = new OnClickListener() {
        	public void onClick(View v) {
				addelement();
				}
        };
        
        OnClickListener removeclick = new OnClickListener() {
        	public void onClick(View v) {
				removeelement();
        	}
        };
        
        ListView lv = (ListView) findViewById(R.id.listView1);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,(List<String>)COUNTRIES);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(itemclick);
      
        Button add = (Button) findViewById(R.id.button1);
        add.setOnClickListener(addclick);
        
        Button remove = (Button) findViewById(R.id.button2);
        remove.setOnClickListener(removeclick);
        
    }
    
    private ArrayAdapter<String> adapter;
    
    private int n = 0;
    
    private final String[] rawcountries = new String[] {
            "Afghanistan", "Albania", "Algeria", "American Samoa", "Andorra",
            "Angola", "Anguilla", "Antarctica", "Antigua and Barbuda", "Argentina",
            "Armenia", "Aruba", "Australia", "Austria", "Azerbaijan",
            "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium",
            "Belize", "Benin", "Bermuda", "Bhutan", "Bolivia",
            "Bosnia and Herzegovina", "Botswana", "Bouvet Island", "Brazil", "British Indian Ocean Territory",
            "British Virgin Islands", "Brunei", "Bulgaria", "Burkina Faso", "Burundi",
            "Cote d'Ivoire", "Cambodia", "Cameroon", "Canada", "Cape Verde"
          };
    
    private ArrayList<String> COUNTRIES = new ArrayList<String>(Arrays.asList(rawcountries)); 
    
    
   	private void addelement() {
    	COUNTRIES.add(Integer.toString(n));
    	n++;
    	adapter.notifyDataSetChanged();
    }
   	
   	private void removeelement() {
		COUNTRIES.remove(COUNTRIES.size()-1);
		adapter.notifyDataSetChanged();
	}
}

