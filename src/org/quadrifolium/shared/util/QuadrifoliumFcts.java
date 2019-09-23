package org.quadrifolium.shared.util;

import java.util.HashMap;

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
public class QuadrifoliumFcts
{
	private static final String CODE_PATTERN = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ" ;
	
	public static final int CONCEPT_CODE_LEN  = 7 ;
	public static final int LEMMA_CODE_LEN    = 11 ;
	public static final int FLEX_CODE_LEN     = 13 ;
	
	public static final int FREETEXT_CODE_LEN = 13 ;
	public static final int FREETEXT_EXT_LEN  = 6 ;
	public static final int FREETEXT_HEAD_LEN = 1 ;  // the header specific information is 1 char long (the '+' sign) 
	
	private static final int LEXIQUE_CONCEPT_CODE_LEN  = 5 ;
	// private static final int LEXIQUE_LEMMA_CODE_LEN    = 6 ;
	
	public enum PartOfSpeech { nullPoS, commonNoun, verb, adjective, determiner, adverb, conjonction, preposition, pronoun, interjection } ;
	public enum Number       { nullNumber, singular, singulative, dual, trial, quadral, paucal, plural, collective, partitive } ;
	public enum Gender       { nullGender, masculine, feminine, neuter } ;
	public enum Semantics    { nullSemantic, isA, isAintran, at, hasUnit } ;
	
	/**
	 * Validate that a string contains valid chars for a code (means inside CODE_PATTERN)
	 * 
	 * The supposition made here is that a hand-made validator is way faster than a regular expression 
	 * 
	 * @param sCode String which validity is to be tested
	 * @param iLen  Mandatory length (if <code><= 0</code>, means any valid code length)
	 * 
	 * @return <code>true</code> if sCode is a valid string, <code>false</code> if not
	 */
	public static boolean isValidCode(final String sCode, final int iLen)
	{
		// Check code's length
		//
		if ((null == sCode) || "".equals(sCode))
			return false ;
		
    int iCodeLen = sCode.length() ;
    
    if (iLen <= 0)
    {
    	if ((CONCEPT_CODE_LEN != iCodeLen) && (LEMMA_CODE_LEN != iCodeLen) && (FLEX_CODE_LEN != iCodeLen))
    		return false ;
    }
    else if (iCodeLen != iLen)
    	return false ;

    // Check if all and every chars belong to the proper interval
    //
    for (int iIter = 0 ; iIter < iCodeLen ; iIter++) 
    {
    	char chAtIter = sCode.charAt(iIter) ;
    	
    	if (-1 == CODE_PATTERN.indexOf(chAtIter))
    		return false ;
    }
    
    return true ;
	}
	
	/**
	 * Get the concept code from a lemma or flex code
	 * 
	 * @param sCode Code to get concept code from
	 * 
	 * @return The CONCEPT_CODE_LEN first chars, or <code>""</code> if passed code if too short 
	 */
	public static String getConceptCode(final String sCode)
	{
		if ((null == sCode) || (sCode.length() < CONCEPT_CODE_LEN)) 
			return "" ;
		
		return sCode.substring(0, CONCEPT_CODE_LEN) ;
	}
	
	/**
	 * Is this code a concept code? (means does it have the proper length and format?)
	 */
	public static boolean isConceptCode(final String sCode) {
		return isValidCode(sCode, CONCEPT_CODE_LEN) ;
	}
	
	/**
	 * Get the lemma code from a flex code
	 * 
	 * @param sCode Code to get lemma code from
	 * 
	 * @return The LEMMA_CODE_LEN first chars, or <code>""</code> if passed code if too short 
	 */
	public static String getFullLemmaCode(final String sCode) 
	{
		if ((null == sCode) || (sCode.length() < LEMMA_CODE_LEN)) 
			return "" ;
		
		return sCode.substring(0, LEMMA_CODE_LEN) ;
	}
	
	/**
	 * Get the lemma specific code located after the concept code
	 * 
	 * @param sCode Code to get lemma specific code from
	 * 
	 * @return The chars located between CONCEPT_CODE_LEN and LEMMA_CODE_LEN, or <code>""</code> if passed code if too short 
	 */
	public static String getSpecificLemmaCode(final String sCode) 
	{
		if ((null == sCode) || (sCode.length() < LEMMA_CODE_LEN)) 
			return "" ;
		
		return sCode.substring(CONCEPT_CODE_LEN, LEMMA_CODE_LEN) ;
	}
	
	/**
	 * Is this code a lemma code? (means does it have the proper length and format?)
	 */
	public static boolean isLemmaCode(final String sCode) {
		return isValidCode(sCode, LEMMA_CODE_LEN) ;
	}
	
	/**
	 * Get the lemma specific code located after the concept code
	 * 
	 * @param sCode Code to get lemma specific code from
	 * 
	 * @return The chars located between CONCEPT_CODE_LEN and LEMMA_CODE_LEN, or <code>""</code> if passed code if too short 
	 */
	public static String getSpecificFlexCode(final String sCode) 
	{
		if ((null == sCode) || (sCode.length() < FLEX_CODE_LEN)) 
			return "" ;
		
		return sCode.substring(LEMMA_CODE_LEN, FLEX_CODE_LEN) ;
	}
	
	/**
	 * Is this code a flex code? (means does it have the proper length and format?)
	 */
	public static boolean isFlexCode(final String sCode) {
		return isValidCode(sCode, FLEX_CODE_LEN) ;
	}
	
	/**
	 * Get the free text specific code located after the concept code
	 * 
	 * @param sCode Code to get free text specific code from
	 * 
	 * @return The chars located between CONCEPT_CODE_LEN and LEMMA_CODE_LEN, or <code>""</code> if passed code if too short 
	 */
	public static String getSpecificFreeTextCode(String sCode) 
	{
		if ((null == sCode) || (sCode.length() < FREETEXT_CODE_LEN)) 
			return "" ;
		
		return sCode.substring(CONCEPT_CODE_LEN, FREETEXT_CODE_LEN) ;
	}
	
	/**
	 * Get the free text specific code located after the concept code
	 * 
	 * @param sCode Code to get free text specific code from
	 * 
	 * @return The chars located between CONCEPT_CODE_LEN and LEMMA_CODE_LEN, or <code>""</code> if passed code if too short 
	 */
	public static String getSpecificFreeTextHeaderCode(String sCode) 
	{
		// Get the specific code
		//
		String sSpecificCode = getSpecificFreeTextCode(sCode) ;
		
		if ("".equals(sSpecificCode))
			return "" ;
		
		// If the specific code doesn't start with a '+', if is not a free text header code
		//
		if ('+' != sSpecificCode.charAt(0))
			return "" ;
		
		return sSpecificCode.substring(FREETEXT_HEAD_LEN, FREETEXT_EXT_LEN) ;
	}
	
	/**
	 * Is this code a free text header code? (means does it have the proper length and format?)
	 */
	public static boolean isFreeTextHeaderCode(final String sCode)
	{
		String sConceptCode = getConceptCode(sCode) ;
		if (false == isConceptCode(sConceptCode))
			return false ;
		
		String sHeaderCode = getSpecificFreeTextHeaderCode(sCode) ;
		if ("".equals(sHeaderCode))
			return false ;
		
		return isValidCode(sHeaderCode, FREETEXT_EXT_LEN - FREETEXT_HEAD_LEN) ;
	}
	
	/**
	 * Get the concept code from a Lexique code
	 * 
	 * @param sCode Code to get concept code from
	 * 
	 * @return The LEXIQUE_CONCEPT_CODE_LEN first chars, or <code>""</code> if passed code if too short 
	 */
	public static String getLexiqueConceptCode(String sCode) 
	{
		if ((null == sCode) || (sCode.length() < LEXIQUE_CONCEPT_CODE_LEN)) 
			return "" ;
		
		return sCode.substring(0, LEXIQUE_CONCEPT_CODE_LEN) ;
	}
	
	/**
	 * Get the specific code (actually the last char) from a Lexique code
	 * 
	 * @param sCode Code to get specific code from
	 * 
	 * @return The last char, or <code>""</code> if passed code if too short 
	 */
	public static String getLexiqueSpecificCode(String sCode) 
	{
		if ((null == sCode) || (sCode.length() < LEXIQUE_CONCEPT_CODE_LEN + 1)) 
			return "" ;
		
		return sCode.substring(LEXIQUE_CONCEPT_CODE_LEN, LEXIQUE_CONCEPT_CODE_LEN + 1) ;
	}
	
	/**
	 * Get the concept code for a Part of speech category
	 */
	public static String getPartOfSpeechConceptCode(final PartOfSpeech iPoS)
	{
		switch(iPoS)
		{
			case commonNoun :
				return "0000001" ;
			case verb : 
				return "000000K" ;
			case adjective :
				return "0000008" ;
			case determiner :
				return "000000L" ;
			case adverb :
				return "000000M" ;
			case conjonction :
				return "000000N" ;
			case preposition :
				return "000000O" ;
			case pronoun :
				return "000000P" ;
			case interjection :
				return "000000T" ;
			default :
				return "" ;
		}
	}
	
	/**
	 * Get the concept code for the Part of speech concept 
	 */
	public static String getConceptCodeForPartOfSpeech() {
		return "000000I" ;
	}
	
	/**
	 * Get the concept code for a grammatical number
	 */
	public static String getGrammaticalNumberConceptCode(final Number iNumber)
	{
		switch(iNumber)
		{
			case singular :
				return "0000006" ;
			case plural :
				return "0000007" ;
			case singulative : 
				return "000000A" ;
			case dual :
				return "000000B" ;
			case trial :
				return "000000C" ;
			case quadral :
				return "000000D" ;
			case paucal :
				return "000000E" ;
			case collective :
				return "000000F" ;
			case partitive :
				return "000000G" ;
			default :
				return "" ;
		}
	}
	
	/**
	 * Get the concept code for the grammatical number concept 
	 */
	public static String getConceptCodeForGrammaticalNumber() {
		return "0000009" ;
	}
	
	/**
	 * Get the concept code for a grammatical gender
	 */
	public static String getGrammaticalGenderConceptCode(final Gender iGender)
	{         
		switch(iGender)
		{
			case feminine :
				return "0000004" ;
			case masculine :
				return "0000003" ;
			case neuter : 
				return "0000005" ;
			default :
				return "" ;
		}
	}
	
	/**
	 * Get the concept code for the grammatical gender concept 
	 */
	public static String getConceptCodeForGrammaticalGender() {
		return "0000002" ;
	}
	
	/**
	 * Get the concept code for a semantic network trait
	 */
	public static String getSemanticNetworkTraitConceptCode(final Semantics iSemantics)
	{                  
		switch(iSemantics)
		{
			case isA :
				return "000000H" ;
			case isAintran :
				return "000000Q" ;
			case at : 
				return "000000R" ;
			case hasUnit : 
				return "000000S" ;
			default :
				return "" ;
		}
	}
	
	/**
	 * Get the concept code for the "preferred term" concept 
	 */
	public static String getConceptCodeForPreferredTerm() {
		return "000000U" ;
	}
	
	/**
	 * Get the concept code for the "meaning clarification" concept 
	 */
	public static String getConceptCodeForMeaningClarification() {
		return "0000015" ;
	}
	
	/**
	 * Get the concept code for the "definition" concept 
	 */
	public static String getConceptCodeForDefinition() {
		return "00007P3" ;
	}
	
	/**
	 * Get the concept code for the "is a" semantic trait 
	 */
	public static String getConceptCodeForIsA() {
		return "000000H" ;
	}
	
	/**
	 * Get the concept code for the intransitive "is a" semantic trait 
	 */
	public static String getConceptCodeForIntransitiveIsA() {
		return "000000Q" ;
	}
	
	/**
	 * Get the concept code for the "at" semantic trait 
	 */
	public static String getConceptCodeForAt() {
		return "000000R" ;
	}
	
	/**
	 * Get the concept code for the "has unit" semantic trait 
	 */
	public static String getConceptCodeForHasUnit() {
		return "000000S" ;
	}
	
	/**
	 * Check if a (code, label) pair already exists in the buffer for a code, and, if true, return the label (if false, return <code>""</code>)
	 */
	public static String getLabelForCodeInBuffer(final String sCode, final String sLanguage, final HashMap<String, String> aLabelsForCodes)
	{
		if ((null == aLabelsForCodes) || aLabelsForCodes.isEmpty())
			return "" ;
		
		return aLabelsForCodes.get(sCode + ":" + sLanguage) ;
	}
	
	/**
	 * Add a (code, label) pair in the buffer
	 */
	public static void addLabelForCodeInBuffer(final String sCode, final String sLanguage, final String sLabel, HashMap<String, String> aLabelsForCodes)
	{
		if ((null == sCode) || "".equals(sCode) || (null == sLabel) || (null == aLabelsForCodes))
			return ;
		
		aLabelsForCodes.put(sCode + ":" + sLanguage, sLabel) ;
	}
}
