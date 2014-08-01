package soda.observer.notifier;

import org.apache.log4j.Logger;

import soda.util.logger.LoggerBuilder;



/**
 * This class is an object that is used to send an alert to the administrator.
 * The type of alert (email, sms or call) depends on its concrete implementation.
 * The contact of the person who will be used to send an alert depends on its concrete implementation too.
 * 
 * @author vong vithyea srey
 *
 */
public abstract class Notifier {
	
	/**
	 * The contact of the person who will be used to send an alert depends on its concrete implementation too.
	 */
	protected String contact = "";
	
	
	
	/**
	 * field that used to tell what is the type of the concrete implementation of the notifier (email or sms)
	 */
	protected String notifierType=null;
	
	
	
	/**
	 * used to tell notifier type is email
	 */
	protected static final String EMAIL = "email";
	
	
	
	/**
	 * used to tell notifier type is sms
	 */
	protected static final String SMS = "sms";
	
	
	
	/**
	 * used to log this SODA program's performances.
	 */
	protected Logger appLogger = LoggerBuilder.getAppLogger();
	
	
	
	/**
	 * setter for this.contact
	 * @param cnt : new contact
	 */
	public void setContact(String cnt){
		if(cnt==null || cnt.trim().isEmpty()){
			throw new IllegalArgumentException();
		}
		
		contact = cnt;
	}
	
	
	
	/**
	 * getter for this.contact
	 * @return contact
	 */
	public String getContact(){
		return contact;
	}
	
	
	
	/**
	 * Builder Pattern's method which return the actual notifier
	 * @return
	 */
	public String getNotifierType(){
		return notifierType;
	}
	
	
	
	/**
	 * Sending an alert to the administrator (given by this.contact).
	 * the type of alert depends on its concrete implementation
	 * 
	 * @param msg is the message of the system performance that need to be delivered to the administrator
	 */
	public abstract void sendNotification(String msg);
	
}
