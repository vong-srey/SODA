package soda.aggregator.collector.tool.minix;

import java.util.Map;
import java.util.Set;

import soda.aggregator.collector.tool.sigarsupportos.MemoryCollector;


/**
 * Collector Tool that responsible to collect the memory usages (Free, used, swap)
 * This tool aggregates Sigar.Free
 * 
 * This class is intentionally left unimplemented for the purpose to show how to
 * implement new Collector tool for Minix OS. So, you can do the same for other OS.
 * You also need to implement MinixCollectorFactory.
 * Then you can just change "OS" value in the config file, it will works automatically. 
 * 
 * @author Vong Vithyea Srey
 *
 */
public class MinixMemoryCollector extends MemoryCollector{

	@Override
	public Set<Map<String, String>> getPerformance() {
		// TODO Auto-generated method stub
		return null;
	}

}
