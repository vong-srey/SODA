package test.soda.observer.core;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import soda.observer.core.Observer;
import soda.observer.queryprocessor.QueryReader;



/**
 * This Test aim to exercise QueryProcessor class.
 * This Test has achieved Code Coverage
 * 
 * @author vong vithyea srey
 *
 */
public class TestObserver {

	@Test
	public void testSetupObserver() {
		Observer ob = new Observer();
		assertEquals(ob.getObserverFrequerncy(), 30);
		assertEquals(QueryReader.getElasticsearchSearchURL(), "https://10.10.1.13:40/_search");
		
		ob.setupObserver();
		
		assertEquals(ob.getObserverFrequerncy(), 1);
		assertEquals(QueryReader.getElasticsearchSearchURL(), "http://172.20.28.126:9200/_search");
	}

}
