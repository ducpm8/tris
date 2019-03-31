package com.web.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.event.TransportEvent;
import javax.mail.event.TransportListener;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class SendEmail {
	public void sendMail(String opt) throws IOException, URISyntaxException {
		
		ClassLoader classLoader = getClass().getClassLoader();
		//InputStream input = new FileInputStream(new File(classLoader.getResource("tris.properties").getFile()));
		
		//InputStream input = classLoader.getResourceAsStream("tris.properties");

		Properties prop = new Properties();
		//prop.load(input);
		prop.load(new FileInputStream("C:\\tris.properties"));
		
		final String username = prop.getProperty("mailUser");
		final String password = prop.getProperty("mailPass");

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("from-email@gmail.com"));
			
			message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse(prop.getProperty("mailRecipient")));
			message.setSubject(prop.getProperty("mailSubject"));

			// Create a multipar message
	        Multipart multipart = new MimeMultipart();
			// Create the message part
	        BodyPart messageBodyPart = new MimeBodyPart();

	         // Now set the actual message
			SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//dd/MM/yyyy
			Date now = new Date();
			String strDate = sdfDate.format(now);
			String emailBody = "処理日 : " + strDate + "\r\n"; 
			emailBody = emailBody + opt;
	         
	        messageBodyPart.setText(emailBody);
	        multipart.addBodyPart(messageBodyPart);
	         
	         // Send the complete message parts
	         message.setContent(multipart);
	         
//	         Transport t = session.getTransport("smtp");
//			
//			//t.send(message);
//			
//			//Transport transport = new Transport();
//			
//			t.addTransportListener(new TransportInherit());
//			
//			t.send(message);
	         Transport.send(message);

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
}