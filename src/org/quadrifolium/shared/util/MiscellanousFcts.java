package org.quadrifolium.shared.util;

import java.util.Vector;

/**
 * <p>
 * FieldVerifier validates that the name the user enters is valid.
 * </p>
 * <p>
 * This class is in the <code>shared</code> packing because we use it in both
 * the client code and on the server. Client side, we verify that the name is
 * valid before sending an RPC request so the user doesn't have to wait for a
 * network round trip to get feedback. Server side, we verify that the name is
 * correct to ensure that the input is correct regardless of where the RPC
 * originates.
 * </p>
 * <p>
 * When creating a class that is used on both the client and the server, be sure
 * that all code is translatable and does not use native JavaScript. Code that
 * is not translatable (such as code that interacts with a database or the file
 * system) cannot be compiled into client side JavaScript. Code that uses native
 * JavaScript (such as Widgets) cannot be run on the server.
 * </p>
 */
public class MiscellanousFcts {

	// private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	private static final String EMAIL_LEFT_PATTERN  = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ.-_" ;
	private static final String EMAIL_RIGHT_PATTERN = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ." ;
		
	public static enum STRIP_DIRECTION { stripLeft, stripRight, stripBoth } ;
	
	/**
	 * Verifies that the specified mail address is valid
	 * 	  
	 * @param name the name to validate
	 * @return true if valid, false if invalid
	 */
	public static boolean isValidMailAddress(String sMail) 
	{
		if ((null == sMail) || (sMail.equals(""))) 
			return false ;
		
		// Check that there is a '@' and only one
		//
		String[] tokens = sMail.split("@") ;
    if ((2 != tokens.length) || tokens[0].equals("") || tokens[1].equals("")) 
    	return false ;

    // Test if chars are valid
    //
    if ((false == isValidString(tokens[0], EMAIL_LEFT_PATTERN, false)) ||
        (false == isValidString(tokens[1], EMAIL_RIGHT_PATTERN, false)))
    	return false ;
    
    // Check '.' position validity
		//
    if ((tokens[0].charAt(tokens[0].length() - 1) == '.') ||
    		(tokens[1].charAt(tokens[1].length() - 1) == '.'))
    	return false ;
    
    String[] left  = tokens[0].split("\\.") ;
		String[] right = tokens[1].split("\\.") ;
    
		int iLL = left.length ;
		int iRL = right.length ;
		
		// Check that there is a '.' or two to the right
		//
		if ((iRL < 2) || (iRL > 3))    	
			return false ;
		
		// Check that '.' are not contiguous, and not beginning and not terminating
		//
		for (int i = 0 ; i < iRL ; i++)
			if (right[i].equals(""))
				return false ;
		
		for (int i = 0 ; i < iLL ; i++)
			if (left[i].equals(""))
				return false ;
    
		// Check that final block is a valid extension (at least 2 chars, and if 2, no digit) 
    //
    if (right[iRL-1].length() < 2)
    	return false ;
        
    if ((right[iRL-1].length() == 2) && (Character.isDigit(right[iRL-1].charAt(0)) ||
    		                                 Character.isDigit(right[iRL-1].charAt(1))))
    	return false ;
   
    return true ;
	}
	
	/**
	 * Check if both Strings are equal and not null
	 * 	  
	 * @return true if valid, false if invalid
	 */
	public static boolean areIdenticalStrings(String element, String confirmedElement) 
	{
		if ((null == element) || (null == confirmedElement))
			return false ;
		
		return element.equals(confirmedElement) ;
	}
	
	/**
	 * Returns a formated date (à la dd/MM/yyyy) from a native date (yyyyMMdd)
	 * 	  
	 * @param sNativeDate the native date (whose format is yyyyMMdd), for example "20151202"
	 * @param sDateFormat the format to conform to, for example "dd/MM/yyyy"
	 * 
	 * @return "" if something went wrong or the formated date, if successful (for example "02/12/2015") 
	 */
	public static String dateFromNativeToFormated(final String sNativeDate, final String sDateFormat)
	{
		if ((null == sDateFormat) || "".equals(sDateFormat))
			return "" ;
		
		// null or "" are considered as 00000000
		//
		String sWorkDate = "00000000" ;
		if ((null != sNativeDate) && (false == "".equals(sNativeDate)))
			sWorkDate = sNativeDate ;
		
		// If even not the length or yyyy (ie 4) or not only digits, better fail
		//
		if ((false == isDigits(sWorkDate)) || (sWorkDate.length() < 4))
			return "" ;
		
		// Completing to 8 digits, for example, 2015 become 20150000 and 201512 becomes 20151200
		//
		if (sWorkDate.length() == 4)
			sWorkDate += "0000" ;
		if (sWorkDate.length() == 4)
			sWorkDate += "0000" ;
		
		// If, even when completed, the String cannot comply to the yyyyMMdd format, then fail
		//
		if (sWorkDate.length() != 8)
			return "" ;
		
		String sReturn = sDateFormat ;
		
		sReturn = sReturn.replace("yyyy", sWorkDate.substring(0, 4)) ;
		sReturn = sReturn.replace("MM",   sWorkDate.substring(4, 6)) ;
		sReturn = sReturn.replace("dd",   sWorkDate.substring(6, 8)) ;
		
		return sReturn ;
	}
	
	/**
	 * Returns a native date (yyyyMMdd) from a formated date (à la dd/MM/yyyy) 
	 * 	  
	 * @param sFormatedDate the formated date, for example "24/12/2015"
	 * @param sDateFormat the format to conform to, for example "dd/MM/yyyy"
	 * 
	 * @return "" if something went wrong or the native date, if successful (for example "20151224") 
	 */
	public static String dateFromFormatedToNative(final String sFormatedDate, final String sDateFormat)
	{
		if ((null == sDateFormat) || "".equals(sDateFormat))
			return "" ;
		if ((null == sFormatedDate) || "".equals(sFormatedDate))
			return "" ;
		
		int iYearsPos   = sDateFormat.indexOf("yyyy") ;
		int iMonthssPos = sDateFormat.indexOf("MM") ;
		int iDaysPos    = sDateFormat.indexOf("dd") ;
		
		// Fail if the format is not valid
		//
		if ((-1 == iYearsPos) || (-1 == iMonthssPos) || (-1 == iDaysPos))
			return "" ;
		
		String sYears  = sFormatedDate.substring(iYearsPos,   iYearsPos   + 4) ;
		String sMonths = sFormatedDate.substring(iMonthssPos, iMonthssPos + 2) ;
		String sDays   = sFormatedDate.substring(iDaysPos,    iDaysPos    + 2) ;
		
		if ("0000".equals(sYears) && "00".equals(sMonths) && "00".equals(sDays))
			return "" ;
		
		if ((false == isDigits(sYears)) || (false == isDigits(sMonths)) || (false == isDigits(sDays)))
			return "" ;
		
		return sYears + sMonths + sDays ;
	}
	
	/**
	 * Returns the position of the first char that is not cChar
	 * 	  
	 * @param sModel the String to explore
	 * @param cChar  the char to find the first occurrence "not of" in sModel
	 * 
	 * @return -1 if there is a problem, the size of the string if the string only contains cChars, an in-between integer in other cases
	 */
	public static int find_first_not_of(final String sModel, char cChar) 
	{
		if ((null == sModel) || "".equals(sModel))
			return -1 ;
		
		int iLen = sModel.length() ;
		
		int i = 0 ;
		for ( ; (i < iLen) && (sModel.charAt(i) == cChar) ; i++) ;
		
		return i ;
	}
	
	/**
	 * Returns the position of the first trailing char that is not cChar
	 * 	  
	 * @param sModel the String to explore
	 * @param cChar  the char to find the first occurrence "not of" in the trailing part of sModel
	 * 
	 * @return -1 if there is a problem or if the string only contains cChars, a positive or null integer in other cases
	 */
	public static int find_last_not_of(final String sModel, char cChar) 
	{
		if ((null == sModel) || "".equals(sModel))
			return -1 ;
		
		int i = sModel.length() - 1 ;
		for ( ; (i >= 0) && (sModel.charAt(i) == cChar) ; i--) ;
		
		return i ;
	}
	
	/**
	 * Returns a String made of iLenght occurrences of cChar 
	 * 	  
	 * @return the String
	 */
	public static String getNChars(final int iLength, char cChar) 
	{
		if (iLength <= 0)
			return "" ;
		
		StringBuffer outputBuffer = new StringBuffer(iLength) ;
		for (int i = 0; i < iLength ; i++) {
		   outputBuffer.append(cChar) ;
		}
		
		return outputBuffer.toString() ;
	}
	
	/**
	 * Returns a String made of the content of a String with a char replaced by another one 
	 * 	  
	 * @param str      Initial String
	 * @param index    Position of the char to replace (zero based)
	 * @param cReplace Char to replace the index-th char with
	 * 
	 * @return the String
	 */
	public static String replace(String str, int index, char cReplace)
	{     
    if (null == str)
    	return str ;
    if ((index < 0) || (index >= str.length()))
    	return str ;
    
    char[] chars = str.toCharArray() ;
    chars[index] = cReplace ;
    
    return String.valueOf(chars) ;       
	}
	
	/**
	 * Returns a string whose value is this string, with any leading and/or trailing cStripChar removed
	 * 	  
	 * @return true if valid, false if invalid
	 */
	public static String strip(final String sModel, STRIP_DIRECTION stripDir, char cStripChar) 
	{
		if (null == sModel)
			return null ;
		
		if ("".equals(sModel))
			return "" ;
		
		if ((STRIP_DIRECTION.stripBoth == stripDir) && (' ' == cStripChar))
			return sModel.trim() ;
		
		// First check if the String only contains cStripChars
		//
		int iFirstNotC = find_first_not_of(sModel, cStripChar) ;
		if (sModel.length() == iFirstNotC)
			return "" ;
		
		String sReturn ;
		
		// If we have to strip left, already use this information
		//
		if ((STRIP_DIRECTION.stripLeft == stripDir) || (STRIP_DIRECTION.stripBoth == stripDir))
		{
			sReturn = sModel.substring(iFirstNotC) ;
			if (STRIP_DIRECTION.stripLeft == stripDir)
				return sReturn ;
		}
		else
			sReturn = sModel ;
		
		int iLastNotC = find_last_not_of(sReturn, cStripChar) ;
		if (-1 == iLastNotC)
			return "" ;
		if (sReturn.length() - 1 == iLastNotC)
			return sReturn ;
		
		return sReturn.substring(0, iLastNotC + 1) ;
	}
	
	/**
	 * Get the blocks in a String, according to a separator
	 * 
	 * @param sToParse   String to parse
	 * @param sSeparator Separator that separates the different blocks in the string to parse
	 * 
	 * @return The blocks as a vector of strings if all went well, <code>null</code> if not
	 * 
	 * */
	public static Vector<String> ParseString(final String sToParse, final String sSeparator)
	{
		if ((null == sToParse) || "".equals(sToParse))
			return null ;
		
		Vector<String> result = new Vector<String>() ;
		
		// If there is no separator or the separator is not found in the string to parse, 
		// then simply return the string to parse as only result
		//
		if ((null == sSeparator) || "".equals(sSeparator))
		{
			result.add(sToParse) ;
			return result ;
		}
		
		int iVarSepar = sToParse.indexOf(sSeparator) ;
		if (-1 == iVarSepar)
		{
			result.add(sToParse) ;
			return result ;
		}
		
		int iSeparatorLen = sSeparator.length() ;
		
		// Cut the string to parse while the separator is found
		//
		String sRemains = sToParse ;
		
		while (-1 != iVarSepar)
		{
			String sLeft = sRemains.substring(0, iVarSepar) ;
			result.add(sLeft) ;
			
			// When taking right part, check if the separator doesn't end up the string
			//
			int iRemainsLen = sRemains.length() ;
			if (iVarSepar < iRemainsLen - iSeparatorLen)
			{
				sRemains = sRemains.substring(iVarSepar + iSeparatorLen, iRemainsLen) ;
				iVarSepar = sRemains.indexOf(sSeparator) ;
			}
			else
			{
				sRemains = "" ;
				iVarSepar = -1 ;
			}
		}
		
		if (false == "".equals(sRemains))
			result.add(sRemains) ;
		
		return result ;
	}
	
	/**
	 * Check that every char in sTest belongs to sModel
	 * 	  
	 * @param sTest  string to be tested
	 * @param sModel model string
	 * @param bEmptyAccepted true if empty strings are valid
	 * @return true if valid, false if invalid
	 */
	public static boolean isValidString(String sTest, String sModel, boolean bEmptyAccepted)
	{
		if ((null == sTest) || sTest.equals(""))
			return bEmptyAccepted ;
		
		if ((null == sModel) || sModel.equals(""))
			return false ;
		
		for (int i = 0 ; i < sTest.length() ; i++)
		{
			char c = sTest.charAt(i) ;
			
			int j = 0 ;
			for ( ; j < sModel.length() ; j++)
				if (sModel.charAt(j) == c)
					break ;
			
			if (sModel.length() == j)
				return false ;
		}
		
		return true ;
	}

	/**
	 * Verifies that the specified string is a valid IETF language tag
	 * 	  
	 * @param sLanguage the language code to validate
	 * @return true if valid, false if invalid
	 */
	public static boolean isValidLanguage(String sLanguage)
	{
		if ((null == sLanguage) || (sLanguage.equals(""))) 
			return false ;
		
		if (sLanguage.length() > 6)
			return false ;
		
		String[] sParts = sLanguage.split("-") ;
		
		if (1 == sParts.length)
			return isValidIso639_1(sLanguage) ; 
		
		if (2 == sParts.length)
			return isValidIso639_1(sParts[0]) && isValidIso639_1(sParts[0]) ;
		
		return false ;
	}
	
	/**
	 * Verifies that the specified string is a valid ISO 639-1 code
	 * 
	 * TODO really check code validity
	 * 	  
	 * @param sCode the language code to validate
	 * @return true if valid, false if invalid
	 */
	public static boolean isValidIso639_1(String sCode)
	{
		if ((null == sCode) || (sCode.equals(""))) 
			return false ;
		
		if (sCode.length() > 2)
			return false ;
		
		return true ;
	}
	
	/**
	 * Verifies that the specified string is a valid ISO 3166‑1 code
	 * 
	 * TODO really check code validity
	 * 	  
	 * @param sCode the country code to validate
	 * @return true if valid, false if invalid
	 */
	public static boolean isValidIso3166_1(String sCode)
	{
		if ((null == sCode) || (sCode.equals(""))) 
			return false ;
		
		if (sCode.length() > 2)
			return false ;
		
		return true ;
	}
	
	/**
	 * Checks if a String is only made of one or several digits
	 * 
	 * @param sValue the String to check
	 * @return <code>true</code> if valid, <code>false</code> if invalid<br>Typically returns <code>false</code> for "", "foo", "aa345bbb"
	 */
	public static boolean isDigits(final String sValue)
	{
		if ((null == sValue) || ("".equals(sValue))) 
			return false ;
		
		return sValue.matches("\\d+") ;
	}
	
	/**
	 * Return a String with first char replaced by its upper case version
	 */
	public static String upperCaseFirstChar(final String sInput)
	{
		if ((null == sInput) || "".equals(sInput))
			return "" ;
		
		return Character.toUpperCase(sInput.charAt(0)) + sInput.substring(1) ;
	}
	
	public static boolean areStringsEqual(final String s1, final String s2)
	{
		if (null == s1)
			return (null == s2) ; 
		
		return s1.equals(s2) ;
	}
	
	public static boolean areStringsEqualIgnoreCase(final String s1, final String s2)
	{
		if (null == s1)
			return (null == s2) ; 
		
		return s1.equalsIgnoreCase(s2) ;
	}
}
