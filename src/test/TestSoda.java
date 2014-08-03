package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import test.soda.aggregator.collector.factory.TestCollectorFactoryManager;
import test.soda.aggregator.collector.factory.TestSigarSupportAndCollectorFactory;
import test.soda.aggregator.collector.tool.TestCollectorTool;
import test.soda.aggregator.collector.tool.sigarsupportos.TestCPUCollector;
import test.soda.aggregator.collector.tool.sigarsupportos.TestDFCollector;
import test.soda.aggregator.collector.tool.sigarsupportos.TestDiskCollector;
import test.soda.aggregator.collector.tool.sigarsupportos.TestMemoryCollector;
import test.soda.aggregator.collector.tool.sigarsupportos.TestNetworkCollector;
import test.soda.aggregator.collector.tool.sigarsupportos.TestProcsCollector;
import test.soda.aggregator.core.TestAggregator;
import test.soda.observer.core.TestObserver;
import test.soda.observer.notifier.TestEmailNotifier;
import test.soda.observer.notifier.TestNotifierBuilder;
import test.soda.observer.queryprocessor.TestQueryProcessor;
import test.soda.observer.queryprocessor.TestQueryReader;
import test.soda.util.config.TestConfigReader;
import test.soda.util.logger.TestCustodianDailyRollingFileAppender;
import test.soda.util.logger.TestLoggerBuilder;
import test.soda.util.logger.TestSodaPatternLayout;

@RunWith(Suite.class)
@SuiteClasses({TestSodaPatternLayout.class, TestCustodianDailyRollingFileAppender.class,
	TestLoggerBuilder.class, TestConfigReader.class, TestAggregator.class,
	TestCollectorFactoryManager.class, TestSigarSupportAndCollectorFactory.class,
	TestCollectorTool.class, TestCPUCollector.class, TestDFCollector.class, TestDiskCollector.class,
	TestMemoryCollector.class, TestNetworkCollector.class, TestProcsCollector.class, TestQueryReader.class,
	TestQueryProcessor.class, TestEmailNotifier.class, TestNotifierBuilder.class, TestObserver.class})
public class TestSoda {
	/**
	 * test suite to test all classes.
	 * there's no any other codes needed to be written here.
	 * 
	 * all the test cases are declared in @SuiteClasses
	 */
}
