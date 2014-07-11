package soda.util.config;

import java.util.Arrays;
import java.util.List;

import soda.aggregator.collector.tool.CollectorTool;



/**
 * This file is stored all the static values needed for SODA
 * 
 * @author vong vithyea srey
 *
 */
public class ConfigKeysValues {
	
	/**
	 * OS key
	 */
	public static final String OS = "OS";
	
	
	
	/**
	 * The key to determine how offend a performance should be collected and logged.
	 */
	public static final String LOG_FREQUENCY = "LogFrequency";
	
	
	
	/**
	 * the list of all supported OS. {Linux, Windows, HPUX, Solaris, AIX, FreeBSD, MacOSX} are supported.
	 * However, since {Linux, Windows, HPUX, Solaris, AIX, FreeBSD, MacOSX} are supported by Sigar libraries.
	 * So, SigarSupport includes all these OS(es).
	 * So, if the OS that this tool is going to run on is in this list {Linux, Windows, HPUX, Solaris, AIX, FreeBSD, MacOSX}, please use "SigarSupport" instead of the real name (e.g. Windows).
	 */
	public static final List<String> SUPPORT_OS = Arrays.asList(new String[]{"SigarSupport", "Minix"});
	
	
	
	/**
	 * A List of available tools that can provide performance info and logging
	 * This variable is just a pointer pointing to the CollectorTool.AVAILABLE_TOOLS
	 */
	public static final List<String> AVAILABLE_TOOLS = CollectorTool.AVAILABLE_TOOLS;
	
	
	
	/**
	 * @return DEFAULT_CONFIG_PATH
	 */
	public static final String getDefaultConfigPath(){
		return ConfigReader.getDefaultConfigPath();
	}
}
