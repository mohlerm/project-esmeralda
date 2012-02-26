package ch.esmeralda.notredame.unitHandling;

import java.util.Date;
import java.util.List;

public class WorkdayHandlerImpl implements WorkdayHandler {

	private Workday workday;

	public WorkdayHandlerImpl(Workday workday) {
		if (workday==null)
			System.err.println("Workday is null");
		this.workday = workday;
	}
	
	@Override
	/**
	 * Handles incoming requests
	 * @param request a command from a client
	 * @return a response to the given request
	 * 
	 * REQUEST DENOMINATORS (first letter of request string followed by a comma):
	 * 	a	add		- adds the unit specified later in the string
	 * 	c	clear	- clears the whole Workday
	 * 	d	default	- resets the Workday to predefined default value
	 * 	e	edit	- edits the unit specified later in the string
	 * 	p	poll	- polls the server for actual Workday
	 * 	r	remove	- removes the unit specified later in the string
	 */
	public String getResponse(String request) {

		if (request == null)
			System.err.println("Request is null");
		
		// Split request string into substrings
		String[] arguments = request.split(",");
		
		
		// Create request denominator
		char denom = request.charAt(0);
		
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
