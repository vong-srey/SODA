package soda.aggregator.collector.tool.sigarsupportos;

import java.sql.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hyperic.sigar.ProcCpu;
import org.hyperic.sigar.ProcCredName;
import org.hyperic.sigar.ProcMem;
import org.hyperic.sigar.ProcState;
import org.hyperic.sigar.ProcUtil;
import org.hyperic.sigar.SigarException;

import soda.aggregator.collector.tool.CollectorTool;
import soda.util.config.ConfigReader;
import soda.util.logger.LoggerBuilder;

/**
 * Collector Tool that responsible to collect the process statistic (the processes' owner, CPU usage, memory usage, threads and its status)
 * The processes that its performances will be collected are defined in the config file with its PID(s).
 * This tool aggregates Sigar.ProcCredName, Sigar.ProcMem, Sigar.ProcState, Sigar.ProcCPU, Sigar.ProcUtil
 * 
 * Classes and Interfaces in this package is implemented using Sigar.
 * Sigar is supporting Linux, Windows, HPUX, Solaris, AIX, FreeBSD, MacOSX.
 * So, these OS(es) will not be implemented separately. The implementations
 * in this package will be able to support those OS(es).
 * However, if you want this tool to support any other OS(es), you need to
 * implements each tools for each OS by extending the tool from this package.
 * And you also need to implement the CollectorFactory class for that OS as well.
 * An example has been done for the Minix OS, please check package soda.aggregator.collector.tool.minix
 * and MinixCollectorFactory 
 * 
 * @author Vong Vithyea Srey
 *
 */
public class ProcsCollector extends CollectorTool{

	protected List<String> pids;
	protected Map<String, String> pidsNames;
	
	/**
	 * configure the logger during this object instantiation
	 * @throws SigarException if the Method cannot retrieve the info about that hardware (i.e. for CPU, can't get number of core, etc)
	 */
	public ProcsCollector() throws SigarException{
		// need to get the PIDsList first (at least, it must be before configureLogger())
		// beucase configureLogger() need to know how many processes needed to be logged
		// so, it will create logger as many as processes needed to be logged.
		setupPIDsListFromDefaultConfigFile();
		configureLogger();
		LoggerBuilder.getAppLogger().info("NetworkCollector is instantiated successfully");
	}
	
	
	
	/**
	 * a helper method to collect data for the given PID
	 * @param pid - is the given pid that need to get its performance data
	 * @return a map (LinkedHashMap) of "Performance Description" (as a key) and "Performance Value" for the given pid
	 */
	public Map<String, String> getPerformanceOfGivenPID(String pid){
		Map<String, String> performance = new LinkedHashMap<String, String>();
		
		String name = pidsNames.get(pid);
		name = name.replaceAll("\\W", ""); //remove all non-word (word: [a-zA-Z0-9]) chars
		performance.put(DEVICE_NAME, "Proc_"+name);
		
		DecimalFormat d = new DecimalFormat("0.0");
		
		/* ***********************************************************************************
		 * Appending the log data into the strBuilder.
		 * If you are modifying the order or log data, you also need to modify the logHeader
		 * in setupLogHeader() method 
		 * ***********************************************************************************/
		strBuilder.setLength(0);
		
		strBuilder.append(pid);															// pid of the process
		strBuilder.append(" ");
		
		try{
			ProcCredName procCredName = sigar.getProcCredName(pid);
			strBuilder.append(procCredName.getUser());									// user name of the process's owner
			strBuilder.append(" ");
			strBuilder.append(procCredName.getGroup());									// group name of the process's owner			
			strBuilder.append(" ");
		} catch (SigarException e){
			// if getProcCredName(pid) throws exception, means it cannot retrieve data => log as unknown ???
			strBuilder.append("??? ??? ");
		}
		
		try{
			ProcCpu procCpu = sigar.getProcCpu(pid);
			strBuilder.append(getFormatedStartTime(procCpu.getStartTime()));			// time it was strated in second need to convert to a proper readable time
			strBuilder.append(" ");
			strBuilder.append(formatNumb(procCpu.getSys()));										// process cpu sys time
			strBuilder.append(" ");
			strBuilder.append(formatNumb(procCpu.getUser()));										// process cpu user time
			strBuilder.append(" ");
			strBuilder.append(getOneDecPerc(procCpu.getPercent()));									// get process cpu usage
			strBuilder.append(" ");
		} catch (SigarException e){
			// if getProcCpu(pid) throws exception, means it cannot retrieve data => log as unknown ???
			strBuilder.append("??? ??? ??? ??? ");
		}
		
		try{
			ProcState procState = sigar.getProcState(pid);
			strBuilder.append(procState.getState());									// the state of the process (Running, Zomibe, Idle, Sleep, Stop, )
			strBuilder.append(" ");
			strBuilder.append(formatNumb(procState.getThreads()));									// the number of active threads running in this process
			strBuilder.append(" ");
			strBuilder.append(formatNumb(procState.getPriority()));									// kernel scheduling priority
			strBuilder.append(" ");
			strBuilder.append(formatNumb(procState.getNice()));										// nice value of the process
			strBuilder.append(" ");
		} catch (SigarException e){
			// if getProcState(pid) throws exception, means it cannot retrieve data => log as unknown ???
			strBuilder.append("??? ??? ??? ??? ");
		}
		
		try{
			ProcMem procMem = sigar.getProcMem(pid);
			strBuilder.append(byteToMegaByte(procMem.getShare()));									// get process total shared memory
			strBuilder.append(" ");
			strBuilder.append(byteToMegaByte(procMem.getRss()));									// Residential Set Size of the process (default is Byte) divide by 1024 for MB
			strBuilder.append(" ");
			strBuilder.append(byteToMegaByte(procMem.getSize()));									// process virtual memory (default is Byte) divide by 1024 for MB
			strBuilder.append(" ");
			strBuilder.append(formatNumb(procMem.getMinorFaults()));								// number of non i/o page faults
			strBuilder.append(" ");
			strBuilder.append(formatNumb(procMem.getMajorFaults()));								// number of i/o page faults
			strBuilder.append(" ");
			strBuilder.append(formatNumb(procMem.getPageFaults()));									// number of tottal page faults
		} catch (SigarException e){
			// if getProcMem(pid) throws exception, means it cannot retrieve data => log as unknown ???
			strBuilder.append("??? ??? ??? ??? ??? ???");
		}
		
		// cannot decide whether should log the process description or not. 
		// so, I just uncomment these lines and decide later.
		// If uncomment these lines, you also need to uncomment a line in setupLogHeader() method
//		try {
//			strBuilder.append(ProcUtil.getDescription(sigar, Long.parseLong(pid)));		// process description
//		} catch (NumberFormatException e) {
//			// this case would not be possible to happen 
//			// if it can't be parse to long, means it's nt a valid pid => it has been removed since setupPIDsListFromDefaultConfigFile() 
//		} catch (SigarException e) {
//			strBuilder.append("???");
//		}		
		
		
		performance.put(VALUE, strBuilder.toString());
		strBuilder.setLength(0);
		
		return performance;
	}
	
	
	
	/**
	 * This method is adapted from Sigar examples
	 * @param time
	 * @return
	 */
	private String getFormatedStartTime(long time) {
        if (time == 0) {
            return "00:00";
        }
        long timeNow = System.currentTimeMillis();
        String fmt = "MMMd";

        if ((timeNow - time) < ((60*60*24) * 1000)) {
            fmt = "HH:mm";
        }

        return new SimpleDateFormat(fmt).format(new Date(time));
    }
	
	
	
	/**
	 * collect a set of maps. A map contains "Performance Description" (as a key) and "Performance Value" from the implemented CollectorTool
	 * @return a set of maps. a map (LinkedHashMap) contains "Performance Description" (as a key) and "Performance Value" from the implemented CollectorTool. Each set of map represent the data of each different component (e.g. a set of CPU0, set of CPU1 and so on)
	 * @throws SigarException if the Method cannot retrieve the info about that hardware (i.e. for CPU, can't get number of core, etc)
	 */
	@Override
	public Set<Map<String, String>> getPerformance() throws SigarException {
		Set<Map<String, String>> perfSet = new LinkedHashSet();
		
		// get performances of each pid and add them into the set
		for(String pid : pids){
			perfSet.add(getPerformanceOfGivenPID(pid));
		}
		
		return perfSet;
	}

	

	/**
	 * Setup the description header that will be print out once at the top of every log file.
	 * The setting up depends on the DeviceCollector
	 */
	@Override
	public void setupLogHeader() {
		logHeader = "LogTimeStamp\t"
					+ "ProcName\t"
					+ "PID\t"
					+ "UserName\t" 
					+ "OwnerGroup\t" 
					+ "StartTime\t" 
					+ "CpuSysTime\t" 
					+ "CpuUserTime\t" 
					+ "CpuUsage-%\t"
					+ "State(R:Running, S:Sleep)\t"   
					+ "NumActiveThreads\t" 
					+ "Priority\t" 
					+ "Nice\t"
					+ "SharedMem-MB\t" 
					+ "RssMem-MB\t" 
					+ "VirtualMem-MB\t" 
					+ "NonIOpgFaults\t" 
					+ "IOpgFaults\t" 
					+ "TotalPgFaults\t"
//					+ "Description\t"
					+ "\t??? means unknown or unimplemented value";
	}
	
	
	
	/**
	 * A helper method to Constructor to set up a list to store all the PIDs that need to be logged.
	 */
	private void setupPIDsListFromDefaultConfigFile(){
		// if you changed the property in the config file, please double check the key "ProcsCollectorPIDsList"
		String pidsString = ConfigReader.getProperty("ProcsCollectorPIDsList");
		pids = new ArrayList<String>(Arrays.asList(pidsString.split(",")));
		pidsNames = new HashMap<String, String>();
		
		List<String> invalidPids = new ArrayList<String>();
		
		// checking if all the pids specified in the config file are valid
		// if pid is invalid, the 4 methods in this try block will throw an exception
		// remove any invalid pids
		for(String pid : pids){
			try {
				sigar.getProcCpu(pid);
				sigar.getProcMem(pid);
				sigar.getProcState(pid);
				sigar.getProcCredName(pid);
				
				String description = ProcUtil.getDescription(sigar, Long.parseLong(pid));		// process description
				description = description.substring(description.lastIndexOf("/") + 1);
				description = description.replaceAll(" ", "");
				pidsNames.put(pid, description);
			} catch (SigarException e) {
				invalidPids.add(pid);
				LoggerBuilder.getAppLogger().info("ProcsCollector: the pid: " + pid + " is invalid");
			}
		}
		pids.removeAll(invalidPids);
	}
}










