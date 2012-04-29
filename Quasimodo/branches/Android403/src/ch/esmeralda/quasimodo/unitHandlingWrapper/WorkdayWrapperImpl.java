package ch.esmeralda.quasimodo.unitHandlingWrapper;

import java.io.NotActiveException;

import ch.esmeralda.DataExchange.*;

import java.util.Date;
import java.util.List;

import android.util.Log;

import ch.esmeralda.quasimodo.net.QClient;

public class WorkdayWrapperImpl implements WorkdayWrapper {

	private QClient connection;
	private List<TaskUnit> listofTU;   	// Pointer to our TaskUnitList or Workday to be displayed (change accordingly)
	private AnsDataPkg lastanswer;				// always the last answer package (unchecked for errors!)
	
	public WorkdayWrapperImpl(QClient client, List<TaskUnit> m_qtus) throws NotActiveException {
		this.listofTU = m_qtus;
		this.connection = client;
		if (!connection.isConnected()) {
			throw new NotActiveException();
		}
		lastanswer = null;
	}
	
	/**
	 *  Gets a full Workday Update from the Server
	 * @return the boolean state: false if the operation failed or true if it was successful
	 */
	public boolean getNewList(){
		QueryDataPkg req = new QueryDataPkg(0, null);
		return sendAndHandleAns(req);
	}
	
	public TaskUnit getActiveUnit() {
		QueryDataPkg req = new QueryDataPkg(3, null);
		boolean state = sendAndHandleAns(req);
		if (!state) return null;
		if (lastanswer.getworkday().size() != 1) {
			ErrIncorrectPackage();
		}
		return lastanswer.getworkday().get(0);
	}

	public boolean addUnit(Date starttime, long duration, String streamURL) {
		TaskUnit senditem = new TaskUnit(starttime, duration, streamURL);
		QueryDataPkg req = new QueryDataPkg(1, senditem);
		return sendAndHandleAns(req);
	}

	public boolean removeUnitByIndex(int index) {
		// does nothing!
		Log.e("WorkdayWrapper","removeUnitByIndex is not implemented!");
		return false;
	}

	public boolean removeUnitByKey(long key) {
		TaskUnit senditem = new TaskUnit(null, 0L);
		senditem.setKey(key);
		QueryDataPkg req = new QueryDataPkg(2, senditem);
		return sendAndHandleAns(req);
	}

	public int size() {
		if (lastanswer != null)
			return lastanswer.getworkday().size();
		return -1;
	}

	public List<TaskUnit> getList() {
		if (lastanswer != null)
			return lastanswer.getworkday();
		return null;
	}

	public boolean reset(int minutes) {
		TaskUnit reqdata = new TaskUnit(null, minutes);
		QueryDataPkg req = new QueryDataPkg(4, reqdata);
		return sendAndHandleAns(req);
	}
	
	/**
	 * This function sends a premade DataPackage and updates the workday to be displayed if the answer is ok and without errors.
	 * @param datapkg The Data Package to be sent.
	 * @return A boolean value if the request succeeded or not.
	 */
	private boolean sendAndHandleAns(QueryDataPkg datapkg) {
		AnsDataPkg ans;
		try{
			ans = (AnsDataPkg)connection.sendRequest(datapkg);
			this.lastanswer = ans;
		} catch (Exception e) {
			ErrIncorrectPackage();
			return false;
		}
		if (ans != null && ans.getaction() == datapkg.getaction()){
			if (ans.getstate()) {
				if (ans.getaction() == 0 || ans.getaction() == 1 || ans.getaction() == 2 || ans.getaction() == 4) {
					Log.d("WorkdayWrapper","I received a Workday of size:"+ans.getworkday().size()+" - here it is:");
//					DispWorkday(ans.getworkday());
					synchronized (listofTU) {
						listofTU.clear();
						listofTU.addAll(ans.getworkday());
					}
				} else if (ans.getaction() == 3){
					// not implemented since not used.
					Log.d("WorkdayWrapper","getActiveUnit is not yet implemented in WorkdayWrapper since we don't ever use it.");
				} else {
					ErrIncorrectPackage();
					return false;
				}
				return true;
			} else {
				System.out.println("Warning: The Server could not complete the request.");
				Log.w("WorkdayWrapper", "The Server could not complete the request.");
				return false;
			}
		}
		ErrIncorrectPackage();
		return false;
	}
	
	/**
	 * Simples loggen und ausgeben eines Fehlers des Antwortpackets.
	 */
	private void ErrIncorrectPackage() {
		System.out.println("Warning: I did not receive a proper Answer Data Package.");
		Log.w("WorkdayWrapper", "I did not receive a proper Answer Data Package.");
	}
	
//	/**
//	 * Debug funktion zum Ausgeben eines ganzen Workdays.
//	 */
//	private void DispWorkday(List<TaskUnit> wd){
//		for (TaskUnit tu : wd) {
//			System.out.println(tu.toString());
//		}
//	}
	
}
