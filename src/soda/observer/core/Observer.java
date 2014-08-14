package soda.observer.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.json.JSONException;

import soda.observer.notifier.Notifier;
import soda.observer.notifier.NotifierBuilder;
import soda.observer.queryprocessor.QueryProcessor;
import soda.observer.queryprocessor.QueryReader;
import soda.util.config.ConfigReader;
import soda.util.logger.LoggerBuilder;

public class Observer extends Thread{
	
	/**
	 * Logger object that responsible to log the application (itself) performance, error and other statuses
	 */
	private Logger appLogger = LoggerBuilder.getAppLogger();
	
	
	
	/**
	 * how frequent the observer should actually analyst the data
	 */
	private int obsvrFrqc = 30; // default value for observer to be executed (30 seconds)
	
	
	
	/**
	 * used to stop the observer
	 */
	private AtomicBoolean stopObserving = new AtomicBoolean(false);
	
	
	
	/**
	 * Object used to communicate with the elasticsearch server
	 */
	private QueryReader queryReader = new QueryReader();
	
	
	
	/**
	 * object used to process the received query or process the query before sending to server
	 */
	private QueryProcessor queryProc = new QueryProcessor();
	
	
	
	/**
	 * Notifiers object that responsible to send an alert email to those notifiers (sms or/and email) that are available
	 */
	private List<Notifier> notifiers = new ArrayList<Notifier>();
	
	
	
	/**
	 * getter for obsvrFrqc
	 */
	public int getObserverFrequerncy(){
		return obsvrFrqc;
	}
	
	
	
	/**
	 * Setup the environment for observer
	 */
	public void setupObserver() {
		// 1). read the observe frequency
		try{
			obsvrFrqc = Integer.parseInt(ConfigReader.getProperty("ObserveFrequency"));
		} catch (NumberFormatException e){
			appLogger.error("ObserveFrequency is not configured correct.");
		}
		
		// 2). set elasticsearch search url
		QueryReader.setElasticsearchSearchURL(ConfigReader.getProperty("ElasticsearchSearchUrl"));
	}
	
	
	
	/**
	 * run the observing operation
	 */
	@Override
	public void run(){
		while(!stopObserving.get()){
			try {
				boolean debug = false;
				try{
					debug = ConfigReader.getProperty("Debug").equalsIgnoreCase("true");
				} catch (Exception e){
					appLogger.error("Debug property is incorrectly configured");
				}
				
				System.out.println("debug is:" + debug);
				
				// we create another thread to handle this time process
				// because this process requires connecting to elasticsearch server which can be time consuming
				// so, it can effect to next cycle of the observer of we dt hv a seperate thread to handle this.
//				Thread t = new Thread(){
//					public void run(){
						
						// 3). read from elasticsearch to get query and email
						queryReader.readObservedQuery();
						final String query = queryReader.getObservedQuery();
						String email = queryReader.getReceiverEmail();
						if(query.trim().isEmpty() || query==null || email==null || email.trim().isEmpty()){
							appLogger.info("There is no query to be observed or no email address of the reciever.");
							System.out.println("no query");
							return;
						}
						
						// 4). process the recieved result from the server
						String rsltStr = null;
						List<List<Map<String, String>>> results = queryProc.processOrQueriesOfAndQueries(query, obsvrFrqc);
						if(!results.isEmpty()){
							rsltStr = queryProc.convertOrRsltOfAndRsltToString(results);
						} else {
							appLogger.info("System is observing this query: " + query + " But, there's no result found.");
//							debug
//							System.out.println("no result found");
						}
						
						// 5). set notifiers and sending the alert message
						NotifierBuilder ntfBuilder = new NotifierBuilder();
						ntfBuilder.setEmailNotifier(email);
						notifiers = ntfBuilder.getNotifiers();
						
						// every notifier is sending an alert message to the registered contact
						if(!(rsltStr==null || rsltStr.trim().isEmpty())){
							if(debug){
								System.out.println(rsltStr);
							} else {
								for(Notifier ntf : notifiers){
									System.out.println("sent out email: ");
									ntf.sendNotification(rsltStr);
//									debug
//									System.out.println(rsltStr);
								}	
							}
							
							int sendEmailFrqncy = 60;
							try{
								sendEmailFrqncy = Integer.parseInt(ConfigReader.getProperty("ResendEmailFrequency"));
							} catch(Exception e){
								appLogger.error("ResendEmailFrequency property is incorrectly configured");
							}
							
							Thread.sleep(sendEmailFrqncy * 1000);
						} else {
							if(debug) System.out.println("There's nothing has been observed.");
						}
//					}
//				};
//				
//				t.start();
//				
						
				Thread.sleep(obsvrFrqc * 1000); //obsvrFrqc is in seconds => multiply to get milliseconds
			} catch (InterruptedException e) {
				appLogger.error("Thread is interrupted", e);
			}
		}
	}
	
	
	
	/**
	 * The interface provide to others object who want to invoke the observing operation (thread.start()).
	 * We have this method, because we don't like people to use .start(). which it is not meaningful enough to understand
	 * what will be going on with the observer.
	 */
	public void observe(){
		start();
	}
}
