package com.smatechnologies.vision.impex.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.smatechnologies.vision.impex.interfaces.IVisionImpexConstants;
import com.smatechnologies.vision.impex.modules.UtilDateFormat;

public class Utilities {

	public String getExceptionDetails(
			Exception e
			) {
		
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		String exceptionDetails = sw.toString();
		return exceptionDetails;
	}

	public String getLogDateTimeStamp(
			) throws Exception {
		
		SimpleDateFormat timeStampFormat = new SimpleDateFormat(IVisionImpexConstants.LOG_DATE_TIME_FORMAT);
		String timeStamp = null;
		
		Calendar cal = Calendar.getInstance();
		timeStamp = timeStampFormat.format(cal.getTime());
	    return timeStamp;
	}
	
	public boolean checkDateFormat(
			String checkDateFormat
			) throws Exception {

		String regex = "^[0-9]{4}-(1[0-2]|0[1-9])-(3[01]|[12][0-9]|0[1-9])$";
		
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(checkDateFormat);
		return matcher.matches();
	}

	public String getCurrentDate(
			) throws Exception {
		
		String currentDate = null;
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(IVisionImpexConstants.CURRENT_DATE_PATTERN);
		currentDate = simpleDateFormat.format(new Date());
		return currentDate;
	}	// END : getCurrentDate

	public GregorianCalendar setCalendarDate(
			String sdate, 
			UtilDateFormat ddate
			) throws Exception {

		Collection <String> dateFields = new ArrayList<String>();
		String sDay = null;
		String sYear = null;
		int iMonth = 0;
		GregorianCalendar gcal = null;
		int fieldCntr = 0;
		
		if(ddate.getDfDateSeperator() != null) {
			dateFields = extractFields(sdate, ddate.getDfDateSeperator(), 4);
			fieldCntr = 0;
			for(Iterator<String> i_dateFields = dateFields.iterator(); i_dateFields.hasNext();) {
				String field = (String)i_dateFields.next();
				if(fieldCntr == 0){
					// extract date position one
					switch (ddate.getDfDatePositionOne()) {
					
						case IVisionImpexConstants.DAY_PARAMETER:
							// day in position one
							sDay = field;
							break;
							
						case IVisionImpexConstants.MONTH_PARAMETER:
							// month in position one
							iMonth = Integer.parseInt(field);
							iMonth--;
							break;
			
						case IVisionImpexConstants.YEAR_PARAMETER:
							// insert year in position one
							sYear = field;
							break;
					}
				} else if (fieldCntr == 1){
					// extract date position two
					switch (ddate.getDfDatePositionTwo()) {
					
						case IVisionImpexConstants.DAY_PARAMETER:
							// day in position two
							sDay = field;
							break;
							
						case IVisionImpexConstants.MONTH_PARAMETER:
							// month in position two
							iMonth = Integer.parseInt(field);
							iMonth--;
							break;
			
						case IVisionImpexConstants.YEAR_PARAMETER:
							// insert year in position two
							sYear = field;
							break;
					}
				} else if (fieldCntr == 2){
					// extract date position three
					switch (ddate.getDfDatePositionThree()) {
					
						case IVisionImpexConstants.DAY_PARAMETER:
							// day in position three
							sDay = field;
							break;
							
						case IVisionImpexConstants.MONTH_PARAMETER:
							// month in position three
							iMonth = Integer.parseInt(field);
							iMonth--;
							break;
			
						case IVisionImpexConstants.YEAR_PARAMETER:
							// insert year in position three
							sYear = field;
							break;
					}
				}
				fieldCntr++;
			}
		} else {
			if(ddate.getDfDateSeperator() == null) {
				if(sdate.length() == 8) {
					switch (ddate.getDfDatePositionOne()) {

					case IVisionImpexConstants.DAY_PARAMETER:
							// day in position three
							sDay = sdate.substring(0,2);
							break;
							
						case IVisionImpexConstants.MONTH_PARAMETER:
							// month in position three
							iMonth = Integer.parseInt(sdate.substring(0,2));
							iMonth--;
							break;
			
						case IVisionImpexConstants.YEAR_PARAMETER:
							// insert year in position three
							sYear = sdate.substring(0,4);
							break;
					}
				} else if(sdate.length() == 7){
					
				} else if(sdate.length() == 6) {
					
				}
			}
		}
		// set the day in the GregorianCalendar
		gcal = new GregorianCalendar(Integer.parseInt(sYear), iMonth, Integer.parseInt(sDay));
		return gcal;
	} // END : setCalendarDate

//	public PropertyList getPropertyList(
//			String properties
//			) throws Exception {
//		
//		PropertyList propertyList = new PropertyList();
//		List<PropertyDefinition> propertydefs = new ArrayList<PropertyDefinition>();
//		
//		try {
//			if(properties != null) {
//				String[] propertyArray = tokenizeParameters(properties, false, CommandLineConstants.COMMA);
//				for(int cntr = 0; cntr < propertyArray.length; cntr++) {
//					String[] propertyDefArray = tokenizeParameters(propertyArray[cntr], false, CommandLineConstants.EQUAL);
//					PropertyDefinition propertyDefinition = new PropertyDefinition();
//					propertyDefinition.setName(propertyDefArray[0]);
//					propertyDefinition.setValue(propertyDefArray[1]);
//					propertydefs.add(propertyDefinition);
//				}
//			}
//			propertyList.setPropertyList(propertydefs);
//		} catch (Exception ex) {
//			throw new Exception(ex);
//		}
//		return propertyList;
//	}	// END : getPropertyList
//
	public String[] tokenizeParameters(
			String parameters, 
			boolean keepQuote, 
			String delimiter
			) throws Exception {

		final char QUOTE = IVisionImpexConstants.QUOTE.toCharArray()[0];
		final char BACK_SLASH = IVisionImpexConstants.BACKSLASH.toCharArray()[0];
		char prevChar = 0;
		char currChar = 0;
		StringBuffer sb = new StringBuffer(parameters.length());

		if (!keepQuote) {
			for (int i = 0; i < parameters.length(); i++) {
				if (i > 0) {
					prevChar = parameters.charAt(i - 1);
				}
				currChar = parameters.charAt(i);

				if (currChar != QUOTE || (currChar == QUOTE && prevChar == BACK_SLASH)) {
					sb.append(parameters.charAt(i));
				}
			}

			if (sb.length() > 0) {
				parameters = sb.toString();
			}
		}
		return parameters.split(delimiter);
	}	// END : tokenizeParameters

	
	public List<String> extractFields(
			String inputrec, 
			String delimiter, 
			int fields) throws Exception {
		
		List<String> colExtractedFields = new	ArrayList<String>();
		String sNullString = null;
		int ifieldCounter = 0;
		int iEndOfField = 0;
		int iCharCntr = 0;
		int iRecLength = inputrec.length();
		
		while(iCharCntr < iRecLength) {
			// process the input record
			if(ifieldCounter > fields) {
				// only process the first 8 fields
				// terminate
				iCharCntr = iRecLength;
			} else {
				iEndOfField = inputrec.indexOf(delimiter);
				// get first occurrence of delimiter character
				if(iEndOfField == -1) {
					// no more detected?
					String sLastField =	inputrec.substring(0, inputrec.length());
					// YES, so everything to end of string is last field

					colExtractedFields.add(sLastField);
					// inserted extracted field into field collection
					break;
					// end the while loop
				} else {
					if(iEndOfField == 0) {
						// we got a null field?

						colExtractedFields.add(sNullString);
						// YES, insert a null string into the field

						// collection
						inputrec = inputrec.substring(1, inputrec.length());				
						//	adjust the input record
						iCharCntr =	iCharCntr + 1;	// increment characters processed count
						ifieldCounter++; // increment the field counter
					} else {
						String sField = inputrec.substring(0, iEndOfField);
							// NO, extract the field, from beginning of input 
							// rec to next delim			
						colExtractedFields.add(sField);
						// inserted extracted field into field collection
						inputrec = inputrec.substring(iEndOfField + 1, inputrec.length());	
							// adjust the input record
						iCharCntr =	iCharCntr + iEndOfField+1;
						// increment characters processed count
						ifieldCounter++;
						// increment the field counter
					}
				}
			}
		}
	    // return extracted information
	    return colExtractedFields;
	} // END : extractFields

	public UtilDateFormat getDateFormat(
			) throws Exception {
		
		UtilDateFormat dateformat = new UtilDateFormat();
		
		dateformat.setDfDateSeperator(IVisionImpexConstants.DASH);
		dateformat.setDfDatePositionOne(IVisionImpexConstants.YEAR_PARAMETER);
		dateformat.setDfDatePositionTwo(IVisionImpexConstants.MONTH_PARAMETER);
		dateformat.setDfDatePositionThree(IVisionImpexConstants.DAY_PARAMETER);
	    return dateformat;
	} // END : getDateFormat

}
