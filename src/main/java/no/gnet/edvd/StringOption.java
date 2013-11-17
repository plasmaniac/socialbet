package no.gnet.edvd;

public class StringOption extends Option {
	
	public static StringOption YES(Event event){
		return new StringOption("Yes");
	}
	
	public static StringOption NO(Event event){
		return new StringOption("No");
	}	
	
	public StringOption(String name) {
		this.name=name;
	}
}
