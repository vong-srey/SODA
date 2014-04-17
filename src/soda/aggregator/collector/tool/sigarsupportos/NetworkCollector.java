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

	/**
	 * configure the logger during this object instantiation
	 * @throws SigarException if the Method cannot retrieve the info about that hardware (i.e. for CPU, can't get number of core, etc)
	 */
	public NetworkCollector() throws SigarException{
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
		
		map.put(this.DEVICE_NAME, "Network-TCP");
		map.put(this.DESCRIPTION, "ActiveConnOpen,PassieConnOpen,FailedConnAttempts,ConnResetsReceived,ConnEstablished,"
				+ "PcktsReceived,PcktsSent,PcktsRetransmited,BadPcktsReceived,PcktsResetSent");

		strBuilder.setLength(0);
		
		strBuilder.append(stat.getActiveOpens());			// active connection openings
		strBuilder.append(" ");
		strBuilder.append(stat.getPassiveOpens());			// passive connection openings
		strBuilder.append(" ");
		strBuilder.append(stat.getAttemptFails());			// failed connection attempts
		strBuilder.append(" ");
		strBuilder.append(stat.getEstabResets());			// connections resets received
		strBuilder.append(" ");
		strBuilder.append(stat.getCurrEstab());				// connections established
		strBuilder.append(" ");
		strBuilder.append(stat.getInSegs());				// packets received
		strBuilder.append(" ");
		strBuilder.append(stat.getOutSegs());				// packets set out
		strBuilder.append(" ");
		strBuilder.append(stat.getRetransSegs());			// packets retransmitted
		strBuilder.append(" ");
		strBuilder.append(stat.getInErrs());				// bad packets received
		strBuilder.append(" ");
		strBuilder.append(stat.getOutRsts());				// packets resets Sent
		
		map.put(VALUE, strBuilder.toString());
		perfSet.add(map);
		
		strBuilder.setLength(0);
		
		return perfSet;
	}

}
