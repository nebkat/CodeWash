package ws.codewash.java.statement;

import ws.codewash.java.Scope;
import ws.codewash.parser.tree.SyntacticTreeNode;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class CWStatement extends Scope {
	private final SyntacticTreeNode mNode;

	public CWStatement(SyntacticTreeNode node, Scope enclosingScope) {
		super(enclosingScope);
		mNode = node;
	}

	public SyntacticTreeNode getNode() {
		return mNode;
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
