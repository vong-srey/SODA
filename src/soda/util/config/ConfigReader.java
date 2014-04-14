package soda.util.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;

import org.apache.log4j.Logger;

import soda.util.logger.LoggerBuilder;

/**
 * Read the config file and return the property that asked for.
 * @author adminuser
 *
 */
public class ConfigReader {

	/**
	 * Logger object that responsible to log the application (itself) performance, error and other statuses
	 */
	private static Logger appLogger = LoggerBuilder.getAppLogger();
	
	
	
	/**
	 * the default path to find config.cfg
	 */
	private static String DEFAULT_CONFIG_PATH = "./config.cfg";
	
	
	
	/**
	 * get the Properties object for the config file, given by the path.
	 * @param path to that config file
	 * @return Properties object
	 */
	public static Properties getConfigProperty(String path){
		Properties configFile = new Properties();
		
		File file = new File(path);
		
		try{
			configFile.load(new FileInputStream(file));
		} catch (IOException e){
			appLogger.error(String.format("Configure file at: %s cannot be found/read.", path), e);
			System.out.println("Config file cannot not be found or cannot be read.");
		}
		
		appLogger.info(String.format("Configure file at: %s loaded successfully.", path));
		
		return configFile;
	}
	
	
	
	/**
	 * read the property "propName" from the config file given by its "path"
	 * @param path to the config file
	 * @param propName that need to get its property value
	 * @return value of the given propName key as a String
	 * @throws NoSuchElementException if the propName key is not found
	 */
	public static String getPropertyFrom(String path, String propName) throws NoSuchElementException{
		
		Properties configFile = getConfigProperty(path);
		String property = configFile.getProperty(propName);
		if(property == null){
			appLogger.error(String.format("Key %s cannot be found/gotten from config file: %s.", propName, path));
			throw new NoSuchElementException("Property: " + propName + " cannot be found in the config file.");
		}
		
		appLogger.info(String.format("Key %s retrieved successfully from config file: %s.", propName, path));
		
		return property;
	}
	
	
	
	/**
	 * read the property "propName" from the config file given by the DEFAULT_CONFIG_PATH
	 * @param propName that need to get its property value
	 * @return
	 */
	public static String getProperty(String propName){	
		return getPropertyFrom(DEFAULT_CONFIG_PATH, propName);
	}
	
	
	
	/**
	 * set the DEFAULT_CONFIG_PATH constant
	 * @param path to the config file that used to set the DEFAULT_CONFIG_PATH
	 */
	public static void setDefaultConfigPath(String path){
		DEFAULT_CONFIG_PATH = path;
	}
	
	
	
	/**
	 * check the given OS name whether it is supported by this application
	 * @param os - OS name
	 * @return true if the OS name is in the supported OS(es) list.
	 */
	public static boolean isGivenOSSupported(String os){
		return ConfigKeysValues.SUPPORT_OS.contains(os);
	}
	
	
	
	/**
	 * @return the DEFAULT_CONFIG_PATH
	 */
	public static String getDefaultConfigPath(){
		return DEFAULT_CONFIG_PATH;
	}
	
}
