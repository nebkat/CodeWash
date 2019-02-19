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
	public List<Path> getSources() {
		try (FileSystem fs = FileSystems.newFileSystem(mPath, null);) {
			return Files.walk(fs.getPath("/"))
					.filter(Files::isRegularFile)
					.collect(Collectors.toList());
		} catch (IOException e) {
			// TODO: Handle
			e.printStackTrace();

			return null;
		}
	}
}
