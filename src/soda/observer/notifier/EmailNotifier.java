package soda.observer.notifier;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import soda.util.config.ConfigReader;



/**
 * This class is an object that is used to send an alert email to the administrator.
 * The administrator email address is stored in field this.contact.
 * 
 * @author vong vithyea srey
 *
 */
public class EmailNotifier extends Notifier{
	
	/**
	 * Constructor 
	 * @param cnt : This must be a validate email address. the contact of the administrator that will be used to alert when the observed criterias met. 
	 */
	public EmailNotifier(String cnt){
		if(cnt==null || cnt.trim().isEmpty() || !isValidEmailAddress(cnt)){
			throw new IllegalArgumentException();
		}
		
		contact = cnt;
		notifierType = EMAIL;
	}
	

	
	/**
	 * A helper method to validate the given email (to check whether the given email is a validat email address)
	 * this implementation is adapted from: http://stackoverflow.com/questions/624581/what-is-the-best-java-email-address-validation-method
	 * @param email : the email address that need to be valdiated
	 * @return true of the given email is a validate email address. false otherwise
	 */
	public static boolean isValidEmailAddress(String email) {
		boolean result = true;
		try {
			InternetAddress emailAddr = new InternetAddress(email);
			emailAddr.validate();
		} catch (AddressException ex) {
			result = false;
		}
		return result;
	}



	/**
	 * Sending an alert email to the administrator (given by this.contact).
	 * this implementation is adapted from: http://www.tutorialspoint.com/java/java_sending_email.htm
	 * 
	 * @param msg is the message of the system performance that need to be delivered to the administrator
	 */
	@Override
	public void sendNotification(final String msg) {
		appLogger.info("About to send an alert email to the administrator");
		
		/**
		 * this if block for testing purpose only. once deploy need to remove this block
		 */
		if(true){
			System.out.println(msg);
			return;
		}
		
		if(msg==null || msg.trim().isEmpty()){
			throw new IllegalArgumentException();
		}
		
		// Sender's email
		String from = ConfigReader.getProperty("FromAddress");

		// message body
		String alertBody = ConfigReader.getProperty("AlertBody");
		String alertSig = ConfigReader.getProperty("AlertSignature");
		String content = alertBody + msg + alertSig;

		// configuring smtp mail server
		String host = ConfigReader.getProperty("SMTPHost");
		Properties prop = System.getProperties();
		prop.put("mail.smtp.host", host); // Setup mail server

		// Get the default Session object.
		Session session = Session.getDefaultInstance(prop);

		try {
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);
			// Set From: header field of the header.
			message.setFrom(new InternetAddress(from));
			// Set To: header field of the header.
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					contact));
			// Set Subject: header field
			message.setSubject(ConfigReader.getProperty("AlertEmailSubject"));
			// Now set the actual message
			message.setText(content);

			// Send message
			Transport.send(message);
			
			appLogger.info("Alert email sent successfully....");
		} catch (MessagingException mex) {
			appLogger.error("Message can't be sent.", mex);
		}
	}
}
