package no.gnet.edvd.engines;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import no.gnet.edvd.Option;

public class EventOdds {

	Map<Option, Double> theOdds;
	
	public EventOdds(){
		theOdds = new HashMap<Option, Double>();
	}

	public void put(Option option, double d) {
		theOdds.put(option, d);
	}

	public double get(Option option) {
		return theOdds.get(option);
	}

	public Set<Option> keySet() {
		return theOdds.keySet();
	}

	public boolean oddsAvailable() {
		if(theOdds.values()==null || theOdds.values().size()<1){
			System.out.println("odds null");
			return false;
		}
		Iterator<Double> iterator = theOdds.values().iterator();
		while (iterator.hasNext()) {
			Double d = iterator.next();
			if(d.isInfinite() || d.isNaN()){
				System.out.println("nan: " + d);
				return false;
			}
		}
		return true;
	}
	
}
