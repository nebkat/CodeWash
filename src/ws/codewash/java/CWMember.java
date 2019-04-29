package ws.codewash.java;

public interface CWMember extends Modifiable, Locatable{
	CWClassOrInterface getParent();
	String getName();
}
