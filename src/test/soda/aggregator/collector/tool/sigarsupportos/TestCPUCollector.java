package test.soda.aggregator.collector.tool.sigarsupportos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hyperic.sigar.SigarException;
import org.junit.Test;

import soda.aggregator.collector.tool.sigarsupportos.CPUCollector;
import soda.util.config.ConfigReader;
import soda.util.logger.LoggerBuilder;



/**
 * This Test aim to exercise CPUCollector class.
 * This Test has achieved Code Coverage, Branch Coverage
 * 
 * @author vong vithyea srey
 *
 */
public class TestCPUCollector {

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
		CPUCollector cpu = new CPUCollector();       
		assertTrue(cpu instanceof CPUCollector);
		assertEquals(cpu.getClass().getSuperclass().getSimpleName(), "CollectorTool");	
	}
	
	
	
	@Test
	public void testConstructorAndSettingUpLogHeader() throws SigarException{
		setupTestCases();
		CPUCollector cpu = new CPUCollector();
		String expectedHeader = "LogTimeStamp\t"
									+ "DeviceName\t"
									+ "User-%\t"
									+ "Sys-%\t"
									+ "Idle-%\t"
									+ "Wait-%\t"
									+ "Nice-%\t"
									+ "Combined-%\t"
									+ "IRQ-%";
		assertEquals(cpu.getLogHeader(), expectedHeader);
	}
	
	
	
	@Test
	public void testConstructorAndConfiguringLoggers() throws SigarException {
		setupTestCases();
		CPUCollector cpu = new CPUCollector();   
		Map<String, Logger> loggers = cpu.getLoggers();
		
		// test logger for CPU0
		assertTrue(loggers.containsKey("CPU0"));
		assertEquals(loggers.get("CPU0").getName(), "");
		assertEquals(loggers.get("CPU0").getClass().getName(), "org.apache.log4j.Logger");
		
		// test logger for total CPU or CPUs
		assertTrue(loggers.containsKey("CPUs"));
		assertEquals(loggers.get("CPUs").getName(), "");
		assertEquals(loggers.get("CPUs").getClass().getName(), "org.apache.log4j.Logger");
	}
	
	
	
	@Test
	public void testConstructorAndGettingPerformances() throws SigarException {
		setupTestCases();
		CPUCollector cpu = new CPUCollector();
		Set<Map<String, String>> performances = cpu.getPerformance();
		
		for(Map<String,String> perf : performances){
			String[] values = perf.get("value").split(" ");
			
			try{
				for(String value : values){
					double fv = Double.parseDouble(value);
					if( fv < 0.0 || fv > 100){
						fail("Expected between 0 to 100, but actual value is: " + fv);
					}
				}
			} catch (NullPointerException | NumberFormatException e){
				fail("Expected floating point string, but the actual value is either null or non-floating point string.");
			}
		}
	}

}
