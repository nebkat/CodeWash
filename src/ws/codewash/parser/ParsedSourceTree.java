package ws.codewash.parser;

import ws.codewash.java.CWArray;
import ws.codewash.java.CWClassOrInterface;
import ws.codewash.java.CWParameterizedType;
import ws.codewash.java.CWPrimitive;
import ws.codewash.java.CWType;
import ws.codewash.java.PendingType;
import ws.codewash.java.TypeResolver;
import ws.codewash.parser.exception.RedeclarationParseException;
import ws.codewash.util.Log;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ParsedSourceTree implements TypeResolver {
	private Set<Source> mSources = new HashSet<>();
	private ExternalClassLoader mExternalClassLoader = new ExternalClassLoader();

	private Map<String, CWType> mTypes = Collections.synchronizedMap(new HashMap<>() {{
		for (CWPrimitive primitive : CWPrimitive.values()) {
			put(primitive.getSimpleName(), primitive);
		}
	}});
	private Map<String, CWClassOrInterface> mClasses = Collections.synchronizedMap(new HashMap<>());
	private Set<String> mClassDeclarations = Collections.synchronizedSet(new HashSet<>());

	private Set<PendingType> mPendingTypes = new HashSet<>();
	private static final String TAG = "PARSEDSOURCETREE";

	ParsedSourceTree(List<Path> paths) throws IOException {
		for (Path path : paths) {
			if (path.toString().endsWith(".java")) {
				mSources.add(new Source(path));
			} else if (path.toString().endsWith(".class")) {
				mExternalClassLoader.addClassFile(path);
			} else if (path.toString().endsWith(".jar")) {
				mExternalClassLoader.addJarFile(path);
			}
		}
	}

	Set<Source> getSources() {
		return mSources;
	}

	public Map<String, CWClassOrInterface> getClasses() {
		return mClasses;
	}

	private CWClassOrInterface getOrInitCanonicalClass(String canonicalName) throws ClassNotFoundException {
		String className = canonicalName;
		do {
			String fullClassName = null;
			try {
				getOrInitClass(className);

				fullClassName = className + canonicalName.substring(className.length()).replace('.', '$');
			} catch (ClassNotFoundException e) {
				// Ignore, go to next step
			}

			if (fullClassName != null) {
				return getOrInitClass(fullClassName);
			}

			className = className.substring(0, Math.max(className.lastIndexOf("."), 0));
		} while (!className.isEmpty());

		throw new ClassNotFoundException();
	}

	private CWClassOrInterface getOrInitClass(String className) throws ClassNotFoundException {
		if (!mClasses.containsKey(className)) {
			Class externalClass;

			try {
				externalClass = Class.forName(className, false, mExternalClassLoader);
			} catch (ClassNotFoundException e) {
				externalClass = Class.forName(className, false, getClass().getClassLoader());
			}

			CWClassOrInterface cwClass = CWClassOrInterface.forExternalClass(this, externalClass);

			Log.d(TAG, "Loaded external class " + cwClass.getName());

			addType(cwClass);
		}

		return mClasses.get(className);
	}

	void resolvePendingTypes() {
		// Loop until all pending types have been resolved
		while (mPendingTypes.size() > 0) {
			// Prevent concurrent modification exception by copying locally
			Set<PendingType> pendingTypes = new HashSet<>(mPendingTypes);
			mPendingTypes.clear();

			for (PendingType pending : pendingTypes) {
				String typeName = pending.getCanonicalName();
				CWType type = resolveImmediate(typeName);

				if (type == null) {
					throw new IllegalStateException("Could not resolve type " + typeName);
				}

				pending.accept(type);
			}
		}
	}

	boolean hasClass(String _class) {
		if (mClasses.containsKey(_class)) {
			return true;
		}

		try {
			Class.forName(_class, false, getClass().getClassLoader());
		} catch (ClassNotFoundException e) {
			return false;
		}
		return true;
	}

	void addType(CWType type) {
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

	@Override
	public void resolve(PendingType pendingType) {
		mPendingTypes.add(pendingType);
	}

	@Override
	public CWType resolveImmediate(String typeName) {
		typeName = typeName.replaceAll("\\s", "");
		if (mTypes.containsKey(typeName)) {
			return mTypes.get(typeName);
		} else if (typeName.endsWith("[]")) {
			CWType arrayType = resolveImmediate(typeName.substring(0, typeName.length() - 2));
			if (arrayType == null) {
				return null;
			}

			CWType type = new CWArray(arrayType);
			addType(type);
			return type;
		} else if (typeName.contains("<") && typeName.contains(">")) {
			CWType genericClass = resolveImmediate(typeName.substring(0, typeName.indexOf("<")));
			String genericParameters = typeName.substring(typeName.indexOf("<") + 1, typeName.lastIndexOf(">"));
			List<CWType> genericTypes = Arrays.stream(genericParameters.split(","))
					.map(this::resolveImmediate)
					.collect(Collectors.toList());

			CWType type = new CWParameterizedType((CWClassOrInterface) genericClass, genericTypes);
			addType(type);
			return type;
		} else {
			try {
				return getOrInitCanonicalClass(typeName);
			} catch (ClassNotFoundException e) {
				// Ignore
			}
		}

		return null;
	}

	public class Source {
		private Path mPath;
		private String mOriginalContent;
		private String mProcessedContent;

		private String mPackageName = "";

		private int mImportStartLocation;
		private int mDeclarationStartLocation;

		private Map<String, String> mTypeImportsSingle = new HashMap<>();
		private List<String> mTypeImportsOnDemand = new ArrayList<>();

		private Map<String, String> mStaticImportsSingle = new HashMap<>();
		private List<String> mStaticImportsOnDemand = new ArrayList<>();

		Source(Path path) throws IOException {
			mPath = path;
			mOriginalContent = new String(Files.readAllBytes(path));
			mProcessedContent = mOriginalContent;
			mDeclarationStartLocation = mProcessedContent.length();
		}

		public String getName() {
			return mPath.toString();
		}

		public String getFileName() {
			return mPath.getFileName().toString();
		}

		public String getOriginalContent() {
			return mOriginalContent;
		}

		public String getProcessedContent() {
			return mProcessedContent;
		}

		void setProcessedContent(String content) {
			mProcessedContent = content;
		}

		void addClassDeclaration(String localName) {
			mClassDeclarations.add((mPackageName.isEmpty() ? "" : (mPackageName + ".")) + localName);
		}

		public int getImportStartLocation() {
			return mImportStartLocation;
		}

		public void setImportStartLocation(int importStartLocation) {
			mImportStartLocation = importStartLocation;
		}

		public int getDeclarationStartLocation() {
			return mDeclarationStartLocation;
		}

		void setDeclarationStartLocation(int classStartLocation) {
			mDeclarationStartLocation = classStartLocation;
		}

		void addSingleTypeImport(String simpleName, String canonicalName, int sourceOffset) {
			// Check if simple name is already imported
			if (mTypeImportsSingle.containsKey(simpleName)) {
				// Check if previous import is the same as new import
				if (!mTypeImportsSingle.get(simpleName).equals(canonicalName)) {
					throw new RedeclarationParseException("Duplicate type import for " + simpleName + ". Previous declaration: " + mTypeImportsSingle.get(simpleName), this, sourceOffset);
				} else {
					return;
				}
			}

			mTypeImportsSingle.put(simpleName, canonicalName);
		}

		void addOnDemandTypeImport(String packageName) {
			mTypeImportsOnDemand.add(packageName);
		}

		void addSingleStaticImport(String simpleName, String canonicalName) {
			mStaticImportsSingle.put(simpleName, canonicalName);
		}

		void addOnDemandStaticImport(String packageName) {
			mStaticImportsOnDemand.add(packageName);
		}

		public String getPackage() {
			return mPackageName;
		}

		void setPackage(String packageName) {
			mPackageName = packageName;
		}

		String resolveFullName(String type, List<String> enclosingClasses) {
			// TODO: Correct resolution

			// Deal with multipart types (inner classes)
			String[] multiPart = type.split("\\.", 2);
			if (multiPart.length > 1) {
				String rootResolve = resolveFullName(multiPart[0], enclosingClasses);

				if (rootResolve == null) {
					return null;
				} else {
					return dot(rootResolve) + multiPart[1];
				}
			}

			// Check for local definitions first (in file)
			if (mClassDeclarations.contains(dot(String.join(".", enclosingClasses)) + type)) {
				return dot(String.join(".", enclosingClasses)) + type;
			}

			// Check for definitions in package with local context
			if (mClassDeclarations.contains(dot(mPackageName) + dot(String.join(".", enclosingClasses)) + type)) {
				return dot(mPackageName) + dot(String.join(".", enclosingClasses)) + type;
			}

			// Check for definitions in package without local context
			if (mClassDeclarations.contains(dot(mPackageName) + type)) {
				return dot(mPackageName) + type;
			}

			// Check single type imports
			if (mTypeImportsSingle.containsKey(type)) {
				return mTypeImportsSingle.get(type);
			}

			// Check for absolute definitions
			if (mClassDeclarations.contains(type)) {
				return type;
			}

			// Check on demand imports
			for (String packageName : mTypeImportsOnDemand) {
				try {
					Class.forName(dot(packageName) + type, false, getClass().getClassLoader());
				} catch (ClassNotFoundException e) {
					continue;
				}
				return packageName + "." + type;
			}

			// Check default on demand imports
			for (String packageName : Parser.DEFAULT_IMPORTS) {
				try {
					Class.forName(dot(packageName) + type, false, getClass().getClassLoader());
				} catch (ClassNotFoundException e) {
					continue;
				}
				return packageName + "." + type;
			}

			return null;
		}
	}

	public static String dot(String string) {
		return string.isEmpty() ? string : string + ".";
	}
}
