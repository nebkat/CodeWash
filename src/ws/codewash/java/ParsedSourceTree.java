package ws.codewash.java;

import ws.codewash.util.Log;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ParsedSourceTree extends Scope {
	private Set<CompilationUnit> mSources = new HashSet<>();
	private ExternalClassLoader mExternalClassLoader = new ExternalClassLoader();

	private Map<String, CWType> mTypes = Collections.synchronizedMap(new HashMap<>() {{
		for (CWPrimitive primitive : CWPrimitive.values()) {
			put(primitive.getSimpleName(), primitive);
		}
	}});
	private Map<String, CWClassOrInterface> mClasses = Collections.synchronizedMap(new HashMap<>());
	private Map<String, CWPackage> mPackages = Collections.synchronizedMap(new HashMap<>());

	private static final String TAG = "ParsedSourceTree";

	public ParsedSourceTree(List<Path> paths) throws IOException {
		for (Path path : paths) {
			if (path.toString().endsWith(".java")) {
				mSources.add(new CompilationUnit(this, path));
			} else if (path.toString().endsWith(".class")) {
				mExternalClassLoader.addClassFile(path);
			} else if (path.toString().endsWith(".jar")) {
				mExternalClassLoader.addJarFile(path);
			}
		}
	}

	public Set<CompilationUnit> getSources() {
		return mSources;
	}

	public Map<String, CWClassOrInterface> getClasses() {
		return mClasses;
	}

	@Override
	public CWType resolveOutwards(RawType type, Scope startScope) {
		// Bounce back
		String packageName = "";
		RawType subType = type;
		for (int i = 0; subType.getIdentifiers().size() > 0; i++) {
			CWType resolvedType = getOrInitPackage(packageName).resolveQualified(subType, startScope);
			if (resolvedType != null) return resolvedType;

			packageName += (i == 0 ? "" : ".") + subType.getFirst().getIdentifier();
			subType = subType.getRemainder();
		}

		return getOrInitPackage("java.lang").resolveQualified(type, startScope);
	}

	CWClassOrInterface getOrInitClass(String className) {
		if (!mClasses.containsKey(className)) {
			Class externalClass;

			try {
				externalClass = Class.forName(className, false, mExternalClassLoader);
			} catch (ClassNotFoundException e) {
				try {
					externalClass = Class.forName(className, false, getClass().getClassLoader());
				} catch (ClassNotFoundException ex) {
					return null;
				}
			}

			CWClassOrInterface cwClass = CWClassOrInterface.forExternalClass(getOrInitPackage(externalClass.getPackageName()), externalClass);
			addType(cwClass);
		}

		return mClasses.get(className);
	}

	public void addType(CWType type) {
		if (mTypes.containsKey(type.getName())) {
			throw new IllegalStateException("Duplicate type declaration");
		}
		mTypes.put(type.getName(), type);

		if (type instanceof CWClassOrInterface) {
			if (mClasses.containsKey(type.getName())) {
				throw new IllegalStateException("Duplicate class declaration");
			}
			mClasses.put(type.getName(), (CWClassOrInterface) type);
		}
	}

	public CWPackage getOrInitPackage(String name) {
		mPackages.putIfAbsent(name, new CWPackage(this, name));
		return mPackages.get(name);
	}

	@Override
	public ParsedSourceTree getRoot() {
		return this;
	}

	public static String dot(String string) {
		return string.isEmpty() ? string : string + ".";
	}
}
