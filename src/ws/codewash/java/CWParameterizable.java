package ws.codewash.java;

public interface CWParameterizable {
	Scope getEnclosingScope();
	void addTypeParameter(CWTypeParameter typeParameter);
}
