package test.soda.aggregator.collector.tool;

import static org.junit.Assert.assertEquals;

import org.hyperic.sigar.SigarException;
import org.junit.Test;

import soda.aggregator.collector.tool.CollectorTool;
import soda.aggregator.collector.tool.sigarsupportos.CPUCollector;
import soda.aggregator.collector.tool.sigarsupportos.DFCollector;
import soda.aggregator.collector.tool.sigarsupportos.DiskCollector;
import soda.aggregator.collector.tool.sigarsupportos.MemoryCollector;



/**
 * This Test aim to exercise all implemented methods in CollectorTool abstract class.
 * all its abstract methods are not tested in this test cases. 
 * they are tested in their respective implementation class.
 * e.g. CPUCollector is tested in TestCPUCollector
 * 
 * This Test has achieved Code Coverage, Branch Coverage
 * 
 * @author vong vithyea srey
 *
 */
public class TestCollectorTool {

	@Test(expected=IllegalArgumentException.class)
	public void testConvertingMByteToByte() throws SigarException {
		CollectorTool ct = new CPUCollector();
		
		// test valid partition
		assertEquals(ct.megaByteToByte(-99999), "0");
		assertEquals(ct.megaByteToByte(0), "0");
		assertEquals(ct.megaByteToByte(1), "1048576");
		assertEquals(ct.megaByteToByte(99999), "104856551424");

		// test invalid partition
		ct.megaByteToByte(Integer.MAX_VALUE);
	}
	
	
	
	@Test
	public void testConvertingByteToMByte() throws SigarException {
		CollectorTool ct = new DFCollector();
		
		assertEquals(ct.byteToMegaByte(-99999), "0");
		assertEquals(ct.byteToMegaByte(0), "0");
		assertEquals(ct.byteToMegaByte(1), "0");
		assertEquals(ct.byteToMegaByte(99999999999L), "95367");
	}
	
	
	
	@Test
	public void testGetOneDecPerc() throws SigarException{
		CollectorTool ct = new DiskCollector();
		
		assertEquals(ct.getOneDecPerc(-99999), "0.0");
		assertEquals(ct.getOneDecPerc(0), "0.0");
		assertEquals(ct.getOneDecPerc(0), "0.0");
		assertEquals(ct.getOneDecPerc(0.000000000001), "0.0");
		assertEquals(ct.getOneDecPerc(0.9999999999), "100.0");
		assertEquals(ct.getOneDecPerc(1), "100.0");
		assertEquals(ct.getOneDecPerc(9999999.00), "999999900.0");
	}
	
	
	
	@Test
	public void testFormatNumb() throws SigarException {
		CollectorTool ct = new MemoryCollector();
		
		// test valid partition
		assertEquals(ct.formatNumb(-99999), "0");
		assertEquals(ct.formatNumb(0), "0");
		assertEquals(ct.formatNumb(1), "1");
		assertEquals(ct.formatNumb(104856551424L), "104856551424");
	}
}
