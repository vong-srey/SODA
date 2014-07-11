package soda.util.logger;

import org.apache.log4j.PatternLayout;


/**
 * This class defining the PatternLayout of the Log4j.
 * Additional feature (over PatternLayout) is to print header on the top of every log.
 * The header can be defined using setHeader(String header)
 * 
 * @author vongvithyeasrey
 *
 */
public class SodaPatternLayout extends PatternLayout{
	
	// used to set and get the header
	// header will be printed out only once every log file
	private String header = "";
	
	
	
	/**
	 * Constructor
	 */
	public SodaPatternLayout(){
		super();
		setHeader("");
	}
	
	
	
	/**
	 * Construct a PatternLayout (SodaPatternLayout) with header
	 * @param header - the header content
	 */
	public SodaPatternLayout(String header){
		super();
		
		// check the given parameter
		if(header == null) throw new IllegalArgumentException();
			
		setHeader(header);
	}
	
	
	
	/**
	 * returns the header for the layout format. 
	 * The header will printed only once every log file.
	 */
	@Override
	public String getHeader(){
		return header;
	}
	
	
	
	/**
	 * set the content of the header
	 * @param header - new header content
	 */
	public void setHeader(String header){
		// check the given parameter
		if(header == null) throw new IllegalArgumentException();
		
		// add a line seperator to the header
		String lineSeparator = System.getProperty("line.separator");
		header = header.trim();
		if(!header.isEmpty() && !header.endsWith(lineSeparator)){
			header += lineSeparator;
		}
		
		this.header = header;
		
		
		
	}
}
