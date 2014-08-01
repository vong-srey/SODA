package soda.observer.notifier;



/**
 * This class is an object that is used to send an alert sms to the administrator.
 * The administrator sms phone number is stored in field this.contact.
 * 
 * There's no implementation for this class because it is no need for this project.
 * But, we have this class for the demonstration of extensibility of the notification services
 * that SODA can offers
 * 
 * @author vong vithyea srey
 *
 */
public class SMSNotifier extends Notifier{
	
	/**
	 * Constructor 
	 * @param cnt : This must be a validate phone number. the contact of the administrator that will be used to alert when the observed criterias met. 
	 */
	public SMSNotifier(String cnt){
		contact = cnt;
		notifierType = SMS;
	}

	
	
	/**
	 * this message is not implemented because it is not needed for this project.
	 * But, we have this class for the demonstration of extensibility of the notification services
	 * that SODA can offers
	 */
	@Override
	public void sendNotification(String msg) {
		// TODO Auto-generated method stub
		
	}

}
