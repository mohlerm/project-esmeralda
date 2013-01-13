package ch.esmeralda.quasimodo.radiostation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

public class QFileIO {
	/**
	 * 
	 * @param act 		Activity zu der etwas gelesen werden sollte.
	 * @param liste		Liste mit RadioStation Objekten die aufgefüllt werden soll.
	 * @param filename 	Filename für das lokale File.
	 * @return boolean 	wert ob erfolgreich gelesen wurde.
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
				fos_rl.write("DI Trance\nhttp://pub3.di.fm:80/di_trance\nDI Eurodance\nhttp://pub3.di.fm:80/di_eurodance\n".getBytes());
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
					//Log.d("Settings File IO","Received EOF.");
					break;
				}
				// liste auffï¿½llen.
				if (name) {
					adder = new RadioStation(inline,"");
					name = false;
				} else {
					name = true;
					adder.url = inline;
					liste.add(adder);
				}
				//Log.d("File IO read",inline);
			}
		} catch (Exception e) {
			liste.clear();
			RadioStation errobj = new RadioStation("ERROR","Error reading list!");
			liste.add(errobj);
			Log.e("IO","Cannot Read from the File!");
			return false;
		}
		
		// debug
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
	
	/**
	 * Speichert die Radio Liste in ein lokales auf dem Handy gespeichertes File.
	 * @param liste		Referenz zu der zu speichernden Radio Liste.
	 * @param filename	Name des abzuspeichernden Files.
	 * @param act		Activity zu der der FileOutput erzeugt werden soll.
	 * @return 			boolean wert ob IO aktivität erfolgreich war.
	 */
	
	public static boolean writeRadioList(Activity act, List<RadioStation> liste, String filename) {
		FileOutputStream fos_rl = null;
		//Log.d("Settings IO","Writing Radio list to File");
		try {
			fos_rl = act.openFileOutput(filename,Context.MODE_PRIVATE);  // überschreibt bisherige files!
				BufferedWriter bwout = new BufferedWriter(new OutputStreamWriter(fos_rl));
				//Log.d("Settings IO","RadioList hat: "+Integer.toString(liste.size())+" Einträge");
				for (RadioStation rs : liste) {
					bwout.write(rs.name);
					bwout.newLine();
					bwout.write(rs.url);
					bwout.newLine();
					bwout.flush();
					//Log.d("Settings IO","wrote: "+rs.toString());
				}
			fos_rl.close();
		} catch (Exception e) {
			Log.e("Settings File IO","Strange things happened... (writeRadioList(...))");
			return false;
		}
		return true;
	}
	
}
