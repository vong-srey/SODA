package soda.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import soda.aggregator.collector.factory.CollectorFactory;
import soda.aggregator.collector.factory.CollectorFactoryManager;
import soda.aggregator.collector.tool.CollectorTool;
import soda.config.ConfigReader;


/**
 * temporary main method for this application
 * This TempMain will be based on or refactored to create actual Main.
 * @author Vong Vithyea Srey
 *
 */
public class TempMain {
	
	public static String CONFIG_PATH = null;
	
	
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		
		// get the config file path, from -config flag
		List<String> argsList = Arrays.asList(args);
		int configIndex = argsList.indexOf("-config");
		if(configIndex >= 0){
			CONFIG_PATH = argsList.get(configIndex + 1);
			ConfigReader.setDefaultConfigPath(CONFIG_PATH);
		}
		
		CollectorFactory cf = CollectorFactoryManager.getCollectorFactory();
		
		List<CollectorTool> collectors = new ArrayList<CollectorTool>();
		CollectorTool cpu = cf.getCPUCollector();
		collectors.add(cpu);
		collectors.add(cf.getDFCollector());
		collectors.add(cf.getDiskCollector());
		collectors.add(cf.getMemoryCollector());
		collectors.add(cf.getNetworkCollector());
		
		
		
		
	}
}
