package test.soda.util.logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.FileAppender;
import org.apache.log4j.HTMLLayout;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import soda.util.logger.CustodianDailyRollingFileAppender;
import soda.util.logger.LoggerBuilder;
import soda.util.logger.SodaPatternLayout;



/**
 * 
 * This Test aim to exercise LoggerBuilder class.
 * This Test has achieved Code Coverage.
 * We aimed to cover Branch Coverage too.
 * But, there are a few branches that have not been covered.
 * Because, those branches are not reachable. For example the branch: if(datePattern==null)
 * Because, there's no code that can lead datePattern to have null value.
 * 
 * @author vong vithyea srey
 *
 */
public class TestLoggerBuilder {

	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	

	@Test
	public void testDefaultConstructor() {
		LoggerBuilder lb = new LoggerBuilder();
		assertTrue(lb != null);		
		assertTrue(lb.getAppenders() != null);
		assertTrue(lb.getAppenders() instanceof List);
		assertTrue(lb.getAppender() != null);
		assertTrue(lb.getAppender() instanceof CustodianDailyRollingFileAppender);
	}
	
	
	
	@Test
	public void testSetterGetterOfAppender() throws IOException{
		LoggerBuilder lb = new LoggerBuilder();
		lb.setAppender(new FileAppender(new HTMLLayout() , "test"));
		assertEquals(lb.getAppender().getFile(), "test");
		assertTrue(lb.getAppender().getLayout() instanceof HTMLLayout);
	
		exception.expect(IllegalArgumentException.class);
		lb.setAppender(null);
	}
	
	
	
	@Test
	public void testSetLoggerName(){
		LoggerBuilder lb = new LoggerBuilder();
		lb.setLoggerName("test");
		assertEquals(lb.getAppender().getName(), "test");
		
		exception.expect(IllegalArgumentException.class);
		lb.setLoggerName(null);
	}
	
	
	
	@Test
	public void testSetLoggerFile(){
		LoggerBuilder lb = new LoggerBuilder();
		lb.setLoggerFile("test");
		assertEquals(lb.getAppender().getFile(), "test");
		
		exception.expect(IllegalArgumentException.class);
		lb.setLoggerFile(null);
	}
	
	
	
	@Test
	public void testImmediateFlush(){
		LoggerBuilder lb = new LoggerBuilder();
		lb.setImmediateFlush(false);
		assertFalse(lb.getAppender().getImmediateFlush());
		
		lb.setImmediateFlush(true);
		assertTrue(lb.getAppender().getImmediateFlush());
		
		lb.setImmediateFlush("true");
		assertTrue(lb.getAppender().getImmediateFlush());
		
		lb.setImmediateFlush("yes");
		assertTrue(lb.getAppender().getImmediateFlush());
		
		lb.setImmediateFlush("no");
		assertFalse(lb.getAppender().getImmediateFlush());
		
		lb.setImmediateFlush("false");
		assertFalse(lb.getAppender().getImmediateFlush());
		
		lb.setImmediateFlush("hello world");
		assertFalse(lb.getAppender().getImmediateFlush());
	}
	
	
	
	@Test
	public void testAppend(){
		LoggerBuilder lb = new LoggerBuilder();
		lb.setAppend(false);
		assertFalse(lb.getAppender().getAppend());
		
		lb.setAppend(true);
		assertTrue(lb.getAppender().getAppend());
		
		lb.setAppend("true");
		assertTrue(lb.getAppender().getAppend());
		
		lb.setAppend("yes");
		assertTrue(lb.getAppender().getAppend());
		
		lb.setAppend("no");
		assertFalse(lb.getAppender().getAppend());
		
		lb.setAppend("false");
		assertFalse(lb.getAppender().getAppend());
		
		lb.setAppend("hello world");
		assertFalse(lb.getAppender().getAppend());
	}
	
	
	
	@Test
	public void testSetMaxNumberOfDays(){
		LoggerBuilder lb = new LoggerBuilder();
		
		lb.setMaxNumberOfDays(-9999);
		assertEquals(((CustodianDailyRollingFileAppender)lb.getAppender()).getMaxNumberOfDays(), "7");
		
		lb.setMaxNumberOfDays(9999);
		assertEquals(((CustodianDailyRollingFileAppender)lb.getAppender()).getMaxNumberOfDays(), "9999");

		lb.setMaxNumberOfDays("13245");
		assertEquals(((CustodianDailyRollingFileAppender)lb.getAppender()).getMaxNumberOfDays(), "13245");
		
		try{
			lb.setMaxNumberOfDays(Integer.MAX_VALUE);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {}
		
		try{
			lb.setMaxNumberOfDays(null);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {}
		
		try{
			lb.setMaxNumberOfDays("");
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {}
		
		lb.setAppender(new FileAppender());
		try{
			lb.setMaxNumberOfDays("99");
			fail("Expected UnsupportedOperationException");
		} catch (UnsupportedOperationException e){}
	}
	
	
	
	@Test
	public void testSetCompressBackups(){
		LoggerBuilder lb = new LoggerBuilder();
		
		lb.setCompressBackups(false);
		assertEquals(((CustodianDailyRollingFileAppender)lb.getAppender()).getCompressBackups(), "false");
		
		lb.setCompressBackups(true);
		assertEquals(((CustodianDailyRollingFileAppender)lb.getAppender()).getCompressBackups(), "true");

		lb.setCompressBackups("false");
		assertEquals(((CustodianDailyRollingFileAppender)lb.getAppender()).getCompressBackups(), "false");
		
		try{
			lb.setCompressBackups(null);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {}
		
		lb.setAppender(new FileAppender());
		try{
			lb.setCompressBackups("false");
			fail("Expected UnsupportedOperationException");
		} catch (UnsupportedOperationException e){}
	}
	
	
	
	@Test(expected=IllegalArgumentException.class)
	public void testSetAppenderThreshold(){
		LoggerBuilder lb = new LoggerBuilder();
		
		lb.setAppenderThreshold("ERROR");
		assertEquals(lb.getAppender().getThreshold().toString(), "ERROR");
		
		lb.setAppenderThreshold("hello world");
		assertEquals(lb.getAppender().getThreshold().toString(), "DEBUG");
		
		lb.setAppenderThreshold(null);
	}
	
	
	
	@Test
	public void testSetLayoutPattern(){
		LoggerBuilder lb = new LoggerBuilder();
		
		lb.setLayoutPattern("helloPattern", "hello header");
		SodaPatternLayout ly = (SodaPatternLayout) lb.getAppender().getLayout();
		assertEquals(ly.getConversionPattern(), "helloPattern");
		assertEquals(ly.getHeader(), "hello header\n");
		
		try{
			lb.setLayoutPattern(null, "hello header");
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {}
		
		try{
			lb.setLayoutPattern("helloPattern", null);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {}
	}
}
