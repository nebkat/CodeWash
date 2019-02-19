package ws.codewash.parser;

import ws.codewash.parser.tree.LexicalTreeNode;

public class Token {
	private CompilationUnit mCompilationUnit;
	private final Type mType;
	private final String mValue;
	private final int mStart;
	private final int mEnd;

	Token(CompilationUnit unit, Type type, String value, int start, int end) {
		mCompilationUnit = unit;
		mType = type;
		mValue = value;
		mStart = start;
		mEnd = end;
	}

	Token(LexicalTreeNode node) {
		this(node.getCompilationUnit(), Type.valueOf(node.getSymbolName()), node.getContent(),
				node.getContentStart(), node.getContentEnd());
	}

	public CompilationUnit getCompilationUnit() {
		return mCompilationUnit;
	}

	public String getValue() {
		return mValue;
	}

	public int getStart() {
		return mStart;
	}

	public int getEnd() {
		return mEnd;
	}

	public Type getType() {
		return mType;
	}

	public enum Type {
		Identifier, Keyword, Literal, Separator, Operator
	}

	@Override
	public String toString() {
		return mType.name() + "<" + getValue() + ">";
	}
}
