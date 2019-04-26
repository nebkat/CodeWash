import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public class Server implements Runnable{

    private static final File WEB_ROOT = new File("D:/Progs/Java/ServerTest/src/html");
    private static final String DEFAULT_FILE = "index.html";
    private static final String FILE_NOT_FOUND = "404.html";
    private static final String LENGTH_REQUIRED = "411.html";
    private static final String METHOD_NOT_SUPPORTED = "501.html";
    private static final String SERVER_NAME = "Kuba's Spicy Java HTTP Server : 0.1";

    private InputStream stream = null;
    private BufferedReader in = null;
    private PrintWriter out = null;
    private DataInputStream dataIn = null;
    private BufferedOutputStream dataOut = null;

    private static final int PORT = 8080;

    private static final boolean verbose = true;

    private Socket client;

    private Server(Socket c) {
        client = c;
    }

    public static void main(String[] args) {
        try {
            ServerSocket serverConnect = new ServerSocket(PORT);
            System.out.println("Server started.\nListening for connections on port : " + PORT + " ...\n");

            while (true) {
                Server myServer = new Server(serverConnect.accept());

                if (verbose) {
                    System.out.println("Connection opened. (" + new Date() + ")");
                }

                Thread thread = new Thread(myServer);
                thread.start();
            }

        } catch (IOException e) {
            System.err.println("Server Connection error : " + e.getMessage());
        }
    }

    @Override
    public void run() {
        String fileRequested;

        try {
            stream = client.getInputStream();
            in = new BufferedReader(new InputStreamReader(stream));
            out = new PrintWriter(client.getOutputStream());
            dataOut = new BufferedOutputStream(client.getOutputStream());

            List<String> requestHead = new LinkedList<>();
            String input = in.readLine();
            if (input == null)
                return;
            StringTokenizer parse = new StringTokenizer(input);
            String method = parse.nextToken().toUpperCase();
            fileRequested = parse.nextToken().toLowerCase();
            requestHead.add(input);
            System.out.println(input);
            while (!(input = in.readLine()).equals("")){
                System.out.println(input);
                requestHead.add(input);
            }
            System.out.println("------------------\nFinished printing request head");

            if (!method.equals("GET") && !method.equals("HEAD") && !method.equals("POST")) {
                if (verbose) {
                    System.out.println("501 Not Implemented : " + method + " method.");
                }
                sendHTTP(METHOD_NOT_SUPPORTED);
            } else {
                if (method.equals("GET") || method.equals("HEAD")) {
                    if (fileRequested.endsWith("/")) {
                        fileRequested += DEFAULT_FILE;
                    }

                    sendHTTP(fileRequested, method);

                    if (verbose) {
                        System.out.println("File " + fileRequested + " of type " + getContentType(fileRequested) + " returned");
                    }
                }
                // else method is POST
                else {
                    int length = -1;
                    for (String line : requestHead){
                        if (line.toLowerCase().contains("content-length")){
                            StringTokenizer token = new StringTokenizer(line);
                            token.nextToken();
                            length = Integer.parseInt(token.nextToken());
                            break;
                        }
                    }
                    if (length == -1)
                        sendHTTP(LENGTH_REQUIRED);
                    else{
                        System.out.println("All good");
                        dataIn = new DataInputStream(new BufferedInputStream(stream));
                        System.out.println("Data input stream created");
                        byte[] bytes = new byte[length];
                        dataIn.read(bytes, 0, length);
                        System.out.println("File data read");
                        String fileName = "test.zip";
                        FileOutputStream fileOut = new FileOutputStream(WEB_ROOT.getPath() + "/" + fileName);
                        System.out.println("File Output Stream created");
                        fileOut.write(bytes);
                        System.out.println("Bytes written to stream");
                        fileOut.close();
                        System.out.println("Strema closed");
                        sendHTTP(FILE_NOT_FOUND, "POST");
                    }
                }
            }

        } catch (FileNotFoundException notFound) {
            notFound.printStackTrace();
            try {
                sendHTTP(FILE_NOT_FOUND);
            } catch (IOException e) {
                System.err.println("Could not find FILE NOT FOUND page : " + e.getMessage());
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Server error : " + e);
        } finally {
            try {
                in.close();
                out.close();
                dataOut.close();
                client.close();
            } catch (Exception e) {
                System.err.println("Error closing stream : " + e.getMessage());
            }

            if (verbose) {
                System.out.println("Connection closed.\n");
            }
        }
    }

    private byte[] readFileData(File file, int fileLength) throws IOException {
        FileInputStream fileIn = null;
        byte[] fileData = new byte[fileLength];

        try {
            fileIn = new FileInputStream(file);
            fileIn.read(fileData);
        } finally {
            if (fileIn != null)
                fileIn.close();
        }

        return fileData;
    }

    private String getContentType(String fileRequested) {
        if (fileRequested.endsWith(".htm")  ||  fileRequested.endsWith(".html"))
            return "text/html";
        if (fileRequested.endsWith(".css"))
            return "text/css";
        if (fileRequested.endsWith(".jpg") || fileRequested.endsWith(".jpeg"))
            return "image/jpeg";
        if (fileRequested.endsWith(".zip"))
            return "application/x-zip-compressed";
        return "text/plain";
    }

    private void sendHTTP(String fileRequested, String method, int fileLength) throws IOException{
        File file = new File(WEB_ROOT, fileRequested);
        int length = fileLength == -1 ? (int)file.length() : fileLength;
        String content = getContentType(fileRequested);

        if (fileRequested.equals(FILE_NOT_FOUND))
            out.println("HTTP/1.1 404 File Not Found");
        else if (fileRequested.equals(METHOD_NOT_SUPPORTED))
            out.println("HTTP/1.1 501 Not Implemented");
        else if (fileRequested.equals(LENGTH_REQUIRED))
            out.println("HTTP/1.1 411 Length Required");
        else
            out.println("HTTP/1.1 200 OK");
        out.println("Server: " + SERVER_NAME);
        System.out.println("Server: " + SERVER_NAME);
        out.println("Date: " + new Date());
        System.out.println("Date: " + new Date());
        out.println("Content-type: " + content);
        System.out.println("Content-type: " + content);
        out.println("Content-length: " + length);
        System.out.println("Content-length: " + length);
        out.println(); // blank line between headers and content, very important !
        System.out.println();
        out.flush(); // flush character output stream buffer

        if (method.equals("GET")) {
            byte[] fileData = readFileData(file, length);
            dataOut.write(fileData, 0, length);
            dataOut.flush();
        }
        else if (method.equals("POST")){
            byte[] fileData = readFileData(file, length);
            dataOut.write(fileData, 0, length);
            dataOut.flush();
        }
    }

    private void sendHTTP(String fileRequested, String method) throws IOException{
        sendHTTP(fileRequested, method, -1);
    }

    private void sendHTTP(String fileRequested) throws IOException{
        sendHTTP(fileRequested, "GET");
    }
}