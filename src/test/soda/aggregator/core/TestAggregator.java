package test.soda.aggregator.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.junit.Test;

import soda.aggregator.collector.factory.CollectorFactory;
import soda.aggregator.collector.factory.CollectorFactoryManager;
import soda.aggregator.collector.tool.CollectorTool;
import soda.aggregator.core.Aggregator;
import soda.util.config.ConfigReader;
import soda.util.logger.LoggerBuilder;



/**
 * This Test aim to exercise Aggreagtor class.
 * This Test has achieved Code Coverage, Branch Coverage
 * 
 * @author vong vithyea srey
 *
 */
public class TestAggregator {

	@Test
	public void testRunAggregator() throws InstantiationException, IllegalAccessException, ClassNotFoundException, IllegalArgumentException, InvocationTargetException {		
		ConfigReader.setDefaultConfigPath("./ConfigForTest.cfg");
		LoggerBuilder.setAppenderForAppLoggerFromDefaultConfigFile();
		
		// setup expected value
		CollectorFactory expectedCollectorFactory = CollectorFactoryManager.getCollectorFactory();
		List<CollectorTool> expectedCollectors = CollectorFactoryManager.getAllCollectors(expectedCollectorFactory);
		
		// setup actual values
		Aggregator aggregator = new Aggregator();
		aggregator.runAggregation();
		List<CollectorTool> actualCollectors = aggregator.getAllCollectors();
		for(CollectorTool ct: actualCollectors){
			ct.setStopCollection(false);
		}
		
		// test valid partition
		assertEquals(expectedCollectorFactory, aggregator.getCollectorFactory());
		try{
			for(int i=0; i<expectedCollectors.size(); i++){
				assertEquals(expectedCollectors.get(i).getClass(), actualCollectors.get(i).getClass());
			}
		} catch (IndexOutOfBoundsException e){
			fail("actualCollectors has fewer elements than expectedCollectors");
		}
	}

}
