package test.soda.aggregator.collector.tool.sigarsupportos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hyperic.sigar.SigarException;
import org.junit.Test;

import soda.aggregator.collector.tool.sigarsupportos.DFCollector;
import soda.util.config.ConfigReader;
import soda.util.logger.CustodianDailyRollingFileAppender;
import soda.util.logger.LoggerBuilder;



/**
 * This Test aim to exercise DFCollector class.
 * This Test has achieved Code Coverage, Branch Coverage
 * 
 * @author vong vithyea srey
 *
 */
public class TestDFCollector {

	/**
	 * a helper method used to setup the environment before each test case starts 
	 */
	private void setupTestCases(){
		ConfigReader.setDefaultConfigPath("./ConfigForTest.cfg");                
		LoggerBuilder.setAppenderForAppLoggerFromDefaultConfigFile();
	}
	
	
	
	@Test
	public void testConstructor() throws SigarException {
		setupTestCases();
		DFCollector df = new DFCollector();       
		assertTrue(df instanceof DFCollector);
		assertEquals(df.getClass().getSuperclass().getSimpleName(), "CollectorTool");	
	}
	
	
	
	@Test
	public void testConstructorAndSettingUpLogHeader() throws SigarException{
		setupTestCases();
		DFCollector df = new DFCollector();
		String expectedHeader = "LogTimeStamp\t"
									+ "DeviceName\t"
									+ "Size-MB\t"
									+ "Used-MB\t"
									+ "Avail-MB\t"
									+ "UsedInPerc-%";
		assertEquals(df.getLogHeader(), expectedHeader);
	}
	
	
	
	@Test
	public void testConstructorAndConfiguringLoggers() throws SigarException {
		setupTestCases();
		DFCollector df = new DFCollector();
		Map<String, Logger> loggers = df.getLoggers();
		
		// there must be at least one logger
		assertFalse(loggers.isEmpty());
		
		// test each logger
		for(Map.Entry<String, Logger> entry : loggers.entrySet()){
			Logger log = entry.getValue();
			String logName = entry.getKey();
			CustodianDailyRollingFileAppender app = (CustodianDailyRollingFileAppender)log.getAppender(logName);
			
			// test key (logger name)
			assertTrue(logName.contains("DF-Df_"));
			
			// test logger
			assertEquals(entry.getValue().getName(), entry.getKey());
			assertEquals(app.getFile(), "Logs/DFLogs/" + logName + ".log");
			assertEquals(app.getCompressBackups(), "true");
			assertEquals(app.getDatePattern(), "'.'yyyy-MM-dd");
			assertEquals(app.getMaxNumberOfDays(), "1");
		}
	}
	

	
	@Test
	public void testConstructorAndGettingPerformances() throws SigarException {
		setupTestCases();
		DFCollector df = new DFCollector();
		Set<Map<String, String>> performances = df.getPerformance();
		
		for(Map<String,String> perf : performances){
			String[] values = perf.get("value").split(" ");
			
			for (String value : values) {
				try {
					double fv = Double.parseDouble(value);
					if (fv < 0.0) {
						fail("Expected between 0 to 100, but actual value is: " + fv);
					}
				} catch (NullPointerException | NumberFormatException e) { }
			}
		}
	}

}
