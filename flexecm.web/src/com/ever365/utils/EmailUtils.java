package com.ever365.utils;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

/**
 * 
 * @author LiuHan
 *
 */
public class EmailUtils {
	
	public static int sendEmail(final String host, final String port, final String user, final String password,final String from, 
			final String subject,final  String msg, final String to) {
		
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				
				Email email = new SimpleEmail();
				email.setHostName(host);
				email.setSmtpPort(Integer.valueOf(port));
				email.setAuthentication(user, password);
				email.setSSLOnConnect(false);
				try {
					email.setFrom(user);
					email.setSubject(subject);
					email.setMsg(msg);
					email.addTo(to);
					email.send();
				} catch (EmailException e) {
					e.printStackTrace();
					throw new RuntimeException();
				}
			}
		});
		t.run();
		return 1;
	}

	public static boolean check(String smtp, String smtpport, String email,
			String smtppass) {
		
		return true;
	}
	
	public static void main(String[] args) {
		try {
			EmailUtils.sendEmail("smtp.126.com", "25", "liuhann@126.com", "overlord123!@#",  "liuhann@126.com",
					"hi", "this is a test", "liuhann@126.com");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
