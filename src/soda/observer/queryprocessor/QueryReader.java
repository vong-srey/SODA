package soda.observer.queryprocessor;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
	 * url of the elasticsearch server
	 */
	private static String elsSrchUrl = "http://localhost:9200/_search"; //default value of elasticsearch Search URL
	
	
	
	/**
	 * used to log this SODA program's performances.
	 * Because some methods in this class have to be static. So, this field has to be static
	 */
	private static Logger appLogger = LoggerBuilder.getAppLogger();
	
	
	
	/**
	 * setter for elsSrchUrl
	 * @param newElsSrchUrl : new url of the elasticsearch server
	 */
	public static void setElasticsearchSearchURL(String newElsSrchUrl){
		if(newElsSrchUrl==null || newElsSrchUrl.trim().isEmpty()){
			throw new IllegalArgumentException();
		}
		if(!newElsSrchUrl.endsWith("/_search")){
			throw new IllegalArgumentException();
		}
		elsSrchUrl = newElsSrchUrl;
	}
	
	/**
	 * getter for elsSrchUrl
	 */
	public static String getElasticsearchSearchURL(){
		return elsSrchUrl;
	}
	
	
	
	
	/**
	 * Read the query that user defined to be observed and set the contents of the query and email address
	 * to this.observedQuery and this.receiverEmail
	 */
	public void readObservedQuery(){
		appLogger.info("Reading query that need to be observed");
		
		
		String srchForObsverQr = "{\"query\":{\"match\":{\"_type\":\"observer\"}}}";
		
		String response = QueryReader.searchElasticsearchWith(QueryReader.getElasticsearchSearchURL(), srchForObsverQr, "POST");
		try {
			JSONObject rspJs = new JSONObject(response);
			JSONArray hits = rspJs.getJSONObject("hits").getJSONArray("hits");
			
			if(hits.length() == 0){
				observedQuery="";
				receiverEmail="";
				return;
			}
			
			JSONObject src = hits.getJSONObject(0).getJSONObject("_source");
			
			// because elasticsearch doesn't allow to add double quote " into its index.
			// so, observer ui (on kibana) replace those double quote " with @$
			// so, the raw observedQuery contains @$. so, we have to replace all @$ with double quote "
			observedQuery = src.getString("observedquery");
			observedQuery = observedQuery.replaceAll("@%", "\"");
			
			receiverEmail = src.getString("email");
			
		} catch (JSONException e) {
			observedQuery="";
			receiverEmail="";
			appLogger.error(e.getMessage(), e);
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
	
	
	
	/**
	 * Send a POST request to the given urlStr with the data in urlParameters.
	 * This method is used to send a request to elasticsearch server (POST request for searching and PUT request for creating an index).
	 * This code has been adapted from: from:http://www.xyzws.com/Javafaq/how-to-use-httpurlconnection-post-data-to-web-server/139 This block of code
	 * @param urlStr : url of the server that will be sent a POST request to
	 * @param urlParameters : data that will be attached with the POST request
	 * @return the string of the response from the server
	 */
	public static String searchElasticsearchWith(String urlStr, String urlParameters, String reqMethod){
		
		if(urlStr==null || urlStr.trim().isEmpty() || urlParameters==null || urlParameters.trim().isEmpty()) return "";
		
		URL url;
		HttpURLConnection connection = null;
		try {
			// Create connection
			url = new URL(urlStr);
			connection = (HttpURLConnection) url.openConnection();
			// set POST request
			connection.setRequestMethod(reqMethod);
			connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			// set data of the parameters
			connection.setRequestProperty("Content-Length",
					"" + Integer.toString(urlParameters.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");
			// other properties
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			// Send request
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			// Get Response
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			
			return response.toString();
		} catch (IOException e) {
			appLogger.error("Failled to get response from elasticsearch server: " + urlStr + " with parameter: " + urlParameters, e);
		} finally {
			if (connection != null)
				connection.disconnect();
		}

		return "";
	}
}
