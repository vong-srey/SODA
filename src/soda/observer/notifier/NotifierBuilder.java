package soda.observer.notifier;

import java.util.ArrayList;
import java.util.List;



/**
 * Builder from Builder Pattern which is used to build the notifiers
 * 
 * @author vong vithyea srey
 *
 */
public class NotifierBuilder {
	
	/**
	 * the actual product of the notifiers
	 */
	private List<Notifier> notifiers = new ArrayList<Notifier>();
	
	
	
	/**
	 * add an email notifier to the notifiers product
	 * @param contact : email address of contact person
	 */
	public void setEmailNotifier(String contact){
		if(contact==null || contact.trim().isEmpty()){
			throw new IllegalArgumentException();
		}
		notifiers.add(new EmailNotifier(contact));
	}
	
	
	
	/**
	 * add an sms notifier to the notifiers product
	 * @param contact : phone number that will be recieved the sms
	 */
	public void setSMSNotifier(String contact){
		if(contact==null || contact.trim().isEmpty()){
			throw new IllegalArgumentException();
		}
		notifiers.add(new SMSNotifier(contact));
	}
	
	
	
	/**
	 * Builder Pattern's method which return the actual notifier
	 * @return the final product of list of Notifiers
	 */
	public List<Notifier> getNotifiers(){
		return notifiers;
	}	
}