package soda.aggregator.collector.factory;

import soda.aggregator.collector.tool.sigarsupportos.CPUCollector;
import soda.aggregator.collector.tool.sigarsupportos.DFCollector;
import soda.aggregator.collector.tool.sigarsupportos.DiskCollector;
import soda.aggregator.collector.tool.sigarsupportos.MemoryCollector;
import soda.aggregator.collector.tool.sigarsupportos.NetworkCollector;

/**
 * Collector Factory for OS(es) that are supported by Sigar. It will create all
 * the collector tools that are supposed to work on those OS(es).
 * 
 * OS(es) that supported by Sigar are: Linux, Windows, HPUX, Solaris, AIX, FreeBSD, MacOSX
 *
 * @author Vong Vithyea Srey
 *
 */
public class SigarSupportCollectorFactory implements CollectorFactory{

	public SigarSupportCollectorFactory(){
		System.out.print("this is Linux collector");
	}
	
	
	@Override
	public CPUCollector getCPUCollector() {
		return new CPUCollector();
	}

	@Override
	public MemoryCollector getMemoryCollector() {
		// TODO Auto-generated method stub
		return new MemoryCollector();
	}

	@Override
	public DFCollector getDFCollector() {
		// TODO Auto-generated method stub
		return new DFCollector();
	}

	@Override
	public DiskCollector getDiskCollector() {
		// TODO Auto-generated method stub
		return new DiskCollector();
	}

	@Override
	public NetworkCollector getNetworkCollector() {
		// TODO Auto-generated method stub
		return new NetworkCollector();
	}

}
