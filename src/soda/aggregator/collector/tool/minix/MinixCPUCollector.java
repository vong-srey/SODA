package soda.aggregator.collector.tool.minix;

import java.util.Map;
import java.util.Set;

import org.hyperic.sigar.SigarException;

import soda.aggregator.collector.tool.sigarsupportos.CPUCollector;


/**
 * Collector Tool that responsible to collect CPU performance
 * This tool aggregates Sigar.CpuInfo
 * 
 * This class is intentionally left unimplemented for the purpose to show how to
 * implement new CollectorTool for Minix OS. So, you can do the same for other OS.
 * Then you need to re-implement collectorTool.configureLogger()
 * You also need to implement MinixCollectorFactory.
 * Then you can just change "OS" value in the config file, it will works automatically. 
 * 
 * @author Vong Vithyea Srey
 *
 */
public class MinixCPUCollector extends CPUCollector{

	public MinixCPUCollector() throws SigarException {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public Set<Map<String, String>> getPerformance() {
		// TODO Auto-generated method stub
		return null;
	}

}
