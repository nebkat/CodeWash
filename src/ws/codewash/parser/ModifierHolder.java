package ws.codewash.parser;

import ws.codewash.java.CWAccessModifier;

public class ModifierHolder {
	private CWAccessModifier mAccessModifier = null;
	private boolean mStatic = false;
	private boolean mFinal = false;
	private boolean mAbstract = false;

	public CWAccessModifier getAccessModifier() {
		return mAccessModifier;
	}

	public void parse(String modifier) {
		switch (modifier) {
			case Parser.Keywords.ABSTRACT:
				mAbstract = true;
				mFinal = false;
				break;
			case Parser.Keywords.FINAL:
				mFinal = true;
				mAbstract = false;
				break;
			case Parser.Keywords.PRIVATE:
				mAccessModifier = CWAccessModifier.PRIVATE;
				break;
			case Parser.Keywords.PROTECTED:
				mAccessModifier = CWAccessModifier.PROTECTED;
				break;
			case Parser.Keywords.PUBLIC:
				mAccessModifier = CWAccessModifier.PUBLIC;
				break;
			case Parser.Keywords.STATIC:
				mStatic = true;
				break;
		}
	}

	public boolean isAbstract() {
		return mAbstract;
	}

	public boolean isFinal() {
		return mFinal;
	}

	public boolean isStatic() {
		return mStatic;
	}

	public void reset() {
		mAccessModifier = null;
		mFinal = mStatic = mAbstract = false;
	}
}
