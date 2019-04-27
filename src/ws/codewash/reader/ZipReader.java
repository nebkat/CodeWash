/**
 * The ZipReader class allows to open zip files and extract java
 * source files as a list of Source objects.
 *
 * @author
 */

package ws.codewash.reader;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class ZipReader implements SourceReadable {
	private Path mPath;
	private FileSystem mFileSystem;

	public ZipReader(Path path) throws IOException {
		mPath = path;
		mFileSystem = FileSystems.newFileSystem(mPath, null);
	}

	@Override
	public FileSystem getFileSystem() {
		return mFileSystem;
	}

	@Override
	public String getRootPath() {
		return "/";
	}

	@Override
	public void close() throws Exception {
		mFileSystem.close();
	}
}
