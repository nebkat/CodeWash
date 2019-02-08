/**
 * The ZipReader class allows to open zip files and extract java
 * source files as a list of Source objects.
 *
 * @author
 */

package ws.codewash.reader;

import java.io.*;
import java.util.*;
import java.util.zip.*;

public class ZipReader implements SourceReadable {
	private File mFile;

    public ZipReader(File file) {
        mFile = file;
    }

    @Override
    public List<Source> getSources() {
		ArrayList<Source> sources = new ArrayList<>();
		try {
			ZipFile zip = new ZipFile(mFile);
			Enumeration<? extends ZipEntry> entries = zip.entries();

			while (entries.hasMoreElements()) {
				StringBuilder fileContent = new StringBuilder();
				ZipEntry entry = entries.nextElement();
				String entryName = entry.getName();

				if (entryName.endsWith(".java")) {
					InputStream stream = zip.getInputStream(entry);
					Scanner scanner = new Scanner(stream);
					while (scanner.hasNextLine()) {
						fileContent.append(scanner.nextLine());
						fileContent.append("\n");
					}
					ArrayList<String> splitEntryName = new ArrayList<>(Arrays.asList(entryName.split("/")));
					String outputFileName = splitEntryName.get(splitEntryName.size() - 1);

					sources.add(new Source(outputFileName, fileContent.toString()));
				}
			}
		} catch (IOException e) {
			return null;
		}
		return sources;
    }
}
