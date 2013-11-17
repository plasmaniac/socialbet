package no.gnet.edvd;

public class Option extends Persist{
	public String description;
	public String name;
	public Event event;

	@Override
	public String toString() {
		return "Option [description=" + description + ", name=" + name + ", event=" + event + "]";
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		Option other = (Option) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public String getName() {
		return name;
	}
	
}
