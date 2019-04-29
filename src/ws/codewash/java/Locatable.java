package ws.codewash.java;

public interface Locatable {
	Location getLocation();
	default String getContent() {
		Location location = getLocation();
		return location.unit.getContentRange(location.startElement, location.endElement);
	}
}
