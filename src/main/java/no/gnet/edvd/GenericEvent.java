package no.gnet.edvd;

import java.util.Iterator;
import java.util.List;

public class GenericEvent extends Event {

	private List<Option> options;

	@Override
	public List<Option> getOptions() {
		return options;
	}

	@Override
	public Option getOptionByName(String name) {
		List<Option> options = getOptions();
		for (Iterator<Option> iterator = options.iterator(); iterator.hasNext();) {
			Option option = (Option) iterator.next();
			if(option.name.equals(name))
				return option;
		}
		return null;
	}
	
	
	public void setOptions(List<Option> options){
		this.options=options;
	}

}
