package ch.esmeralda.quasimodo;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

public class QFileIO {
	/**
	 * F�llt die RadioListe mit statischen werten.
	 * Eventuell sp�ter noch dynamisch machen.
	 */
	public static boolean loadRadioList(Activity act,List<RadioStation> liste,String filename) {
		
		liste.clear();
		FileInputStream fis_rl = null;
		FileOutputStream fos_rl = null;
		try {
			fis_rl = act.openFileInput(filename);
		} catch (FileNotFoundException e) {
			Log.d("Settings File IO","Did not found a settings file, creating a new one...");
			try {
				// Macht eine neue Datei wenn die App das erste Mal gestartet wurde.
				fos_rl = act.openFileOutput(filename,Context.MODE_PRIVATE);
				fos_rl.write("DI Trance\nhttp://u11aw.di.fm:80/di_trance\nDI Eurodance\nhttp://u11aw.di.fm:80/di_eurodance\n".getBytes());
				fos_rl.close();
				fis_rl = act.openFileInput(filename);
			} catch (IOException e1) {
				Log.e("Settings File IO","Massive Failure with file handling!");
				e1.printStackTrace();
				return false;
			}
		}
		BufferedReader brin = new BufferedReader(new InputStreamReader(fis_rl));
		String inline;
		try {
			RadioStation adder = null; 
			boolean name = true;
			while (true) 
			{
				inline = brin.readLine(); // lesen
				// abbruch bed.
				if (inline == null) {
					Log.d("Settings File IO","Received EOF.");
					break;
				}
				// liste auff�llen.
				if (name) {
					adder = new RadioStation(inline,"");
					name = false;
				} else {
					name = true;
					adder.url = inline;
					liste.add(adder);
				}
				Log.d("File IO read",inline);
			}
		} catch (Exception e) {
			Log.e("IO","Cannot Read from the File!");
			return false;
		}
		
		if (liste.isEmpty()){
			Log.e("File IO","read Radio List was empty! (Should never happen!)");
			return false;
		}
		
		// present for debug reasons
//		if (liste.get(liste.size()-1).name == null || liste.get(liste.size()-1).url == null) {
//			liste.remove(liste.size()-1);
//			Log.e("File IO","Massive Fail, File is not propper formatted!");
//		}
		
		return true;
	}
	
}
