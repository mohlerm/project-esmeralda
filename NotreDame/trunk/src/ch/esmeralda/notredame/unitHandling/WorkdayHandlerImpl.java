package ch.esmeralda.notredame.unitHandling;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * implementation of WorkdayHandler
 * 
 * receives an Object over network from the Android handheld device app, modifies the workday
 * and sends an answer Object back.
 * @author Stefan Mach, Marco Eppenberger
 *
 */
public class WorkdayHandlerImpl implements WorkdayHandler {

	private Workday workday;

	public WorkdayHandlerImpl(Workday workday) {
		if (workday==null)
			System.err.println("Workday is null");
		this.workday = workday;
		System.out.println("System out stream!");
		System.err.println("System error stream!");
	}
	
	@Override
	/**
	 * Handles incoming requests
	 * @param request a command from a client	(QueryDataPkg object)
	 * @return a response to the given request	(AnsDataPkg object)
	 */
	public Object getResponse(Object request) {
			QueryDataPkg req = null;
			AnsDataPkg ans = null;
			try {
				req = (QueryDataPkg) request;
			} catch (Exception e) {
				System.err.println("The client sent a not valid QueryDataPkg!");
			}
			int action = req.getaction();
			TaskUnit data = req.getTU();
			switch (action) {
			case 0:		// get the actual workday
				ans = new AnsDataPkg(action,true,workday.getList());
				break;
			case 1:		// add new tu
				TaskUnit TU = new TaskUnit(data.getStarttime(),data.getDuration(),data.getStreamURL());
				TU.setDescription(data.getDescription());
				TU.setKey(data.getKey());
				workday.addUnit(TU);
				ans = new AnsDataPkg(action,true,workday.getList());
				break;
			case 2:		// remove tu by key
				long key = data.getKey();
				if (key != 0){
					workday.removeUnitByKey((int) key);								// workaround!! Key just should be long type!
					ans = new AnsDataPkg(action,false,workday.getList());
				} else {
					System.err.println("WorkdayHandlerImpl: Key to remove was default 0.");
					ans = new AnsDataPkg(action,true,workday.getList());
				}
				break;
			case 3:		// get active tu
				TaskUnit tu = workday.getActiveUnit(new Date());	// copy old TaskUnit from Workday
				TaskUnit anstu = new TaskUnit(tu.getStarttime(),tu.getDuration(),data.getStreamURL());
				anstu.setDescription(tu.getDescription());
				anstu.setKey(tu.getKey());
				ArrayList<TaskUnit> anslist = new ArrayList<TaskUnit>();	// create list with only one TU item.
				anslist.add(anstu);
				ans = new AnsDataPkg(action,true,(List<TaskUnit>)anslist);
				break;
			case 4:		// reset the workday
				resetworkday();
				ans = new AnsDataPkg(action,true,workday.getList());
				break;
			default:
				ans = new AnsDataPkg(-1,false,null);
				System.err.println("An invalid QueryDAtaPkg was sent.");
				break;
			}
			
			return ans;
	}
	
	/**
	 * Resets the whole workday to the default values.
	 */
	private void resetworkday() {
		
		String DI_TRANCE = "http://u11aw.di.fm:80/di_trance";
		
		workday.reset();
		
		long a = System.currentTimeMillis();
		a = a-a%(1000*3600*24);
		long start = a + 9*3600*1000;
		
		TaskUnit task;
		task = new TaskUnit(new Date(start), 75*60*1000, "");
		task.setDescription("Work1");
		workday.addUnit(task);
		start += 75*60*1000;
		
		task = new TaskUnit(new Date(start), 15*60*1000, DI_TRANCE);		
		task.setDescription("break1");
		workday.addUnit(task);
		start += 15*60*1000;
		
		task = new TaskUnit(new Date(start), 60*60*1000, "");
		task.setDescription("Work2");
		workday.addUnit(task);
		start += 60*60*1000;
		
		task = new TaskUnit(new Date(start), 15*60*1000, DI_TRANCE);		
		task.setDescription("break2");
		workday.addUnit(task);
		start += 15*60*1000;
		
		task = new TaskUnit(new Date(start), 45*60*1000, "");
		task.setDescription("Work3");
		workday.addUnit(task);
		start += 45*60*1000;
		
		task = new TaskUnit(new Date(start), 60*60*1000, DI_TRANCE);		
		task.setDescription("supper");
		workday.addUnit(task);
		start += 60*60*1000;
		
		task = new TaskUnit(new Date(start), 75*60*1000, "");
		task.setDescription("Work1");
		workday.addUnit(task);
		start += 75*60*1000;
		
		task = new TaskUnit(new Date(start), 15*60*1000, DI_TRANCE);		
		task.setDescription("break1");
		workday.addUnit(task);
		start += 15*60*1000;
		
		task = new TaskUnit(new Date(start), 60*60*1000, "");
		task.setDescription("Work2");
		workday.addUnit(task);
		start += 60*60*1000;
		
		task = new TaskUnit(new Date(start), 15*60*1000, DI_TRANCE);		
		task.setDescription("break2");
		workday.addUnit(task);
		start += 15*60*1000;
		
		task = new TaskUnit(new Date(start), 45*60*1000, "");
		task.setDescription("Work3");
		workday.addUnit(task);
		start += 45*60*1000;
		
		task = new TaskUnit(new Date(start), 10*60*1000, DI_TRANCE);		
		task.setDescription("end of day");
		workday.addUnit(task);
	}
	
	

}






/**
old stuff:

public class WorkdayHandlerImpl implements WorkdayHandler {

	private Workday workday;

	public WorkdayHandlerImpl(Workday workday) {
		if (workday==null)
			System.err.println("Workday is null");
		this.workday = workday;
	}
	
	@Override
	public Object getResponse(Object request) {

		if (request == null)
			System.err.println("Request is null");
		
		// Split request string into substrings
		String string = (String) request;
		String[] arguments = string.split(",");
		
		
		// Create request denominator
		char denom = string.charAt(0);
		
		if ((denom == 'a' && arguments.length == 3)||(denom == 'e' && arguments.length == 4))
			arguments[arguments.length] = "";	// takes care of the workshift, so no special cases occur later
		
		boolean fail = false;
		
		// Act according to the denominator
		switch(denom)
		{
			case 'a'	:	adder(new Date(Long.decode(arguments[1])),Long.decode(arguments[2]),arguments[3]); break;
			case 'c'	:	workday.reset(); break;
			case 'd'	:	defaulter(); break;
			case 'e'	:	workday.removeUnitByKey(Integer.decode(arguments[1]));
							adder(new Date(Long.decode(arguments[2])),Long.decode(arguments[3]),arguments[4]); break;
			case 'p'	:	break;
			case 'r'	:	workday.removeUnitByKey(Integer.decode(arguments[1])); break;
			default		:	fail = true; break;
		}

		return requester(fail);
	}
	
	// creates a unit and adds it to the workday
	private void adder(Date starttime, long duration, String streamURL) {
		workday.addUnit(new TaskUnit(starttime, duration, streamURL));
	}
	
	// clears the workday and fills it with default units
	private void defaulter() {
		workday.reset();

		//today at midnight
		long a = System.currentTimeMillis();
		a = a-a%(1000*3600*24);
		
		workday.addUnit(new TaskUnit(new Date(a+9*3600*1000), 75*60*1000, ""));
		workday.addUnit(new TaskUnit(new Date(a+10*3600*1000+15*60*1000), 15*60*1000, "STREAM!"));
		workday.addUnit(new TaskUnit(new Date(a+10*3600*1000+30*60*1000), 60*60*1000, ""));
		workday.addUnit(new TaskUnit(new Date(a+11*3600*1000+30*60*1000), 15*60*1000, "STREAM!"));
		workday.addUnit(new TaskUnit(new Date(a+11*3600*1000+45*60*1000), 45*60*1000, ""));
		workday.addUnit(new TaskUnit(new Date(a+12*3600*1000+30*60*1000), 60*60*1000, "STREAM!"));
		
		workday.addUnit(new TaskUnit(new Date(a+13*3600*1000+30*60*1000), 75*60*1000, ""));
		workday.addUnit(new TaskUnit(new Date(a+14*3600*1000+45*60*1000), 15*60*1000, "STREAM!"));
		workday.addUnit(new TaskUnit(new Date(a+15*3600*1000), 60*60*1000, ""));
		workday.addUnit(new TaskUnit(new Date(a+16*3600*1000), 15*60*1000, "STREAM!"));
		workday.addUnit(new TaskUnit(new Date(a+16*3600*1000+15*60*1000), 45*60*1000, ""));
		workday.addUnit(new TaskUnit(new Date(a+17*3600*1000), 15*60*1000, "STREAM!"));
	}
	
	// requests the list of all units from the workday and reduces it to a string
	private String requester(boolean fail) {
		if (fail==true)
			return "ERROR: String syntax incorrect - denom fail";
		List<TaskUnit> list = workday.getList();
		
		String returnstring = "";
		
		returnstring += list.size() + ",";
		
		for (int i = 0; i<list.size(); i++)
		{
			returnstring += (list.get(i)).getStarttime() + ",";
			returnstring += (list.get(i)).getDuration() + ",";
			returnstring += (list.get(i)).getStreamURL() + ",";
		}
		
		return returnstring;
	}
	

}

*/