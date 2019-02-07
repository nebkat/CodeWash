/**
 * The ZipReader class allows to open zip files and extract java
 * source files as a list of Source objects.
 *
 * @author
 */

package ws.codewash.reader;

import ws.codewash.parser.Parsable;

import java.io.*;
import java.util.Arrays;
import java.util.zip.*;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.Scanner;

public class ZipReader implements Parsable {
    private ZipFile zipFile;

    public ZipReader(String name) throws IOException{
        zipFile = new ZipFile(name);
    }

    /**
     * Extracts all java files from the zip file
     * @return list of java files as Source objects
     * @throws IOException
     */
    public ArrayList<Source> sources() {
        ArrayList<Source> sourcesList = new ArrayList<>();
        try {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                StringBuilder fileContent = new StringBuilder();
                ZipEntry entry = entries.nextElement();
                String entryName = entry.getName();

                if (entryName.endsWith(".java")) {
                    InputStream stream = zipFile.getInputStream(entry);
                    Scanner scanner = new Scanner(stream);
                    while (scanner.hasNextLine()) {
                        fileContent.append(scanner.nextLine());
                        fileContent.append("\n");
                    }
                    ArrayList<String> splitEntryName = new ArrayList<>(Arrays.asList(entryName.split("/")));
                    String outputFileName = splitEntryName.get(splitEntryName.size() - 1);

                    sourcesList.add(new Source(outputFileName, fileContent.toString()));
                }
            }
        } catch (IOException e) {

        }
        return sourcesList;
    }

    public void close() throws IOException{
        zipFile.close();
    }

    @Override
    public ArrayList<Source> getSources() {
        return sources();
    }
}
