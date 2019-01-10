package demo.security;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.Scanner;

/**
 * This class digitally signs <dataFileName>, using the the private key in <keyStoreName>,
 * and saves the signature in <sigFileName>. The class VerifySignature is used to
 * verify the file hasn't been changed since the signature was created.
 * Run SignAFile, then run VerifySignature; the result should be "verifies: true".
 * Edit <dataFileName> in some (small) way, then run VerifySignature again;
 * the result should be "verifies: false".
 * 
 * @author john.denny@gmail.com
 *
 */
public class SignAFile {
	public static final String keyStoreName = "/Users/John/java/keystores/johnIdentity2.jks";
	public static final String dataFileName = "/temp/tempLog.txt";
	public static final String sigFileName = "/temp/signature.dat";
	public static final String keyAlias = "john2";

	public static void main(String[] args) throws GeneralSecurityException, IOException {
		new SignAFile();
	}
	public SignAFile() throws GeneralSecurityException, IOException {
		Signature signature = Signature.getInstance("SHA256withRSA");
		PrivateKey privateKey = getPrivateKey(keyStoreName);
		signature.initSign(privateKey);
		FileInputStream fis = new FileInputStream(dataFileName);
		BufferedInputStream bufin = new BufferedInputStream(fis);
		byte[] buffer = new byte[1024];
		int len;
		while ((len = bufin.read(buffer)) >= 0) {
			signature.update(buffer, 0, len);
		};
		bufin.close();
		byte[] signBytes = signature.sign();
		FileOutputStream sigfos = new FileOutputStream(sigFileName);
		sigfos.write(signBytes);
		sigfos.close();
		System.out.println("signature for " + dataFileName +
				" written to " + sigFileName);
	}
	private PrivateKey getPrivateKey(String ksName) throws GeneralSecurityException, IOException {
		Scanner scanner = new Scanner(System.in);
		System.out.println("spass: ");
		String spass = scanner.nextLine();
		System.out.println("kpass: ");
		String kpass = scanner.nextLine();
		scanner.close();

		KeyStore ks = KeyStore.getInstance("JKS");
		FileInputStream ksfis = new FileInputStream(ksName); 
		BufferedInputStream ksbufin = new BufferedInputStream(ksfis);
		ks.load(ksbufin, spass.toCharArray());
		return (PrivateKey) ks.getKey(keyAlias, kpass.toCharArray());
	}

}
