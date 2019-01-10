package jarden.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class MyHttpClient {
	private final static int BUFFER_SIZE = 1024;
	/**
	 * Get the page returned by the named URL.
	 * Uses URLConnection; this is equivalent to:
	 *		get the host, port & filename from the URL;
	 *		open a socket to host, port;
	 *		send "GET " + filename + " HTTP/1.0\n" to socket;
	 *		read the output from the socket.
	 */
	public static String getPage(String urlStr) throws IOException {
		URL url = new URL(urlStr);
		HttpURLConnection httpUrlConnection = (HttpURLConnection)url.openConnection();
		httpUrlConnection.connect();
		String page = getPageFromUrlConnection(httpUrlConnection);
		httpUrlConnection.disconnect();
		return page;
	}
	private static String getPageFromUrlConnection(HttpURLConnection httpUrlCon) throws IOException {
		int code = httpUrlCon.getResponseCode();
		if (code != HttpURLConnection.HTTP_OK) {
			throw new IOException("Error code: " + code +
					"; response message: " + httpUrlCon.getResponseMessage());
		}
		InputStream is = httpUrlCon.getInputStream();
		Reader reader = new InputStreamReader(is, "UTF-8");
		char[] buffer = new char[BUFFER_SIZE];
		int readSize;
		StringBuilder builder = new StringBuilder();
		while (	(readSize = reader.read(buffer, 0, BUFFER_SIZE)) > 0) {
			builder.append(buffer, 0, readSize);
		}
		reader.close();
		return builder.toString();
	}
	public static ArrayList<String> getPageLines(String urlStr) throws IOException {
		URL url = new URL(urlStr);
		HttpURLConnection httpUrlConnection = (HttpURLConnection)url.openConnection();
		httpUrlConnection.connect();
		ArrayList<String> pageLines = getPageLinesFromUrlConnection(httpUrlConnection);
		httpUrlConnection.disconnect();
		return pageLines;
	}
	private static ArrayList<String> getPageLinesFromUrlConnection(HttpURLConnection httpUrlCon) throws IOException {
		int code = httpUrlCon.getResponseCode();
		if (code != HttpURLConnection.HTTP_OK) {
			throw new IOException("Error code: " + code +
					"; response message: " + httpUrlCon.getResponseMessage());
		}
		ArrayList<String> pageLines = new ArrayList<String>();
		InputStream is = httpUrlCon.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		String line;
		while ((line = reader.readLine()) != null) {
			pageLines.add(line);
		}
		reader.close();
		return pageLines;
	}
	public static String post(String urlName, String fileName) throws IOException {
		URL url = new URL(urlName);
		Path path = Paths.get(fileName);
		if (!Files.isReadable(path)) {
			throw new IOException("file not readable: " + fileName);
		}
		int fileLength = (int)Files.size(path);
		HttpURLConnection httpUrl = (HttpURLConnection)url.openConnection();
		httpUrl.setRequestMethod("POST");
		httpUrl.setDoInput(true);
		httpUrl.setDoOutput(true);
		httpUrl.setRequestProperty("Content-Type", "multipart/form-data; boundary=12345678");
		// httpUrl.setRequestProperty("Accept-Encoding", "gzip, deflate");
		// httpUrl.setRequestProperty("Content-Type", "text/xml");
		httpUrl.setRequestProperty("Content-Length", "" + fileLength);
		httpUrl.connect();
		try (InputStream fis = Files.newInputStream(path);
				OutputStream out = httpUrl.getOutputStream()) {
			byte[] buffer = new byte[fileLength];
			fis.read(buffer);
			out.write(buffer);
			out.flush();
			// now read response:
			String page = getPageFromUrlConnection(httpUrl);
			return page;
		}
	}
}
