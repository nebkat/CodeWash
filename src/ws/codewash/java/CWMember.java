package ws.codewash.java;

public interface CWMember extends Modifiable {
	CWClassOrInterface getParent();
	String getName();
}
