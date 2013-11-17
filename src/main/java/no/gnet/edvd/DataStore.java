package no.gnet.edvd;

import java.util.List;

public interface DataStore {
	
	public int placeBet(Bet bet);
	public void updateBet(Bet bet);
	public Bet getBet(Bet bet);
	public List<Bet> getBetsForEvent(Event event);
	public List<Bet> getBetsForEvent(int eventid);	
	public Event storeEvent(Event event);
	public void updateEvent(Event event);
	public abstract List<Event> listEvents();	
	public void insertCurrency(Currency currency);
	public Player storePlayer(Player player);	
	public boolean playerExists(Player player);
	public abstract Player getPlayer(Player jimmy);
	public abstract void placeBets(Bet... bets);
	public boolean currenciesEstablished();
	public abstract List<Option> getOptionsForEvent(int eventid);
	public Event getEvent(int iid);
	public Option getOption(int optionid);
	public boolean eventTypesEstablished();
	public void insertEventType(EventType centrallyManaged);
	
}
