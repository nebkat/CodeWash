package ws.codewash.parser;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExternalClassLoader extends ClassLoader {
	private Map<String, byte[]> mClassMap = new HashMap<>();

	void addJarFile(Path path) throws IOException {
		try (FileSystem fs = FileSystems.newFileSystem(path, null);) {
			List<Path> classFiles = Files.walk(fs.getPath("/"))
					.filter(Files::isRegularFile)
					.filter(f -> f.toString().endsWith(".class"))
					.collect(Collectors.toList());
			for (Path p : classFiles) {
				addClassFile(p);
			}
		}
	}

	void addClassFile(Path path) throws IOException {
		String className = path.toString()
				.replaceAll("^[\\\\/]", "")
				.replaceAll("[\\\\/]", ".")
				.replaceAll("\\.class$", "");

		byte[] classContents = Files.readAllBytes(path);
		mClassMap.put(className, classContents);
	}

	@Override
	public Class<?> findClass(String name) {
		byte[] classContents = mClassMap.get(name);
		if (classContents == null) {
			return null;
		}
		return defineClass(name, classContents, 0, classContents.length);
	}
}
