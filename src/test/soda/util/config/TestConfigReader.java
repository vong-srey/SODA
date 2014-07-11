package test.soda.util.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Properties;

import org.junit.Test;

import soda.util.config.ConfigReader;

public class TestConfigReader {

	@Test
	public void testGetterSetterDefaultConfigPath() {
		assertEquals(ConfigReader.getDefaultConfigPath(), "./config.cfg");
		
		ConfigReader.setDefaultConfigPath("helloworld");
		assertEquals(ConfigReader.getDefaultConfigPath(), "helloworld");
		
		try{
			ConfigReader.setDefaultConfigPath("  ");
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e){}
		
		try{
			ConfigReader.setDefaultConfigPath(null);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e){}
		
		ConfigReader.setDefaultConfigPath("./ConfigForTest.cfg");
		assertEquals(ConfigReader.getDefaultConfigPath(), "./ConfigForTest.cfg");
	}
	
	
	
	@Test
	public void testIsGivenOSSupported(){
		assertTrue(ConfigReader.isGivenOSSupported("SigarSupport"));
		assertTrue(ConfigReader.isGivenOSSupported("Minix"));
		assertFalse(ConfigReader.isGivenOSSupported("helloworld"));
		
		try{
			ConfigReader.isGivenOSSupported("  ");
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e){}
		
		try{
			ConfigReader.isGivenOSSupported(null);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e){}
	}
	
	
	
	@Test
	public void testGettingProperty(){
		assertEquals(ConfigReader.getProperty("AppLogger"), "yes");
		assertEquals(ConfigReader.getProperty("CPUCollector"), "no");
		assertEquals(ConfigReader.getProperty("DiskCollectorLogCompressBackups"), "true");
		assertEquals(ConfigReader.getProperty("DFCollectorLogImmediateFlush"), "false");
		assertEquals(ConfigReader.getProperty("MemoryCollectorLogMaxNumberOfDays"), "-9999");
		assertEquals(ConfigReader.getProperty("NetworkCollectorLogMaxNumberOfDays"), "4");
		assertEquals(ConfigReader.getProperty("AppLoggerLogPattern"), "%d{dd/MMM/yyyy-HH:mm:ss.SSS} %-5p %c{1}:%L - %m%n");
		
		try{
			ConfigReader.getProperty("helloworld");
		} catch (NoSuchElementException e){}
		
		try{
			ConfigReader.getProperty("  ");
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e){}
		
		try{
			ConfigReader.getProperty(null);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e){}
	}
	
	
	
	@Test
	public void testGettingPropertyFromAFile(){
		String filename = "./ConfigForTest.cfg";
		
		assertEquals(ConfigReader.getPropertyFrom(filename, "AppLogger"), "yes");
		assertEquals(ConfigReader.getPropertyFrom(filename, "CPUCollector"), "no");
		assertEquals(ConfigReader.getPropertyFrom(filename, "DiskCollectorLogCompressBackups"), "true");
		assertEquals(ConfigReader.getPropertyFrom(filename, "DFCollectorLogImmediateFlush"), "false");
		assertEquals(ConfigReader.getPropertyFrom(filename, "MemoryCollectorLogMaxNumberOfDays"), "-9999");
		assertEquals(ConfigReader.getPropertyFrom(filename, "NetworkCollectorLogMaxNumberOfDays"), "4");
		assertEquals(ConfigReader.getPropertyFrom(filename, "AppLoggerLogPattern"), "%d{dd/MMM/yyyy-HH:mm:ss.SSS} %-5p %c{1}:%L - %m%n");
		
		try{
			ConfigReader.getPropertyFrom(filename, "helloworld");
		} catch (NoSuchElementException e){}
		
		try{
			ConfigReader.getPropertyFrom(filename, "  ");
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e){}
		
		try{
			ConfigReader.getPropertyFrom(filename, null);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e){}
		
		try{
			ConfigReader.getPropertyFrom("  ", "CPUCollector");
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e){}
		
		try{
			ConfigReader.getPropertyFrom(null, "CPUCollector");
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e){}
	}
	
	
	
	@Test
	public void testGettingConfigProperty(){
		String path = "./ConfigForTest.cfg";
		
		Properties configFile = new Properties();
		File file = new File(path);
		try{
			configFile.load(new FileInputStream(file));
		} catch (IOException e){
			fail(String.format("file %s cannot be found/read", path));
		}
		
		assertTrue(configFile.equals(ConfigReader.getConfigProperty(path)));
		
		try{
			ConfigReader.getConfigProperty("  ");
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e){}
		
		try{
			ConfigReader.getConfigProperty(null);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e){}
	}

}
