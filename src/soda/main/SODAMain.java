package soda.main;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import org.hyperic.sigar.SigarException;

import soda.aggregator.core.Aggregator;
import soda.observer.core.Observer;
import soda.util.config.ConfigReader;
import soda.util.logger.LoggerBuilder;


/**
 * This is the main method (starting point) of SODA
 * @author Vong Vithyea Srey
 *
 */
public class SODAMain{
	
	public static String CONFIG_PATH = null;
	
	
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SigarException{
		
		// 1). get the config file path, from -config flag (if there is)
		List<String> argsList = Arrays.asList(args);
		int configIndex = argsList.indexOf("-config");
		if(configIndex >= 0){
			CONFIG_PATH = argsList.get(configIndex + 1);
			ConfigReader.setDefaultConfigPath(CONFIG_PATH);
		}
		
		
		// 2). config the appLogger
		LoggerBuilder.setAppenderForAppLoggerFromDefaultConfigFile();
		

		// 3). read the flags and instantiate Aggreagtor or/and Observer accordingly
		boolean runAggregator = true;
		boolean runObserver = true;
		if(argsList.indexOf("-a") >= 0){
			runObserver = false;
		}
		if(argsList.indexOf("-o") >= 0){
			runAggregator = false;
		}
		
		// Instantiate aggregator and start collecting and logging machine performance.
		// if the program is run with "-a" flag or no-flag is specified
		if(runAggregator){
			Aggregator aggregator = new Aggregator();
			aggregator.runAggregation();
		}
		
		// Instantiate observer and start its process
		// if the program is run with "-o" flag or no-flag is specified
		if(runObserver){
			Observer observer = new Observer();
			observer.setupObserver();
			observer.observe();
		}
	}
}
