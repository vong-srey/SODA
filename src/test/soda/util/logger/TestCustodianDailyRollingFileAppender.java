package test.soda.util.logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

import soda.util.logger.CustodianDailyRollingFileAppender;
import soda.util.logger.SodaPatternLayout;



/**
 * 
 * This Test aim to exercise CustodianDailyRollingFileAppender class.
 * This Test has achieved Code Coverage.
 * We aimed to cover Branch Coverage too.
 * But, there are a few branches that have not been covered.
 * Because, those branches are not reachable. For example the branch: if(datePattern==null)
 * Because, there's no code that can lead datePattern to have null value.
 * 
 * @author vong vithyea srey
 *
 */
public class TestCustodianDailyRollingFileAppender {
	
	@Test
	public void testDefaultConstructorAndInhirited() {
		CustodianDailyRollingFileAppender cfa = new CustodianDailyRollingFileAppender();
		assertTrue(cfa != null);
		assertEquals(cfa.getLayout(), null);
		assertEquals(cfa.getClass().getSuperclass().getName(), "org.apache.log4j.FileAppender");
	}
	
	
	
	@Test
	public void testConstructor() throws IOException {
		SodaPatternLayout ly = new SodaPatternLayout("header");
		CustodianDailyRollingFileAppender cfa = new CustodianDailyRollingFileAppender(ly, "test.log", "dd/MMM/yyyy-HH:mm:ss.SSS");
		
		// test valid partition
		assertTrue(cfa != null);
		assertEquals(cfa.getFile(), "test.log");
		assertEquals(cfa.getLayout().getClass().getName(),"soda.util.logger.SodaPatternLayout");
		assertEquals(cfa.getClass().getSuperclass().getName(), "org.apache.log4j.FileAppender");
		
		// test invalid partition	
		try{
			new CustodianDailyRollingFileAppender(null, null, null);
		} catch (NullPointerException e) {}

		try{
			new CustodianDailyRollingFileAppender(ly, null, null);
		} catch (NullPointerException e) {}

		try{
			new CustodianDailyRollingFileAppender(null, "", null);
		} catch (NullPointerException e) {}

		try{
			new CustodianDailyRollingFileAppender(null, null, "");
		} catch (NullPointerException e) {}
		
		// clean up after test
		new File("test.log").delete();
	}
	
	
	
	@Test
	public void testSetAndGetDatePattern() throws IOException{
		SodaPatternLayout ly = new SodaPatternLayout("header");
		CustodianDailyRollingFileAppender cfa = new CustodianDailyRollingFileAppender(ly, "test.log", "dd/MMM/yyyy-HH:mm");
		
		// check before do test
		assertEquals(cfa.getDatePattern(), "dd/MMM/yyyy-HH:mm");
	
		// test valid partition
		cfa.setDatePattern("dd/MMM/yyyy-HH:mm:ss.SSS");
		assertEquals("dd/MMM/yyyy-HH:mm:ss.SSS", cfa.getDatePattern());
		
		// test invalid partition
		try{
			cfa.setDatePattern(null);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e){ }
		
		// clean up after test
		new File("test.log").delete();
	}
	
	
	
	@Test
	public void testActiveOptions() throws IOException{
		// set up test
		SodaPatternLayout ly = new SodaPatternLayout("header");
		String datePattern = "dd/MMM/yyyy-HH:mm:ss.SSS";
		String filename = "test.log";
		Date now = new Date();
		now.setTime(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
		File file = new File(filename);
		String scfn = filename + sdf.format(new Date(file.lastModified()));
		scfn = scfn.substring(0, scfn.indexOf("/")-2);
		
		// test valid partition
		CustodianDailyRollingFileAppender cfa = new CustodianDailyRollingFileAppender(ly, filename, datePattern);
		String expScfn = cfa.getScheduledFilename();
		expScfn = expScfn.substring(0, expScfn.indexOf("/")-2);
		assertEquals(expScfn, scfn);
		
		// the branch of datePattern==null || fileName==null
		// will not be able to executed.
		// because our code block all those cases.
		// so we don't need to test that cases too.
		
		// clean up after test
		new File(filename).delete();
	}
	
	
	
	@Test
	public void testPrintPeriodicity() throws IOException{
		SodaPatternLayout ly = new SodaPatternLayout("header");
		String datePattern = "dd/MMM/yyyy-HH:mm:ss.SSS";
		String filename = "test.log";
		CustodianDailyRollingFileAppender cfa = new CustodianDailyRollingFileAppender(ly, filename, datePattern);
		
		// test valid partition
		
		cfa.printPeriodicity(-1);
		assertEquals(cfa.getDummyLog(), "Unknown periodicity for appender [null].");
		
		cfa.printPeriodicity(0);
		assertEquals(cfa.getDummyLog(), "Appender [null] to be rolled every minute.");
		
		cfa.printPeriodicity(1);
		assertEquals(cfa.getDummyLog(), "Appender [null] to be rolled on top of every hour.");
		
		cfa.printPeriodicity(2);
		assertEquals(cfa.getDummyLog(), "Appender [null] to be rolled at midday and midnight.");
		
		cfa.printPeriodicity(3);
		assertEquals(cfa.getDummyLog(), "Appender [null] to be rolled at midnight.");
		
		cfa.printPeriodicity(4);
		assertEquals(cfa.getDummyLog(), "Appender [null] to be rolled at start of week.");
		
		cfa.printPeriodicity(5);
		assertEquals(cfa.getDummyLog(), "Appender [null] to be rolled at start of every month.");
		
		// clean up after test
		new File(filename).delete();
	}
	

	
	@Test
	public void testComputeCheckPerioid() throws IOException{
		SodaPatternLayout ly = new SodaPatternLayout("header");
		String datePattern = "dd/MMM/yyyy-HH:mm:ss.SSS";
		String filename = "test.log";
		CustodianDailyRollingFileAppender cfa = new CustodianDailyRollingFileAppender(ly, filename, datePattern);
		
		// test valid partition
		assertEquals(cfa.computeCheckPeriod(),0);
		
		// the branch of datePattern==null
		// will not be able to executed.
		// because our code block all those cases.
		// so we don't need to test that cases too.
		
		// clean up after test
		new File(filename).delete();
	}
	
	
	
	@Test
	public void testSubAppend() throws IOException, ParseException{
		SodaPatternLayout ly = new SodaPatternLayout("header");
		String datePattern = "dd/MMM/yyyy-HH:mm:ss.SSS";
		String filename = "test.log";
		
		SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss");
		Date now = new Date();
		now.setTime(System.currentTimeMillis());
		
		Date d = f.parse(""+now.getDate()+"/"+now.getMonth()+"/"+now.getYear()+"-"+now.getHours()+":"+now.getMinutes()+":"+now.getSeconds());
		long nowMilSec = d.getTime();
		
		// test valid partition
		CustodianDailyRollingFileAppender cfa = new CustodianDailyRollingFileAppender(ly, filename, datePattern);
		cfa.subAppend(null);
		assertTrue(cfa.getNextCheck() > nowMilSec);
		
		// clean up after test
		new File(filename).delete();
	}
	
	
	
	@Test
	public void testZipGivenFile() throws IOException{
		String filename = "test";
		File file = null;
		try {
			file = new File(filename);
			file.createNewFile();
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("test");
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		SodaPatternLayout ly = new SodaPatternLayout("header");
		String datePattern = "dd/MMM/yyyy-HH:mm:ss.SSS";
		CustodianDailyRollingFileAppender cfa = new CustodianDailyRollingFileAppender(ly, filename, datePattern);
		cfa.zipGivenFile(file);
		
		// test valid partition
		assertTrue(new File(filename+".zip").exists());
		
		// clean up after test
		new File(filename+".zip").delete();
		file.delete();
	}
	
	
	
	@Test
	public void testSetterGetterCompressBackups(){
		CustodianDailyRollingFileAppender cfa = new CustodianDailyRollingFileAppender();
		assertEquals(cfa.getCompressBackups(),"false");
		
		// test valid partition
		cfa.setCompressBackups("true");
		assertEquals(cfa.getCompressBackups(),"true");
		
		cfa.setCompressBackups("yes");
		assertEquals(cfa.getCompressBackups(),"yes");
		
		cfa.setCompressBackups("no");
		assertEquals(cfa.getCompressBackups(),"no");
		
		cfa.setCompressBackups("false");
		assertEquals(cfa.getCompressBackups(),"false");
		
		// test invalid partition
		try{
			cfa.setCompressBackups(null);
			fail("Expected to through IllegalArgumentException");
		} catch (IllegalArgumentException e){ }
	}
	
	
	
	@Test
	public void testSetterGetterMaxNumberOfDays(){
		CustodianDailyRollingFileAppender cfa = new CustodianDailyRollingFileAppender();
				
		// test valid partition
		assertEquals(cfa.getMaxNumberOfDays(),"7");
		
		cfa.setMaxNumberOfDays("-99999");
		assertEquals(cfa.getMaxNumberOfDays(),"7");
		
		cfa.setMaxNumberOfDays("9999");
		assertEquals(cfa.getMaxNumberOfDays(),"9999");
		
		cfa.setMaxNumberOfDays("0");
		assertEquals(cfa.getMaxNumberOfDays(),"0");
		
		// test invalid partition
		try{
			cfa.setMaxNumberOfDays("");
			fail("Expected to through IllegalArgumentException");
		} catch (IllegalArgumentException e){ }
		
		try{
			cfa.setMaxNumberOfDays("hello world");
			fail("Expected to through IllegalArgumentException");
		} catch (IllegalArgumentException e){ }
		
		try{
			cfa.setMaxNumberOfDays(null);
			fail("Expected to through IllegalArgumentException");
		} catch (IllegalArgumentException e){ }

	}
}
