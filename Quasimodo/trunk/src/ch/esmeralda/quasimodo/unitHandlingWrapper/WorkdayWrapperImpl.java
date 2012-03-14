package ch.esmeralda.quasimodo.unitHandlingWrapper;

import java.io.NotActiveException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.util.Log;

import ch.esmeralda.quasimodo.net.QClient;

public class WorkdayWrapperImpl implements WorkdayWrapper {

	private QClient connection;
	private ArrayList<TaskUnit> listofTU;   	// Pointer to our TaskUnitList or Workday to be displayed (change accordingly)
	private AnsDataPkg lastanswer;				// always the last answer package (unchecked for errors!)
	
	public WorkdayWrapperImpl(QClient client, ArrayList<TaskUnit> inputlist) throws NotActiveException {
		this.listofTU = inputlist;
		this.connection = client;
		if (!connection.isConnected()) {
			System.err.println("Error, the connection is not set!");
			Log.e("connection to server", "The QClient must be fully connected prior to create a WorkdayWrapper!");
			throw new NotActiveException();
		}
		lastanswer = null;
	}
	
	/**
	 *  Gets a full Workday Update from the Server
	 * @param listofTU insert the List of TaskUnits here to update.  (Exchange with the to be modified workday later)
	 * @return the boolean state: false if the operation failed or true if it was successful
	 */
	public boolean getWorkday(ArrayList<TaskUnit> listofTU){
		QueryDataPkg req = new QueryDataPkg(0, null);
		return sendAndHandleAns(req);
	}
	
	public TaskUnit getActiveUnit() {
		QueryDataPkg req = new QueryDataPkg(3, null);
		boolean state = sendAndHandleAns(req);
		if (!state) return null;
		return lastanswer.getworkday().getList().get(0);
	}

	public void addUnit(Date starttime, long duration, String streamURL) {
		// TODO Auto-generated method stub

	}

	public void removeUnitByIndex(int index) {
		// TODO Auto-generated method stub

	}

	public void removeUnitByKey(int key) {
		// TODO Auto-generated method stub

	}

	public int size() {
		if (lastanswer != null)
			return lastanswer.getworkday().getList().size();
		return -1;
	}

	public List<TaskUnit> getList() {
		if (lastanswer != null)
			return lastanswer.getworkday().getList();
		return null;
	}

	public void reset() {
		// TODO Auto-generated method stub

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
			System.out.println("Warning: I did not receive a proper Answer Data Package.");
			Log.w("WorkdayWrapper", "I did not receive a proper Answer Data Package.");
			return false;
		}
		if (ans != null && ans.getaction() == datapkg.getaction()){
			if (ans.getstate()) {
				listofTU.clear();							// change accordingly to workday!
				listofTU.addAll(ans.getworkday().getList());
				Log.d("WorkdayWrapper","The server completed the request and sent a new workday.");
				return true;
			} else {
				System.out.println("Warning: The Server could not complete the request.");
				Log.w("WorkdayWrapper", "The Server could not complete the request.");
				return false;
			}
		}
		System.out.println("Warning: I did not receive a proper Answer Data Package.");
		Log.w("WorkdayWrapper", "I did not receive a proper Answer Data Package.");
		return false;
	}
	
}
