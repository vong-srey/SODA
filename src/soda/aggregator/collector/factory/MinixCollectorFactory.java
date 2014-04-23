package soda.aggregator.collector.factory;

import org.hyperic.sigar.SigarException;

import soda.aggregator.collector.tool.minix.MinixCPUCollector;
import soda.aggregator.collector.tool.minix.MinixDFCollector;
import soda.aggregator.collector.tool.minix.MinixDiskCollector;
import soda.aggregator.collector.tool.minix.MinixMemoryCollector;
import soda.aggregator.collector.tool.minix.MinixNetworkCollector;
import soda.aggregator.collector.tool.minix.MinixProcsCollector;


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

	/**
	 * producing the CPU collector and logger for this OS
	 * @return collector and logger of CPU performance for this OS
	 * @throws SigarException if the Method cannot retrieve the info about that hardware (i.e. for CPU, can't get number of core, etc)
	 */
	@Override
	public MinixCPUCollector getCPUCollector() throws SigarException {
		// TODO Auto-generated method stub
		return new MinixCPUCollector();
	}

	

	/**
	 * producing the Memory collector and logger for this OS
	 * @return collector and logger of Memory performance for this OS
	 * @throws SigarException if the Method cannot retrieve the info about that hardware (i.e. for CPU, can't get number of core, etc)SigarException 
	 */
	@Override
	public MinixMemoryCollector getMemoryCollector() throws SigarException {
		// TODO Auto-generated method stub
		return new MinixMemoryCollector();
	}

	

	/**
	 * producing the DF collector and logger for this OS (DF is producing used space and free space of each HDD. Same as df command in Linux/Unix)
	 * @return collector and logger of DF performance for this OS
	 * @throws SigarException if the Method cannot retrieve the info about that hardware (i.e. for CPU, can't get number of core, etc) 
	 */
	@Override
	public MinixDFCollector getDFCollector() throws SigarException {
		// TODO Auto-generated method stub
		return new MinixDFCollector();
	}

	
	
	/**
	 * producing the Disk collector and logger for this OS (the amount of writing and reading, IO)
	 * @return collector and logger of Disk performance for this OS
	 * @throws SigarException if the Method cannot retrieve the info about that hardware (i.e. for CPU, can't get number of core, etc) 
	 */
	@Override
	public MinixDiskCollector getDiskCollector() throws SigarException {
		// TODO Auto-generated method stub
		return new MinixDiskCollector();
	}

	

	/**
	 * producing the Network collector and logger for this OS (TCP, and UDP traffic volumes)
	 * @return collector and logger of Network performance for this OS
	 * @throws SigarException if the Method cannot retrieve the info about that hardware (i.e. for CPU, can't get number of core, etc) 
	 */
	@Override
	public MinixNetworkCollector getNetworkCollector() throws SigarException {
		// TODO Auto-generated method stub
		return new MinixNetworkCollector();
	}



	/**
	 * producing the Processes collector and logger for this OS (the processes' owner, CPU usage, memory usage, threads and its status)
	 * @return collector and logger of Processes performance for the specified PID (in config file) for this OS
	 * @throws SigarException if the Method cannot retrieve the info about that hardware (i.e. for CPU, can't get number of core, etc) 
	 */
	@Override
	public MinixProcsCollector getProcsCollector() throws SigarException {
		// TODO Auto-generated method stub
		return new MinixProcsCollector();
	}

}
