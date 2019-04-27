package ws.codewash.http;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import ws.codewash.http.util.ByteSearch;
import ws.codewash.http.util.TokenGenerator;
import ws.codewash.util.Log;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServerHandler implements HttpHandler {
	private static final String TAG = "HTTP";

	private static final String GET = "GET";
	private static final String HEAD = "HEAD";
	private static final String POST = "POST";
	private static final String HEADER_ALLOW = "Allow";
	private static final String HEADER_CONTENT_TYPE = "Content-Type";

	private static final int STATUS_OK = 200;
	private static final int STATUS_NOT_FOUND = 404;
	private static final int STATUS_METHOD_NOT_ALLOWED = 405;


	private static final String ERROR = "ERROR";
	private static final String INCORRECT_TYPE = "INCORRECT_TYPE";

	private static final int PORT = 8080;
	private static final File WEB_ROOT = new File(Paths.get("www\\").toUri());
	private static final String DEFAULT_FILE = "index.html";
	private static final String FILE_NOT_FOUND = "404.html";
	private static final String LENGTH_REQUIRED = "411.html";
	private static final String METHOD_NOT_SUPPORTED = "501.html";
	private static final Charset CHARSET = StandardCharsets.UTF_8;

	private ServerHandler() {
	}

	public static void init() {
		try {
			List<String> pages = new ArrayList<>(
					Arrays.asList("/", "/codesmells/", "/how-to-use/", "/about/")
			);
			HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
			pages.forEach(s -> server.createContext(s, new ServerHandler()));
			server.setExecutor(null);
			server.start();
			Log.i(TAG, "Server live, listening on port: " + PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		String reqMethod = exchange.getRequestMethod();
		Log.i(TAG, "Request type: " + reqMethod);

		if (reqMethod.equalsIgnoreCase(GET)) {
			Log.i(TAG, exchange.getRequestURI().getPath());

			File file = getFile(exchange.getRequestURI().getPath());
			exchange.sendResponseHeaders(STATUS_OK, file.length());
			try (OutputStream os = exchange.getResponseBody()) {
				Files.copy(file.toPath(), os);
			}
		} else if (reqMethod.equalsIgnoreCase(POST)) {
			Log.d(TAG, "RequestHeaders");
			Headers reqHeaders = exchange.getRequestHeaders();
			reqHeaders.forEach((k, v) -> Log.d(TAG, k + ":\t" + v));

			List<String> contentType = reqHeaders.get("Content-type");
			String boundary = "--" + contentType.get(0).substring(contentType.get(0).lastIndexOf("boundary=") + 9);
			String response = parseForm(exchange, boundary);

			exchange.getResponseHeaders().set("content-type", "text/html");
			exchange.sendResponseHeaders(STATUS_OK, response.length());
			exchange.getResponseBody().write(response.getBytes());

			/*
			File file = getFile(exchange.getRequestURI().getPath());
			exchange.sendResponseHeaders(302, 0);
*/
			//exchange.getResponseHeaders().put("Location", Collections.singletonList(exchange.getRequestURI().getPath()));

/*
			SourceReadable sources = new ZipReader(Paths.get("www/files/output.zip"));

			try {
				FileSystem fs = sources.getFileSystem();
				List<Path> files = Files.walk(fs.getPath(sources.getRootPath()))
						.filter(Files::isRegularFile)
						.collect(Collectors.toList());
				Grammar grammar = Grammar.parse(Paths.get("resources/language/java-11.cwls"));
				ParsedSourceTree tree = new Parser(grammar).parse(files);
				List<Report> reports = new Analyzer(tree).analyse();
			} finally {
				try {
					sources.close();
				} catch (Exception e) {
					// Ignore
				}
			}*/

		} else {
			File file = getFile(METHOD_NOT_SUPPORTED);
			exchange.sendResponseHeaders(STATUS_METHOD_NOT_ALLOWED, file.length());
			try (OutputStream os = exchange.getResponseBody()) {
				Files.copy(file.toPath(), os);
			}
		}

		Log.i(TAG, "-----------------\n\n");

	}

	private File getFile(String path) {
		if (!path.matches(".*\\.([a-zA-Z])+"))
			path += "/";

		if (path.contains("#"))
			path = path.replaceFirst("#", "/" + DEFAULT_FILE + "#");

		if (path.endsWith("/"))
			path += DEFAULT_FILE;

		Log.d("FILEFINDER", "www" + path);

		File file = new File(WEB_ROOT.getPath() + path);

		if (!file.exists()) {
			Log.e("FILEFINDER", "NOT FOUND. 404");
			file = new File(WEB_ROOT.getPath() + "\\" + FILE_NOT_FOUND);
			Log.d("FILEFINDER", WEB_ROOT.getPath() + "\\" + FILE_NOT_FOUND);
		}

		return file;
	}

	private String parseForm(HttpExchange exchange, String boundary) {
		final String uploadFile = "name=\"datafile\"";
		final String parseButton = "name=\"parseButton\"";

		DataInputStream d = new DataInputStream(new BufferedInputStream(exchange.getRequestBody()));
		StringBuilder response = new StringBuilder();

		byte[] contentBytes = new byte[0];
		try {
			contentBytes = d.readAllBytes();
		} catch (IOException e) {
			e.printStackTrace();
		}

		int formContent = 0;
		List<byte[]> requestBytes = new ArrayList<>();
		int lastPosition = 0;
		int position;
		while ((position = ByteSearch.indexOf(contentBytes, boundary.getBytes(), formContent)) != -1) {
			if (formContent > 0) {
				byte[] currentBytes = new byte[position - lastPosition];
				System.arraycopy(contentBytes, lastPosition, currentBytes, 0, position - lastPosition);
				requestBytes.add(currentBytes);
			}
			lastPosition = position + boundary.getBytes().length;
			formContent++;
		}

		for (byte[] b : requestBytes) {
			if (ByteSearch.indexOf(b, uploadFile.getBytes(), 0) > -1) {
				String status = parseFile(b, boundary);
				String error = "";
				if (status.equalsIgnoreCase(ERROR)) {
					status = "false";
					error = "Error parsing file..";
				} else if (status.equalsIgnoreCase(INCORRECT_TYPE)) {
					status = "false";
					error = "Incorrect File type.";
				} else
					status = "true";

				response.append("{\"success\": ").append(status);
				response.append(",\"error\": \"").append(error).append("\"}");
			} else {
				Log.i(TAG, "RequestBody:\n" + new String(b));
			}
		}
		return response.toString();
	}

	private String parseFile(byte[] contentBytes, String boundary) {
		final String startString = "\r\n\r\n";
		Log.i(TAG, "Parsing form to file.");

		int fileNameStart = ByteSearch.indexOf(contentBytes, "filename=".getBytes(), 0) + "filename=".getBytes().length;
		byte[] currentBytes = new byte[contentBytes.length - 1 - fileNameStart];
		System.arraycopy(contentBytes, fileNameStart, currentBytes, 0, contentBytes.length - 1 - fileNameStart);
		int fileNameEnd = ByteSearch.indexOf(currentBytes, "\"".getBytes(), 1) - 1;

		byte[] fileNameBytes = new byte[fileNameEnd];
		System.arraycopy(contentBytes, fileNameStart + 1, fileNameBytes, 0, fileNameEnd);

		String fileName = new String(fileNameBytes);
		String fileType = fileName.split("\\.")[fileName.split("\\.").length - 1];

		if (fileType.equalsIgnoreCase("JAR") || fileType.equalsIgnoreCase("ZIP")) {
			String token = TokenGenerator.getNextToken();
			fileName = token + "." + fileType;
			Log.i(TAG, "Saving to: " + WEB_ROOT.getParent() + "\\upload\\" + token + "\\" + fileName);
			int start = 0;
			int length = 0;
			if (ByteSearch.indexOf(contentBytes, startString.getBytes(), 0) > -1) {
				start = ByteSearch.indexOf(contentBytes, startString.getBytes(), 0) + startString.getBytes().length;
				length = ByteSearch.indexOf(contentBytes, boundary.getBytes(), 1) - start - 2;
				if (length < 0) {
					length = contentBytes.length - start - 2;
				}
			}

			Log.d(TAG, "File start: " + start + " length: " + length);
			byte[] fileBytes = new byte[length];
			System.arraycopy(contentBytes, start, fileBytes, 0, length);

			try {
				File dir = new File(WEB_ROOT.getParent() + "\\upload\\" + token + "\\");
				dir.mkdirs();
				FileOutputStream fos = new FileOutputStream(WEB_ROOT.getParent() + "\\upload\\" + token + "\\" + fileName);
				fos.write(fileBytes);
				fos.close();

				Log.i(TAG, "Finished writing to file \"" + fileName + "\". Length: " + fileBytes.length);
			} catch (IOException ignored) {
				Log.e(TAG, "Couldn't write to file.");
				return ERROR;
			}
			return token;
		} else {
			Log.e(TAG, "File type not valid.");
			return INCORRECT_TYPE;
		}
	}
}
