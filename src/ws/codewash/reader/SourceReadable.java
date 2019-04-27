package ws.codewash.reader;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.List;

public interface SourceReadable extends AutoCloseable {
	FileSystem getFileSystem() throws IOException;
	String getRootPath();
}
