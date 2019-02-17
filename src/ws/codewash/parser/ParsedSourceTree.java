package ws.codewash.parser;

import ws.codewash.java.CWClassOrInterface;
import ws.codewash.java.PendingType;
import ws.codewash.java.PendingTypeReceiver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ParsedSourceTree {
	private Set<Source> mSources = new HashSet<>();
	private ExternalClassLoader mExternalClassLoader = new ExternalClassLoader();

	private Map<String, CWClassOrInterface> mClasses = new HashMap<>();
	private Set<String> mClassDeclarations = new HashSet<>();

	private Set<PendingTypeReceiver> mPendingTypeReceivers = new HashSet<>();

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

	Map<String, CWClassOrInterface> getClasses() {
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

			externalClass = Class.forName(className, false, mExternalClassLoader);
			if (externalClass == null) {
				externalClass = Class.forName(className, false, getClass().getClassLoader());
			}

			addClass(CWClassOrInterface.forExternalClass(externalClass));
			resolvePendingTypes();
		}

		return mClasses.get(className);
	}

	void resolvePendingTypes() {
		// Loop until all pending types have been resolved
		while (mPendingTypeReceivers.size() > 0) {
			// Prevent concurrent modification exception by copying locally
			Set<PendingTypeReceiver> receivers = new HashSet<>(mPendingTypeReceivers);
			mPendingTypeReceivers.clear();

			for (PendingTypeReceiver receiver : receivers) {
				for (PendingType pending : receiver.getPendingTypes()) {
					CWClassOrInterface classOrInterface;
					try {
						classOrInterface = getOrInitCanonicalClass(pending.getCanonicalName());
					} catch (ClassNotFoundException e) {
						// TODO: Handle;
						throw new IllegalStateException("Could not find class " + pending.getCanonicalName(), e);
					}
					pending.accept(classOrInterface);
				}
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

	void addClass(CWClassOrInterface _class) {
		mPendingTypeReceivers.add(_class);
		mClasses.put(_class.getName(), _class);
	}

	public class Source {
		private String mName;
		private String mOriginalContent;
		private String mProcessedContent;

		private String mPackageName = "";

		private int mDeclarationStartLocation;

		private Map<String, String> mTypeImportsSingle = new HashMap<>();
		private List<String> mTypeImportsOnDemand = new ArrayList<>();

		private Map<String, String> mStaticImportsSingle = new HashMap<>();
		private List<String> mStaticImportsOnDemand = new ArrayList<>();

		Source(Path path) throws IOException {
			mName = path.toString();
			mOriginalContent = new String(Files.readAllBytes(path));
			mProcessedContent = mOriginalContent;
			mDeclarationStartLocation = mProcessedContent.length();
		}

		public String getName() {
			return mName;
		}

		public String getOriginalContent() {
			return mOriginalContent;
		}

		String getProcessedContent() {
			return mProcessedContent;
		}

		void setProcessedContent(String content) {
			mProcessedContent = content;
		}

		void addClassDeclaration(String localName) {
			mClassDeclarations.add((mPackageName.isEmpty() ? "" : (mPackageName + ".")) + localName);
		}

		public int getDeclarationStartLocation() {
			return mDeclarationStartLocation;
		}

		void setDeclarationStartLocation(int classStartLocation) {
			mDeclarationStartLocation = Math.min(mDeclarationStartLocation, classStartLocation);
		}

		void addSingleTypeImport(String simpleName, String canonicalName) {
			// Check if simple name is already imported
			if (mTypeImportsSingle.containsKey(simpleName)) {
				// Check if previous import is the same as new import
				if (!mTypeImportsSingle.get(simpleName).equals(canonicalName)) {
					// TODO: Handle
					throw new IllegalStateException("Type import for `" + simpleName + "` is already defined: " + mTypeImportsSingle.get(simpleName));
				} else {
					return;
				}
			}

			mTypeImportsSingle.put(simpleName, canonicalName);
		}

		void addOnDemandTypeImport(String packageName) {
			mTypeImportsOnDemand.add(packageName);
		}

		public String getPackage() {
			return mPackageName;
		}

		void setPackage(String packageName) {
			mPackageName = packageName;
		}

		String resolveType(String type, List<String> enclosingClasses) {
			// TODO: Correct resolution

			// Deal with multipart types (inner classes)
			String[] multiPart = type.split("\\.", 2);
			if (multiPart.length > 1) {
				String rootResolve = resolveType(multiPart[0], enclosingClasses);

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
