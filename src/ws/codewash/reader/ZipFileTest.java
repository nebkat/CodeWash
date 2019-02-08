package ws.codewash.reader;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class ZipFileTest {
	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("File name argument required");
			return;
		}

		ZipReader zipReader = new ZipReader(new File(args[0]));
		List<Source> list = zipReader.getSources();

		if (list == null) {
			System.err.println("Error parsing file");
			return;
		}

		Scanner scanner = new Scanner(System.in);
		for (Source source : list) {
			System.out.println(source.getName());
			System.out.println("-----------------------------------------------------------------------------");
			for (String line : source) {
				System.out.println(line);
			}
			System.out.println("-----------------------------------------------------------------------------");
			System.out.println(source.getName());
			System.out.println("\nPress ENTER to continue\n");
			scanner.nextLine();
		}
	}
}
