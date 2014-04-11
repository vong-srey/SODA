package soda.aggregator.collector.factory;

import soda.aggregator.collector.tool.CollectorTool;
import soda.config.ConfigKeysValues;
import soda.config.ConfigReader;


/**
 * Help to decide which OS a CollectorFactory should be created for.
 * @author Vong Vithyea Srey
 *
 */
public class CollectorFactoryManager {
	
	public enum CollectorTools{NETWORK, DISK, DF, CPU, MEMORY};
	
	private static CollectorFactory collectorFactory = null;
	
	public static String COLLECTOR_FACTORY_PACKAGE = "soda.aggregator.collector.factory";
	
	
	public static CollectorTool getCollector(CollectorTools tool){
		return null;
	}
	
	public static CollectorFactory getCollectorFactory() throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		if(collectorFactory == null){
			// read the config file, create a singleton factory associate with this OS
			String os = ConfigReader.getPropertyFrom(ConfigKeysValues.OS);
			if(ConfigReader.isGivenOSSupported(os)){
				collectorFactory = (CollectorFactory)Class.forName(COLLECTOR_FACTORY_PACKAGE + "." + os + "CollectorFactory").newInstance();
			}
		}
		
		return collectorFactory;
	}
}
