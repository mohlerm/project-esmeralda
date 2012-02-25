package ch.esmeralda.quasimodo;

import java.util.ArrayList;
import java.util.Arrays;

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
        
        
        String[] rawcountries = new String[] {
                "Afghanistan", "Albania", "Algeria", "American Samoa", "Andorra",
                "Angola", "Anguilla", "Antarctica", "Antigua and Barbuda", "Argentina",
                "Armenia", "Aruba", "Australia", "Austria", "Azerbaijan",
                "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium",
                "Belize", "Benin", "Bermuda", "Bhutan", "Bolivia",
                "Bosnia and Herzegovina", "Botswana", "Bouvet Island", "Brazil", "British Indian Ocean Territory",
                "British Virgin Islands", "Brunei", "Bulgaria", "Burkina Faso", "Burundi",
                "Cote d'Ivoire", "Cambodia", "Cameroon", "Canada", "Cape Verde"
              };
        
        ArrayList<String> COUNTRIES = new ArrayList<String>(Arrays.asList(rawcountries)); 
        
        OnItemClickListener itemclick = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Toast toast = Toast.makeText(getBaseContext(), (CharSequence) ((ListView)arg0).getItemAtPosition(position), Toast.LENGTH_SHORT);
				toast.show();
			}
        };
        
        OnClickListener buttonclick = new OnClickListener() {
        	@Override
			public void onClick(View v) {
				
			}
        };
        
        ListView lv = (ListView) findViewById(R.id.listView1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, );
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(itemclick);
      
        Button add = (Button) findViewById(R.id.button1);
        add.setOnClickListener(buttonclick);
        
    }
}