package demo.security;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;

public class MyMessageDigest {
	private MessageDigest messageDigest;
	
	public MyMessageDigest() throws GeneralSecurityException, IOException {
		String fileName = "c:/temp/tempLog.txt";
				// "/temp/KnowMeApp4.zip";
		this.messageDigest = MessageDigest.getInstance("SHA-1");
		FileInputStream fis = new FileInputStream(fileName);
		int available;
		byte[] data;
		messageDigest.reset();
		while (true) {
			available = fis.available();
			System.out.println("available=" + available);
			if (available == 0) break;
			data = new byte[available];
			fis.read(data);
			messageDigest.update(data);
		}
		fis.close();
		byte[] hash = messageDigest.digest();
		for (byte b: hash) {
			System.out.print(Integer.toString(b&0xFF, 16) + " ");
		}
		System.out.println();
	}

	public static void main(String[] args) throws GeneralSecurityException, IOException {
		new MyMessageDigest();
	}

}
