package soda.observer.queryprocessor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import soda.util.logger.LoggerBuilder;



/**
 * This object provides all the necessary methods to process the query.
 * 
 * @author vong vithyea srey
 *
 */
public class QueryProcessor {
	
	
	
	/**
	 * used to define what field of the query
	 */
	public static final String RPRT_TYPE = "type";
	
	
	
	/**
	 * used to define what is the timestamp of the query
	 */
	public static final String RPRT_TIME = "timestamp";
	
	
	
	/**
	 * used to define what is the value of the field of the query
	 */
	public static final String RPRT_VALUE = "value";
	
	
	
	/**
	 * used to define what is the host of the query
	 */
	public static final String RPRT_HOST = "host";
	
	
	
	/**
	 * used to log this SODA program's performances.
	 */
	private Logger appLogger = LoggerBuilder.getAppLogger();

	
	
	/**
	 * convert the string represent json object to a list of list of JSONObjects
	 * e.g. of a given jsonStr: {"OR":[{"AND":[{"type":"Idle","value":10.3,"operator":"gte"},{"type":"Sys","operator":"gte","value":10}]},{"AND":[{"type":"Sys","operator":"lte","value":10}]}]} 
	 * @param jsonStr : A string that represents json 
	 * @return the list of list of JSONObjects that represented by the given jsonStr
	 */
	public List<List<JSONObject>> getListOfORqueries(String jsonStr){
		List<List<JSONObject>> orQrOfAndQr = new ArrayList<List<JSONObject>>();
		
		if(jsonStr==null || jsonStr.trim().isEmpty()) return orQrOfAndQr;
		
		JSONObject js;
		try {
			js = new JSONObject(jsonStr);
			
			// get the list of JSONObjects
			// iterate through each JSONObject in the OR array
			JSONArray orQueriesJS = js.getJSONArray("OR");
			for(int i=0; i < orQueriesJS.length(); i++){
				JSONObject jsi = orQueriesJS.getJSONObject(i);
				
				// each JSONObject (in the array) contains an array of AND
				JSONArray andQueriesJS = jsi.getJSONArray("AND");
				
				List<JSONObject> andQueriesList = new ArrayList<JSONObject>();
				
				// iterates through each JSONObject in the AND array, then add them one by one into the list
				for(int j=0; j < andQueriesJS.length(); j++){
					JSONObject jsj = andQueriesJS.getJSONObject(j);
					andQueriesList.add(jsj);
				}
				
				orQrOfAndQr.add(andQueriesList);
			}
		} catch (JSONException e) {
			appLogger.error("Query's Synatx is not correct", e);
		}
		
		return orQrOfAndQr;
	}
	
	
	
	/**
	 * process the given query and return the result
	 * @param jsonStr : A string that represents json
	 * e.g. of the given jsonStr:  {"OR":[{"AND":[{"type":"Idle","value":10.3,"operator":"gte"},{"type":"Sys","operator":"gte","value":10}]},{"AND":[{"type":"Sys","operator":"lte","value":10}]}]}
	 * @param frqncyInSec : this method will search from elasticsearch in the last frqncyInSec seconds 
	 * (It means that it will search from "current time - frqncyInSec" to "current time"
	 * 
	 * @return the result of the criterias that met with the given jsonStr query.
	 */
	public List<List<Map<String, String>>> processOrQueriesOfAndQueries(String jsonStr, int frqncyInSec){
		List<List<Map<String, String>>> orRsltOfAndRslt = new ArrayList<List<Map<String, String>>>();
		
		if(frqncyInSec <= 0) frqncyInSec = 5; // observeFrequency should be 5 seconds in default
		if(jsonStr==null || jsonStr.trim().isEmpty()) return orRsltOfAndRslt;
		
		JSONObject orQrAndQrJs;
		try{
			orQrAndQrJs = new JSONObject(jsonStr);
			JSONArray orQueriesJS = orQrAndQrJs.getJSONArray("OR");
			
			// get the list of JSONObjects
			// iterate through each JSONObject in the OR array
			for(int i=0; i<orQueriesJS.length(); i++){
				JSONObject jsi = orQueriesJS.getJSONObject(i);
				
				// each JSONObject (in the array) contains an array of AND
				JSONArray andQrArrJs = jsi.getJSONArray("AND");
				
				List<Map<String, String>> andRslt = new ArrayList<Map<String, String>>();
				// iterate each and query
				for(int j=0; j<andQrArrJs.length(); j++){
					JSONObject jsQr = andQrArrJs.getJSONObject(j);
					Map<String, String> rslt = searchElasticsearchAt(QueryReader.getElasticsearchSearchURL(), jsQr, frqncyInSec);
					
					// and query means all the sub-queries have to be true.
					// if there is one query is false => break, we don't need to check other queries
					// and set andRslt to and empty list (it means the whole condition is false)
					if(rslt.isEmpty() || rslt==null){
						andRslt = new ArrayList<Map<String, String>>();
						break;
					} else {
						andRslt.add(rslt);
					}
				}
				
				// or query means: if there is only one sub-query is true => then the whole thing is true
				// so, we add all the true condition into the result, but not the false condition.
				if(!andRslt.isEmpty()) orRsltOfAndRslt.add(andRslt);
			}
		} catch (JSONException e){
			appLogger.error("Query's syntax is not correct.", e);
		}
		
		return orRsltOfAndRslt;
	}
	
	
	
	/**
	 * Search on the given elasticsearch server for the given qrJS at the last frqncyInSec seconds.
	 * @param urlStr : url of the elasticsearch server
	 * @param qrJS : JSONObject that represent the query that need to be searched
	 * e.g. of qrJS:   {"type":"Idle","value":10.3,"operator":"gte"}
	 * @param frqncyInSec : the last fqncyInSec that need to search for
	 * @return empty map if there's no result met the criteria. otherwise, it will return a map that contains the first search result.
	 * the map's keys are: RPRT_HOST (map's value is the host that the event happened at)
	 * the map's keys are: RPRT_TYPE (map's value is the field that matched the criteria)
	 * the map's keys are: RPRT_TIME (map's value is the time stamp that the event happened)
	 * the map's keys are: RPRT_VALUE (map's value is the value (that met the criteria) of the field)
	 */
	public Map<String, String> searchElasticsearchAt(String urlStr, JSONObject qrJS, int frqncyInSec) {
		Map<String, String> srchRslts = new HashMap<String, String>();
		
		if(frqncyInSec <= 0) frqncyInSec = 5; // observeFrequency should be 5 seconds in default
		if(urlStr==null || urlStr.trim().isEmpty() || qrJS==null) return srchRslts;

		// build the actual parameter that used to search (look at builSearchJsonString() method for the detail and what is the actual parameters look like)
		String urlParameters = this.buildSeachJsonString(qrJS, frqncyInSec);
		if (urlParameters == null || urlParameters.trim().isEmpty()) return srchRslts;

		// e.g of the rspStr = {"_shards":{"total":35,"failed":0,"successful":35},"hits":{"hits":[{"_index":"logstash-2014.07.29","_type":"Idle","_source":{"@timestamp":"2014-07-29T22:36:00.668Z","dev":"CPUs","host":"localhost.localdomain","type":"Idle","value":85.2},"_id":"6DeFlaE3RFGWK7P8VAQT8w","_score":2.609438},{"_index":"logstash-2014.07.29","_type":"Idle","_source":{"@timestamp":"2014-07-29T22:58:00.668Z","dev":"CPUs","host":"localhost.localdomain","type":"Idle","value":85.2},"_id":"z6pLbZb8SLyGa7oEwMeslQ","_score":2.252763},{"_index":"logstash-2014.07.29","_type":"Idle","_source":{"@timestamp":"2014-07-29T20:36:00.668Z","dev":"CPUs","host":"localhost.localdomain","type":"Idle","value":85.2},"_id":"JCEKP5BxRgSdpHcbbjTNGg","_score":2.098612},{"_index":"logstash-2014.07.29","_type":"Idle","_source":{"@timestamp":"2014-07-29T20:47:30.668Z","dev":"CPUs","host":"localhost.localdomain","type":"Idle","value":85.2},"_id":"aEs7VVQUT8effkjUFuz88g","_score":1.4054651}],"total":4,"max_score":2.609438},"took":8,"timed_out":false}
		String rspStr = QueryReader.searchElasticsearchWith(urlStr, urlParameters, "POST");
		if (rspStr == null || rspStr.trim().isEmpty()) return srchRslts;
//		debug
//		System.out.println(urlParameters + "\n" + rspStr);
		
		// process the result "rspStr" into a map that can be understood.
		try{
			JSONObject json = new JSONObject(rspStr);
			JSONArray hits = json.getJSONObject("hits").getJSONArray("hits");
	
			if(hits.length() == 0) return srchRslts;
			
			for (int i = 0; i < hits.length(); i++) {
				// src = {"_index":"logstash-2014.07.29","_type":"Idle","_source":{"@timestamp":"2014-07-29T22:36:00.668Z","dev":"CPUs","host":"localhost.localdomain","type":"Idle","value":85.2},"_id":"6DeFlaE3RFGWK7P8VAQT8w","_score":2.609438}
				JSONObject src = hits.getJSONObject(0).getJSONObject("_source");
				srchRslts.put(RPRT_HOST, src.getString("host"));
				srchRslts.put(RPRT_TYPE, src.getString("type"));
				srchRslts.put(RPRT_TIME, src.getString("@timestamp"));
				srchRslts.put(RPRT_VALUE, "" + src.getDouble("value"));
				break;
			}
		} catch (JSONException e){
			return new HashMap<String, String>();
		}

		return srchRslts;
	}
	
	
	
	
	
	
	
	/**
	 * Build an actual parameters that will be sent to the elasticsearch.
	 * This is a helper to searchElasticsearchAt()
	 * @param jsonQuery : query that is used to build the parameters
	 * @param frqncyInSec : number of seconds that need to go backward
	 * @return the json result as a string
	 */
	public String buildSeachJsonString(JSONObject jsonQuery, int frqncyInSec) {
		try{
			// e.g. jsonQuery = {"type":"Idle","value":10.3,"operator":"gte"}
			String type = jsonQuery.getString("type");
			double value = jsonQuery.getDouble("value");
			String operator = jsonQuery.getString("operator");
			String timestamp = getCurrentTimeMinusSeconds(frqncyInSec);
			String ts = getCurrentTimeMinusSeconds(0);
			
			String s = "{\"query\":{"
				       + "\"filtered\":{"
				          + "\"query\":{"
				             + "\"match\":{"
				                + "\"type\":\"%s\""
				                + "}"
				             + "},"
				                
				             + "\"filter\":{"
				                + "\"bool\":{\"must\":{"
				                   + "\"range\":{"
				                      + "\"@timestamp\":{\"gte\":\"%s\", \"lte\":\"%s\"},"
				                      + "\"_cache\":false"
				                   + "}"
				                + "}}"
				             + "}"
				          + "}"
				        + "},"
				          
				        + "\"post_filter\":{"
				           + "\"range\":{"
				              + "\"value\":{"
				                 + "\"%s\":%f"
				              + "}"
				           + "}"
				        + "}"
				           
				      + "}";
			String query = String.format(s, type, timestamp, ts, operator, value);
			return query;
		} catch (JSONException e){
			appLogger.error(jsonQuery + " can't be used to build search query", e);
		}
		return "";
	}
	
	
	
	/**
	 * convert the given list of list of map result into the result string that can be used to attach to an alert email
	 * @param rslt : that need to be converted
	 * @return : the result string that will be attached to the alert email
	 */
	public String convertOrRsltOfAndRsltToString(List<List<Map<String, String>>> rslt){
		StringBuffer result = new StringBuffer();
		
		if(rslt==null || rslt.isEmpty()) return "";
		
		// iterate through first level list (this is the OR level)
		// each element of this first level list is an issue
		for(int i=0; i<rslt.size(); i++){
			List<Map<String, String>> andLst = rslt.get(i);
			result.append("Issue ");
			result.append(i+1);
			result.append(" is: ");
			
			// iterate 2nd level list (this is the AND level)
			// all elements of this list are in conjunction (AND) clause.  
			for(int j=0; j<andLst.size(); j++){
				Map<String, String> issue = andLst.get(j);
				result.append(issue.toString());
				
				if(j < andLst.size()-1) result.append(" AND ");
			}
			
			if(i < rslt.size()-1) result.append("\n\n");
		}
		
		return result.toString();
	}
	
	
	
	/**
	 * a helper calculate backward from current time to number of given seconds
	 * @param seconds : number of backward seconds from current time
	 * @return the string in this form "2014-07-29T20:00:00"
	 */
	public String getCurrentTimeMinusSeconds(int seconds){
		// retrieved time must be in this format "2014-07-29T20:00:00"
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		// get current date time with Date()
		Date date = new Date();
		date.setSeconds(date.getSeconds() - seconds); // time at the last seconds
		return dateFormat.format(date).toString();
	}
}
