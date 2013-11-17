package no.gnet.edvd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class MatchEvent extends Event {

	public StringOption hometeam;
	public StringOption awayteam;
	public StringOption draw;
	
	public MatchEvent(String hometeam, String awayteam){
		this.hometeam=new StringOption(hometeam);
		this.awayteam=new StringOption(awayteam);
		draw = new StringOption("draw");
		this.name=hometeam+"-"+awayteam;
		this.description="Match between " + hometeam+"-"+awayteam;
	}
	
	@Override
	public List<Option> getOptions() {
		ArrayList <Option> list = new ArrayList<Option>();
		Collections.addAll(list,hometeam,draw,awayteam);
		return list;
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
	
	

}
