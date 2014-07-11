package test.soda.util.logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import soda.util.logger.SodaPatternLayout;



/**
 * This Test aim to exercise SodaPatternLayout class.
 * This Test has achieved Code Coverage, Branch Coverage and Equivalent Partitioning.
 * 
 * @author vong vithyea srey
 *
 */
public class TestSodaPatternLayout {

	@Test
	public void testDefaultConstructor() {
		SodaPatternLayout pl = new SodaPatternLayout();
		assertEquals("", pl.getHeader());
		assertTrue(pl != null);		
	}
	
	
	
	@Test
	public void testPatternLayoutInherted(){
		SodaPatternLayout pl = new SodaPatternLayout();
		assertEquals("org.apache.log4j.PatternLayout", pl.getClass().getSuperclass().getName());	
	}
	
	
	
	@Test(expected=IllegalArgumentException.class)
	public void testContructorWithOneParameter(){
		// test empty string parameter
		SodaPatternLayout pl2 = new SodaPatternLayout("");
		assertEquals("", pl2.getHeader());
		assertTrue(pl2 != null);
		
		// test non-empty string parameter
		SodaPatternLayout pl3 = new SodaPatternLayout("hello");
		assertEquals("hello\n", pl3.getHeader());
		assertTrue(pl3 != null);
		
		// test large string (1000 chars in length) parameter
			// create a 1000 chars header
		int delta = 126-32;  		//ASCII visible char range WHITE_SPACE to ~
		StringBuffer header = new StringBuffer("");
		for(int i=0; i<1000; i++){
			char c = (char) (i%delta + 32);
			header.append(c);
		}
		
		SodaPatternLayout pl4 = new SodaPatternLayout(header.toString());
		assertEquals(header.toString().trim()+"\n", pl4.getHeader());
		assertTrue(pl4 != null);
		
		
		// test null parameter
		new SodaPatternLayout(null);
	}

	
	
	@Test(expected=IllegalArgumentException.class)
	public void testSetAndGetHeader(){
		SodaPatternLayout pl = new SodaPatternLayout();
		assertEquals("", pl.getHeader());
		
		// test non-empty string parameter
		pl.setHeader("hello world");
		assertEquals("hello world\n", pl.getHeader());
		assertTrue(pl.getHeader().endsWith(System.getProperty("line.separator")));
		
		// test large string (1000 chars in length) parameter
			// create a 1000 chars header
		int delta = 126 - 32; // ASCII visible char range WHITE_SPACE to ~
		StringBuffer header = new StringBuffer("");
		for (int i = 0; i < 1000; i++) {
			char c = (char) (i % delta + 32);
			header.append(c);
		}
		pl.setHeader(header.toString());
		assertEquals(header.toString().trim()+"\n", pl.getHeader());
		assertTrue(pl.getHeader().endsWith(System.getProperty("line.separator")));
		
		// test empty string
		pl.setHeader("");
		assertEquals("", pl.getHeader());
		assertTrue(!pl.getHeader().endsWith(System.getProperty("line.separator")));
		
		// tell null parameter
		pl.setHeader(null);
	}
	
}
