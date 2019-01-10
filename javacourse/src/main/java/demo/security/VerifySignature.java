package demo.security;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.util.Scanner;

/**
 * See SignAFile description.
 * 
 * @author john.denny@gmail.com
 *
 */
public class VerifySignature {

	public static void main(String[] args) throws GeneralSecurityException, IOException {
		new VerifySignature();
	}
	public VerifySignature() throws GeneralSecurityException, IOException {
		PublicKey publicKey = getPublicKey(SignAFile.keyStoreName);
		FileInputStream sigfis = new FileInputStream(SignAFile.sigFileName);
		byte[] sigToVerify = new byte[sigfis.available()]; 
		sigfis.read(sigToVerify);
		sigfis.close();

		Signature signature = Signature.getInstance("SHA256withRSA");
		signature.initVerify(publicKey);
		FileInputStream datafis = new FileInputStream(SignAFile.dataFileName);
		BufferedInputStream bufin = new BufferedInputStream(datafis);
		byte[] buffer = new byte[1024];
		int len;
		while (bufin.available() != 0) {
		    len = bufin.read(buffer);
		    signature.update(buffer, 0, len);
		};
		bufin.close();
		boolean verifies = signature.verify(sigToVerify);
		System.out.println("verifies: " + verifies);
	}
	private PublicKey getPublicKey(String ksName) throws GeneralSecurityException, IOException {
		Scanner scanner = new Scanner(System.in);
		System.out.println("spass: ");
		String spass = scanner.nextLine();
		scanner.close();

		KeyStore ks = KeyStore.getInstance("JKS");
		FileInputStream ksfis = new FileInputStream(ksName); 
		BufferedInputStream ksbufin = new BufferedInputStream(ksfis);
		ks.load(ksbufin, spass.toCharArray());
		Certificate certificate = ks.getCertificate(SignAFile.keyAlias);
		return certificate.getPublicKey();
	}

}
