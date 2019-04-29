package ws.codewash.java.statement;

import ws.codewash.java.Location;
import ws.codewash.java.Scope;

public abstract class CWControlStatement extends CWStatement {
	public CWControlStatement(Location location, Scope enclosingScope) {
		super(location, enclosingScope);
	}
}
