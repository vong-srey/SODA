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

import soda.aggregator.collector.tool.sigarsupportos.ProcsCollector;
import soda.util.config.ConfigReader;
import soda.util.logger.CustodianDailyRollingFileAppender;
import soda.util.logger.LoggerBuilder;


/**
 * Please setup a process ID (PID) in ConfigForTest.cfg file.
 * Otherwise, these test cases will be failed because it can't find a valid PID.
 * 
 * Please correct the expected value in testGetFormatedStartTime().
 * because the expected value is calculate against current time.
 * So, when you redo the testing is different from the time I did this testing.
 */


/**
 * This Test aim to exercise ProcsCollector class.
 * This Test has achieved Code Coverage, Branch Coverage
 * 
 * @author vong vithyea srey
 *
 */
public class TestProcsCollector {

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
		ProcsCollector pro = new ProcsCollector();       
		assertTrue(pro instanceof ProcsCollector);
		assertEquals(pro.getClass().getSuperclass().getSimpleName(), "CollectorTool");	
	}
	
	
	
	@Test
	public void testConstructorAndSettingUpLogHeader() throws SigarException{
		setupTestCases();
		ProcsCollector pro = new ProcsCollector();     
		String expectedHeader = "LogTimeStamp\t"
										+ "ProcName\t"
										+ "PID\t"
										+ "UserName\t" 
										+ "OwnerGroup\t" 
										+ "StartTime\t" 
										+ "CpuSysTime\t" 
										+ "CpuUserTime\t" 
										+ "CpuUsage-%\t"
										+ "State(R:Running, S:Sleep)\t"   
										+ "NumActiveThreads\t" 
										+ "Priority\t" 
										+ "Nice\t"
										+ "SharedMem-MB\t" 
										+ "RssMem-MB\t" 
										+ "VirtualMem-MB\t" 
										+ "NonIOpgFaults\t" 
										+ "IOpgFaults\t" 
										+ "TotalPgFaults\t"
										+ "\t??? means unknown or unimplemented value";
		assertEquals(pro.getLogHeader(), expectedHeader);
	}
	
	
	
	@Test
	public void testConstructorAndConfiguringLoggers() throws SigarException {
		setupTestCases();
		ProcsCollector pro = new ProcsCollector();     
		Map<String, Logger> loggers = pro.getLoggers();
		
		// there must be at least one logger
		assertFalse(loggers.isEmpty());
		
		// test each logger
		for(Map.Entry<String, Logger> entry : loggers.entrySet()){
			Logger log = entry.getValue();
			String logName = entry.getKey();
			CustodianDailyRollingFileAppender app = (CustodianDailyRollingFileAppender)log.getAppender(logName);
			
			// test key (logger name)
			assertTrue(logName.contains("Proc_"));
			
			// test logger
			assertEquals(entry.getValue().getName(), entry.getKey());
			assertEquals(app.getFile(), "Logs/ProcsLogs/" + logName + ".log");
			assertEquals(app.getCompressBackups(), "true");
			assertEquals(app.getDatePattern(), "'.'yyyy-MM-dd");
			assertEquals(app.getMaxNumberOfDays(), "4");
		}
	}
	

	
	@Test
	public void testConstructorAndGettingPerformances() throws SigarException {
		setupTestCases();
		ProcsCollector pro = new ProcsCollector();     
		Set<Map<String, String>> performances = pro.getPerformance();
		
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
	
	
	
	@Test
	public void testGetFormatedStartTime() throws SigarException{
		ProcsCollector pro = new ProcsCollector();
		
		// test invalid partition
		assertEquals(pro.getFormatedStartTime(Integer.MIN_VALUE), "00:00");
		assertEquals(pro.getFormatedStartTime(-1), "00:00");
		
		// test valid partition
		assertEquals(pro.getFormatedStartTime(0), "00:00");
		assertEquals(pro.getFormatedStartTime(99999), "Jan1");
		assertEquals(pro.getFormatedStartTime(Integer.MAX_VALUE), "Jan26");
		
	}

}
