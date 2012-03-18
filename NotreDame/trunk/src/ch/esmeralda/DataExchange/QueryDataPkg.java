package ch.esmeralda.DataExchange;

import java.io.Serializable;

/**
 * Used for sending data between quasimodo and notredame
 * action codes:
 * 0  =  get whole workday
 * 1  =  add TaskUnit
 * 2  =  remove TaskUnit
 * 3  =  get active TaskUnit
 * 4  =  reset workday
 * 
 * Just put all nescessary data in the DataTU and it will be sent properly.
 * @author Marco Eppenberger
 */
public class QueryDataPkg implements Serializable{
	private static final long serialVersionUID = -3934629610220252062L;

	// control vars:
	public int action;
	
	// data vars:
	public TaskUnit DataTU;
	
	public QueryDataPkg(int action, TaskUnit DataTU) {
		this.action = action;
		this.DataTU = DataTU;
	}
	
	public int getaction(){
		return this.action;
	}
	
	public TaskUnit getTU(){
		return this.DataTU;
	}
}
