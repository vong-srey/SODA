package soda.aggregator.collector.factory;

import soda.aggregator.collector.tool.sigarsupportos.CPUCollector;
import soda.aggregator.collector.tool.sigarsupportos.DFCollector;
import soda.aggregator.collector.tool.sigarsupportos.DiskCollector;
import soda.aggregator.collector.tool.sigarsupportos.MemoryCollector;
import soda.aggregator.collector.tool.sigarsupportos.NetworkCollector;


/**
 * Abstract Factory interface
 * @author Vong Vithyea Srey
 *
 */
public interface CollectorFactory {
	public CPUCollector	getCPUCollector();
	public MemoryCollector getMemoryCollector();
	public DFCollector getDFCollector();
	public DiskCollector getDiskCollector();
	public NetworkCollector getNetworkCollector();
}
