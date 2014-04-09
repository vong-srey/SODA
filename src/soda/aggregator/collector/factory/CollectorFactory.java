package soda.aggregator.collector.factory;

import soda.aggregator.collector.tool.CPUCollector;
import soda.aggregator.collector.tool.DFCollector;
import soda.aggregator.collector.tool.DiskCollector;
import soda.aggregator.collector.tool.MemoryCollector;
import soda.aggregator.collector.tool.NetworkCollector;


/**
 * Abstract Factory interface
 * @author Vong Vithyea Srey
 *
 */
public interface CollectorFactory {
	public CollectorFactory getCollectorFactory();
	public CPUCollector	getCPUCollector();
	public MemoryCollector getMemoryCollector();
	public DFCollector getDFCollector();
	public DiskCollector getDiskCollector();
	public NetworkCollector getNetworkCollector();
}
