package ch.esmeralda.DataExchange;

import java.io.Serializable;
import java.util.List;


public class AnsDataPkg implements Serializable{

	private static final long serialVersionUID = -5951515345498462431L;
	// Control fields:
	private int action;
	private boolean state;
	
	// Data fields for answer:
	private List<TaskUnit> workday;
	
	public AnsDataPkg(int action, boolean state, List<TaskUnit> workday){
		this.action = action;
		this.state = state;
		this.workday = workday;
	}
	
	public int getaction() {
		return this.action;
	}
	public boolean getstate() {
		return this.state;
	}
	public List<TaskUnit> getworkday() {
		return this.workday;
	}
}
