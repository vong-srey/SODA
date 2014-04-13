package soda.aggregator.collector.tool.sigarsupportos;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.NfsFileSystem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import soda.aggregator.collector.tool.CollectorTool;
import soda.util.logger.LoggerBuilder;


/**
 * Collector Tool that responsible to collect the disk usages (Free, used in %)
 * This tool aggregates Sigar.Df
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
public class DFCollector extends CollectorTool{
	
	/**
	 * configure the logger during this object instantiation
	 */
	public DFCollector(){
		configureLogger();
		LoggerBuilder.getAppLogger().info("DFCollector is instantiated successfully");
	}

	
	
	/**
	 * a helper method to convert the size in MB (in long) to GB 
	 * @param size in MB
	 * @return String of size in GB
	 */
	public static String sigarFormatSize(long size){
		return Sigar.formatSize(size * 1024);
	}
	
	
	
	/**
	 * a helper method to collect data for the give Volume (fs)
	 * @param fs - sigar.FileSystem Object represent a FileSystem (or a Volume)
	 * @return a map (LinkedHashMap) of "Performance Description" (as a key) and "Performance Value" for the given volume (fs)
	 */
	public Map<String, String> getPerformanceOfGivenVolume(FileSystem fs) throws SigarException {
		
		Sigar sigar = new Sigar();

		Map<String, String> performance = new LinkedHashMap<String, String>();
		
		// check whether this volume can be accessed or not (if it is a NFS FileSystem)
		if (fs instanceof NfsFileSystem) {
			NfsFileSystem nfs = (NfsFileSystem) fs;
			// if nfs can't reply the ping, means this nfs drive can't be accessed.
			// we are not concerning about this drive anyway. => return empty map
			if (!nfs.ping()) {
				LoggerBuilder.getAppLogger().error(nfs.getUnreachableMessage());
				return performance;
			}
		}
		
		FileSystemUsage volume = sigar.getFileSystemUsage(fs.getDirName());
		
		// get the volume name
		String name = fs.getDevName();
		// if there's no "/" => substring start from 0 (-1 + 1)
		// else it will start from index + 1 ("/" will not be included)
		name = name.substring(name.lastIndexOf("/") + 1);
		
		performance.put(name + "-Size", sigarFormatSize(volume.getTotal()));
		
		long temp = volume.getTotal() - volume.getFree();
		performance.put(name + "-Used", sigarFormatSize(temp));
		
		performance.put(name + "-Avail", sigarFormatSize(volume.getAvail()));
		
		temp = (long)(volume.getUsePercent() * 100);
		performance.put(name + "-UsedInPerc", temp + "%");
		
		return performance;
    }
	
	
	
	/**
	 * collect a map of "Performance Description" (as a key) and "Performance Value" from the implemented CollectorTool
	 * @return a map (LinkedHashMap) of "Performance Description" (as a key) and "Performance Value" from the implemented CollectorTool
	 * @throws SigarException if the Method cannot retrieve the data about that hardware (i.e. for CPU, can't get number of core, etc)
	 */
	@Override
	public Set<Map<String, String>> getPerformance() throws SigarException{
		Sigar sigar = new Sigar();
		Set<Map<String,String>> perfSet = new LinkedHashSet<Map<String, String>>();
		
		FileSystem[] fslist = sigar.getFileSystemList();
		
		for(int i=0; i < fslist.length; i++){
			perfSet.add(getPerformanceOfGivenVolume(fslist[i]));
		}

		return perfSet; 
	}

}
