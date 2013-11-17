package no.gnet.edvd;

public class Currency extends Persist{
	public static Currency NOK;
	public static Currency EURO;
	public static Currency VIRTUALCOIN;
	public static Currency BITCOIN;
	
	static{
		NOK= new Currency("NOK");
		NOK.id=1;
		EURO= new Currency("EURO");
		EURO.id=2;
		VIRTUALCOIN= new Currency("VIRTUALCOIN");
		VIRTUALCOIN.id=3;
		BITCOIN= new Currency("BITCOIN");
		BITCOIN.id=4;		
	}
	
	public String name;

	public Currency(String name) {
		super();
		this.name = name;
	}
}
