package soda.aggregator.collector.factory;

import soda.aggregator.collector.tool.CPUCollector;
import soda.aggregator.collector.tool.DFCollector;
import soda.aggregator.collector.tool.DiskCollector;
import soda.aggregator.collector.tool.MemoryCollector;
import soda.aggregator.collector.tool.NetworkCollector;

/**
 * Collector Factory for Linux OS. It will create all
 * the collector tools that are supposed to work on Linux OS
 * @author Vong Vithyea Srey
 *
 */
public class LinuxCollectorFactory implements CollectorFactory{

	@Override
	public CollectorFactory getCollectorFactory() {
		// TODO Auto-generated method stub
		return null;
	}

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
