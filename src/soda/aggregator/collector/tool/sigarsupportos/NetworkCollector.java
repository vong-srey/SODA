package soda.aggregator.collector.tool.sigarsupportos;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.Tcp;

import soda.aggregator.collector.tool.CollectorTool;
import soda.util.logger.LoggerBuilder;


/**
 * Collector Tool that responsible to collect the network statistic (TCP, UDP in and out)
 * This tool aggregates Sigar.NetStat
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
public class NetworkCollector extends CollectorTool{

	public NetworkCollector(){
		configureLogger();
		LoggerBuilder.getAppLogger().info("NetworkCollector is instantiated successfully");
	}
	
	
	/**
	 * collect a set of maps. A map contains "Performance Description" (as a key) and "Performance Value" from the implemented CollectorTool
	 * @return a set of maps. a map (LinkedHashMap) contains "Performance Description" (as a key) and "Performance Value" from the implemented CollectorTool. Each set of map represent the data of each different component (e.g. a set of CPU0, set of CPU1 and so on)
	 * @throws SigarException if the Method cannot retrieve the info about that hardware (i.e. for CPU, can't get number of core, etc)
	 */
	@Override
	public Set<Map<String, String>> getPerformance() throws SigarException {
		Set<Map<String, String>> perfSet = new LinkedHashSet<Map<String, String>>();
		Map<String, String> map = new LinkedHashMap<String, String>();
		
		Sigar sigar = new Sigar();
		Tcp stat = sigar.getTcp();
		
		map.put("TCP-ActiveConnOpen", String.valueOf(stat.getActiveOpens()));		// active connection openings
		map.put("TCP-PassieConnOpen", String.valueOf(stat.getPassiveOpens()));		// passive connection openings
		map.put("TCP-FailedConnAttempts", String.valueOf(stat.getAttemptFails()));	// failed connection attempts
		map.put("TCP-ConnResetsReceived", String.valueOf(stat.getEstabResets()));	// connections resets received
		map.put("TCP-ConnEstablished", String.valueOf(stat.getCurrEstab()));		// connections established
		map.put("TCP-PcktsReceived", String.valueOf(stat.getInSegs()));				// packets received
		map.put("TCP-PcktsSent", String.valueOf(stat.getOutSegs()));				// packets set out
		map.put("TCP-PcktsRetransmited", String.valueOf(stat.getRetransSegs()));	// packets retransmitted
		map.put("TCP-BadPcktsReceived", String.valueOf(stat.getInErrs()));			// bad packets received
		map.put("TCP-PcktsResetSent", String.valueOf(stat.getOutRsts()));			// packets resets Sent
		
		perfSet.add(map);
		
		return perfSet;
	}

}
