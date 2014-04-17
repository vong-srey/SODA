package soda.aggregator.collector.tool;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.hyperic.sigar.SigarException;

import soda.aggregator.collector.tool.sigarsupportos.CPUCollector;
import soda.aggregator.collector.tool.sigarsupportos.DFCollector;
import soda.aggregator.collector.tool.sigarsupportos.DiskCollector;
import soda.aggregator.collector.tool.sigarsupportos.MemoryCollector;
import soda.aggregator.collector.tool.sigarsupportos.NetworkCollector;
import soda.util.config.ConfigKeysValues;
import soda.util.config.ConfigReader;
import soda.util.logger.LoggerBuilder;


/**
 * Defining the interface of each tool features
 * 
 * Classes and Interfaces in this package is implemented using Sigar.
 * Sigar is supporting Linux, Windows, HPUX, Solaris, AIX, FreeBSD, MacOSX.
 * So, these OS(es) will not be implemented separately. The implementations
 * in this package will be able to support those OS(es).
 * However, if you want this tool to support any other OS(es), you need to
 * implements each tools for each OS by extending the tool from this package.
 * And you also need to implement the CollectorFactory class for that OS as well.
 * An example has been done for the Minix OS, please check package soda.aggregator.collector.tool.minix
 * and MinixCollectorFactory 
 * 
 * @author Vong Vithyea Srey
 *
 */
public abstract class CollectorTool extends Thread implements Serializable{

	/**
	 * A List of available tools that can provide performance info and logging
	 * If you are going to add new Collector into this application, please add that Collector at the end of this list.
	 * Which means, please keep the order and the content of this current list
	 */
	public static final List<String> AVAILABLE_TOOLS = Arrays.asList(new String[]{"CPUCollector", "DFCollector", "DiskCollector", "MemoryCollector", "NetworkCollector"});

	
	
	/**
	 * A helper, used to build a StringBuilder (Good performance, but non thread-safe)
	 */
	protected StringBuilder strBuilder = new StringBuilder();
	
	
	
	/**
	 * a logger that will log the performance into file system
	 */
	protected Map<String, Logger> loggers;
	
	
	
	/**
	 * Logger object that responsible to log the application (itself) performance, error and other statuses
	 * appLogger != logger
	 */
	private Logger appLogger = LoggerBuilder.getAppLogger();
	
	
	
	/**
	 * 
	 */
	protected final String DEVICE_NAME = "deviceName";
	/**
	 * 
	 */
	protected final String DESCRIPTION = "description";
	/**
	 * 
	 */
	protected final String VALUE = "value";
	
	
	
	/**
	 * a control variable of the collection statu. If this flag set to true, the collection and logging process will be stop and thread will be killed.
	 */
	protected AtomicBoolean stopCollection = new AtomicBoolean(false);
	
	
	
	/**
	 * collect a set of maps. A map contains "Performance Description" (as a key) and "Performance Value" from the implemented CollectorTool
	 * @return a set of maps. a map (LinkedHashMap) contains "Performance Description" (as a key) and "Performance Value" from the implemented CollectorTool. Each set of map represent the data of each different component (e.g. a set of CPU0, set of CPU1 and so on)
	 * @throws SigarException if the Method cannot retrieve the info about that hardware (i.e. for CPU, can't get number of core, etc)
	 */
	public abstract Set<Map<String, String>> getPerformance() throws SigarException;
	
	
	
	/**
	 * Configure the logger to get it ready for log the performances into file system.
	 * This method is supposed to invoke during the Constructor
	 * @throws SigarException if the Method cannot retrieve the info about that hardware (i.e. for CPU, can't get number of core, etc)
	 */
	public void configureLogger() throws SigarException{
		LoggerBuilder lb = new LoggerBuilder();
		
		// getPerforamnce, because we want to know how many devices out there (e.g.: for CPU, there can be CPU0, CPU1, ... and Disk there can be dev01, dev02, /tmp, /var ...)
		// the put those devices name sinto the "devices" list.
		// because we need to create a Logger for each device
		Set<Map<String, String>> performances = getPerformance();
		List<String> devicesNames = new ArrayList<String>();
		
		Object obj = this;
		if(obj instanceof CPUCollector){
			devicesNames.add(AVAILABLE_TOOLS.get(0));
		} else if(obj instanceof DFCollector){
			devicesNames.add(AVAILABLE_TOOLS.get(1));
		} else if(obj instanceof DiskCollector){
			devicesNames.add(AVAILABLE_TOOLS.get(2));
		} else if(obj instanceof MemoryCollector){
			devicesNames.add(AVAILABLE_TOOLS.get(3));
		} else if(obj instanceof NetworkCollector){
			devicesNames.add(AVAILABLE_TOOLS.get(4));
		}
		
		for(Map<String, String> per : performances){
			devicesNames.add(per.get(DEVICE_NAME));
		}
		
		loggers = lb.getLoggersMapFromDefaultConfigFile(devicesNames);
	}
	
	
	
	/**
	 * Record the given performance into the file system using logger
	 *	@param performance is a Set of Map. A Map object (inside that Set) has Key as a "Description of the performance" (e.g. CPU0-Nice) and Valuse as the value for that description. Each set of map represent the data of each different component (e.g. a set of CPU0, set of CPU1 and so on)
	 */
	public void recordPerformance(Set<Map<String, String>> performances){
		
		for(Map<String,String> perf : performances){
			String deviceName = perf.remove(DEVICE_NAME);
			Logger log = loggers.get(deviceName);
					
			strBuilder.setLength(0);
			strBuilder.append(deviceName);
			strBuilder.append(" ");
			strBuilder.append(perf.get(DESCRIPTION));
			strBuilder.append(" ");
			strBuilder.append(perf.get(VALUE));
			
			log.info(strBuilder);
			
			strBuilder.setLength(0);
		}
	}
	
	
	
	/**
	 * the run() method invoked when this thread is strated.
	 */
	@Override
	public void run() {
		int logFrequency = Integer.parseInt(ConfigReader.getProperty(ConfigKeysValues.LOG_FREQUENCY));
		
		// while the flag stopCollection is false, keep collecting and logging the perfromance
		while(!stopCollection.get()){
			try {
				Thread.sleep(logFrequency);
				Set<Map<String, String>> performances = getPerformance();
				recordPerformance(performances);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				// if thread is interrupted
				appLogger.error("Collector thread has been interrupted", e);
				break;
			} catch (SigarException e){
				// if Sigar library can't access the hardware data.
				appLogger.error("Sigar Library cannot access the hardware data", e);
				break;
			} catch (NullPointerException e){
				// the first time to access logger, it might be containing null value
				appLogger.error("logger who logging the data has not been instantiated", e);
				break;
			}
			
		}
	}
}
