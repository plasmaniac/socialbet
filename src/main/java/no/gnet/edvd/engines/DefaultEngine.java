package no.gnet.edvd.engines;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import no.gnet.edvd.Bet;
import no.gnet.edvd.DataStore;
import no.gnet.edvd.Event;
import no.gnet.edvd.Option;

import org.springframework.stereotype.Component;

@Component
public class DefaultEngine {
	
	public EventOdds getOddsForEvent(Event event, DataStore ds){
		List <Bet> bets = ds.getBetsForEvent(event.id);
		double totalbet = getTotalBet(bets);
		List<Option> options = event.getOptions();
		EventOdds oddsmap = new EventOdds();
		if(options==null || options.size()==0){
			System.out.println("Warning, event with no options: " + event.name);
			return oddsmap;
		}
		for (Iterator<Option> iterator = options.iterator(); iterator.hasNext();) {
			Option option =  iterator.next();
			oddsmap.put(option, totalbet / getTotalForOption(bets, option));
		}
		return oddsmap;
	}

	private double getTotalForOption(List<Bet> bets, Option option) {
		double tot=0.0D;
		for (Iterator<Bet> iterator = bets.iterator(); iterator.hasNext();) {
			Bet bet = iterator.next();
			if(bet.option.equals(option))
				tot+=bet.amount;
		}
		return tot;
	}

	private double getTotalBet(List <Bet> bets) {
		double tot=0.0D;
		for (Iterator<Bet> iterator = bets.iterator(); iterator.hasNext();) {
			tot+=iterator.next().amount;
		}
		return tot;
	}

}
