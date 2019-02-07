package ws.codewash.reader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class ZipFileTest {
    public static final String fileName = "C:/Users/Kuba/Downloads/Cluedo-master.zip";

    public static void main(String[] args){
        try{
            ZipReader zipReader = new ZipReader(fileName);
            ArrayList<Source> list = zipReader.sources();

            Scanner scanner = new Scanner(System.in);

            for (Source source : list){
                System.out.println(source.getName());
                System.out.println("-----------------------------------------------------------------------------");
                for (String line : source){
                    System.out.println(line);
                }
                System.out.println("-----------------------------------------------------------------------------");
                System.out.println(source.getName());
                System.out.println("\nPress ENTER to continue\n");
                scanner.nextLine();
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
