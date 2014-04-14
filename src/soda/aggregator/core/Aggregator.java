package soda.aggregator.core;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import soda.aggregator.collector.factory.CollectorFactory;
import soda.aggregator.collector.factory.CollectorFactoryManager;
import soda.aggregator.collector.tool.CollectorTool;


/**
 * This is the object to control the collection.
 * This object will provide relevant info to Logger object.
 * Where Logger object will log those info into the persistent files.
 * @author Vong Vithyea Srey
 *
 */
public class Aggregator {
	
	/**
	 * list of all avaliable collectors.
	 */
	private List<CollectorTool> collectors;

	
	
	/**
	 * the main method for Aggregation the machine paerformance (CPU, DF, Disk, Memory and Network).
	 * 
	 * CollectorFactory and CollectorFactoryManager will throw these exceptions when they are trying to creating 
	 * the collectors object, but those objects can't be created.
	 * 
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public void runAggregation() throws InstantiationException, IllegalAccessException, ClassNotFoundException, IllegalArgumentException, InvocationTargetException{
		// Initiating the factory that support this OS
		// telling factoryManager to use this OS factory to get all the collectors that support this OS
		CollectorFactory cf = CollectorFactoryManager.getCollectorFactory();
		collectors = CollectorFactoryManager.getAllCollectors(cf);
		
		for(CollectorTool ct : collectors){
			ct.start();
		}	
	}

}
