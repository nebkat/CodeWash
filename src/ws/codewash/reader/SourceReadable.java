package ws.codewash.reader;

import java.nio.file.Path;
import java.util.List;

public interface SourceReadable {
	List<Path> getSources();
}
