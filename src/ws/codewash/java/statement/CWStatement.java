package ws.codewash.java.statement;

import ws.codewash.java.Locatable;
import ws.codewash.java.Location;
import ws.codewash.java.Scope;
import ws.codewash.parser.tree.SyntacticTreeNode;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class CWStatement extends Scope implements Locatable {
	private final Location mLocation;

	public CWStatement(Location location, Scope enclosingScope) {
		super(enclosingScope);
		mLocation = location;
	}

	@Override
	public Location getLocation() {
		return mLocation;
	}

	public List<CWStatement> getSubStatements() {
		return Collections.emptyList();
	}

	public final <T extends CWStatement> List<T> getClosestDescendants(Class<T> type) {
		if (type.isAssignableFrom(getClass())) {
			return Collections.singletonList((T) this);
		}

		return getSubStatements().stream()
				.filter(Objects::nonNull)
				.map(sub -> sub.getClosestDescendants(type))
				.flatMap(List::stream)
				.collect(Collectors.toList());
	}
}
