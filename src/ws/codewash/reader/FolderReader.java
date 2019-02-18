package ws.codewash.reader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class FolderReader implements SourceReadable {
	private Path mPath;

	public FolderReader(Path path) {
		mPath = path;
	}

	@Override
	public List<Path> getSources() {
		try {
			return Files.walk(mPath)
					.filter(Files::isRegularFile)
					.collect(Collectors.toList());
		} catch (IOException e) {
			// TODO: Handle
			e.printStackTrace();

			return null;
		}
	}
}
