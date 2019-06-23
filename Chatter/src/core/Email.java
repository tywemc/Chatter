package core;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

// Class to send Emails
public class Email {
	public static void send (String from, String to) {
		final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
		// Get a Properties object
		Properties props = System.getProperties();
		props.setProperty("mail.smtp.host", "smtp.gmail.com");
		props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
		props.setProperty("mail.smtp.socketFactory.fallback", "false");
		props.setProperty("mail.smtp.port", "465");
		props.setProperty("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.auth", "true");
		props.put("mail.debug", "true");
		props.put("mail.store.protocol", "pop3");
		props.put("mail.transport.protocol", "smtp");
		final String username = "chatterproject486@gmail.com";//
		final String password = "code5353";
		try {
			Session session = Session.getDefaultInstance(props, 
					new Authenticator(){
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}});

			// Create a new message
			Message msg = new MimeMessage(session);

			// Set the FROM and TO fields
			msg.setFrom(new InternetAddress("chatterproject486@gmail.com"));
			msg.setRecipients(Message.RecipientType.TO, 
					InternetAddress.parse(to,false));
			msg.setSubject("Chatter - Someone wants to connect!");
			msg.setText(from + " is tying to reach you.");
			msg.setSentDate(new Date());
			Transport.send(msg);
			System.out.println("Message sent.");
		} catch (MessagingException e){ 
			e.printStackTrace();
		}

	}
}
