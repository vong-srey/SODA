package soda.aggregator.collector.tool.sigarsupportos;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.Swap;

import soda.aggregator.collector.tool.CollectorTool;
import soda.util.logger.LoggerBuilder;


/**
 * Collector Tool that responsible to collect the memory usages (Free, used, swap)
 * This tool aggregates Sigar.Free
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
public class MemoryCollector extends CollectorTool{

	/**
	 * configure the logger during this object instantiation
	 * @throws SigarException if the Method cannot retrieve the info about that hardware (i.e. for CPU, can't get number of core, etc)SigarException 
	 */
	public MemoryCollector() throws SigarException{
		configureLogger();
		LoggerBuilder.getAppLogger().info("MemoryCollector is instantiated successfully");
	}
	
	
	
	
	/**
	 * collect a set of maps. A map contains "Performance Description" (as a key) and "Performance Value" from the implemented CollectorTool
	 * @return a set of maps. a map (LinkedHashMap) contains "Performance Description" (as a key) and "Performance Value" from the implemented CollectorTool. Each set of map represent the data of each different component (e.g. a set of CPU0, set of CPU1 and so on)
	 * @throws SigarException if the Method cannot retrieve the info about that hardware (i.e. for CPU, can't get number of core, etc)
	 */
	@Override
	public Set<Map<String, String>> getPerformance() throws SigarException {
		Set<Map<String, String>> perfSet = new LinkedHashSet<Map<String, String>>();
		
		
		
		Sigar sigar = new Sigar();
		Mem mem   = sigar.getMem();
        Swap swap = sigar.getSwap();
        
        
        Map<String, String> map = new LinkedHashMap<String, String>();
        
        map.put(this.DEVICE_NAME, "Memory-Swap");
        map.put(this.DESCRIPTION, "Total-Byte,Used-Byte,Free-Byte,Swap_Total-Byte,Swap_Used-Byte,Swap_Free-Byte");
        
        strBuilder.setLength(0);
        
        // add memory Total, Used, Free
        // just use Sigar.formatSize(long size) if we want to format GB or MB automatically
        strBuilder.append(mem.getTotal());
        strBuilder.append(" ");
        strBuilder.append(mem.getUsed());
        strBuilder.append(" ");
        strBuilder.append(mem.getFree());
        strBuilder.append(" ");
        
        // Swap
        strBuilder.append(swap.getTotal());
        strBuilder.append(" ");
        strBuilder.append(swap.getUsed());
        strBuilder.append(" ");
        strBuilder.append(swap.getFree());
        
        map.put(this.VALUE, strBuilder.toString());
        strBuilder.setLength(0);
        
        perfSet.add(map);
        
        return perfSet;
	}

}
