/**
 * The Source class represents a single java source file. The iterator allows
 * to extract the contents of the source file line by line. It is also
 * possible to return the entire file as one string.
 *
 * @author Jakub Gajewski
 */

package ws.codewash.reader;

import java.util.Iterator;
import java.util.Scanner;

public class Source implements Iterable<String>{
    private String name;
    private String content;

    public Source(String name){
        this.name = name;
    }

    public Source(String name, String content){
        this.name = name;
        this.content = content;
    }

    public String getName(){
        return name;
    }

    public String getContent(){
        return content;
    }

    @Override
    public Iterator<String> iterator() {
        return new SourceIterator();
    }

    private class SourceIterator implements Iterator<String>{
        private Scanner scanner = new Scanner(getContent());

        public boolean hasNext(){
            return scanner.hasNextLine();
        }

        public String next(){
            return scanner.nextLine();
        }
    }
}
