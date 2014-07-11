package soda.util.logger;

/**
 * CustodianDailyRollingFileAppender.java
 * Adapted from the Apache Log4j DailyRollingFileAppender to extend the functionality
 * of the existing class so that the user can limit the number of log backups
 * and compress the backups to conserve disk space.
 * @author Ryan Kimber

 * This class has been tested, updated and modified by Vong Vithyea Srey
 * As part of the Part 4 Project of Software Engineer Student at Auckland University

 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;



/**
 * CustodianDailyRollingFileAppender is based on
 * {@link org.apache.log4j.appender.DailyRollingFileAppender} so most of the
 * configuration options can be taken from the documentation on that class.
 */
public class CustodianDailyRollingFileAppender extends FileAppender {
	
	// The code assumes that the following constants are in a increasing sequence.
	protected static final int TOP_OF_TROUBLE = -1;
	protected static final int TOP_OF_MINUTE = 0;
	protected static final int TOP_OF_HOUR = 1;
	protected static final int HALF_DAY = 2;
	protected static final int TOP_OF_DAY = 3;
	protected static final int TOP_OF_WEEK = 4;
	protected static final int TOP_OF_MONTH = 5;

	
	
	/**
	 * The date pattern. By default, the pattern is set to "'.'yyyy-MM-dd"
	 * meaning daily rollover.
	 */
	private String datePattern = "'.'yyyy-MM-dd";
	private String compressBackups = "false";
	private String maxNumberOfDays = "7";

	
	
	/**
	 * The log file will be renamed to the value of the scheduledFilename
	 * variable when the next interval is entered. For example, if the rollover
	 * period is one hour, the log file will be renamed to the value of
	 * "scheduledFilename" at the beginning of the next hour.
	 * 
	 * The precise time when a rollover occurs depends on logging activity.
	 */
	private String scheduledFilename;

	
	
	/**
	 * The next time we estimate a rollover should occur.
	 */
	private long nextCheck = System.currentTimeMillis() - 1;
	protected Date now = new Date();
	protected SimpleDateFormat sdf;
	protected RollingCalendar rc = new RollingCalendar();
	protected int checkPeriod = TOP_OF_TROUBLE;

	
	
	// The gmtTimeZone is used only in computeCheckPeriod() method.
	protected static final TimeZone gmtTimeZone = TimeZone.getTimeZone("GMT");
	
	
	// used for testing
	private String dummyLog = "";

	
	
	/**
	 * The default constructor does nothing.
	 */
	public CustodianDailyRollingFileAppender() {
	}

	
	
	/**
	 * Instantiate a CustodianDailyRollingFileAppender and open the file
	 * designated by filename. The opened filename will become the ouput
	 * destination for this appender.
	 */
	public CustodianDailyRollingFileAppender(Layout layout, String filename,
			String datePattern) throws IOException {
		super(layout, filename, true);
		
		this.datePattern = datePattern;
		activateOptions();
	}

	
	
	/**
	 * getter for scheduledFilename
	 */
	public String getScheduledFilename(){
		return scheduledFilename;
	}
	
	
	
	/**
	 * getter for nextCheck
	 * @return now
	 */
	public long getNextCheck(){
		return nextCheck;
	}
	
	

	/**
	 * getter for dummyLog. this method is used for testing purpose only
	 * @return
	 */
	public String getDummyLog(){
		return dummyLog;
	}
	
	
	
	/**
	 * The DatePattern takes a string in the same format as expected by
	 * {@link SimpleDateFormat}. This options determines the rollover schedule.
	 */
	public void setDatePattern(String pattern) {
		if(pattern==null){
			throw new IllegalArgumentException();
		}
		
		datePattern = pattern;
	}

	
	
	/**
	 *  Returns the value of the DatePattern option. 
	 */
	public String getDatePattern() {
		return datePattern;
	}
	
	
	
	/**
	 * setter of maxNumberOfDays
	 * @param maxNumberOfDays
	 */
	public void setMaxNumberOfDays(String maxNumberOfDays) {
		if(maxNumberOfDays==null) 
			throw new IllegalArgumentException();
		
		int numb = 0;
		try{
			numb = Integer.parseInt(maxNumberOfDays);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException();
		}
		
		if(numb >= 0){
			this.maxNumberOfDays = maxNumberOfDays;
		}
	}
	
	
	
	/**
	 * getter of maxNumberOfDays
	 * @return
	 */
	public String getMaxNumberOfDays() {
		return maxNumberOfDays;
	}

	
	
	/**
	 * getter for comperssBackups
	 * @return
	 */
	public String getCompressBackups() {
		return compressBackups;
	}

	
	
	/**
	 * setter for compressBackups
	 * @param compressBackups
	 */
	public void setCompressBackups(String compressBackups) {
		if(compressBackups == null){
			throw new IllegalArgumentException();
		}
		this.compressBackups = compressBackups;
	}

	
	
	/**
	 * Activate the logger. The logger can be activated only if the datePattern and log's fileName
	 * have been assigned and both are not null
	 */
	public void activateOptions() {
		super.activateOptions();
		
		// the logger can be activated only if the datePattern and log's fileName are not null
		if (datePattern != null && fileName != null) {
			now.setTime(System.currentTimeMillis());
			sdf = new SimpleDateFormat(datePattern);
			
			// set the check interval onto FileAppender
			int type = computeCheckPeriod();
			printPeriodicity(type);
			rc.setType(type);
			File file = new File(fileName);
			scheduledFilename = fileName + sdf.format(new Date(file.lastModified()));

		} else {
			LogLog.error("Either File or DatePattern options are not set for appender ["
					+ name + "].");
		}
	}

	
	
	/**
	 * Log the performance of Logger itself
	 * @param type
	 */
	public void printPeriodicity(int type) {
		String log = "";
		switch (type) {
		case TOP_OF_MINUTE:
			log = "Appender [" + name + "] to be rolled every minute.";
			dummyLog = log;
			LogLog.debug(log);
			break;
		case TOP_OF_HOUR:
			log = "Appender [" + name
					+ "] to be rolled on top of every hour.";
			dummyLog = log;
			LogLog.debug(log);
			break;
		case HALF_DAY:
			log = "Appender [" + name
					+ "] to be rolled at midday and midnight.";
			dummyLog = log;
			LogLog.debug(log);
			break;
		case TOP_OF_DAY:
			log = "Appender [" + name + "] to be rolled at midnight.";
			dummyLog = log;
			LogLog.debug(log);
			break;
		case TOP_OF_WEEK:
			log = "Appender [" + name
					+ "] to be rolled at start of week.";
			dummyLog = log;
			LogLog.debug(log);
			break;
		case TOP_OF_MONTH:
			log = "Appender [" + name
					+ "] to be rolled at start of every month.";
			dummyLog = log;
			LogLog.debug(log);
			break;
		default:
			log = "Unknown periodicity for appender [" + name + "].";
			dummyLog = log;
			LogLog.warn(log);
		}
	}

	
	/**
	 * This method computes the roll over period by looping over the
	 * periods, starting with the shortest, and stopping when the r0 is
	 * different from from r1, where r0 is the epoch formatted according
	 * the datePattern (supplied by the user) and r1 is the
	 * epoch+nextMillis(i) formatted according to datePattern. All date
	 * formatting is done in GMT and not local format because the test
	 * logic is based on comparisons relative to 1970-01-01 00:00:00
	 * GMT (the epoch).
	 * @return the interval number that the logger should periodically check the file and do cleanup
	 */
	public int computeCheckPeriod() {
		RollingCalendar rollingCalendar = new RollingCalendar(gmtTimeZone, Locale.ENGLISH);
		// set sate to 1970-01-01 00:00:00 GMT
		Date epoch = new Date(0);
		if (datePattern != null) {
			for (int i = TOP_OF_MINUTE; i <= TOP_OF_MONTH; i++) {
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
						datePattern);
				simpleDateFormat.setTimeZone(gmtTimeZone); // do all date
				// formatting in GMT
				String r0 = simpleDateFormat.format(epoch);
				rollingCalendar.setType(i);
				Date next = new Date(rollingCalendar.getNextCheckMillis(epoch));
				String r1 = simpleDateFormat.format(next);
				if (r0 != null && r1 != null && !r0.equals(r1)) {
					return i;
				}
			}
		}
		return TOP_OF_TROUBLE; // Deliberately head for trouble if datePattern is null...
	}

	
	
	/**
	 * Rollover the current file to a new file.
	 */
	public void rollOver() throws IOException {
		
		/* Compute filename, but only if datePattern is specified */
		if (datePattern == null) {
			errorHandler.error("Missing DatePattern option in rollOver().");
			return;
		}

		String datedFilename = fileName + sdf.format(now);
		
		// It is too early to roll over because we are still within the
		// bounds of the current interval. Rollover will occur once the
		// next interval is reached.
		if (scheduledFilename.equals(datedFilename)) {
			return;
		}

		
		/** start roll over **/
		
		
		// close current file, and rename it to datedFilename
		this.closeFile();

		File target = new File(scheduledFilename);
		
		if (target.exists()) { //replace the existing file with a new file (if the name are duplicate)
			target.delete();
		}

		File file = new File(fileName);
		boolean result = file.renameTo(target);
		if (result) {
			LogLog.debug(fileName + " -> " + scheduledFilename);
		} else {
			LogLog.error("Failed to rename [" + fileName + "] to ["
					+ scheduledFilename + "].");
		}

		try {
			// This will also close the file. This is OK since multiple
			// close operations are safe.
			this.setFile(fileName, false, this.bufferedIO, this.bufferSize);
		} catch (IOException e) {
			errorHandler.error("setFile(" + fileName + ", false) call failed.");
		}
		
		scheduledFilename = datedFilename;
	}

	
	
	/**
	 * This method differentiates DailyRollingFileAppender from its super class.
	 * 
	 * Before actually logging, this method will check whether it is time to do
	 * a rollover. If it is, it will schedule the next rollover time and then rollover.
	 *
	 */
	public void subAppend(LoggingEvent event) {
		
		long n = System.currentTimeMillis();
		
		// check to see if it is a time to clean up and roll over
		if (n >= nextCheck) {
			// reset the nextCheck before clean up
			now.setTime(n);
			nextCheck = rc.getNextCheckMillis(now);
			
			try {
				cleanupAndRollOver();
			} catch (IOException ioe) {
				LogLog.error("cleanupAndRollover() failed.", ioe);
			}
		}
		if(event != null) super.subAppend(event);
	}

	
	
	/**
	 * This method checks to see if we're exceeding the number of log backups
	 * that we are supposed to keep, and if so, deletes the offending files. It
	 * then delegates to the rollover method to rollover to a new file if
	 * required.
	 */
	protected void cleanupAndRollOver() throws IOException {
		
		rollOver();
		
		// get the number of maxDays (the default maxDays=7, if the value is invalid)
		int maxDays = 7;
		try {
			maxDays = Integer.parseInt(getMaxNumberOfDays());
			// if user give some crapy maxDays (zero or negative) stay stick with 7 days
			if(maxDays <= 0) maxDays = 7;
		} catch (Exception e) {
			// if there's an exception when reading the config file just leave it at 7.
		}
		
		
		// get all the files and check if any files are older than maxDays (e.g 5) days before today
		// if there are, delete or zip and delete
		File file = new File(fileName);
		Calendar cal = Calendar.getInstance();
		
		cal.add(Calendar.DATE, -maxDays);
		Date cutoffDate = cal.getTime();
		if (file.getParentFile()!=null && file.getParentFile().exists()) {
			File[] files = file.getParentFile().listFiles(
					new StartsWithFileFilter(file.getName(), false));
			int nameLength = file.getName().length();
			// run through each file and check if it is older than maxDays (e.g 5) days before today
			// if there are, delete or zip and delete
			for (int i = 0; i < files.length; i++) {
				if(files[i].getName().endsWith(".zip")) continue;
				
				String datePart = null;
				try {
					datePart = files[i].getName().substring(nameLength);
					Date date = sdf.parse(datePart);
					
					// if this file is older than maxDays (e.g 5) days before today
					if (date.before(cutoffDate)) {
						
						// If we're supposed to zip files and this isn't already a zip
						if (getCompressBackups().equalsIgnoreCase("YES")
								|| getCompressBackups().equalsIgnoreCase("TRUE")) {
							
							zipGivenFile(files[i]);						
						}
						
						// delete the file either we need to zip or not
						files[i].delete();
					}
					
				} catch (ParseException e ) {
					// This exception just catch to hold the name that end with .zip
					// So, we don't need to do anything.
					
				} catch (Exception pe) {
					// This isn't a file we should touch (it isn't named correctly)
					pe.printStackTrace();
				}
			}
		}
	}

	
	
	/**
	 * Compresses the passed file to a .zip file, stores the .zip in the same
	 * directory as the passed file
	 * @param file
	 */
	public void zipGivenFile(File file) throws IOException {
		// check before zip
		if (!file.getName().endsWith(".zip")) {
			File zipFile = new File(file.getParent(), file.getName() + ".zip");
			FileInputStream fis = new FileInputStream(file);
			FileOutputStream fos = new FileOutputStream(zipFile);
			ZipOutputStream zos = new ZipOutputStream(fos);
			ZipEntry zipEntry = new ZipEntry(file.getName());
			zos.putNextEntry(zipEntry);

			byte[] buffer = new byte[4096];
			while (true) {
				int bytesRead = fis.read(buffer);
				if (bytesRead < 0)
					break;
				else {
					zos.write(buffer, 0, bytesRead);
				}
			}
			zos.closeEntry();
			fis.close();
			zos.close();
		}
	}

	
	
	/**
	 * Helper to Logger (FileFilter)
	 */
	class StartsWithFileFilter implements FileFilter {
		private String startsWith;
		private boolean inclDirs = false;

		/**
	     * override
	     */
		public StartsWithFileFilter(String startsWith,
				boolean includeDirectories) {
			super();
			this.startsWith = startsWith.toUpperCase();
			inclDirs = includeDirectories;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.io.FileFilter#accept(java.io.File)
		 */
		public boolean accept(File pathname) {
			if (!inclDirs && pathname.isDirectory()) {
				return false;
			} else
				return pathname.getName().toUpperCase().startsWith(startsWith);
		}
	}
}



/**
 * RollingCalendar is a helper class to DailyRollingFileAppender. Given a
 * periodicity type and the current time, it computes the start of the next
 * interval.
 * @author Ryan Kimber
 * 
 */
class RollingCalendar extends GregorianCalendar {
	private static final long serialVersionUID = -3560331770601814177L;

	private int type = CustodianDailyRollingFileAppender.TOP_OF_TROUBLE;

	public RollingCalendar() {
		super();
	}

	public RollingCalendar(TimeZone tz, Locale locale) {
		super(tz, locale);
	}

	public void setType(int type) {
		this.type = type;
	}

	public long getNextCheckMillis(Date now) {
		return getNextCheckDate(now).getTime();
	}

	public Date getNextCheckDate(Date now) {
		this.setTime(now);

		switch (type) {
		case CustodianDailyRollingFileAppender.TOP_OF_MINUTE:
			this.set(Calendar.SECOND, 0);
			this.set(Calendar.MILLISECOND, 0);
			this.add(Calendar.MINUTE, 1);
			break;
		case CustodianDailyRollingFileAppender.TOP_OF_HOUR:
			this.set(Calendar.MINUTE, 0);
			this.set(Calendar.SECOND, 0);
			this.set(Calendar.MILLISECOND, 0);
			this.add(Calendar.HOUR_OF_DAY, 1);
			break;
		case CustodianDailyRollingFileAppender.HALF_DAY:
			this.set(Calendar.MINUTE, 0);
			this.set(Calendar.SECOND, 0);
			this.set(Calendar.MILLISECOND, 0);
			int hour = get(Calendar.HOUR_OF_DAY);
			if (hour < 12) {
				this.set(Calendar.HOUR_OF_DAY, 12);
			} else {
				this.set(Calendar.HOUR_OF_DAY, 0);
				this.add(Calendar.DAY_OF_MONTH, 1);
			}
			break;
		case CustodianDailyRollingFileAppender.TOP_OF_DAY:
			this.set(Calendar.HOUR_OF_DAY, 0);
			this.set(Calendar.MINUTE, 0);
			this.set(Calendar.SECOND, 0);
			this.set(Calendar.MILLISECOND, 0);
			this.add(Calendar.DATE, 1);
			break;
		case CustodianDailyRollingFileAppender.TOP_OF_WEEK:
			this.set(Calendar.DAY_OF_WEEK, getFirstDayOfWeek());
			this.set(Calendar.HOUR_OF_DAY, 0);
			this.set(Calendar.MINUTE, 0);
			this.set(Calendar.SECOND, 0);
			this.set(Calendar.MILLISECOND, 0);
			this.add(Calendar.WEEK_OF_YEAR, 1);
			break;
		case CustodianDailyRollingFileAppender.TOP_OF_MONTH:
			this.set(Calendar.DATE, 1);
			this.set(Calendar.HOUR_OF_DAY, 0);
			this.set(Calendar.MINUTE, 0);
			this.set(Calendar.SECOND, 0);
			this.set(Calendar.MILLISECOND, 0);
			this.add(Calendar.MONTH, 1);
			break;
		default:
			throw new IllegalStateException("Unknown periodicity type.");
		}
		return getTime();
	}
}
