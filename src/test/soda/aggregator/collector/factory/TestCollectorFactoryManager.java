package test.soda.aggregator.collector.factory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.hyperic.sigar.SigarException;
import org.junit.Test;

import soda.aggregator.collector.factory.CollectorFactory;
import soda.aggregator.collector.factory.CollectorFactoryManager;
import soda.aggregator.collector.factory.SigarSupportCollectorFactory;
import soda.aggregator.collector.tool.CollectorTool;
import soda.aggregator.core.Aggregator;
import soda.util.config.ConfigReader;
import soda.util.logger.LoggerBuilder;



/**
 * This Test aim to exercise CollectorFactoryManager class.
 * This Test has achieved Code Coverage, Branch Coverage
 * 
 * @author vong vithyea srey
 *
 */
public class TestCollectorFactoryManager {

	public static CollectorFactory actualCollectorFactory;
	public static List<CollectorTool> actualCollectors;
	
	
	
	@Test
	public void setupTestCases() {
		ConfigReader.setDefaultConfigPath("./ConfigForTest.cfg");
		LoggerBuilder.setAppenderForAppLoggerFromDefaultConfigFile();
		
		// test expected value
		try {
			actualCollectorFactory = CollectorFactoryManager.getCollectorFactory();
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException e) {
			fail("Configuartion file is not correctly configured.");
		}
		try {
			actualCollectors = CollectorFactoryManager.getAllCollectors(actualCollectorFactory);
		} catch (IllegalArgumentException | IllegalAccessException
				| InvocationTargetException e) {
			fail("Configuartion file is not correctly configured.");
		}	
	}
	
	
	
	@Test
	public void testCollectorFactoryType() {
		setupTestCases();
		assertTrue(actualCollectorFactory instanceof SigarSupportCollectorFactory);
	}
	
	
	
	@Test
	public void testCollectorFactory() throws SigarException{
		setupTestCases();
		SigarSupportCollectorFactory cf = null;
		try{
			cf = (SigarSupportCollectorFactory)actualCollectorFactory;
		} catch (ClassCastException e){
			fail("Expected collectorFactory is instanceof SigarSupportCollectorFacotry. But the actual instance is: " 
					+ actualCollectorFactory.getClass().getName());
		}
		assertEquals(cf.getCPUCollector().getClass().getName(),"soda.aggregator.collector.tool.sigarsupportos.CPUCollector");
		assertEquals(cf.getDFCollector().getClass().getName(),"soda.aggregator.collector.tool.sigarsupportos.DFCollector");
		assertEquals(cf.getDiskCollector().getClass().getName(),"soda.aggregator.collector.tool.sigarsupportos.DiskCollector");
		assertEquals(cf.getMemoryCollector().getClass().getName(),"soda.aggregator.collector.tool.sigarsupportos.MemoryCollector");
		assertEquals(cf.getNetworkCollector().getClass().getName(),"soda.aggregator.collector.tool.sigarsupportos.NetworkCollector");
		assertEquals(cf.getProcsCollector().getClass().getName(),"soda.aggregator.collector.tool.sigarsupportos.ProcsCollector");
	}
	
	
	
	@Test
	public void testGetAllCollectors(){
		setupTestCases();
		String[] expectedCollectors = new String[]{
		"soda.aggregator.collector.tool.sigarsupportos.CPUCollector", 
		"soda.aggregator.collector.tool.sigarsupportos.DFCollector", 
		"soda.aggregator.collector.tool.sigarsupportos.DiskCollector", 
		"soda.aggregator.collector.tool.sigarsupportos.MemoryCollector", 
		"soda.aggregator.collector.tool.sigarsupportos.NetworkCollector", 
		"soda.aggregator.collector.tool.sigarsupportos.ProcsCollector"};
		
		try{
			for(int i=0; i<expectedCollectors.length; i++){
				assertEquals(expectedCollectors[i], actualCollectors.get(i).getClass().getName());
			}
		} catch (IndexOutOfBoundsException e){
			fail("actualCollectors has fewer elements than expectedCollectors");
		}
	}

}
