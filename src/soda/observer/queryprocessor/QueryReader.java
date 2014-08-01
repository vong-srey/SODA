package soda.observer.queryprocessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;

import soda.util.logger.LoggerBuilder;



/**
 * This is an object that used to read the query of the criteria that the user want SODA-observer
 * to observe the server based on.
 * 
 * @author vong vithyea srey
 *
 */
public class QueryReader {
	
	/**
	 * query that is used to observe the server against.
	 */
	public String observedQuery = "";
	
	
	
	/**
	 * email address of ther administrator that will be used to send an email alert when the observed criterias met. 
	 */
	public String receiverEmail = "";
	
	
	
	/**
	 * used to log this SODA program's performances.
	 */
	Logger appLogger = LoggerBuilder.getAppLogger();
	
	
	
	/**
	 * Read the query that used defined to be observed and set the contents of the query and email address
	 * to this.observedQuery and this.receiverEmail
	 * 
	 * @param filePath : filePath that is stored the query and email address
	 */
	public void readQuery(String filePath){
		appLogger.info("Reading query that need to be observed");
		
		if(filePath==null || filePath.trim().isEmpty()){
			throw new IllegalArgumentException();
		}
		
		BufferedReader br = null;
		File file = new File(filePath);
		if(!file.exists()){
			appLogger.error("queryfile doesn't exist at: " + filePath);;
			return;
		}
		
		try {
			br = new BufferedReader(new FileReader(filePath));
			observedQuery = br.readLine();
			receiverEmail = br.readLine();
		} catch (IOException e) {
			appLogger.error("File can't be read. filepath: " + filePath);
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				appLogger.error("File can't be read or can't be closed. filepath: " + filePath, ex);
			}
		}
	}
	
	
	
	/**
	 * getter for observedQuery
	 * @return query that is observed
	 */
	public String getObservedQuery(){
		return observedQuery;
	}
	
	
	
	/**
	 * getter for receiverEmail
	 * @return the email address of the administrator that will be used to send an alert email.
	 */
	public String getReceiverEmail(){
		return receiverEmail;
	}
}
