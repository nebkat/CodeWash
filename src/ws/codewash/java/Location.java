package ws.codewash.java;

public class Location {
	public final CompilationUnit unit;
	public final int startElement;
	public final int endElement;

	public Location(CompilationUnit unit, int startElement, int endElement) {
		this.unit = unit;
		this.startElement = startElement;
		this.endElement = endElement;
	}
}
