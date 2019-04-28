package ws.codewash.java;

import ws.codewash.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ws.codewash.java.ParsedSourceTree.dot;

public abstract class CWClassOrInterface extends CWReferenceType implements CWParameterizable, Modifiable {
	private final Class mClass;
	private final CWPackage mPackage;
	private Set<CWType> mInterfaces = new HashSet<>();
	private final String mName;

	private final int mModifiers;

	private CWClassOrInterface mOuterClass;
	private Set<CWClassOrInterface> mInnerClasses = new HashSet<>();

	private List<CWTypeParameter> mTypeParameters = new ArrayList<>();

	private Set<CWInitializer> mInitializers = new HashSet<>();
	private Set<CWConstructor> mConstructors = new HashSet<>();
	private Set<CWMethod> mMethods = new HashSet<>();
	private Set<CWField> mFields = new HashSet<>();

	CWClassOrInterface(Scope enclosingScope, CWPackage _package, int modifiers, String name, List<RawType> interfaces) {
		super(enclosingScope);

		mClass = null;
		mPackage = _package;
		mName = name;

		enclosingScope.addTypeDeclaration(this);

		mModifiers = modifiers;

		// Outer class
		if (enclosingScope instanceof CWClassOrInterface) {
			setOuterClass((CWClassOrInterface) enclosingScope);
		}

		// Interfaces
		for (RawType _interface : interfaces) {
			resolve(new PendingType<>(_interface, this::addInterface));
		}
	}

	CWClassOrInterface(Scope enclosingScope, Class _class) {
		super(enclosingScope);

		mClass = _class;
		// TODO: Package
		mPackage = enclosingScope.getRoot().getOrInitPackage(_class.getPackageName());
		mName = _class.getSimpleName();

		enclosingScope.addTypeDeclaration(this);

		mModifiers = _class.getModifiers();

		// Outer class
		if (enclosingScope instanceof CWClassOrInterface) {
			setOuterClass((CWClassOrInterface) enclosingScope);
		}

		// Interfaces
		for (Class _interface : _class.getInterfaces()) {
		//	resolve(new PendingType<>(_interface.getName(), this::addInterface));
		}

		// TODO: _class.getTypeParameters()
	}

	public static CWClassOrInterface forExternalClass(Scope enclosingScope, Class _class) {
		if (enclosingScope == null) {
			// TODO: "AAAAAAAAA"
		}

		CWClassOrInterface cwClassOrInterface;
		if (_class.isEnum()) {
			cwClassOrInterface = new CWEnum(enclosingScope, _class);
		} else if (_class.isInterface()) {
			cwClassOrInterface = new CWInterface(enclosingScope, _class);
		} else {
			cwClassOrInterface = new CWClass(enclosingScope, _class);
		}

		for (Class declaredClass :_class.getDeclaredClasses()) {
			forExternalClass(cwClassOrInterface, declaredClass);
		}

		return cwClassOrInterface;
	}

	protected abstract int getValidModifiers();

	public void addTypeParameter(CWTypeParameter typeParameter) {
		mTypeParameters.add(typeParameter);
		addTypeDeclaration(typeParameter);
	}

	public void setOuterClass(CWClassOrInterface outerClass) {
		mOuterClass = outerClass;
		outerClass.addInnerClass(this);
	}

	public CWClassOrInterface getOuterClass() {
		return mOuterClass;
	}

	public void addInnerClass(CWClassOrInterface innerClass) {
		mInnerClasses.add(innerClass);
	}

	public Set<CWClassOrInterface> getInnerClasses() {
		return mInnerClasses;
	}

	protected void addInterface(CWType superType) {
		mInterfaces.add(superType);
		if (superType instanceof CWParameterizedType) {
			superType = ((CWParameterizedType) superType).getType();
		}

		if (!(superType instanceof CWInterface)) {
			// TODO:
			throw new IllegalStateException("Attempting to set non-interface " + superType.getName() + " as superinterface of " + getName());
		}

		((CWInterface) superType).addImplementingClass(this);
	}

	public Set<CWType> getInterfaces() {
		return mInterfaces;
	}

	public String getSimpleName() {
		if (mClass != null) return mClass.getSimpleName();
		return mName;
	}

	public String getName() {
		if (mClass != null) return mClass.getName();
		return getHierarchicalName("$");
	}

	public String getCanonicalName() {
		if (mClass != null) return mClass.getCanonicalName();
		return getHierarchicalName(".");
	}

	public String getHierarchicalName(String classDelimiter) {
		if (mOuterClass == null) {
			return dot(mPackage.getName()) + mName;
		}
		return mOuterClass.getHierarchicalName(classDelimiter) + classDelimiter + mName;
	}

	public String getPackageName() {
		return mPackage.getName();
	}

	public boolean isExternal() {
		return mClass != null;
	}

	public Class getJavaClass() {
		return mClass;
	}

	public boolean isInner() {
		return mOuterClass != null;
	}

	public int getModifiers() {
		return mModifiers;
	}

	public void addInitializer(CWInitializer initializer) {
		mInitializers.add(initializer);
	}

	public void addConstructor(CWConstructor constructor) {
		mConstructors.add(constructor);
	}

	public void addMethod(CWMethod method) {
		mMethods.add(method);
	}

	public void addField(CWField field) {
		mFields.add(field);
	}

	public Set<CWInitializer> getInitializers() {
		return mInitializers;
	}

	public Set<CWConstructor> getConstructors() {
		return mConstructors;
	}

	public Set<CWMethod> getMethods() {
		return mMethods;
	}

	public Set<CWField> getFields() {
		return mFields;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append(getModifiersForToString());
		if (this instanceof CWClass) {
			builder.append("class ");
		} else if (this instanceof CWEnum) {
			builder.append("enum ");
		} else if (this instanceof CWInterface) {
			builder.append("interface ");
		}
		builder.append(getSimpleName());
		if (!mTypeParameters.isEmpty()) {
			builder.append("<");
			builder.append(mTypeParameters.stream()
					.map(CWTypeParameter::toString)
					.collect(Collectors.joining(", ")));
			builder.append(">");
		}
		if (this instanceof CWClass) {
			CWClass cwClass = (CWClass) this;
			if (cwClass.getSuperClass() != null && !cwClass.getSuperClass().getName().equals(Object.class.getName())) {
				builder.append(" extends ");
				builder.append(cwClass.getSuperClass().getName());
			}
		}
		if (!mInterfaces.isEmpty()) {
			if (this instanceof CWInterface) {
				builder.append(" extends ");
			} else {
				builder.append(" implements ");
			}
			builder.append(getInterfaces().stream()
					.map(CWType::getName)
					.collect(Collectors.joining(", ")));
		}

		return builder.toString();
	}
}
