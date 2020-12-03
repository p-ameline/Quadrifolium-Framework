package org.quadrifolium.server.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import org.quadrifolium.server.ontology.FlexManager;
import org.quadrifolium.server.ontology.FreeTextManager;
import org.quadrifolium.server.ontology.LemmaManager;
import org.quadrifolium.server.ontology.TripleManager;
import org.quadrifolium.shared.ontology.Flex;
import org.quadrifolium.shared.ontology.Lemma;
import org.quadrifolium.shared.ontology.Triple;
import org.quadrifolium.shared.ontology.TripleWithLabel;
import org.quadrifolium.shared.rpc_util.SessionElements;
import org.quadrifolium.shared.util.ParsedLanguageTag;
import org.quadrifolium.shared.util.QuadrifoliumFcts;

import com.ldv.server.DBConnector;
import com.ldv.server.Logger;

/**
 * Server's global functions
 */
public class QuadrifoliumServerFcts
{
	public static final String FIRST_CONCEPT_CODE  = "0000001" ;
	public static final String FIRST_LEMMA_CODE    = "0001" ;
	public static final String FIRST_FLEX_CODE     = "01" ;
	
	public static final String FIRST_FREETEXT_HEAD = "+00001" ;
	public static final String FIRST_FREETEXT_CODE = "000001" ;
	
	/**
	 * Computes an increment in base 36 (from 0 to 9, then A to Z)
	 * 
	 * @param id the String id in base 36.
	 * @return the next id or <code>null</code>.
	 */
	static public String getNextId(final String sId) throws NumberFormatException
	//====================================================================
	{
		if (null == sId)
			return null ;
		
		int iLen = sId.length() ;
		if (0 == iLen)
			return null ;

		StringBuffer sNextId = new StringBuffer(sId) ;	//make a copy of the id
		
		// Start working with the last char
		//
		int iIndex = iLen - 1 ;
		
		while (true)
		{
			// Getting the char at current index and increment it
			//
			char cProposed = sId.charAt(iIndex) ;
			cProposed++ ;
			
			// If the new value is inside the valid range (0-9 A-Z) we are done 
			//
			if ((cProposed >= '0' && cProposed <= '9') || (cProposed >= 'A' && cProposed <= 'Z'))
			{
				sNextId.setCharAt(iIndex, cProposed) ;
				break;
			}
			// The valid "digit" after 9 is A and we are done 
			//
			else if (cProposed > '9' && cProposed < 'A')
			{
				sNextId.setCharAt(iIndex, 'A') ;
				break ;
			}
			// If not inside the valid range, put 0 at current index and move it one step to the left 
			//
			else
			{
				sNextId.setCharAt(iIndex, '0') ;
				if (0 == iIndex)
				  throw new NumberFormatException() ;
				iIndex-- ;
      }
    }
		
    return sNextId.toString() ;
	}
	
	/**
	 * Return the code of the first concept to be inserted in database 
	 */
	static public String getFirstConceptCode() {
		return FIRST_CONCEPT_CODE ;
	}
	
	/**
	 * Get the code of the first Lemma for a given concept
	 * 
	 * @param sConceptCode Concept's code
	 * 
	 * @return The lemma code, or <code>""</code> if the concept's code if wrong
	 */
	static public String getFirstLemmaCodeForConcept(final String sConceptCode)
	{
		// Check that the concept code is really a concept code
		//
		String sVerifiedConceptCode = QuadrifoliumFcts.getConceptCode(sConceptCode) ;
		
		if ((null == sVerifiedConceptCode) || "".equals(sVerifiedConceptCode))
			return "" ;
		
		return sVerifiedConceptCode + FIRST_LEMMA_CODE ;
	}
	
	/**
	 * Get the code of the next Lemma for a new concept from the max code in database
	 * 
	 * @param sMaxExistingLemmaCode Max lemma code from the database
	 * 
	 * @return The lemma code, or <code>""</code> if something is wrong
	 */
	static public String getNextLemmaCodeForNewConcept(final String sMaxExistingLemmaCode)
	{
		// Get a new concept code
		//
		String sMaxExistingConceptCode = QuadrifoliumFcts.getConceptCode(sMaxExistingLemmaCode) ;
		
		if ((null == sMaxExistingConceptCode) || "".equals(sMaxExistingConceptCode))
			return "" ;
		
		String sNewConceptCode = getNextId(sMaxExistingConceptCode) ;
		
		if ((null == sNewConceptCode) || "".equals(sNewConceptCode))
			return "" ;
		
		// Return the new concept code + the specific code of the first lemma for a concept
		//
		return sNewConceptCode + FIRST_LEMMA_CODE ;
	}
	
	/**
	 * Get the code of the next Lemma for an existing concept from the max code in database
	 * 
	 * @param sMaxExistingLemmaCode Max lemma code from the database for a given concept
	 * 
	 * @return The lemma code, or <code>""</code> if the concept's code if wrong
	 */
	static public String getNextLemmaCodeForExistingConcept(final String sMaxExistingLemmaCode)
	{
		// Get the concept code
		//
		String sConceptCode = QuadrifoliumFcts.getConceptCode(sMaxExistingLemmaCode) ;
		
		if ((null == sConceptCode) || "".equals(sConceptCode))
			return "" ;
		
		// Get the max lemma code for this concept
		//
		String sMaxLemmaCodeForConcept = QuadrifoliumFcts.getSpecificLemmaCode(sMaxExistingLemmaCode) ; 
				
		if ((null == sMaxLemmaCodeForConcept) || "".equals(sMaxLemmaCodeForConcept))
			return "" ;
		
		// Get the next lemma code
		//
		String sNewLemmaCode = getNextId(sMaxLemmaCodeForConcept) ;
		
		if ((null == sNewLemmaCode) || "".equals(sNewLemmaCode))
			return "" ;
		
		return sConceptCode + sNewLemmaCode ;
	}
	
	/**
	 * Get the code of the first Flex for a given lemma
	 * 
	 * @param sLemmaCode Lemma's code
	 * 
	 * @return The flex code, or <code>""</code> if lemma's code if wrong
	 */
	static public String getFirstFlexCodeForLemma(final String sLemmaCode)
	{
		// Check that the lemma code is really a lemma code
		//
		String sVerifiedLemmaCode = QuadrifoliumFcts.getFullLemmaCode(sLemmaCode) ;
		
		if ((null == sVerifiedLemmaCode) || "".equals(sVerifiedLemmaCode))
			return "" ;
		
		return sVerifiedLemmaCode + FIRST_FLEX_CODE ;
	}
	
	/**
	 * Get the code of the next Lemma for an existing concept from the max code in database
	 * 
	 * @param sMaxExistingLemmaCode Max lemma code from the database for a given concept
	 * 
	 * @return The lemma code, or <code>""</code> if the concept's code if wrong
	 */
	static public String getNextFlexCodeForLemma(final String sMaxExistingFlexCode)
	{
		// Get the lemma code
		//
		String sLemmaCode = QuadrifoliumFcts.getFullLemmaCode(sMaxExistingFlexCode) ;
		
		if ((null == sLemmaCode) || "".equals(sLemmaCode))
			return "" ;
		
		// Get the max flex code for this lemma
		//
		String sMaxFlexCodeForLemma = QuadrifoliumFcts.getSpecificFlexCode(sMaxExistingFlexCode) ; 
				
		if ((null == sMaxFlexCodeForLemma) || "".equals(sMaxFlexCodeForLemma))
			return "" ;
		
		// Get the next lemma code
		//
		String sNewFlexCode = getNextId(sMaxFlexCodeForLemma) ;
		
		if ((null == sNewFlexCode) || "".equals(sNewFlexCode))
			return "" ;
		
		return sLemmaCode + sNewFlexCode ;
	}
	
	/**
	 * Get the "best label" for a code (concept, lemma or inflection)
	 * 
	 * @param dbConnector Database connector to the Ontology database
	 * @param sLanguage   Language of the label to look for (if empty, English is assumed)
	 * @param sCode       Code to get label for
	 * 
	 * @return The label if found, <code>""</code> if not
	 */
	public static String getLabelForCodeInBase(DBConnector dbConnector, final String sLanguage, final String sCode, final SessionElements sessionElements)
	{
		String sFctName = "QuadrifoliumServerFcts.getLabelForCodeInBase" ;
		
		// Check parameters
		// 
		if ((null == dbConnector) || (null == sCode) || "".equals(sCode))
		{
			Logger.trace(sFctName + ": bad parameter, leaving.", -1, Logger.TraceLevel.ERROR) ;
			return "" ;
		}
		
		String sActiveLanguage = sLanguage ;
		
		if ((null == sLanguage) || "".equals(sLanguage))
		{
			Logger.trace(sFctName + ": bad parameter for language, switching to English.", -1, Logger.TraceLevel.ERROR) ;
			sActiveLanguage = "en" ;
		}
		
		// The behavior depends from code category (concept, lemma or inflection)
		//
		if (QuadrifoliumFcts.isConceptCode(sCode))
			return getLabelForConceptCodeInBase(dbConnector, sActiveLanguage, sCode, sessionElements) ;
		
		if (QuadrifoliumFcts.isLemmaCode(sCode))
		{
			Lemma lemma = new Lemma() ;
			
			LemmaManager lemmaManager = new LemmaManager(sessionElements, dbConnector) ;
			if (lemmaManager.existData(sCode, lemma))
				return lemma.getLabel() ;
		}
		
		if (QuadrifoliumFcts.isFlexCode(sCode))
		{
			Flex flex = new Flex() ;
			
			FlexManager flexManager = new FlexManager(sessionElements, dbConnector) ;
			if (flexManager.existData(sCode, flex))
				return flex.getLabel() ;
		}
		
		if (QuadrifoliumFcts.isFreeTextHeaderCode(sCode))
			return getFreeTextForCodeInBase(dbConnector, sCode, sessionElements) ;
		
		return "" ;
	}
	
	/**
	 * Get the "best label" for a concept code, actually the first inflection of the preferred lemma
	 * 
	 * @param dbConnector Database connector to the Ontology database
	 * @param sLanguage   Language of the label to look for (if empty, English is assumed)
	 * @param sCode       Concept code to get label for
	 * 
	 * @return The label if found, <code>""</code> if not
	 */
	public static String getLabelForConceptCodeInBase(DBConnector dbConnector, final String sLanguage, final String sCode, final SessionElements sessionElements)
	{
		String sFctName = "QuadrifoliumServerFcts.getLabelForConceptCodeInBase" ;
		
		// Check parameters
		// 
		if ((null == dbConnector) || (null == sCode) || "".equals(sCode))
		{
			Logger.trace(sFctName + ": bad parameter, leaving.", -1, Logger.TraceLevel.ERROR) ;
			return "" ;
		}
		
		String sActiveLanguage = sLanguage ;
		
		if ((null == sLanguage) || "".equals(sLanguage))
		{
			Logger.trace(sFctName + ": bad parameter for language, switching to English.", -1, Logger.TraceLevel.ERROR) ;
			sActiveLanguage = "en" ;
		}
		
		// Get the code of the preferred lemma, if any
		//
		Lemma preferredLemma = getPreferredLemmaForConcept(dbConnector, sActiveLanguage, sCode, sessionElements) ;
		
		if (null == preferredLemma)
			return "" ;
		
		// Get all inflections for this lemma
		//
		FlexManager flexManager = new FlexManager(sessionElements, dbConnector) ;
		
		ArrayList<Flex> aFlexForLemma = new ArrayList<Flex>() ;
		if ((false == flexManager.existDataForLemma(preferredLemma.getCode(), aFlexForLemma)) || aFlexForLemma.isEmpty())
			return preferredLemma.getLabel() ;
		
		// Sort on code
		//
		Collections.sort(aFlexForLemma) ;
		
		// Return first element's label 
		//
		Iterator<Flex> it = aFlexForLemma.iterator() ;
		return it.next().getLabel() ;
	}

	/**
	 * Get the label attached to a given code (be it a concept, a lemma or an inflection)
	 * 
	 * @param dbconnector     Database connector to the Ontology
	 * @param iUserId         User ID
	 * @param sLanguage       Language to get the label for
	 * @param sCode           Object's code to get the label of
	 * @param aLabelsForCodes Buffer of code-label pairs to speed process
	 * 
	 * @return The label if possible, <code>""</code> if not
	 */
	public static String getLabelForCode(DBConnector dbConnector, final SessionElements sessionElements, final String sLanguage, final String sCode, HashMap<String, String> aLabelsForCodes)
	{
		// First, check if this code is already in the buffer 
		//
		String sLabel = QuadrifoliumFcts.getLabelForCodeInBuffer(sCode, sLanguage, aLabelsForCodes) ;
		if ((null != sLabel) && (false == "".equals(sLabel)))
			return sLabel ;
			
		// If not found in the buffer, get information from database
		//
		sLabel = QuadrifoliumServerFcts.getLabelForCodeInBase(dbConnector, sLanguage, sCode, sessionElements) ;
		if (false == "".equals(sLabel))
		{
			QuadrifoliumFcts.addLabelForCodeInBuffer(sCode, sLanguage, sLabel, aLabelsForCodes) ;
			return sLabel ;
		}
		
		return sLabel ;
	}
	
	/**
	 * Fill labels for a trait
	 * 
	 * @param dbconnector     Database connector
	 * @param iUserId         User ID
	 * @param sLanguage       Language to get all synonyms for
	 * @param trait           Object to be completed
	 * @param aLabelsForCodes Buffer of code-label pairs to speed process
	 */
	public static void fillTraitWithLabels(DBConnector dbConnector, final SessionElements sessionElements, final String sLanguage, TripleWithLabel trait, HashMap<String, String> aLabelsForCodes)
	{
		trait.setSubjectLabel(getLabelForCode(dbConnector, sessionElements, sLanguage, trait.getSubject(), aLabelsForCodes)) ;
		trait.setPredicateLabel(getLabelForCode(dbConnector, sessionElements, sLanguage, trait.getPredicate(), aLabelsForCodes)) ;
		trait.setObjectLabel(getLabelForCode(dbConnector, sessionElements, sLanguage, trait.getObject(), aLabelsForCodes)) ;
	}
	
	/**
	 * Get the free text attached to a given code
	 * 
	 * @param dbconnector     Database connector to the Ontology
	 * @param sessionElements Session elements
	 * @param sCode           Free text code
	 * @param aLabelsForCodes Buffer of code-label pairs to speed process
	 * 
	 * @return The free text if found, <code>""</code> if not
	 */
	public static String getFreeTextForCode(DBConnector dbConnector, final SessionElements sessionElements, final String sCode, HashMap<String, String> aLabelsForCodes)
	{
		// First, check if this code is already in the buffer 
		//
		String sLabel = QuadrifoliumFcts.getLabelForCodeInBuffer(sCode, "", aLabelsForCodes) ;
		if ((null != sLabel) && (false == "".equals(sLabel)))
			return sLabel ;
			
		// If not found in the buffer, get information from database
		//
		sLabel = QuadrifoliumServerFcts.getFreeTextForCodeInBase(dbConnector, sCode, sessionElements) ;
		if (false == "".equals(sLabel))
		{
			QuadrifoliumFcts.addLabelForCodeInBuffer(sCode, "", sLabel, aLabelsForCodes) ;
			return sLabel ;
		}
		
		return sLabel ;
	}
	
	/**
	 * Get the preferred lemma for a concept code<br>
	 * <br>
	 * This function is read only, hence it doesn't need a registered user.
	 * 
	 * @param dbConnector Database connector to the Ontology database
	 * @param sLanguage   Language of the label to look for (if empty, English is assumed)
	 * @param sCode       Concept code to get label for
	 * 
	 * @return The Lemma if found, <code>null</code> if not
	 */
	public static Lemma getPreferredLemmaForConcept(DBConnector dbConnector, final String sLanguage, final String sCode, final SessionElements sessionElements) throws NullPointerException
	{
		if (null == dbConnector)
			throw new NullPointerException() ;
		
		String sFctName = "QuadrifoliumServerFcts.getPreferredLemmaForConcept 1" ;
		
		// This function is read only, hence it doesn't need a registered user
		//
		int iUserId = -1 ;
		if (null != sessionElements)
			iUserId = sessionElements.getPersonId() ;
		
		// Check parameters
		// 
		if ((null == dbConnector) || (null == sCode) || "".equals(sCode))
		{
			Logger.trace(sFctName + ": bad parameter, leaving.", iUserId, Logger.TraceLevel.ERROR) ;
			return null ;
		}
		
		String sActiveLanguage = sLanguage ;
		
		if ((null == sLanguage) || "".equals(sLanguage))
		{
			Logger.trace(sFctName + ": bad parameter for language, switching to English.", iUserId, Logger.TraceLevel.ERROR) ;
			sActiveLanguage = "en" ;
		}
		
		// Create required managers and call function #2
		//
		LemmaManager  lemmaManager  = new LemmaManager(sessionElements, dbConnector) ;
		TripleManager tripleManager = new TripleManager(sessionElements, dbConnector) ;

		return getPreferredLemmaForConcept(sActiveLanguage, sCode, lemmaManager, tripleManager, sessionElements) ;		
	}
	
	/**
	 * Get the preferred lemma for a concept code<br>
	 * <br>
	 * This function is read only, hence it doesn't need a registered user.
	 * 
	 * @param sLanguage       Language of the label to look for (if empty, English is assumed)
	 * @param sCode           Concept code to get label for
	 * @param lemmaManager    Manager for the lemma table
	 * @param tripleManager   Manager for the triple table
	 * @param sessionElements Session information
	 * 
	 * @return The Lemma if found, <code>null</code> if not
	 */
	public static Lemma getPreferredLemmaForConcept(final String sLanguage, final String sCode, LemmaManager lemmaManager, TripleManager tripleManager, final SessionElements sessionElements) throws NullPointerException
	{
		if ((null == lemmaManager) || (null == tripleManager))
			throw new NullPointerException() ;
		
		String sFctName = "QuadrifoliumServerFcts.getPreferredLemmaForConcept 2" ;
		
		// This function is read only, hence it doesn't need a registered user
		//
		int iUserId = -1 ;
		if (null != sessionElements)
			iUserId = sessionElements.getPersonId() ;
		
		// Check parameters
		// 
		if ((null == sCode) || "".equals(sCode))
		{
			Logger.trace(sFctName + ": bad parameter, leaving.", iUserId, Logger.TraceLevel.ERROR) ;
			return null ;
		}
		
		String sActiveLanguage = sLanguage ;
		
		if ((null == sLanguage) || "".equals(sLanguage))
		{
			Logger.trace(sFctName + ": bad parameter for language, switching to English.", iUserId, Logger.TraceLevel.ERROR) ;
			sActiveLanguage = "en" ;
		}
		
		// Look for a triple that points to the preferred lemma for a term (such a triple can exists for each different language)
		//
		
		// Getting all preferred terms triples for this concept
		//
		ArrayList<Triple> aResults = new ArrayList<Triple>() ;
		tripleManager.getObjects(sCode, QuadrifoliumFcts.getConceptCodeForPreferredTerm(), "", aResults) ;
				
		// Check if there is a result for this specific language
		//
		if (false == aResults.isEmpty())
		{
			for (Iterator<Triple> it = aResults.iterator() ; it.hasNext() ; )
			{
				String sLemmaCode = it.next().getObject() ;
				
				// Get record and check if it fits with the language
				//
				Lemma lemma = new Lemma() ;
				if (lemmaManager.existData(sLemmaCode, lemma) && sActiveLanguage.equals(lemma.getLanguage()))
					return lemma ;
			}
		}
		
		// If there, it means that no "official" preferred lemma was found for this language,
		// we return the first entered existing one (means "lowest" code).
		//
		ArrayList<Lemma> aLemmasForConcept = new ArrayList<Lemma>() ;
		
		if ((lemmaManager.existDataForConcept(sCode, sActiveLanguage, aLemmasForConcept)) && (false == aLemmasForConcept.isEmpty()))
		{
			// Sort on code
			//
			Collections.sort(aLemmasForConcept) ;
			
			// Return first element's label 
			//
			Iterator<Lemma> it = aLemmasForConcept.iterator() ;
			return it.next() ;
		}
		
		// If there, it means that no lemma where found for this code and this level of precision in language
		// we will try to find something for a more generic language
		//
		
		// "en" is already considered as "the most generic of all languages"
		//
		if ("en".equals(sActiveLanguage))
			return null ;
		
		// Get a "one level more generic language" and call the function recursively on it
		//
		ParsedLanguageTag languageTag = new ParsedLanguageTag(sActiveLanguage) ;
		String sMoreGenericLanguage = languageTag.getMoreGenericTag() ;
		
		return getPreferredLemmaForConcept(sMoreGenericLanguage, sCode, lemmaManager, tripleManager, sessionElements) ;
	}
	
	/**
	 * Get the code of the first free text header for a given concept
	 * 
	 * @param sConceptCode Concept code
	 * 
	 * @return The free text header code, or <code>""</code> if concept code if wrong
	 */
	static public String getFirstFreeTextHeaderCodeForConcept(final String sConceptCode)
	{
		// Check that the concept code is really a concept code
		//
		String sVerifiedConceptCode = QuadrifoliumFcts.getConceptCode(sConceptCode) ;
		
		if ((null == sVerifiedConceptCode) || "".equals(sVerifiedConceptCode))
			return "" ;
		
		return sVerifiedConceptCode + FIRST_FREETEXT_HEAD ;
	}
	
	/**
	 * Get the code of the first free text for a given concept
	 * 
	 * @param sConceptCode Concept code
	 * 
	 * @return The free text code, or <code>""</code> if concept code if wrong
	 */
	static public String getFirstFreeTextCodeForConcept(final String sConceptCode)
	{
		// Check that the concept code is really a concept code
		//
		String sVerifiedConceptCode = QuadrifoliumFcts.getConceptCode(sConceptCode) ;
		
		if ((null == sVerifiedConceptCode) || "".equals(sVerifiedConceptCode))
			return "" ;
		
		return sVerifiedConceptCode + FIRST_FREETEXT_CODE ;
	}
	
	/**
	 * Get the code of the next free text for the concept from the max code in database
	 * 
	 * @param sMaxExistingFreeTextHeaderCode Max free text header code from the database for a given concept
	 * 
	 * @return The next free text header code, or <code>""</code> if something went wrong
	 */
	static public String getNextFreeTextHeaderCodeForConcept(final String sMaxExistingFreeTextHeaderCode)
	{
		// Get the concept code
		//
		String sConceptCode = QuadrifoliumFcts.getConceptCode(sMaxExistingFreeTextHeaderCode) ;
		
		if ((null == sConceptCode) || "".equals(sConceptCode))
			return "" ;
		
		// Get the max header code for this concept
		//
		String sMaxFreeTextHeaderCodeForConcept = QuadrifoliumFcts.getSpecificFreeTextHeaderCode(sMaxExistingFreeTextHeaderCode) ; 
				
		if ((null == sMaxFreeTextHeaderCodeForConcept) || "".equals(sMaxFreeTextHeaderCodeForConcept))
			return "" ;
		
		// Get the next free text header code
		//
		String sNewHeaderCode = getNextId(sMaxFreeTextHeaderCodeForConcept) ;
		
		if ((null == sNewHeaderCode) || "".equals(sNewHeaderCode))
			return "" ;
		
		return sConceptCode + "+" + sNewHeaderCode ;
	}
	
	/**
	 * Get the code of the next free text for the concept from the max code in database
	 * 
	 * @param sMaxExistingFreeTextCode Max free text code from the database for a given concept
	 * 
	 * @return The next free text code, or <code>""</code> if something went wrong
	 */
	static public String getNextFreeTextCodeForConcept(final String sMaxExistingFreeTextCode)
	{
		// Get the concept code
		//
		String sConceptCode = QuadrifoliumFcts.getConceptCode(sMaxExistingFreeTextCode) ;
		
		if ((null == sConceptCode) || "".equals(sConceptCode))
			return "" ;
		
		// Get the max free text code for this concept
		//
		String sMaxFreeTextCodeForConcept = QuadrifoliumFcts.getSpecificFreeTextCode(sMaxExistingFreeTextCode) ; 
				
		if ((null == sMaxFreeTextCodeForConcept) || "".equals(sMaxFreeTextCodeForConcept))
			return "" ;
		
		// Get the next free text header code
		//
		String sNewCode = getNextId(sMaxFreeTextCodeForConcept) ;
		
		if ((null == sNewCode) || "".equals(sNewCode))
			return "" ;
		
		return sConceptCode + sNewCode ;
	}
	
	/**
	 * Get the free text for a code
	 * 
	 * @param dbConnector     Database connector to the Ontology database
	 * @param sCode           Code to get the free text for
	 * @param sessionElements Session elements
	 * 
	 * @return The free text if found, <code>""</code> if not
	 */
	public static String getFreeTextForCodeInBase(DBConnector dbConnector, final String sCode, final SessionElements sessionElements)
	{
		String sFctName = "QuadrifoliumServerFcts.getFreeTextForCodeInBase" ;
		
		// Check parameters
		// 
		if ((null == dbConnector) || (null == sCode) || "".equals(sCode))
		{
			Logger.trace(sFctName + ": bad parameter, leaving.", -1, Logger.TraceLevel.ERROR) ;
			return "" ;
		}
		
		if (false == QuadrifoliumFcts.isFreeTextHeaderCode(sCode))
			return "" ;
		
		FreeTextManager freeTextManager = new FreeTextManager(sessionElements, dbConnector) ;
		
		return freeTextManager.getFreeTextLabel(sCode, null) ;
	}
	
	/**
	 * Insert a new triple in database
	 * 
	 * @return ID of created triple (<code>-1</code> is something went wrong)
	 */
	public static int recordTriple(DBConnector dbConnector, final String sSubject, final String sPredicate, final String sObject, final SessionElements sessionElements) throws NullPointerException
	{
		if ((null == dbConnector) || (null == sSubject) || (null == sPredicate) || (null == sObject))
			throw new NullPointerException() ;
		
		if ("".equals(sSubject) || "".equals(sPredicate) || "".equals(sObject))
			return -1 ;
		
		Triple newTriple = new Triple(-1, sSubject, sPredicate, sObject) ;
		
		return recordTriple(dbConnector, newTriple, sessionElements) ;
	}
	
	/**
	 * Insert a new triple in database
	 * 
	 * @return ID of created triple (<code>-1</code> is something went wrong)
	 */
	public static int recordTriple(DBConnector dbConnector, Triple tripleToInsert, final SessionElements sessionElements) throws NullPointerException
	{
		if ((null == dbConnector) || (null == tripleToInsert))
			throw new NullPointerException() ;
		
		TripleManager tripleManager = new TripleManager(sessionElements, dbConnector) ;
		if (false == tripleManager.insertData(tripleToInsert))
			return -1 ;
		
		return tripleToInsert.getId() ;
	}
	
	/**
	 * Insert a new triple in database
	 * 
	 * @return ID of created triple (<code>-1</code> is something went wrong)
	 */
	public static int recordTriple(TripleManager tripleManager, final String sSubject, final String sPredicate, final String sObject, final SessionElements sessionElements) throws NullPointerException
	{
		if ((null == tripleManager) || (null == sSubject) || (null == sPredicate) || (null == sObject))
			throw new NullPointerException() ;
		
		if ("".equals(sSubject) || "".equals(sPredicate) || "".equals(sObject))
			return -1 ;
		
		Triple newTriple = new Triple(-1, sSubject, sPredicate, sObject) ;
		
		return recordTriple(tripleManager, newTriple, sessionElements) ;
	}
	
	/**
	 * Insert a new triple in database
	 * 
	 * @return ID of created triple (<code>-1</code> is something went wrong)
	 */
	public static int recordTriple(TripleManager tripleManager, Triple tripleToInsert, final SessionElements sessionElements) throws NullPointerException
	{
		if ((null == tripleManager) || (null == tripleToInsert))
			throw new NullPointerException() ;
		
		if (false == tripleManager.insertData(tripleToInsert))
			return -1 ;
		
		return tripleToInsert.getId() ;
	}
	
	/**
	 * Insert a free text in database
	 * 
	 * @param sText The text to be stored
	 * 
	 * @return The "header code" of the stored text
	 */
	public static String recordFreeText(DBConnector dbConnector, final String sConceptCode, final String sText, final String sLanguage, final SessionElements sessionElements)
	{
		if ((null == dbConnector) || (null == sText) || "".equals(sText))
			return "" ;
		
		FreeTextManager freeTextManager = new FreeTextManager(sessionElements, dbConnector) ;
		
		return freeTextManager.insertData(sConceptCode, sText, sLanguage) ;
	}
}
