package ch.esmeralda.notredame.net;

import ch.esmeralda.notredame.unitHandling.WorkdayHandler;

public class NServerFactory {
	public static NServer createNewInstance(WorkdayHandler workdayHandler){
		return new NServerImplEppi(workdayHandler);
	}
}
