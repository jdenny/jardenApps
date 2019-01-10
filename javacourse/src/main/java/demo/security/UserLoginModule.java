package demo.security;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

public class UserLoginModule implements LoginModule {
	private Subject subject = null;
	private CallbackHandler callbackHandler = null;
	private boolean succeeded = false;
	private User principal = null;
	private String email = null;
	private User[] users = {
			new User("john", "john@guitar.com", "password"),
			new User("julie", "julie@shop.com", "password"),
			new User("sam", "sam@study.com", "password"),
			new User("joe", "joe@work.com", "password")
	};
	private Map<String, User> userList;
	
	@Override
	public boolean abort() throws LoginException {
		return false;
	}

	@Override
	// implement for custom login module
	public void initialize(Subject subject, CallbackHandler callbackHandler, 
			Map<String, ?> sharedState, Map<String, ?> options) {
		this.userList = new HashMap<>();
		for (User user: users) {
			userList.put(user.getEmail(), user);
		}
		this.subject = subject;
		this.callbackHandler = callbackHandler;

	}

	@Override
	// implement for custom login module
	public boolean login() throws LoginException {
		// Get a user email and password.
		Callback[] callbacks = {
				new NameCallback("Email:"),
				new PasswordCallback("Password:", false)
		};

		
		// Perform the callbacks
		try {
			callbackHandler.handle(callbacks);
		} catch(IOException | UnsupportedCallbackException e) {
			return false;
		}
		
		// Authenticate against our user store.
		// Which user is it?
		String email = ((NameCallback) callbacks[0]).getName();
		User desiredUser = this.userList.get(email);
		if (desiredUser == null) {
			throw new FailedLoginException("Bad email");
		}
		String password = new String(
				((PasswordCallback)callbacks[1]).getPassword());
		this.succeeded = desiredUser.getPassword().equals(password);
		if (succeeded) {
			this.email = email;
		} else {
			throw new FailedLoginException("Bad password");
		}
		return this.succeeded;
	}

	@Override
	// implement for custom login module
	public boolean commit() throws LoginException {
		if (!succeeded) return false;
		this.principal = this.userList.get(this.email);
		if (!subject.getPrincipals().contains(principal)) {
			subject.getPrincipals().add(principal);
		}
		return true;
	}

	@Override
	// implement for custom login module
	public boolean logout() throws LoginException {
		this.subject.getPrincipals().remove(this.principal);
		this.email = null;
		this.succeeded = false;
		this.principal = null;
		return true;
	}
	class User implements Principal {
		// TODO: consolidate all the Person and User classes used on this project!
		private String name;
		private String email;
		private String password;
		public User() {
			
		}
		public User(String name, String email, String password) {
			super();
			this.name = name;
			this.email = email;
			this.password = password;
		}
		@Override
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}
		public String getPassword() {
			return password;
		}
		public void setPassword(String password) {
			this.password = password;
		}
		@Override
		public String toString() {
			return "User [name=" + name + ", email=" + email + "]";
		}
	}
	
}
