package no.gnet.edvd;

import java.util.ArrayList;
import java.util.List;

public class YesNoEvent extends Event {
	public final Option yes = StringOption.YES(this);
	public final Option no = StringOption.NO(this);

	
	public YesNoEvent(String question){
		name=question;
	}
	
	@Override
	public List<Option> getOptions() {
		List<Option> options = new ArrayList<Option>();
		options.add(yes);
		options.add(no);
		return options;
	}

	@Override
	public Option getOptionByName(String name) {
		if(name.equalsIgnoreCase("yes"))
			return yes;
		if(name.equalsIgnoreCase("no"))
			return no;
		return null;
	}

}
