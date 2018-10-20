package jarden.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MyHttpClient {
	private final static int BUFFER_SIZE = 1024;
	
	public static long getLastModified(String urlStr) throws IOException {
		URL url = new URL(urlStr);
		HttpURLConnection httpUrlConnection = (HttpURLConnection)url.openConnection();
		//? httpUrlConnection.setInstanceFollowRedirects(true);
		//? httpUrlConnection.setReadTimeout(5000); // 5 seconds
		//? System.setProperty("http.keepAlive", "false");
		httpUrlConnection.setRequestMethod("HEAD");
		httpUrlConnection.setRequestProperty("Accept-Encoding",
			"identity"); // see https://code.google.com/p/android/issues/detail?id=24672
		httpUrlConnection.connect();
		// int code = httpUrlConnection.getResponseCode();
		long lastModified = httpUrlConnection.getLastModified();
		httpUrlConnection.disconnect();
		return lastModified;
	}
	
	/**
	 * Get the page returned by the named URL.
	 * Uses URLConnection; this is equivalent to:
	 *		get the host, port & filename from the URL;
	 *		open a socket to host, port;
	 *		send "GET " + filename + " HTTP/1.0\n" to socket;
	 *		read the output from the socket.
	 */
	public static String getPage(String urlStr) throws IOException {
		return getPage(urlStr, "UTF-8");
	}
	public static String getPage(String urlStr, String charSetName) throws IOException {
		HttpURLConnection httpUrlConnection = getConnectionFromUrlStr(urlStr);
		String page = getPageFromUrlConnection(httpUrlConnection, charSetName);
		httpUrlConnection.disconnect();
		return page;
	}
	private static String getPageFromUrlConnection(HttpURLConnection httpUrlCon,
			String charSetName) throws IOException {
		InputStream is = httpUrlCon.getInputStream();
		Reader reader = new InputStreamReader(is, charSetName);
		char[] buffer = new char[BUFFER_SIZE];
		int readSize;
		StringBuilder builder = new StringBuilder();
		while (	(readSize = reader.read(buffer, 0, BUFFER_SIZE)) > 0) {
			builder.append(buffer, 0, readSize);
		}
		reader.close();
		return builder.toString();
	}
	private static HttpURLConnection getConnectionFromUrlStr(String urlStr) throws IOException {
		URL url = new URL(urlStr);
		HttpURLConnection httpUrlConnection = (HttpURLConnection)url.openConnection();
		httpUrlConnection.setInstanceFollowRedirects(true);
		httpUrlConnection.setReadTimeout(5000); // 5 seconds
		// see http://stackoverflow.com/questions/1440957/httpurlconnection-getresponsecode-returns-1-on-second-invocation
		System.setProperty("http.keepAlive", "false");
		httpUrlConnection.connect(); // not sure if this is necessary!
		int code = httpUrlConnection.getResponseCode();
		if (code != HttpURLConnection.HTTP_OK) {
			if (code == HttpURLConnection.HTTP_MOVED_TEMP ||
					code == HttpURLConnection.HTTP_MOVED_PERM ||
					code == HttpURLConnection.HTTP_SEE_OTHER) {
				String newUrlStr = httpUrlConnection.getHeaderField("Location");
				URL newUrl = new URL(newUrlStr);
				httpUrlConnection = (HttpURLConnection)newUrl.openConnection();
				httpUrlConnection.connect(); // not sure if this is necessary!
			}
			else throw new IOException("Error code: " + code +
					"; response message: " + httpUrlConnection.getResponseMessage());
		}
		return httpUrlConnection;
	}
	public static ArrayList<String> getPageLines(String urlStr) throws IOException {
		return getPageLines(urlStr, "UTF-8");
	}
	public static ArrayList<String> getPageLines(String urlStr, String charSetName) throws IOException {
		HttpURLConnection httpUrlConnection = getConnectionFromUrlStr(urlStr);
		ArrayList<String> pageLines = new ArrayList<String>();
		InputStream is = httpUrlConnection.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, charSetName));
		String line;
		while ((line = reader.readLine()) != null) {
			pageLines.add(line);
		}
		reader.close();
		httpUrlConnection.disconnect();
		return pageLines;
	}
	public String post(String urlName, String fileName) throws IOException {
		URL url = new URL(urlName);
		File file = new File(fileName);
		if (!file.canRead()) {
			throw new IOException("unable to read file " + fileName);
		}
		int fileLength = (int)file.length();
		HttpURLConnection httpUrlConnection = (HttpURLConnection)url.openConnection();
		httpUrlConnection.setRequestMethod("POST");
		httpUrlConnection.setDoInput(true);
		httpUrlConnection.setDoOutput(true);
		httpUrlConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=12345678");
		// httpUrl.setRequestProperty("Accept-Encoding", "gzip, deflate");
		// httpUrl.setRequestProperty("Content-Type", "text/xml");
		httpUrlConnection.setRequestProperty("Content-Length", "" + fileLength);
		httpUrlConnection.connect();
		FileInputStream fis = new FileInputStream(file);
		byte[] buffer = new byte[fileLength];
		fis.read(buffer);
		OutputStream out = httpUrlConnection.getOutputStream();
		out.write(buffer);
		out.flush();
		fis.close();
		// now read response:
		String page = getPageFromUrlConnection(httpUrlConnection, "UTF-8");
		out.close();
		return page;
	}
}
