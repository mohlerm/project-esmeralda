package ch.esmeralda.notredame.unitHandling;


public class AnsDataPkg {
	// Control fields:
	public int action;
	public boolean state;
	
	// Data fields for answer:
	public Workday workday;
	
	public AnsDataPkg(int action, boolean state, Workday workday){
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
	public Workday getworkday() {
		return this.workday;
	}
}
