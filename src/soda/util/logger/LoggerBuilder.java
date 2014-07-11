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
		if(app == null) throw new IllegalArgumentException();
		appender = app;
	}
	
	
	
	/**
	 * getter of this.appender
	 * @return
	 */
	public FileAppender getAppender(){
		return appender;
	}

	
	
	/**
	 * Set a name for this logger
	 * @param name
	 */
	public void setLoggerName(String name){
		if(name == null) throw new IllegalArgumentException();
		appender.setName(name);
	}

	
	
	/**
	 * Set the path for the logger file.
	 * If the appender is the CustodianDailyRollingFaileAppender, the logger file will be the name of this logger+TodayDate
	 * @param path
	 */
	public void setLoggerFile(String path){
		if(path == null) throw new IllegalArgumentException();
		
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
		if(value == null) throw new IllegalArgumentException();
		
		if(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes")){
			appender.setImmediateFlush(true);
		} else {
			appender.setImmediateFlush(false);
		}
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
		if(value == null) throw new IllegalArgumentException();
		
		if(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes")){
			appender.setAppend(true);
		} else {
			appender.setAppend(false);
		}
	}

	
	
	/**
	 * Set the number of max days that the logs should be stored.
	 * After this number has been reached, it will remove the earliest log and add a new log 
	 * @param numday - maximum number of log of the days should be kept
	 */
	public void setMaxNumberOfDays(int numday){
		if(numday == Integer.MAX_VALUE) throw new IllegalArgumentException();
		
		setMaxNumberOfDays(String.valueOf(numday));
	}

	
	
	/**
	 * Set the number of max days that the logs should be stored.
	 * After this number has been reached, it will remove the earliest log and add a new log 
	 * @param numday - maximum number of log of the days should be kept
	 */
	public void setMaxNumberOfDays(String numDayInString){
		if(numDayInString==null) 
			throw new IllegalArgumentException();
		
		try{
			Integer.parseInt(numDayInString);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException();
		}
		
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
		if(value == null){
			throw new IllegalArgumentException();
		}
		
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
		if(threshold == null) throw new IllegalArgumentException();
		appender.setThreshold(Level.toPriority(threshold));
	}

	
	
	/**
	 * set the pattern of the layout that the appender used to produce the log format.
	 * @param pattern
	 */
	public void setLayoutPattern(String pattern, String header){
		if(pattern==null || header==null) throw new IllegalArgumentException();
		
		SodaPatternLayout ly = new SodaPatternLayout(header);
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
		setLayoutPattern(ConfigReader.getPropertyFrom(configFilePath, appenderName + "LogPattern"), "");

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
		
		// set appender (our customized appender)
		CustodianDailyRollingFileAppender temp = new CustodianDailyRollingFileAppender();
		temp.setName(appLoggerName);
		String folder = ConfigReader.getProperty(appLoggerName + "LogFolder");
		if(!folder.endsWith("/")){ 
			folder = folder + "/";
		}
		temp.setFile(folder + "app.log");
		String immediateFlush = ConfigReader.getProperty(appLoggerName + "LogImmediateFlush");
		temp.setImmediateFlush(immediateFlush.equalsIgnoreCase("true"));
		String append = ConfigReader.getProperty(appLoggerName + "LogAppend");
		temp.setAppend(append.equalsIgnoreCase("true"));
		temp.setMaxNumberOfDays(ConfigReader.getProperty(appLoggerName + "LogMaxNumberOfDays"));
		temp.setCompressBackups(ConfigReader.getProperty(appLoggerName + "LogCompressBackups"));
		String threshold = ConfigReader.getProperty(appLoggerName + "LogThreshold");
		temp.setThreshold(Level.toPriority(threshold));
		// set layout (our customized PatternLayout)
		SodaPatternLayout ly = new SodaPatternLayout("");
		ly.setConversionPattern(ConfigReader.getProperty(appLoggerName + "LogPattern"));
		temp.setLayout(ly);
		temp.activateOptions();
		// set up logger
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
	
	
	
	/**
	 * @return 
	 * 
	 */
	public List<CustodianDailyRollingFileAppender> getAppenders(){
		return appenders;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// ********************************************************************************************************************
	//
	// used for set up a collection of loggers. a map object which has loggerName as the key and logger object as the value
	//
	// *********************************************************************************************************************
	
	/**
	 * read the config file (given by its path), and set all the CustodianDailyRollingFaileAppender properties.
	 * @param devicesNames - a list of all the devices. it will be used to create a logger for each device
	 * @param header - a header content for SodaPatternLayout (it will print the header on every log file)
	 * return true if this configFile activates this logger and the appender for this logger can be configured successfully
	 * throw IllegalArgumentException if the given appenderName doesn't have the corresponding CollectorTool
	 */
	public Map<String, Logger> getLoggersMapFromDefaultConfigFile(List<String> devicesNames, String header){
		// the first element is the Device Collector (e.g. CPUCollector, DiskCollecor, or DFCollecor, or NetworkCollecor, or MemoryCollecor)
		String deviceCollector = devicesNames.remove(0);

		// check if the deviceCollector is in the AVAILABLE_TOOLS
		// protect the case that user is giving some bad devicesNames list
		if(!CollectorTool.AVAILABLE_TOOLS.contains(deviceCollector)){
			throw new IllegalArgumentException("Given appenderName doesnot have a correspond CollectorTool");
		}
	
		// loggers map (will return this loggers)
		Map<String, Logger> loggers= new HashMap<String, Logger>();
		
		// if it is configured to not log the performance. then just return a map containing an empty logger\
		// empty name logger is not configured to log (so it means it won't do anything)
		if( ! ConfigReader.getProperty(deviceCollector).equalsIgnoreCase("yes")){
			Logger temp = Logger.getLogger("");
			for(String deviceName : devicesNames){
				// by not assigning any appender and layout into the logger.
				// the logger will not be able to log.
				loggers.put(deviceName, temp);
			}
			return loggers;
		}
		
		
		// read the properties from the config files. the properties are applied to all devices
		// example, properties for CPU will be applied to all CPU-0, CPU-1, CPU-2, CPU-3 and so on.
		String folder = ConfigReader.getProperty(deviceCollector + "LogFolder");
		if(!folder.endsWith("/")){ 
			folder = folder + "/";
		}
		boolean immediateFlush = ConfigReader.getProperty(deviceCollector + "LogImmediateFlush").equalsIgnoreCase("true");
		boolean isAppend = ConfigReader.getProperty(deviceCollector + "LogAppend").equalsIgnoreCase("true");
		String maxNumberOfDays = ConfigReader.getProperty(deviceCollector + "LogMaxNumberOfDays");
		String compressBackup = ConfigReader.getProperty(deviceCollector + "LogCompressBackups");
		String layoutPattern = ConfigReader.getProperty(deviceCollector + "LogPattern");
		
		
		// Iterate through each deviceName and set up a logger/appender/layout for each device
		// then put each logger into the loggers map (each device has its own logger, which is different from other loggers)
		for(String deviceName : devicesNames){
			// set appender (our customized appender)
			CustodianDailyRollingFileAppender appTemp = new CustodianDailyRollingFileAppender();
			appTemp.setName(deviceName);
			appTemp.setFile( folder + deviceName + ".log");
			appTemp.setImmediateFlush(immediateFlush);
			appTemp.setAppend(isAppend);
			appTemp.setMaxNumberOfDays(maxNumberOfDays);
			appTemp.setCompressBackups(compressBackup);
			// set layout (our customized PatternLayout)
			SodaPatternLayout ly = new SodaPatternLayout(header);
			ly.setConversionPattern(layoutPattern);
			appTemp.setLayout(ly);
			appTemp.activateOptions();
			// set up logger
			Logger logTemp = Logger.getLogger(deviceName);
			logTemp.addAppender(appTemp);
			loggers.put(deviceName, logTemp);
		}
		
		return loggers;
	}
}
