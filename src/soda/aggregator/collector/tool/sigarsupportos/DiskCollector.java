package soda.aggregator.collector.tool.sigarsupportos;

import java.util.ArrayList;
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
 * Collector Tool that responsible to collect the IO (read and write into disk %)
 * This tool aggregates Sigar.IoStat
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
public class DiskCollector extends CollectorTool{


	/**
	 * configure the logger during this object instantiation
	 * @throws SigarException if the Method cannot retrieve the info about that hardware (i.e. for CPU, can't get number of core, etc)
	 */
	public DiskCollector() throws SigarException{
		configureLogger();
		LoggerBuilder.getAppLogger().info("DiskCollector is instantiated successfully");
	}

	
	
	/**
	 * a helper method to collect data for the give Volume (fs)
	 * @param fs - sigar.FileSystem Object represent a FileSystem (or a Volume)
	 * @return a map (LinkedHashMap) of "Performance Description" (as a key) and "Performance Value" for the given volume (fs)
	 */
	public Map<String, String> getPerformanceOfGivenVolume(FileSystem fs) throws SigarException {
		Sigar sigar = new Sigar();

		Map<String, String> performance = new LinkedHashMap<String, String>();
		
        FileSystemUsage volume = sigar.getFileSystemUsage(fs.getDirName());

		// get the volume name
		String name = fs.getDevName();
		// if there's no "/" => substring start from 0 (-1 + 1)
		// else it will start from index + 1 ("/" will not be included)
		name = name.substring(name.lastIndexOf("/") + 1);
		
		performance.put(DEVICE_NAME, "Disk-" + name);
		performance.put(DESCRIPTION, "R_In-Byte,W_Out-Byte,Data_R,Data_W");
		
		strBuilder.setLength(0);
		
		strBuilder.append(volume.getDiskReads());
		strBuilder.append(" ");
		
		strBuilder.append(volume.getDiskWrites());
		strBuilder.append(" ");
		
		// if this getDiskReadBytes is not implemented for this volume (fs), then add "-"
		// otherwise add the actual number
		long temp = volume.getDiskReadBytes();
		if ( temp == Sigar.FIELD_NOTIMPL) {
			strBuilder.append("-");
		} else {
	        // just use Sigar.formatSize(long size) if we want to format GB or MB automatically
			strBuilder.append(temp);
		}
		strBuilder.append(" ");
		
		// if this getDiskWriteBytes is not implemented for this volume (fs), then add "-"
		// otherwise add the actual number
		temp = volume.getDiskWriteBytes();
		if (temp == Sigar.FIELD_NOTIMPL) {
			strBuilder.append("-");
		} else {
			strBuilder.append(temp);
		}
		
		performance.put(this.VALUE, strBuilder.toString());
		
		strBuilder.setLength(0);
		
		return performance;
    }
	
	
	
	/**
	 * collect a set of maps. A map contains "Performance Description" (as a key) and "Performance Value" from the implemented CollectorTool
	 * @return a set of maps. a map (LinkedHashMap) contains "Performance Description" (as a key) and "Performance Value" from the implemented CollectorTool. Each set of map represent the data of each different component (e.g. a set of CPU0, set of CPU1 and so on)
	 * @throws SigarException if the Method cannot retrieve the info about that hardware (i.e. for CPU, can't get number of core, etc)
	 */
	@Override
	public Set<Map<String, String>> getPerformance() throws SigarException{
		Sigar sigar = new Sigar();
		Set<Map<String,String>> perfSet = new LinkedHashSet<Map<String, String>>();
		
		FileSystem[] fslist = sigar.getFileSystemList();
		
		for(int i=0; i < fslist.length; i++){
			if(fslist[i].getType() == FileSystem.TYPE_LOCAL_DISK){
				perfSet.add(this.getPerformanceOfGivenVolume(fslist[i]));
			}
		}

		return perfSet; 
	}

}
