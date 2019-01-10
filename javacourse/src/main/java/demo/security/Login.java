package demo.security;

import java.security.GeneralSecurityException;

import javax.security.auth.login.LoginContext;

import com.sun.security.auth.callback.DialogCallbackHandler;

public class Login {

	public static void main(String[] args) throws GeneralSecurityException {
		LoginContext loginContext = new LoginContext(
				"Login", new DialogCallbackHandler()); 
		loginContext.login();
		System.out.println("User authenticated as: " + loginContext.getSubject());
	}
}
