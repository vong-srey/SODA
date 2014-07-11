package test.soda.aggregator.collector.factory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.hyperic.sigar.SigarException;
import org.junit.Test;

import soda.aggregator.collector.factory.SigarSupportCollectorFactory;
import soda.aggregator.collector.tool.sigarsupportos.CPUCollector;
import soda.aggregator.collector.tool.sigarsupportos.DFCollector;
import soda.aggregator.collector.tool.sigarsupportos.DiskCollector;
import soda.aggregator.collector.tool.sigarsupportos.MemoryCollector;
import soda.aggregator.collector.tool.sigarsupportos.NetworkCollector;
import soda.aggregator.collector.tool.sigarsupportos.ProcsCollector;



/**
 * This Test aim to exercise SigarSupportAndCollectorFactory class.
 * This Test has achieved Code Coverage, Branch Coverage
 * 
 * @author vong vithyea srey
 *
 */
public class TestSigarSupportAndCollectorFactory {

	private SigarSupportCollectorFactory scf = new SigarSupportCollectorFactory();
	
	@Test
	public void testGetCPUCollector() throws SigarException {
		assertTrue(scf.getCPUCollector() != null);
		assertTrue(scf.getCPUCollector() instanceof CPUCollector);
		assertEquals(scf.getCPUCollector().getClass().getSuperclass().getSimpleName(), "CollectorTool");
	}
	
	
	
	@Test
	public void testGetMemoryCollector() throws SigarException {
		assertTrue(scf.getMemoryCollector() != null);
		assertTrue(scf.getMemoryCollector() instanceof MemoryCollector);
		assertEquals(scf.getMemoryCollector().getClass().getSuperclass().getSimpleName(), "CollectorTool");
	}

	
	
	@Test
	public void testGetDFCollector() throws SigarException {
		assertTrue(scf.getDFCollector() != null);
		assertTrue(scf.getDFCollector() instanceof DFCollector);
		assertEquals(scf.getDFCollector().getClass().getSuperclass().getSimpleName(), "CollectorTool");
	}
	
	
	
	@Test
	public void testGetDiskCollector() throws SigarException {
		assertTrue(scf.getDiskCollector() != null);
		assertTrue(scf.getDiskCollector() instanceof DiskCollector);
		assertEquals(scf.getDiskCollector().getClass().getSuperclass().getSimpleName(), "CollectorTool");
	}
	
	
	
	@Test
	public void testGetNetworkCollector() throws SigarException {
		assertTrue(scf.getNetworkCollector() != null);
		assertTrue(scf.getNetworkCollector() instanceof NetworkCollector);
		assertEquals(scf.getNetworkCollector().getClass().getSuperclass().getSimpleName(), "CollectorTool");
	}
	
	
	
	@Test
	public void testGetProcsCollector() throws SigarException {
		assertTrue(scf.getProcsCollector() != null);
		assertTrue(scf.getProcsCollector() instanceof ProcsCollector);
		assertEquals(scf.getProcsCollector().getClass().getSuperclass().getSimpleName(), "CollectorTool");
	}
	
}
