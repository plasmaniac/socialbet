package no.gnet.edvd.database.inmemory;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import no.gnet.edvd.Bet;
import no.gnet.edvd.Currency;
import no.gnet.edvd.DataStore;
import no.gnet.edvd.Event;
import no.gnet.edvd.EventType;
import no.gnet.edvd.Option;
import no.gnet.edvd.Player;

public class InMemoryDatastore implements DataStore {

	private Hashtable<Integer, Bet> bets;
	private Hashtable<Integer, Event> events;
	private Hashtable<Integer, Option> options;
	private Hashtable<Integer, Player> players;
	private Hashtable<Integer, Currency> currencies;

	private static int autinc_id = 0;

	public InMemoryDatastore() {
		bets = new Hashtable<Integer, Bet>();
		events = new Hashtable<Integer, Event>();
		options = new Hashtable<Integer, Option>();
		players = new Hashtable<Integer, Player>();
	}

	@Override
	public int placeBet(Bet bet) {
		if (events.get(bet.event.id) == null) {
			throw new IllegalStateException("Event " + bet.event.id
					+ " does not exist");
		}
		bets.put(bet.id, bet);
		return bet.id;
	}

	@Override
	public void placeBets(Bet... bets) {
		for (int i = 0; i < bets.length; i++) {
			placeBet(bets[i]);
		}
	}

	@Override
	public void updateBet(Bet bet) {
		bets.put(bet.id, bet);

	}

	@Override
	public Bet getBet(Bet bet) {
		return bets.get(bet.id);

	}

	@Override
	public Event storeEvent(Event event) {
		events.put(event.id, event);
		return event;
	}

	@Override
	public void updateEvent(Event event) {
		events.put(event.id, event);
	}

	@Override
	public List<Bet> getBetsForEvent(Event event) {
		return _getBets(event.id);
	}

	private List<Bet> _getBets(int eventid) {
		Enumeration<Integer> keys = bets.keys();
		List<Bet> betsForEvent = new ArrayList<Bet>();
		while (keys.hasMoreElements()) {
			Bet bet = bets.get(keys.nextElement());
			if (bet.event.id == eventid) {
				betsForEvent.add(bet);
			}
		}
		return betsForEvent;
	}

	public static int inVMautoInc() {
		autinc_id++;
		return autinc_id;
	}

	@Override
	public List<Bet> getBetsForEvent(int eventid) {
		return _getBets(eventid);
	}

	@Override
	public List<Event> listEvents() {
		ArrayList<Event> eventlist = new ArrayList<Event>();
		eventlist.addAll(events.values());
		return eventlist;
	}

	@Override
	public void insertCurrency(Currency currency) {
		currencies.put(inVMautoInc(), currency);
	}

	@Override
	public Player storePlayer(Player player) {
		Player p = player;
		p.id=inVMautoInc();
		players.put(p.id, p);
		return p;
	}

	@Override
	public boolean playerExists(Player player) {
		Enumeration<Player> enumer = players.elements();
		while (enumer.hasMoreElements()) {
			Player object = (Player) enumer.nextElement();
			if(object.equals(player))
				return true;
		}
		return false;
	}

	public Player getPlayer(Player jimmy) {
		return null;
	}

	@Override
	public boolean currenciesEstablished() {
		return true;
	}

	public List<Option> getOptionsForEvent(int eventid) {
		return null;
	}

	@Override
	public Event getEvent(int iid) {
		return events.get(new Integer(iid));
	}

	@Override
	public Option getOption(int optionid) {
		return options.get(new Integer(optionid));
	}

	@Override
	public boolean eventTypesEstablished() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void insertEventType(EventType centrallyManaged) {
		// TODO Auto-generated method stub
		
	}


}
