package ws.codewash.http;

import ws.codewash.util.Log;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public class Server implements Runnable {
	private static final String TAG = "HTTP";

	private static final File WEB_ROOT = new File(Paths.get("www").toUri());
	private static final String DEFAULT_FILE = "index.html";
	private static final String FILE_NOT_FOUND = "404.html";
	private static final String LENGTH_REQUIRED = "411.html";
	private static final String METHOD_NOT_SUPPORTED = "501.html";
	private static final String SERVER_NAME = "CodeWash HTTP Server v0.1";
	private static final int PORT = 8080;

	private InputStream stream = null;
	private BufferedReader in = null;
	private PrintWriter out = null;
	private DataInputStream dataIn = null;
	private BufferedOutputStream dataOut = null;
	private Socket client;

	private Server(Socket c) {
		client = c;
	}

	public static void init() {
		try {
			ServerSocket serverConnect = new ServerSocket(PORT);
			Log.i(TAG, "Server started.\n\t\t\tListening for connections on port : " + PORT + " ...\n");

			while (true) {
				Server myServer = new Server(serverConnect.accept());
				Log.d(TAG, "Connection opened. (" + new Date() + ")");
				Thread thread = new Thread(myServer);
				thread.start();
			}
		} catch (IOException e) {
			Log.e(TAG, "Server Connection error : " + e.getMessage());
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
			Log.i(TAG,"INPUT");
			Log.d(TAG, input);
			while (!(input = in.readLine()).equals("")) {
				Log.d(TAG, input);
				requestHead.add(input);
			}
			Log.d(TAG, "------------------\tFinished printing request head\t------------------\n");

			if (!method.equals("GET") && !method.equals("HEAD") && !method.equals("POST")) {
				Log.d(TAG, "501 Not Implemented : " + method + " method.");
				sendHTTP(METHOD_NOT_SUPPORTED);
			} else {
				if (method.equals("GET") || method.equals("HEAD")) {
					if (fileRequested.endsWith("/")) {
						fileRequested += DEFAULT_FILE;
					}

					sendHTTP(fileRequested, method);

					Log.d(TAG, "File " + fileRequested + " of type " + getContentType(fileRequested) + " returned");
				}
				// else method is POST
				else {
					int length = -1;
					for (String line : requestHead) {
						if (line.toLowerCase().contains("content-length")) {
							StringTokenizer token = new StringTokenizer(line);
							token.nextToken();
							length = Integer.parseInt(token.nextToken());
							break;
						}
					}
					if (length == -1)
						sendHTTP(LENGTH_REQUIRED);
					else {
						Log.d(TAG, "All good");
						dataIn = new DataInputStream(new BufferedInputStream(stream));
						Log.d(TAG, "Data input stream created");
						byte[] bytes = new byte[length];
						dataIn.read(bytes, 0, length);
						Log.d(TAG, "File data read");
						String fileName = "test.txt";
						FileOutputStream fileOut = new FileOutputStream(WEB_ROOT.getPath() + "/" + fileName);
						Log.d(TAG, "File Output Stream created");
						fileOut.write(bytes);
						Log.d(TAG, "Bytes written to stream");
						fileOut.close();

						Log.d(TAG, "Stream closed");
						sendHTTP(FILE_NOT_FOUND, "POST");
					}
				}
			}
		} catch (FileNotFoundException notFound) {
			notFound.printStackTrace();
			sendHTTP(FILE_NOT_FOUND);

		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "Server error : " + e);
		} finally {
			try {
				in.close();
				out.close();
				dataOut.close();
				client.close();
			} catch (Exception e) {
				Log.e(TAG, "Error closing stream : " + e.getMessage());
			}

			Log.d(TAG, "Connection closed.\n");
		}
	}

	private byte[] readFileData(File file, int fileLength) {
		byte[] fileData = new byte[fileLength];

		try (FileInputStream fileIn = new FileInputStream(file)) {
			fileIn.read(fileData);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}

		return fileData;
	}

	private String getContentType(String fileRequested) {
		if (fileRequested.endsWith(".htm") || fileRequested.endsWith(".html"))
			return "text/html";
		if (fileRequested.endsWith(".css"))
			return "text/css";
		if (fileRequested.endsWith(".jpg") || fileRequested.endsWith(".jpeg"))
			return "image/jpeg";
		if (fileRequested.endsWith(".zip"))
			return "application/x-zip-compressed";
		return "text/plain";
	}

	private void sendHTTP(String fileRequested, String method, int fileLength) {
		File file = new File(WEB_ROOT, fileRequested);
		int length = fileLength == -1 ? (int) file.length() : fileLength;
		String content = getContentType(fileRequested);

		switch (fileRequested) {
			case FILE_NOT_FOUND:
				out.println("HTTP/1.1 404 File Not Found");
				break;
			case METHOD_NOT_SUPPORTED:
				out.println("HTTP/1.1 501 Not Implemented");
				break;
			case LENGTH_REQUIRED:
				out.println("HTTP/1.1 411 Length Required");
				break;
			default:
				out.println("HTTP/1.1 200 OK");
				break;
		}

		/*
		Log.d(TAG, "Server: " + SERVER_NAME);
		Log.d(TAG, "Date: " + new Date());
		Log.d(TAG, "Content-type: " + content);
		Log.d(TAG, "Content-length: " + length);
		*/

		out.println("Server: " + SERVER_NAME);
		out.println("Date: " + new Date());
		out.println("Content-type: " + content);
		out.println("Content-length: " + length);

		out.println(); // blank line between headers and content, very important !
		out.flush(); // flush character output stream buffer

		if (method.equalsIgnoreCase("GET") || method.equalsIgnoreCase("POST")) {
			byte[] fileData = readFileData(file, length);
			try {
				dataOut.write(fileData, 0, length);
				dataOut.flush();
			} catch (IOException ignored) {

			}
		}
	}

	private void sendHTTP(String fileRequested, String method) {
		sendHTTP(fileRequested, method, -1);
	}

	private void sendHTTP(String fileRequested) {
		sendHTTP(fileRequested, "GET");
	}
}