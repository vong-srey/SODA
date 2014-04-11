package soda.aggregator.collector.tool.sigarsupportos;

import java.util.Map;

import soda.aggregator.collector.tool.CollectorTool;


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
public class MemoryCollector implements CollectorTool{

	@Override
	public Map<String, String> getPerformance() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void recording() {
		// TODO Auto-generated method stub
		
	}

}
