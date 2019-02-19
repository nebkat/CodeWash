package ws.codewash.parser.tree;

import ws.codewash.parser.CompilationUnit;
import ws.codewash.parser.grammar.GrammarProduction;
import ws.codewash.parser.grammar.GrammarToken;
import ws.codewash.parser.grammar.MultipleGrammarToken;
import ws.codewash.parser.grammar.OptionalGrammarToken;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractTreeNode<T extends AbstractTreeNode<T>> {
	protected T mParent;
	private List<T> mElements = new ArrayList<>();

	protected GrammarProduction mSymbol;
	protected final GrammarToken mGrammarToken;

	protected final CompilationUnit mRawCompilationUnit;
	private int mContentStart;
	private int mContentEnd;

	protected AbstractTreeNode(CompilationUnit rawCompilationUnit, GrammarToken token) {
		mRawCompilationUnit = rawCompilationUnit;
		mGrammarToken = token;
	}

	public AbstractTreeNode(CompilationUnit rawCompilationUnit, GrammarToken token, int contentStart, int contentEnd) {
		this(rawCompilationUnit, token);
		setContentRange(contentStart, contentEnd);
	}

	protected void setContentRange(int start, int end) {
		mContentStart = start;
		mContentEnd = end;

		if (mContentEnd < mContentStart) {
			throw new IllegalArgumentException("Invalid content range provided for " + mGrammarToken.toString());
		}
	}

	public void setSymbol(GrammarProduction symbol) {
		mSymbol = symbol;
	}

	public int length() {
		return getContentLength();
	}

	public boolean isEmpty() {
		return length() == 0;
	}

	public boolean isPresent() {
		return !isEmpty();
	}

	public boolean isOptional() {
		return mGrammarToken instanceof OptionalGrammarToken;
	}

	public boolean isMultiple() {
		return mGrammarToken instanceof MultipleGrammarToken;
	}

	public int getContentLength() {
		return mContentEnd - mContentStart;
	}

	public int getContentStart() {
		return mContentStart;
	}

	public int getContentEnd() {
		return mContentEnd;
	}

	public GrammarProduction getSymbol() {
		return mSymbol;
	}

	public String getSymbolName() {
		return mSymbol != null ? mSymbol.getSymbol() : null;
	}

	public String getGrammarTokenDescription() {
		return mGrammarToken.toString();
	}

	public String getName() {
		if (mSymbol != null) {
			return mSymbol.getSymbol();
		}

		return mGrammarToken.getName();
	}

	public String getDescription() {
		if (isTerminal()) {
			return "'" + getContent()
					.replace("\t", "\\t")
					.replace("\n","\\n")
					.replace("\r","\\r") + "'";
		}

		return getName();
	}

	public abstract boolean isTerminal();

	public String getContent() {
		return mRawCompilationUnit.getContent().substring(mContentStart, mContentEnd);
	}

	public CompilationUnit getCompilationUnit() {
		return mRawCompilationUnit;
	}

	public T getParent() {
		return mParent;
	}

	public void addChild(T child) {
		mElements.add(child);
		child.mParent = (T) this;
	}

	public void addChildren(Collection<T> children) {
		children.forEach(this::addChild);
	}

	public int getElementCount() {
		return mElements.size();
	}

	public List<T> getAll() {
		return mElements;
	}

	public T get() {
		if (mElements.size() > 1) {
			// TODO:
			throw new IllegalStateException("Accessing multiple element node with get()");
		}

		return mElements.isEmpty() ? null : mElements.get(0);
	}

	public T get(int index) {
		return mElements.get(index);
	}

	public T get(String name) {
		return mElements.stream()
				.filter(n -> n.getName().equals(name))
				.findFirst()
				.orElse(null);
	}

	// TODO:
	public List<T> getListElements() {
		List<T> list = new ArrayList<>();
		list.add(get(0));
		for (T element : get(1).getAll()) {
			list.add(element.get(1));
		}
		return list;
	}

	@Override
	public String toString() {
		return toString(Integer.MAX_VALUE);
	}

	public String toString(int maxDepth) {
		StringBuilder sb = new StringBuilder();
		toString(maxDepth, 0, sb);
		return sb.toString();
	}

	private void toString(int maxDepth, int depth, StringBuilder builder) {
		for (int i = 0; i < depth; ++i) {
			builder.append("\t");
		}

		builder.append(getDescription());
		builder.append("\n");

		if (depth >= maxDepth) {
			String[] contentLines = getContent().split("\n");
			for (String line : contentLines) {
				for (int i = 0; i <= depth; ++i) {
					builder.append("\t");
				}
				builder.append("...");
				builder.append("\t");
				builder.append(line.stripTrailing());
				builder.append("\n");
			}
		} else {
			for (AbstractTreeNode node : getAll()) {
				if (node.length() > 0) {
					node.toString(maxDepth, depth + 1, builder);
				}
			}
		}
	}
}
