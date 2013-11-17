package no.gnet.edvd;

public class Bet extends Persist{
	public Bet(Event event, Option option, double amount, Currency currency,Player player) {
		super();
		this.event = event;
		this.option = option;
		this.amount = amount;
		this.currency = currency;
		this.player=player;
	}
	
	public Bet(Event event, Option option, double amount, Currency currency,Player player, int id) {
		super();
		this.event = event;
		this.option = option;
		this.amount = amount;
		this.currency = currency;
		this.id=id;
		this.player=player;		
	}	
	public Event event;
	public Option option;
	public double amount;
	public Currency currency;
	public Player player;

}
