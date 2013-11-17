package no.gnet.edvd;

public enum EventType {
	CENTRALLY_MANAGED(1), PLAYER_MANAGED(2);
	 
	private int type;
 
	private EventType(int t) {
		type = t;
	}
 
	public int getEventType() {
		return type;
	}
}
