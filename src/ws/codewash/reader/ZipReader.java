/**
 * The ZipReader class allows to open zip files and extract java
 * source files as a list of Source objects.
 *
 * @author
 */

package ws.codewash.reader;

import java.io.*;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class ZipReader implements SourceReadable {
	private Path mPath;

	public ZipReader(Path path) {
		mPath = path;
	}

	@Override
	public Map<String, String> getSources() {
		Map<String, String> sources = new HashMap<>();

		try (FileSystem fs = FileSystems.newFileSystem(mPath, null)) {
			List<Path> paths = Files.walk(fs.getPath("/"))
					.filter(Files::isRegularFile)
					.collect(Collectors.toList());

			for (Path path : paths) {
				sources.put(path.toString(), new String(Files.readAllBytes(path)));
			}
		} catch (IOException e) {
			// TODO: Handle
			e.printStackTrace();

			return null;
		}

		return sources;
	}
}
