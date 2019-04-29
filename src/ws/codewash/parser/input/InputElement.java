package ws.codewash.parser.input;

import ws.codewash.java.CompilationUnit;
import ws.codewash.java.Locatable;
import ws.codewash.java.Location;
import ws.codewash.parser.tree.LexicalTreeNode;

public abstract class InputElement implements Locatable {
	private CompilationUnit mCompilationUnit;
	private final String mValue;
	private final int mStart;
	private final int mEnd;

	private int mIndex;

	private Location mLocation;

	InputElement(LexicalTreeNode node) {
		mCompilationUnit = node.getCompilationUnit();
		mValue = node.getContent();
		mStart = node.getContentStart();
		mEnd = node.getContentEnd();
	}

	public CompilationUnit getCompilationUnit() {
		return mCompilationUnit;
	}

	public String getRawValue() {
		return mValue;
	}

	public int getStart() {
		return mStart;
	}

	public int getEnd() {
		return mEnd;
	}

	public int getIndex() {
		return mIndex;
	}

	public void setIndex(int index) {
		mIndex = index;
		mLocation = new Location(mCompilationUnit, index, index);
	}

	@Override
	public Location getLocation() {
		return mLocation;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "<" + getRawValue() + ">";
	}
}
