package soda.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;

/**
 * Read the config file and return the property that asked for.
 * @author adminuser
 *
 */
public class ConfigReader {
	
	private static String DEFAULT_CONFIG_PATH = "/etc/soda/config/config.cfg";
	
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
			System.out.println("Config file cannot not be found or cannot be read.");
		}
		
		return configFile;
	}
	
	
	
	/**
	 * read the property "propName" from the config file given by its "path"
	 * @param path to the config file
	 * @param propName that need to get its property value
	 * @return
	 */
	public static String getPropertyFrom(String path, String propName){
		
		Properties configFile = getConfigProperty(path);
		String property = configFile.getProperty(propName);
		if(property == null){
			throw new NoSuchElementException("Property: " + propName + " cannot be found in the config file.");
		}
		return property;
	}
	
	
	
	/**
	 * read the property "propName" from the config file given by the DEFAULT_CONFIG_PATH
	 * @param propName that need to get its property value
	 * @return
	 */
	public static String getPropertyFrom(String propName){	
		return getPropertyFrom(DEFAULT_CONFIG_PATH, propName);
	}
	
	
	
	/**
	 * set the DEFAULT_CONFIG_PATH constant
	 * @param path to the config file that used to set the DEFAULT_CONFIG_PATH
	 */
	public static void setDefaultConfigPath(String path){
		DEFAULT_CONFIG_PATH = path;
	}
	
	
	
	
	public static boolean isGivenOSSupported(String os){
		return ConfigKeysValues.SUPPORT_OS.contains(os);
	}
	
}
