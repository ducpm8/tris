package com.web.util;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Properties;

public class MyAuthenticator extends Authenticator {
	public static final String USERNAME_KEY = "userName";
	public static final String PASSWORD_KEY = "password";
	private final PasswordAuthentication authentication;

	public MyAuthenticator(Properties properties) {
	    String userName = properties.getProperty(USERNAME_KEY);
	    String password = properties.getProperty(PASSWORD_KEY);
	    if (userName == null || password == null) {
	        authentication = null;
	    } else {
	        authentication = new PasswordAuthentication(userName, password.toCharArray());
	    }
	}

	protected PasswordAuthentication getPasswordAuthentication() {
	    return authentication;
	}
}
