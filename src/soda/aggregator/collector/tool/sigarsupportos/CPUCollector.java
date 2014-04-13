package soda.aggregator.collector.tool.sigarsupportos;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import soda.aggregator.collector.tool.CollectorTool;
import soda.util.logger.LoggerBuilder;


/**
 * Collector Tool that responsible to collect CPU performance
 * This tool aggregates Sigar.CpuInfo
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
public class CPUCollector extends CollectorTool{
	
	/**
	 * configure the logger during this object instantiation
	 */
	public CPUCollector(){
		configureLogger();
		LoggerBuilder.getAppLogger().info("CPUCollector is instantiated successfully");
	}

	
	
	/**
	 * a helper method to collect data for the give CPU
	 * @param cpu - sigar.CpuPerc Object represent a core of CPU or the CPU-Totals
	 * @param coreIndex - the name or index of given core
	 * @return a map (LinkedHashMap) of "Performance Description" (as a key) and "Performance Value" for the given cpu
	 */
	public Map<String, String> getPerformanceOfGivenCpuCore(CpuPerc cpu, String coreIndex) throws SigarException {
		strBuilder.setLength(0);
		Map<String, String> performance = new LinkedHashMap<String, String>();
		
		// get the each category performance and put into HashMap
		performance.put(strBuilder.append(coreIndex).append("-User").toString(), CpuPerc.format(cpu.getUser()));
		strBuilder.setLength(0);
		performance.put(strBuilder.append(coreIndex).append("-Sys").toString(), CpuPerc.format(cpu.getSys()));
		strBuilder.setLength(0);
		performance.put(strBuilder.append(coreIndex).append("-Idle").toString(), CpuPerc.format(cpu.getIdle()));		
		strBuilder.setLength(0);
		performance.put(strBuilder.append(coreIndex).append("-Wait").toString(), CpuPerc.format(cpu.getWait()));
		strBuilder.setLength(0);
		performance.put(strBuilder.append(coreIndex).append("-Nice").toString(), CpuPerc.format(cpu.getNice()));
		strBuilder.setLength(0);
		performance.put(strBuilder.append(coreIndex).append("-IRQ").toString(), CpuPerc.format(cpu.getUser()));
		strBuilder.setLength(0);
		
		return performance;
	}
	
	
	
	/**
	 * collect a set of maps. A map contains "Performance Description" (as a key) and "Performance Value" from the implemented CollectorTool
	 * @return a set of maps. a map (LinkedHashMap) contains "Performance Description" (as a key) and "Performance Value" from the implemented CollectorTool. Each set of map represent the data of each different component (e.g. a set of CPU0, set of CPU1 and so on)
	 * @throws SigarException if the Method cannot retrieve the info about that hardware (i.e. for CPU, can't get number of core, etc)
	 */
	@Override
	public Set<Map<String, String>> getPerformance() throws SigarException {
		Sigar sigar = new Sigar();
		
		// represent the whole core CPU
		CpuPerc cpuTotal = sigar.getCpuPerc();
		// a list of each core of the CPU
		CpuPerc[] cpus = sigar.getCpuPercList();
		
		Set<Map<String, String>> perfSet = new LinkedHashSet();
		
		// put CPU-Totals into the performances
		perfSet.add(getPerformanceOfGivenCpuCore(cpuTotal, "CPUs"));
		
		// put each CPU-core into the performance
		for(int i=0; i<cpus.length; i++){
			perfSet.add(getPerformanceOfGivenCpuCore(cpus[i], "CPU"+i));
		}
		
		return perfSet;
	}

}
