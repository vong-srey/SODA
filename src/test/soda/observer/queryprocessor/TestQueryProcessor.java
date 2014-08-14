package test.soda.observer.queryprocessor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import soda.observer.queryprocessor.QueryProcessor;
import soda.util.config.ConfigReader;



/**
 * This Test aim to exercise QueryProcessor class.
 * This Test has achieved Code Coverage
 * 
 * @author vong vithyea srey
 *
 */
public class TestQueryProcessor {

	@Test
	public void testGetCurrentTimeMinusSeconds() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date date = new Date();
		assertEquals(dateFormat.format(date).toString(), new QueryProcessor().getCurrentTimeMinusSeconds(-3600*Integer.parseInt(ConfigReader.getPropertyFrom("ConfigForTest.cfg", "ElasticsearchServerTimeZone"))));
	}
	
	
	
	@Test
	public void testConvertOrRsltOfAndRsltToString(){
		QueryProcessor qp = new QueryProcessor();
		Map<String, String> m1 = new HashMap<String, String>();
		m1.put("1", "hello");
		m1.put("2", "world");
		Map<String, String> m2 = new HashMap<String, String>();
		m2.put("a", "a-hello");
		List<Map<String,String>> lm1 = new ArrayList<Map<String, String>>();
		lm1.add(m1);
		lm1.add(m2);
		List<Map<String,String>> lm2 = new ArrayList<Map<String, String>>();
		lm2.add(m2);
		List<List<Map<String, String>>> llm = new ArrayList<List<Map<String, String>>>();
		
		assertEquals(qp.convertOrRsltOfAndRsltToString(null), "");
		assertEquals(qp.convertOrRsltOfAndRsltToString(llm), "");
		
		llm.add(lm1);
		llm.add(lm2);
		assertEquals(qp.convertOrRsltOfAndRsltToString(llm), "Issue 1 is: {1=hello, 2=world} AND {a=a-hello}\n\nIssue 2 is: {a=a-hello}");
	}
	
	
	
	@Test
	public void testBuildSeachJsonString() throws JSONException{
		JSONObject js = new JSONObject("{\"type\":\"Idle\",\"value\":10.3,\"operator\":\"gte\"}");
		QueryProcessor qp = new QueryProcessor();
		String s = qp.buildSeachJsonString(js, 0);
		assertTrue(!s.trim().isEmpty());
		assertTrue(s!=null);
		String sf = "{\"query\":{\"filtered\":{\"query\":{\"match\":{\"type\":\"Idle\"}},\"filter\":{\"bool\":{\"must\":{\"range\":{\"@timestamp\":{\"gte\":\"%s\", \"lte\":\"%s\"},\"_cache\":false}}}}}},\"post_filter\":{\"range\":{\"value\":{\"gte\":10.300000}}}}";
		String ts = qp.getCurrentTimeMinusSeconds(0);
		assertEquals(s, String.format(sf, ts, ts));
	}
	
	
	
	@Test
	public void testSearchElasticsearchAt() throws JSONException{
		JSONObject js = new JSONObject("{\"type\":\"Idle\",\"value\":10.3,\"operator\":\"gte\"}");
		String elasticsearchUrl = ConfigReader.getPropertyFrom("./ConfigForTest.cfg", "ElasticsearchSearchUrl");
		QueryProcessor qp = new QueryProcessor();
		assertTrue(!qp.searchElasticsearchAt(elasticsearchUrl, js, 0).toString().equals("{}"));
	}
	
	
	
	@Test
	public void testProcessOrQueriesOfAndQueries() throws JSONException{
		JSONObject js = new JSONObject("{\"OR\":{\"type\":\"Idle\",\"value\":10.3,\"operator\":\"gte\"}}");
		QueryProcessor qp = new QueryProcessor();
		assertEquals(qp.processOrQueriesOfAndQueries(js.toString(), 0).toString(), "[]");
	}
	
	
	
	@Test
	public void testGetListOfORqueries(){
		String s = "{\"OR\":[{\"AND\":[{\"type\":\"Idle\",\"value\":99.2,\"operator\":\"gte\"},{\"type\":\"ReqProcTime\",\"operator\":\"lte\",\"value\":10}]},{\"AND\":[{\"type\":\"ReqProcTime\",\"operator\":\"lte\",\"value\":10}]}]}";
		QueryProcessor qp = new QueryProcessor();
		String at = "[[{\"type\":\"Idle\",\"value\":99.2,\"operator\":\"gte\"}, {\"type\":\"ReqProcTime\",\"value\":10,\"operator\":\"lte\"}], [{\"type\":\"ReqProcTime\",\"value\":10,\"operator\":\"lte\"}]]";
		assertEquals(qp.getListOfORqueries(s).toString(), at);
	}

}
