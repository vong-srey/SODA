package soda.util.logger;

import java.util.NoSuchElementException;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Level;

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
	
	/**
	 * the final product, Logger object.
	 */
	private Logger logger;
	
	/**
	 * FileAppender object that is used by the product logger
	 */
	private FileAppender appender;
	
	/**
	 * name of the logger and appender
	 */
	private String name;
	
	
	
	/**
	 * name of the RootLogger that log this app statuses and activities
	 */
	private static final String appLoggerName = "AppLogger";
	
	/**
	 * Logger object that responsible to log the application (itself) performance, error and other statuses
	 */
	private static final Logger appLogger = Logger.getLogger(appLoggerName);
	
	
	/**
	 * Default constructor, build an appender as CustodianDailyRollingFaileAppender object.
	 * It is a FileAppender object that rolling the files based on the configured maxNumberOfDays. 
	 */
	public LoggerBuilder(){
		appender = new CustodianDailyRollingFileAppender();
	}
	
	
	
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
		this.name = name;
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
	
	
	public static boolean setAppenderForAppLoggerFromDefaultConfigFile(){
		
		if(! ConfigReader.getProperty(appLoggerName).equalsIgnoreCase("yes")){
			return false;
		}
		
		CustodianDailyRollingFileAppender temp = new CustodianDailyRollingFileAppender();
		temp.setName(appLoggerName);
		temp.setFile(ConfigReader.getProperty(appLoggerName + "LogFile"));
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
	 * return the Logger product.
	 * @return the Logger product.
	 */
	public Logger getLogger(){
		appender.activateOptions();
		logger = Logger.getLogger(name);
		logger.addAppender(appender);
		return logger;
	}
	
	
	
	/**
	 * a wrapper to return RootLogger object that shared among the application.
	 * This RootLogger is used to log the application log itself.
	 * @return RootLogger that is used to log the application activity and status itself.
	 */
	public static Logger getAppLogger(){
		return appLogger;
	}
}
