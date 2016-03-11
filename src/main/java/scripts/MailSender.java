package scripts;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.AuthenticationFailedException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailSender {

	public static void main(String[] args) {
		sendMail(args[0], args[1], args[2], args[3], args[4]);
	}

	public static void sendMail(final String username, final String password, String to, String subject, String text) {

		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");

		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Address[] recipients = new Address[] { new InternetAddress(to) };

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username));
			message.setRecipients(Message.RecipientType.TO, recipients);
			message.setSubject(subject);
			message.setText(text);

			Transport.send(message);

		} catch (AuthenticationFailedException e) {
			System.out.println(e.getMessage());
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
}
