package jarden.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;

public class HttpMain {

	public static void main(String[] args) throws IOException {
		String urlStr =
				"https://sites.google.com:443/site/amazequiz/home/problems/colores.properties";
		URL url = new URL(urlStr);
		System.out.println(
				"url=" + urlStr +
				"\n  protocol=" + url.getProtocol() +
				"\n  host=" + url.getHost() +
				"\n  port=" + url.getPort() +
				"\n  file=" + url.getFile() +
				"\n  ref=" + url.getRef());
		InetAddress inetAddress = InetAddress.getByName(url.getHost());
		System.out.println(
				"connection from " + urlStr +
				"\n  hostAddress=" + inetAddress.getHostAddress() +
				"\n  hostName=" + inetAddress.getHostName() +
				"\n  localHost=" + InetAddress.getLocalHost());
		String page = MyHttpClient.getPage(urlStr);
		System.out.println(page);
	}

}
