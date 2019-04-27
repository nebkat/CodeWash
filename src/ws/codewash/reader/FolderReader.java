package ws.codewash.reader;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class FolderReader implements SourceReadable {
	private Path mPath;

	public FolderReader(Path path) {
		mPath = path;
	}

	@Override
	public FileSystem getFileSystem() {
		return FileSystems.getDefault();
	}

	@Override
	public String getRootPath() {
		return mPath.toString();
	}

	@Override
	public void close() {
		// Can't close default file system
	}
}
