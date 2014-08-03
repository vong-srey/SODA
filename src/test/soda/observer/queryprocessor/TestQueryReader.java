package test.soda.observer.queryprocessor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import soda.observer.queryprocessor.QueryReader;



/**
 * This Test aim to exercise QueryReader class.
 * This Test has achieved Code Coverage
 * 
 * @author vong vithyea srey
 *
 */
public class TestQueryReader {
	
	@Test
	public void testConstructorAndDefaultVavlues() {
		QueryReader qr = new QueryReader();
		assertEquals(qr.getReceiverEmail(), "");
		assertEquals(qr.getObservedQuery(), "");
	}
	
	
	
	@Test
	public void testSetElasticsearchUrl(){
		QueryReader qr = new QueryReader();
		qr.setElasticsearchSearchURL("https://10.10.1.13:40/_search");
		assertEquals(qr.getElasticsearchSearchURL(), "https://10.10.1.13:40/_search");
		
		try{
			qr.setElasticsearchSearchURL(null);
			fail("Expect IllegalArgumentException");
		} catch (IllegalArgumentException e){}
		
		try{
			qr.setElasticsearchSearchURL("       ");
			fail("Expect IllegalArgumentException");
		} catch (IllegalArgumentException e){}
		
		try{
			qr.setElasticsearchSearchURL("http://google.com");
			fail("Expect IllegalArgumentException");
		} catch (IllegalArgumentException e){}
	}
	
	
	
	@Test
	public void testSearchElasticsearchWith(){
		String elasticsearchUrl = "http://172.20.20.101:9200/_search";
		QueryReader qr = new QueryReader();
		String actualRsp = qr.searchElasticsearchWith(elasticsearchUrl, "{\"query\":\"\"}", "POST");
		assertEquals(actualRsp, "");
	}
	
	
	
	public void testReadObservedQuery(){
		QueryReader qr = new QueryReader();
		qr.readObservedQuery();
		assertTrue(!qr.getObservedQuery().trim().isEmpty());
		assertTrue(!qr.getReceiverEmail().trim().isEmpty());
	}

}
