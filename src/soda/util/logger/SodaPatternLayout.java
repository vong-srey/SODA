package soda.util.logger;

import org.apache.log4j.PatternLayout;

public class SodaPatternLayout extends PatternLayout{
	
	// used to set and get the header
	// header will be printed out only once every log file
	private String header = "";
	
	
	
	/**
	 * Construct a PatternLayout (SodaPatternLayout) with header
	 * @param header - the header content
	 */
	public SodaPatternLayout(String header){
		super();
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
		String lineSeparator = System.getProperty("line.separator");
		
		header = header.trim();
		if(!header.isEmpty() && !header.endsWith(lineSeparator)){
			header += lineSeparator;
		}
		
		this.header = header;
		
		
		
	}
}
