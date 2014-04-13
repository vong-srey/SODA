package soda.aggregator.collector.factory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import soda.aggregator.collector.tool.CollectorTool;
import soda.util.config.ConfigKeysValues;
import soda.util.config.ConfigReader;
import soda.util.logger.LoggerBuilder;


/**
 * Help to decide which OS a CollectorFactory should be created for.
 * @author Vong Vithyea Srey
 *
 */
public class CollectorFactoryManager {
	
	/**
	 * a static CollectorFacotry object that correspond to this OS
	 */
	private static CollectorFactory collectorFactory = null;
	
	public static String COLLECTOR_FACTORY_PACKAGE = "soda.aggregator.collector.factory";
	
	public static String COLLECTORTOOL_CLASS_FULLNAME = "class soda.aggregator.collector.tool.CollectorTool";
	
	public static Logger appLogger = LoggerBuilder.getAppLogger();
	
	
	
	/**
	 * return a list of all available Collectors that can collect and log the hardware performance.
	 * This method implemented by reflect all the methods from CollectorFactory, and invoke those PUBLIC methods
	 * that are getting all the Collector (i.e. getCPUCollector(), getDFCollector() ...).
	 * 
	 * @param cf a CollectorFacotry object that support to this OS.
	 * @return a list of all available Collectors. 
	 * @throws IllegalArgumentException if the CollectorFactory object (cf) which the invoke() method invoked on doesn't have the method which being invoked.
	 * @throws IllegalAccessException if the method which is being invoked is a private or protected, which can't be invoked from this outside world.
	 * @throws InvocationTargetException if the method being invoked can't be invoked on the given CollectorFactory object (cf)
	 */
	public static List<CollectorTool> getAllCollectors(CollectorFactory cf) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		List<CollectorTool> collectorsList = new ArrayList<CollectorTool>();
		
		// create Class object to represent CollectorFactory class, 
		// then invoke each public "get*Collector()" method to get every available Collector and add it to the collectorsList
		Class<CollectorFactory> c = (Class<CollectorFactory>) cf.getClass();
		Method[] allMethods = c.getDeclaredMethods();
		for(Method method : allMethods){
			String methodName = method.getName();
			Class returnType = method.getReturnType().getSuperclass();
			
			if(returnType != null){
				String returnTypeSuperClassName = method.getReturnType().getSuperclass().toString();
				
				if( Modifier.isPublic(method.getModifiers())
						&& methodName.startsWith("get") 
						&& returnTypeSuperClassName.equals(COLLECTORTOOL_CLASS_FULLNAME) ){
					
					try {
						collectorsList.add((CollectorTool) method.invoke(cf));
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						appLogger.error("method " + method + " cannot be invoked using reflection.", e);
						throw e;
					}
				}
			}
		}
		return collectorsList;
	}
	
	
	
	/**
	 * return a singleton object of CollectorFactory
	 * @return CollectorFactory who is responsible to produce Collector (CPU, Network, DF, Disk and Memory) corresponds to OS it runs on.
	 * @throws ClassNotFoundException if the factory corresponds to that OS can't be created (normally it due to the given OS in config file is incorrect)
	 * @throws InstantiationException if the factory corresponds to that OS can't be instantiated (normally it due to the given OS in config file is incorrect)
	 * @throws IllegalAccessException if the factory corresponds to that OS can't be created (normally it due to the given OS in config file is incorrect)
	 * @throws IllegalArgumentException if the OS name in the config file is not supported.
	 */
	public static CollectorFactory getCollectorFactory() throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		if(collectorFactory == null){
			// read the config file, create a singleton factory associate with this OS
			String os = ConfigReader.getProperty(ConfigKeysValues.OS);
			if(ConfigReader.isGivenOSSupported(os)){
				try{
					collectorFactory = (CollectorFactory)Class.forName(COLLECTOR_FACTORY_PACKAGE + "." + os + "CollectorFactory").newInstance();
					appLogger.info("CollectorFactory singleton instantiated");
				} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e){
					appLogger.error("Cannot Instantiate CollectorFactory: " + COLLECTOR_FACTORY_PACKAGE + "." + os + "CollectorFactory", e);
					throw e;
				}
			} else {
				appLogger.error("The OS name stated in config file is not supported.");
				throw new IllegalArgumentException("The OS name stated in config file is not supported.");
			}
		}
		
		return collectorFactory;
	}
}
