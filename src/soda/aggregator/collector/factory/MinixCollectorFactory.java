package soda.aggregator.collector.factory;

import soda.aggregator.collector.tool.sigarsupportos.CPUCollector;
import soda.aggregator.collector.tool.sigarsupportos.DFCollector;
import soda.aggregator.collector.tool.sigarsupportos.DiskCollector;
import soda.aggregator.collector.tool.sigarsupportos.MemoryCollector;
import soda.aggregator.collector.tool.sigarsupportos.NetworkCollector;


/**
 * Collector Factory for Minix OS. It will create all
 * the collector tools that are supposed to work on Minix OS
 * 
 * This class is intentionally left unimplemented for the purpose to show how to
 * implement new CollectorFactory for Minix OS. So, you can do the same for other OS.
 * You also need to implement all the tools in soda.aggregator.collector.tool.minix package.
 * Then you can just change "OS" value in the config file, it will works automatically. 
 * 
 * @author Vong Vithyea Srey
 *
 */
public class MinixCollectorFactory implements CollectorFactory{

	@Override
	public CPUCollector getCPUCollector() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MemoryCollector getMemoryCollector() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DFCollector getDFCollector() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DiskCollector getDiskCollector() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NetworkCollector getNetworkCollector() {
		// TODO Auto-generated method stub
		return null;
	}

}
