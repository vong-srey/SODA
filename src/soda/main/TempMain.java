package soda.main;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import soda.aggregator.core.Aggregator;
import soda.util.config.ConfigReader;
import soda.util.logger.CustodianDailyRollingFileAppender;
import soda.util.logger.LoggerBuilder;


/**
 * temporary main method for this application
 * This TempMain will be based on or refactored to create actual Main.
 * @author Vong Vithyea Srey
 *
 */
public class TempMain implements Daemon{
	
	public static String CONFIG_PATH = null;
	
	
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		
		// 1). get the config file path, from -config flag (if there is)
		List<String> argsList = Arrays.asList(args);
		int configIndex = argsList.indexOf("-config");
		if(configIndex >= 0){
			CONFIG_PATH = argsList.get(configIndex + 1);
			ConfigReader.setDefaultConfigPath(CONFIG_PATH);
		}
		
		
		// 2). config the appLogger
		LoggerBuilder.setAppenderForAppLoggerFromDefaultConfigFile();
		
		
		
		// 3). Instantiate aggregator and start collecting and logging machine performance.
		Aggregator aggregator = new Aggregator();
		aggregator.runAggregation();
		
	}
	
	
	// how to config a logger
	// need to remove when complete the logging tasks
	public static void logger(){
		CustodianDailyRollingFileAppender a = new CustodianDailyRollingFileAppender();
        a.setName("TESTLOG");
        a.setFile("/home/adminuser/Desktop/testlog/mylog.log");
        a.setAppend(true);
        a.setImmediateFlush(true);
        a.setMaxNumberOfDays("2");
        a.setCompressBackups("true");
        a.setDatePattern("'.'yyyy-MM-dd");
        String pattern = "%d{dd MMM yyyy HH:mm:ss,SSS} %m%n";
        PatternLayout ly = new PatternLayout();
        ly.setConversionPattern(pattern);
        a.setLayout(ly);
        a.activateOptions();
        Logger.getRootLogger().addAppender(a);
	}


	@Override
	public void destroy() {
		System.out.println("SODA Daemon has been killed...");
		
	}


	@Override
	public void init(DaemonContext arg0) throws DaemonInitException, Exception {
		System.out.println("Initialising SODA Daemon...");
		
	}


	@Override
	public void start() throws Exception {
		System.out.println("Starting SODA Daemon...");
		main(null);
		
	}


	@Override
	public void stop() throws Exception {
		System.out.println("Stopping SODA Daemon...");
		
	}
	
	
}
