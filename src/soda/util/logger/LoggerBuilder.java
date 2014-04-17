package soda.util.logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import soda.aggregator.collector.tool.CollectorTool;
import soda.util.config.ConfigKeysValues;
import soda.util.config.ConfigReader;



/**
 * A Builder pattern which helps to build a logger. The logger is a org.apache.log4j.Logger object
 * 
 * @author vongvithyeasrey
 *
 */
public class LoggerBuilder {
	
	// ************************************************************************************************
	//
	//                              used to build a single Logger object
	//
	// *************************************************************************************************
	
	/**
	 * the final product, Logger object.
	 */
	private Logger logger;

	/**
	 * FileAppender object that is used by the product logger
	 */
	private FileAppender appender;
	
	/**
	 * replace the default appender (which is CustodianDailyRollingFaileAppender object) with the given app.
	 * @param app is FileAppender object
	 */
	public void setAppender(FileAppender app){
		appender = app;
	}

	/**
	 * Set a name for this logger
	 * @param name
	 */
	public void setLoggerName(String name){
		appender.setName(name);
	}

	/**
	 * Set the path for the logger file.
	 * If the appender is the CustodianDailyRollingFaileAppender, the logger file will be the name of this logger+TodayDate
	 * @param path
	 */
	public void setLoggerFile(String path){
		appender.setFile(path);
	}

	/**
	 * If given bool is true, the log will be flush out, without holding on the buffer.
	 * @param bool - either immediateFlush is true or false
	 */
	public void setImmediateFlush(boolean bool){
		appender.setImmediateFlush(bool);
	}

	/**
	 * If given value String is true, the log will be flush out, without holding on the buffer.
	 * @param value - either immediateFlush is true or false
	 */
	public void setImmediateFlush(String value){
		appender.setImmediateFlush(value.equalsIgnoreCase("true"));
	}

	/**
	 * Set the appending. In generally it should be true
	 * @param value
	 */
	public void setAppend(boolean value){
		appender.setAppend(value);
	}

	/**
	 * Set the appending. In generally it should be true
	 * @param value
	 */
	public void setAppend(String value){
		appender.setAppend(value.equalsIgnoreCase("true"));
	}

	/**
	 * Set the number of max days that the logs should be stored.
	 * After this number has been reached, it will remove the earliest log and add a new log 
	 * @param numday - maximum number of log of the days should be kept
	 */
	public void setMaxNumberOfDays(int numday){
		setMaxNumberOfDays(String.valueOf(numday));
	}

	/**
	 * Set the number of max days that the logs should be stored.
	 * After this number has been reached, it will remove the earliest log and add a new log 
	 * @param numday - maximum number of log of the days should be kept
	 */
	public void setMaxNumberOfDays(String numDayInString){
		if(appender instanceof CustodianDailyRollingFileAppender){
			((CustodianDailyRollingFileAppender)appender).setMaxNumberOfDays(numDayInString);
		} else {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * If the compressBackup is true, the earliest log will be compressed before removed.
	 * This flag relates to maxNumberOfDays. Please look at setMaxNumberOfDays() method.
	 * @param value - true or false
	 */
	public void setCompressBackups(String value){
		if(appender instanceof CustodianDailyRollingFileAppender){
			((CustodianDailyRollingFileAppender)appender).setCompressBackups(value);
		} else {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * If the compressBackup is true, the earliest log will be compressed before removed.
	 * This flag relates to maxNumberOfDays. Please look at setMaxNumberOfDays() method.
	 * @param value - true or false
	 */
	public void setCompressBackups(boolean value){
		setCompressBackups(String.valueOf(value));
	}

	/**
	 * set the threhold of logger to the given threshold parameter
	 * @param threshold
	 */
	public void setAppenderThreshold(String threshold){
		appender.setThreshold(Level.toPriority(threshold));
	}

	/**
	 * set the pattern of the layout that the appender used to produce the log format.
	 * @param pattern
	 */
	public void setLayoutPattern(String pattern){
		PatternLayout ly = new PatternLayout();
		ly.setConversionPattern(pattern);
		appender.setLayout(ly);
	}

	/**
	 * read the config file (given by its path), and set all the CustodianDailyRollingFaileAppender properties.
	 * @param configFilePath - path to the config file.
	 * @param appenderName - name of the logger and appender.
	 * return true if this configFile activates this logger and the appender for this logger can be configured successfully
	 * throw IllegalArgumentException if the given appenderName doesn't have the corresponding CollectorTool
	 */
	public boolean setAppenderFromConfigFile(String configFilePath, String appenderName){
		if(!CollectorTool.AVAILABLE_TOOLS.contains(appenderName)){
			throw new IllegalArgumentException("Given appenderName doesnot have a correspond CollectorTool");
		}

		return helperSetAppenderFromConfigFile(configFilePath, appenderName);
	}

	/**
	 * A helper to read the config file (given by its path), and set all the CustodianDailyRollingFaileAppender properties.
	 * @param configFilePath - path to the config file.
	 * @param appenderName - name of the logger and appender.
	 * return true if this configFile activates this logger and the appender for this logger can be configured successfully
	 * throw IllegalArgumentException if the given appenderName doesn't have the corresponding CollectorTool
	 */
	private boolean helperSetAppenderFromConfigFile(String configFilePath, String appenderName){
		if(! ConfigReader.getPropertyFrom(configFilePath, appenderName).equalsIgnoreCase("yes")){
			return false;
		}

		setLoggerName(appenderName);
		setLoggerFile(ConfigReader.getPropertyFrom(configFilePath, appenderName + "LogFile"));
		setImmediateFlush(ConfigReader.getPropertyFrom(configFilePath, appenderName + "LogImmediateFlush"));
		setAppend(ConfigReader.getPropertyFrom(configFilePath, appenderName + "LogAppend"));
		setMaxNumberOfDays(ConfigReader.getPropertyFrom(configFilePath, appenderName + "LogMaxNumberOfDays"));
		setCompressBackups(ConfigReader.getPropertyFrom(configFilePath, appenderName + "LogCompressBackups"));
		setLayoutPattern(ConfigReader.getPropertyFrom(configFilePath, appenderName + "LogPattern"));

		return true;
	}

	/**
	 * read the default config file (given by its path), and set all the CustodianDailyRollingFaileAppender properties.
	 * @param appenderName - name of the logger and appender.
	 */
	public boolean setAppenderFromDefaultConfigFile(String appenderName){
		return setAppenderFromConfigFile(ConfigKeysValues.getDefaultConfigPath(), appenderName);
	}
	
	/**
	 * return the Logger product.
	 * @return the Logger product.
	 */
	public Logger getLogger(){
		appender.activateOptions();
		String name = appender.getName();
		logger = Logger.getLogger(name);
		logger.addAppender(appender);
		return logger;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// *************************************************************************************************
	//
	//         used to Build a single static logger object for logging the application activities
	//
	// *************************************************************************************************
	
	/**
	 * name of the RootLogger that log this app statuses and activities
	 */
	private static final String appLoggerName = "AppLogger";
	
	/**
	 * Logger object that responsible to log the application (itself) performance, error and other statuses
	 */
	private static final Logger appLogger = Logger.getLogger(appLoggerName);
	
	
	
	
	
	// used to build a collection (map : loggerName-key and loggerObject-value) of logger. 
	
	/**
	 * the final product, Logger object.
	 */
	private Map<String, Logger> loggers;
	
	/**
	 * FileAppender object that is used by the product logger
	 */
	private List<CustodianDailyRollingFileAppender> appenders;
	
	
	
	/**
	 * Default constructor, build an appender as CustodianDailyRollingFaileAppender object.
	 * It is a FileAppender object that rolling the files based on the configured maxNumberOfDays. 
	 */
	public LoggerBuilder(){
		appender = new CustodianDailyRollingFileAppender();
		appenders = new ArrayList<CustodianDailyRollingFileAppender>();
	}
	
	/**
	 * a method that will read the config properties from default config file and set up a logger that 
	 * will be used anywhere in the application to log all the application acitivities, status
	 * @return a static logger that will be used anywhere in the application to log all the application acitivities, status  
	 */
	public static boolean setAppenderForAppLoggerFromDefaultConfigFile(){
		
		if(! ConfigReader.getProperty(appLoggerName).equalsIgnoreCase("yes")){
			return false;
		}
		
		CustodianDailyRollingFileAppender temp = new CustodianDailyRollingFileAppender();
		temp.setName(appLoggerName);
		temp.setFile(ConfigReader.getProperty(appLoggerName + "LogFolder") + "app.log");
		String immediateFlush = ConfigReader.getProperty(appLoggerName + "LogImmediateFlush");
		temp.setImmediateFlush(immediateFlush.equalsIgnoreCase("true"));
		String append = ConfigReader.getProperty(appLoggerName + "LogAppend");
		temp.setAppend(append.equalsIgnoreCase("true"));
		temp.setMaxNumberOfDays(ConfigReader.getProperty(appLoggerName + "LogMaxNumberOfDays"));
		temp.setCompressBackups(appLoggerName + "LogCompressBackups");
		String threshold = ConfigReader.getProperty(appLoggerName + "LogThreshold");
		temp.setThreshold(Level.toPriority(threshold));
		PatternLayout ly = new PatternLayout();
		ly.setConversionPattern(ConfigReader.getProperty(appLoggerName + "LogPattern"));
		temp.setLayout(ly);
		temp.activateOptions();
		appLogger.addAppender(temp);
		
		return true;
	}
	
	/**
	 * a wrapper to return RootLogger object that shared among the application.
	 * This RootLogger is used to log the application log itself.
	 * @return RootLogger that is used to log the application activity and status itself.
	 */
	public static Logger getAppLogger(){
		return appLogger;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// ********************************************************************************************************************
	//
	// used for set up a collection of loggers. a map object which has loggerName as the key and logger object as the value
	//
	// *********************************************************************************************************************
	
	/**
	 * read the config file (given by its path), and set all the CustodianDailyRollingFaileAppender properties.
	 * @param configFilePath - path to the config file.
	 * @param devicesNames - a list of all the devices. it will be used to create a logger for each device
	 * return true if this configFile activates this logger and the appender for this logger can be configured successfully
	 * throw IllegalArgumentException if the given appenderName doesn't have the corresponding CollectorTool
	 */
	public Map<String, Logger> getLoggersMapFromDefaultConfigFile(List<String> devicesNames){
		// the first element is the Device Collector (e.g. CPUCollector, DiskCollecor, or DFCollecor, or NetworkCollecor, or MemoryCollecor)
		String deviceCollector = devicesNames.remove(0);
		
		if(!CollectorTool.AVAILABLE_TOOLS.contains(deviceCollector)){
			throw new IllegalArgumentException("Given appenderName doesnot have a correspond CollectorTool");
		}
	
		Map<String, Logger> loggers= new HashMap<String, Logger>();
		
		// if it is configured to not log the performance. then just return an empty log
		if( ! ConfigReader.getProperty(deviceCollector).equalsIgnoreCase("yes")){
			Logger temp = Logger.getLogger("");
			for(String deviceName : devicesNames){
				// by not assigning any appender and layout into the logger.
				// the logger will not be able to log.
				loggers.put(deviceName, logger);
			}
			return loggers;
		}
		
		
		// read the properties from the config files. the properties are applie to all devices
		// example, properties for CPU will be applied to all CPU-0, CPU-1, CPU-2, CPU-3
		String folder = ConfigReader.getProperty(deviceCollector + "LogFolder");
		if(!folder.endsWith("/")){ 
			folder = folder + "/";
		}
		
		boolean immediateFlush = ConfigReader.getProperty(deviceCollector + "LogImmediateFlush").equalsIgnoreCase("true");
		boolean isAppend = ConfigReader.getProperty(deviceCollector + "LogAppend").equalsIgnoreCase("true");
		String maxNumberOfDays = ConfigReader.getProperty(deviceCollector + "LogMaxNumberOfDays");
		String compressBackup = ConfigReader.getProperty(deviceCollector + "LogCompressBackups");
		String layoutPattern = ConfigReader.getProperty(deviceCollector + "LogPattern");
		
		for(String deviceName : devicesNames){
			CustodianDailyRollingFileAppender appTemp = new CustodianDailyRollingFileAppender();
			appTemp.setName(deviceName);
			appTemp.setFile( folder + deviceName + ".log");
			appTemp.setImmediateFlush(immediateFlush);
			appTemp.setAppend(isAppend);
			appTemp.setMaxNumberOfDays(maxNumberOfDays);
			appTemp.setCompressBackups(compressBackup);
			PatternLayout ly = new PatternLayout();
			ly.setConversionPattern(layoutPattern);
			appTemp.setLayout(ly);
			appTemp.activateOptions();
			Logger logTemp = Logger.getLogger(deviceName);
			logTemp.addAppender(appTemp);
			loggers.put(deviceName, logTemp);
		}
		
		return loggers;
	}
}
