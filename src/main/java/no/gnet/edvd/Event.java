package no.gnet.edvd;

import java.util.List;

import org.joda.time.DateTime;

public abstract class Event extends Persist{
	
	public String name;
	public String description;
	public DateTime betDeadline;
	public DateTime eventStartTime;
	public Player owner; //null for regular events
	public EventType eventType;

	public abstract List<Option> getOptions();
	public abstract Option getOptionByName(String name);
	public String getName() {
		return name;
	}
	public String getDescription() {
		return description;
	}
}
