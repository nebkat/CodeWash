package ws.codewash.reader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FolderReader implements SourceReadable {
	private Path mPath;

	public FolderReader(Path path) {
		mPath = path;
	}

	@Override
	public Map<String, String> getSources() {
		Map<String, String> sources = new HashMap<>();

		try {
			List<Path> paths = Files.walk(mPath)
					.filter(Files::isRegularFile)
					.collect(Collectors.toList());

			for (Path path : paths) {
				sources.put(mPath.relativize(path).toString(), new String(Files.readAllBytes(path)));
			}
		} catch (IOException e) {
			// TODO: Handle
			e.printStackTrace();

			return null;
		}

		return sources;
	}
}
