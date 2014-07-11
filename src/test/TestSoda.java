package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import test.soda.aggregator.core.TestAggregator;
import test.soda.util.config.TestConfigReader;
import test.soda.util.logger.TestCustodianDailyRollingFileAppender;
import test.soda.util.logger.TestLoggerBuilder;
import test.soda.util.logger.TestSodaPatternLayout;

@RunWith(Suite.class)
@SuiteClasses({TestSodaPatternLayout.class, TestCustodianDailyRollingFileAppender.class,
	TestLoggerBuilder.class, TestConfigReader.class, TestAggregator.class})
public class TestSoda {
	/**
	 * test suite to test all classes.
	 * there's no any other codes needed to be written here.
	 * 
	 * all the test cases are declared in @SuiteClasses
	 */
}
