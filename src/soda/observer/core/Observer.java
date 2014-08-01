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
	
	private Logger appLogger = LoggerBuilder.getAppLogger();
	private int obsvrFrqc = 30; // default value for observer to be executed (30 seconds)
	private AtomicBoolean stopObserving = new AtomicBoolean(false);
	private QueryReader queryReader = new QueryReader();
	private QueryProcessor queryProc = new QueryProcessor();
	private String queryFile = "/var/www/html/kibana/queryfile";
	private List<Notifier> notifiers = new ArrayList<Notifier>();
	
	public void setupObserver() throws JSONException{
		// 1). read the observe frequency
		try{
			obsvrFrqc = Integer.parseInt(ConfigReader.getProperty("ObserveFrequency"));
		} catch (NumberFormatException e){
			appLogger.error("ObserveFrequency is not configured correct.");
		}
		
		// 2). read the queryfile path
		queryFile = ConfigReader.getProperty("QueryFilePath");
		
		// 3). set elasticsearch search url
		queryProc.setElasticsearchSearchURL(ConfigReader.getProperty("ElasticsearchSearchUrl"));
	}
	
	@Override
	public void run(){
		while(!stopObserving.get()){
			try {
				Thread.sleep(5000);
//				Thread.sleep(obsvrFrqc * 1000); //obsvrFrqc is in seconds => multiply to get milliseconds
				try{
					queryReader.readQuery(queryFile);
				} catch (IllegalArgumentException ie){
					continue;
				}
				final String query = queryReader.getObservedQuery();
				String email = queryReader.getReceiverEmail();
				if(email==null || query==null || email.trim().isEmpty() || query.trim().isEmpty()){
					appLogger.error(String.format("query: %s, email: %s is in malform", query, email));
					continue;
				}
				
				// 4). set notifiers
				NotifierBuilder ntfBuilder = new NotifierBuilder();
				ntfBuilder.setEmailNotifier(email);
				notifiers = ntfBuilder.getNotifiers();
				
				Thread t = new Thread(){
					public void run(){
						String rsltStr = null;
						List<List<Map<String, String>>> results = queryProc.processOrQueriesOfAndQueries(query, obsvrFrqc);
						if(!results.isEmpty()){
							rsltStr = queryProc.convertOrRsltOfAndRsltToString(results);
						} else {
							appLogger.info("System is observing this query: " + query + " But, there's no result found.");
						}
						
						if(!(rsltStr==null || rsltStr.trim().isEmpty())){
							for(Notifier ntf : notifiers){
								ntf.sendNotification(rsltStr);
							}
						}
					}
				};
				
				t.start();
				
				
			} catch (InterruptedException e) {
				appLogger.error("Thread is interrupted", e);
			}
		}
	}
	
	public void observe(){
		start();
	}
	
}
