package org.quadrifolium.server.ontology;

import org.quadrifolium.server.DBConnector;
import org.quadrifolium.server.Logger;
import org.quadrifolium.server.util.QuadrifoliumServerFcts;
import org.quadrifolium.shared.ontology.Flex;
import org.quadrifolium.shared.ontology.Lemma;
import org.quadrifolium.shared.ontology.OntologyLexicon;
import org.quadrifolium.shared.ontology.OntologyLexicon.Declination;
import org.quadrifolium.shared.ontology.OntologyLexicon.Gender;
import org.quadrifolium.shared.rpc_util.SessionElements;
import org.quadrifolium.shared.util.QuadrifoliumFcts;

/** 
 * Object in charge of operations to create a lemma and its flexed forms from a <code>lexique</code> 
 *   
 */
public class LexiconToLemmaManager  
{	
	protected final DBConnector     _dbConnector ;
	protected final SessionElements _sessionElements ;
	
	/**
	 * Constructor 
	 */
	public LexiconToLemmaManager(final SessionElements sessionElements, final DBConnector dbConnector)
	{
		_dbConnector     = dbConnector ;
		_sessionElements = sessionElements ;
	}

	/**
	 * Insert a lemma from the Lexique in Quadrifolium
	 * 
	 * @param sQConceptCode Quadrifolium concept code for this lemma, can be <code>""</code> if this is the first lemma for this concept
	 * @param lexicon       Lexique object to process
	 * @param sLanguageTag  Language 
	 */
	public String processLemmaFromLexique(final String sQConceptCode, final OntologyLexicon lexicon, final String sLanguageTag, boolean bUpdateLexicon)
	{
		String sFunctionName = "LexiqueTo4foliumHandler.processLexiqueLemma" ;
		
		// First step, just treat nouns and adjectives
		//
		if ((false == lexicon.isAdjective()) && (false == lexicon.isNoon()))
		{
			Logger.trace(sFunctionName +  ": only noons and adjectives can be processed, leaving", _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
			return "" ;
		}
		
		// To make certain that the connection will be closed, a "finally" block must be added to secure the call to closeAll
		//
		DBConnector dbConnector2 = null ; 
		
		String sLemmaCode = "" ;
		
		try
		{
			dbConnector2 = new DBConnector(false, _sessionElements.getPersonId()) ;
		
			if ((null == dbConnector2) || (null == lexicon))
			{
				Logger.trace(sFunctionName +  ": bad parameters, leaving", _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
				return "" ;
			}
		
			// Is there a meaning clarification information?
			//
			String sMeaningClarification = lexicon.getMeaningClarification() ;
		
			String sLemmaLabel = lexicon.getLabel() ;
			if (false == "".equals(sMeaningClarification))
				sLemmaLabel = OntologyLexicon.removeTrailingComments(sLemmaLabel) ;
		
			// Prepare the Lemma to be inserted
			//
			Lemma lemma = new Lemma() ;
			lemma.setLabel(sLemmaLabel) ;
			lemma.setLanguage(sLanguageTag) ;
		
			// Get the new lemma code
			//
			LemmaManager lemmaManager = new LemmaManager(_sessionElements, dbConnector2) ;
			sLemmaCode = lemmaManager.getNextLemmaCode(sQConceptCode) ;
			
			if ("".equals(sLemmaCode))
			{
				Logger.trace(sFunctionName +  ": lemma \"" + lexicon.getLabel() + "\" could not be inserted since no code could be affected to it, leaving", _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
				return "" ;
			}
		
			// Add the new lemma in database
			//
			lemma.setCode(sLemmaCode) ;
			if (false == lemmaManager.insertData(lemma))
				return "" ;
		
			// Update the lexicon
			//
			if (bUpdateLexicon)
			{
				LexiconManager lexiconManager = new LexiconManager(_sessionElements.getPersonId(), dbConnector2) ;
				lexicon.setLemma(sLemmaCode) ;
				lexiconManager.updateData(lexicon) ;
			}
		
			// If Lexicon code ends with a '1', it is considered the preferred term for its concept
			//
			if (false == "".equals(lexicon.getCode()))
				if ("1".equals(QuadrifoliumFcts.getLexiqueSpecificCode(lexicon.getCode())))
					recordPreferredTerm(sLemmaCode, dbConnector2) ;
		
			// Process the "meaning clarification" information
			//
			if (false == "".equals(sMeaningClarification))
				recordMeaningClarification(sLemmaCode, sMeaningClarification, dbConnector2) ;
		
			// Create the flexed forms
			//
			if      (lexicon.isNoon())
				processNounLemma(sLemmaCode, lexicon, dbConnector2) ;
			else if (lexicon.isAdjective())
				processAdjectiveLemma(sLemmaCode, lexicon, dbConnector2) ;
		}
		finally
		{
			if (null != dbConnector2)
				dbConnector2.closeAll() ;
		}
		
		return sLemmaCode ;
	}
	
	/**
	 * Insert a noun lemma from the Lexique in Quadrifolium
	 * 
	 * @param sLemmaCode  Quadrifolium code for this lemma
	 * @param lexicon     Lexique object to process
	 * @param dbConnector DBConnector
	 */
	protected void processNounLemma(final String sLemmaCode, OntologyLexicon lexicon, DBConnector dbConnector) throws NullPointerException
	{
		if ((null == sLemmaCode) || (null == dbConnector) || (null == lexicon))
			throw new NullPointerException() ;
		
		// Record the part of speech for this lemma
		//
		recordPartOfSpeech(sLemmaCode, QuadrifoliumFcts.PartOfSpeech.commonNoun, dbConnector, _sessionElements) ;
		
		// Record the gender for this lemma
		//
		QuadrifoliumFcts.Gender iGender = QuadrifoliumFcts.Gender.nullGender ;
		if      (lexicon.isFemaleGender())
			iGender = QuadrifoliumFcts.Gender.feminine ;
		else if (lexicon.isMaleGender())
			iGender = QuadrifoliumFcts.Gender.masculine ;
		else if (lexicon.isNeutralGender())
			iGender = QuadrifoliumFcts.Gender.neuter ;
		
		if (QuadrifoliumFcts.Gender.nullGender != iGender)
			recordGrammaticalGender(sLemmaCode, iGender, dbConnector, _sessionElements) ;
		
		// Create inflections
		//
		createFlexedFormsForNoun(sLemmaCode, lexicon, "fr", dbConnector, _sessionElements) ;		
	}

	/**
	 * Create flexed forms for a noun lemma from the Lexique in Quadrifolium
	 * 
	 * @param sLemmaCode  Quadrifolium code for this lemma
	 * @param lexicon     Lexique object to process
	 * @param sLanguage   Language to create flexed forms for
	 * @param dbConnector DBConnector
	 */
	public static void createFlexedFormsForNoun(final String sLemmaCode, OntologyLexicon lexicon, final String sLanguage, DBConnector dbConnector, final SessionElements sessionElements) throws NullPointerException
	{
		if ((null == sLemmaCode) || (null == dbConnector) || (null == lexicon) || (null == sLanguage))
			throw new NullPointerException() ;

		// Languages which noons have a singular and a plural flexed forms
		//
		if (("fr".equals(sLanguage)) || 
				("en".equals(sLanguage)) || 
				("it".equals(sLanguage)) || 
				("es".equals(sLanguage)) ||
				("pt".equals(sLanguage)))
		{
			createDeclinationForNoun(sLemmaCode, lexicon, sLanguage, QuadrifoliumFcts.Number.singular, dbConnector, sessionElements) ;
			createDeclinationForNoun(sLemmaCode, lexicon, sLanguage, QuadrifoliumFcts.Number.plural, dbConnector, sessionElements) ;
		}
	}
	
	/**
	 * Create a flexed form, consistent with a given declination, for a noun lemma from the Lexique
	 * 
	 * @param sLemmaCode        Quadrifolium code for this lemma
	 * @param lexicon           Lexique object to process
	 * @param sLanguage         Language to create the flexed forms for
	 * @param grammaticalNumber Grammatical number to create the flexed forms for
	 * @param dbConnector       DBConnector
	 */
	public static void createDeclinationForNoun(final String sLemmaCode, OntologyLexicon lexicon, final String sLanguage, final QuadrifoliumFcts.Number grammaticalNumber, DBConnector dbConnector, final SessionElements sessionElements) throws NullPointerException
	{
		if ((null == sLemmaCode) || (null == dbConnector) || (null == lexicon) || (null == sLanguage))
			throw new NullPointerException() ;
		
		// Get label for this declination and this language
		//
		OntologyLexicon.Declination declination = OntologyLexicon.getDeclinationFromGrammaticalNumber(grammaticalNumber) ; 
				
		if (OntologyLexicon.Declination.nullDeclination == declination)
			return ;
		
		String sLabel = lexicon.getDisplayLabelForNoon(declination, sLanguage) ;
		
		if ((null == sLabel) || "".equals(sLabel))
			return ;
		
		// Create Flex object to be saved 
		//
		Flex flex = new Flex() ;
		flex.setLabel(sLabel) ;
		flex.setLanguage(sLanguage) ;
		
		// Record in database after getting a code
		//
		FlexManager flexManager = new FlexManager(sessionElements, dbConnector) ;
		
		String sFlexCode = flexManager.getNextFlexCode(sLemmaCode) ;
		if (false == "".equals(sFlexCode))
		{
			flex.setCode(sFlexCode) ;
			flexManager.insertData(flex) ;
		}
		
		// Record the grammatical number for this inflection
		//
		recordGrammaticalNumber(sFlexCode, grammaticalNumber, dbConnector, sessionElements) ;
	}
	
	/**
	 * Create a flexed form, consistent with a given declination, for a noun lemma from the Lexique
	 * 
	 * @param sLemmaCode        Quadrifolium code for this lemma
	 * @param lexicon           Lexique object to process
	 * @param sLanguage         Language to create the flexed forms for
	 * @param grammaticalNumber Grammatical number to create the flexed forms for
	 * @param flexManager       Manager for the Flex table
	 * @param tripleManager     Manager for the Triple table
	 */
	public static void createDeclinationForNoun(final String sLemmaCode, OntologyLexicon lexicon, final String sLanguage, final QuadrifoliumFcts.Number grammaticalNumber, FlexManager flexManager, TripleManager tripleManager, final SessionElements sessionElements) throws NullPointerException
	{
		if ((null == sLemmaCode) || (null == flexManager) || (null == tripleManager) || (null == lexicon) || (null == sLanguage))
			throw new NullPointerException() ;
		
		// Get label for this declination and this language
		//
		OntologyLexicon.Declination declination = OntologyLexicon.getDeclinationFromGrammaticalNumber(grammaticalNumber) ; 
				
		if (OntologyLexicon.Declination.nullDeclination == declination)
			return ;
		
		String sLabel = lexicon.getDisplayLabelForNoon(declination, sLanguage) ;
		
		if ((null == sLabel) || "".equals(sLabel))
			return ;
		
		// Create Flex object to be saved 
		//
		Flex flex = new Flex() ;
		flex.setLabel(sLabel) ;
		flex.setLanguage(sLanguage) ;
		
		// Record in database after getting a code
		//
		String sFlexCode = flexManager.getNextFlexCode(sLemmaCode) ;
		if (false == "".equals(sFlexCode))
		{
			flex.setCode(sFlexCode) ;
			flexManager.insertData(flex) ;
		}
		
		// Record the grammatical number for this inflection
		//
		recordGrammaticalNumber(sFlexCode, grammaticalNumber, tripleManager, sessionElements) ;
	}
	
	/**
	 * Insert an adjective lemma from the Lexique in Quadrifolium
	 * 
	 * @param sLemmaCode  Quadrifolium code for this lemma
	 * @param lexicon     Lexique object to process
	 * @param dbConnector DBConnector
	 */
	protected void processAdjectiveLemma(final String sLemmaCode, OntologyLexicon lexicon, DBConnector dbConnector) throws NullPointerException
	{
		if ((null == sLemmaCode) || (null == dbConnector) || (null == lexicon))
			throw new NullPointerException() ;

		// Record the part of speech for this lemma
		//
		recordPartOfSpeech(sLemmaCode, QuadrifoliumFcts.PartOfSpeech.adjective, dbConnector, _sessionElements) ;
				
		// Create inflections for singular and plural, masculine and feminine
		//
		createFlexedFormsForAdjective(sLemmaCode, lexicon, "fr", dbConnector, _sessionElements) ;		
	}
	
	/**
	 * Create flexed forms for a noun lemma from the Lexique in Quadrifolium
	 * 
	 * @param sLemmaCode  Quadrifolium code for this lemma
	 * @param lexicon     Lexique object to process
	 * @param sLanguage   Language to create flexed forms for
	 * @param dbConnector DBConnector
	 */
	public static void createFlexedFormsForAdjective(final String sLemmaCode, OntologyLexicon lexicon, final String sLanguage, DBConnector dbConnector, final SessionElements sessionElements) throws NullPointerException
	{
		if ((null == sLemmaCode) || (null == dbConnector) || (null == lexicon) || (null == sLanguage))
			throw new NullPointerException() ;

		// Languages which adjectives get flexed according to both the grammatical gender and the grammatical number
		//
		if (("fr".equals(sLanguage)) || 
				("it".equals(sLanguage)) || 
				("es".equals(sLanguage)) || 
				("pt".equals(sLanguage)))
		{
			createDeclinationForAdjective(sLemmaCode, lexicon, sLanguage, QuadrifoliumFcts.Number.singular, QuadrifoliumFcts.Gender.feminine, dbConnector, sessionElements) ;
			createDeclinationForAdjective(sLemmaCode, lexicon, sLanguage, QuadrifoliumFcts.Number.plural,   QuadrifoliumFcts.Gender.feminine, dbConnector, sessionElements) ;
			createDeclinationForAdjective(sLemmaCode, lexicon, sLanguage, QuadrifoliumFcts.Number.singular, QuadrifoliumFcts.Gender.masculine, dbConnector, sessionElements) ;
			createDeclinationForAdjective(sLemmaCode, lexicon, sLanguage, QuadrifoliumFcts.Number.plural,   QuadrifoliumFcts.Gender.masculine, dbConnector, sessionElements) ;
		}
	}
	
	/**
	 * Create a flexed form, consistent with a given declination, for a noun lemma from the Lexique
	 * 
	 * @param sLemmaCode        Quadrifolium code for this lemma
	 * @param lexicon           Lexique object to process
	 * @param sLanguage         Language to create the flexed forms for
	 * @param grammaticalNumber Grammatical number to create the flexed forms for
	 * @param dbConnector       DBConnector
	 */
	public static void createDeclinationForAdjective(final String sLemmaCode, OntologyLexicon lexicon, final String sLanguage, final QuadrifoliumFcts.Number grammaticalNumber, final QuadrifoliumFcts.Gender grammaticalGender, DBConnector dbConnector, final SessionElements sessionElements) throws NullPointerException
	{
		if ((null == sLemmaCode) || (null == dbConnector) || (null == lexicon) || (null == sLanguage))
			throw new NullPointerException() ;
		
		// Get label for this declination and this language
		//
		OntologyLexicon.Gender gender = OntologyLexicon.getGenderFromNumberAndGender(grammaticalNumber, grammaticalGender) ; 
				
		if (OntologyLexicon.Gender.nullGender == gender)
			return ;
		
		String sLabel = lexicon.getDisplayLabelForAdjective(gender, sLanguage) ;
		
		if ((null == sLabel) || "".equals(sLabel))
			return ;
		
		// Create Flex object to be saved 
		//
		Flex flex = new Flex() ;
		flex.setLabel(sLabel) ;
		flex.setLanguage(sLanguage) ;
		
		// Record in database after getting a code
		//
		FlexManager flexManager = new FlexManager(sessionElements, dbConnector) ;
		
		String sFlexCode = flexManager.getNextFlexCode(sLemmaCode) ;
		if (false == "".equals(sFlexCode))
		{
			flex.setCode(sFlexCode) ;
			flexManager.insertData(flex) ;
		}
		
		// Record the grammatical number and the grammatical gender for this inflection
		//
		recordGrammaticalNumber(sFlexCode, grammaticalNumber, dbConnector, sessionElements) ;
		recordGrammaticalGender(sFlexCode, grammaticalGender, dbConnector, sessionElements) ;
	}
	
	/**
	 * Create a flexed form, consistent with a given declination, for a noun lemma from the Lexique
	 * 
	 * @param sLemmaCode        Quadrifolium code for this lemma
	 * @param lexicon           Lexique object to process
	 * @param sLanguage         Language to create the flexed forms for
	 * @param grammaticalNumber Grammatical number to create the flexed forms for
	 * @param dbConnector       DBConnector
	 */
	public static void createDeclinationForAdjective(final String sLemmaCode, OntologyLexicon lexicon, final String sLanguage, final QuadrifoliumFcts.Number grammaticalNumber, final QuadrifoliumFcts.Gender grammaticalGender, FlexManager flexManager, TripleManager tripleManager, final SessionElements sessionElements) throws NullPointerException
	{
		if ((null == sLemmaCode) || (null == flexManager) || (null == tripleManager) || (null == lexicon) || (null == sLanguage))
			throw new NullPointerException() ;
		
		// Get label for this declination and this language
		//
		OntologyLexicon.Gender gender = OntologyLexicon.getGenderFromNumberAndGender(grammaticalNumber, grammaticalGender) ; 
				
		if (OntologyLexicon.Gender.nullGender == gender)
			return ;
		
		String sLabel = lexicon.getDisplayLabelForAdjective(gender, sLanguage) ;
		
		if ((null == sLabel) || "".equals(sLabel))
			return ;
		
		// Create Flex object to be saved 
		//
		Flex flex = new Flex() ;
		flex.setLabel(sLabel) ;
		flex.setLanguage(sLanguage) ;
		
		// Record in database after getting a code
		//
		String sFlexCode = flexManager.getNextFlexCode(sLemmaCode) ;
		if (false == "".equals(sFlexCode))
		{
			flex.setCode(sFlexCode) ;
			flexManager.insertData(flex) ;
		}
		
		// Record the grammatical number and the grammatical gender for this inflection
		//
		recordGrammaticalNumber(sFlexCode, grammaticalNumber, tripleManager, sessionElements) ;
		recordGrammaticalGender(sFlexCode, grammaticalGender, tripleManager, sessionElements) ;
	}
	
	/**
	 * Record the part of speech information for a lemma
	 * 
	 * @param sLemmaCode      Lemma's code
	 * @param iPoS            Part of speech category
	 * @param dbConnector     Database connector
	 * @param sessionElements Quadrifolium session elements 
	 * 
	 * @return The ID affected to the triple if anything went well; <code>-1</code> if not
	 */
	public static int recordPartOfSpeech(final String sLemmaCode, final QuadrifoliumFcts.PartOfSpeech iPoS, DBConnector dbConnector, final SessionElements sessionElements)
	{
		if ((null == dbConnector) || (null == sLemmaCode) || "".equals(sLemmaCode))
			return -1 ;
		
		String sPoSCode = QuadrifoliumFcts.getPartOfSpeechConceptCode(iPoS) ;
		if ("".equals(sPoSCode))
			return -1 ;
		
		return QuadrifoliumServerFcts.recordTriple(dbConnector, sLemmaCode, QuadrifoliumFcts.getConceptCodeForPartOfSpeech(), sPoSCode, sessionElements) ;
	}
	
	/**
	 * Record the grammatical gender category for a lemma
	 * 
	 * @param sLemmaCode  Lemma's code
	 * @param iPoS        Grammatical gender category
	 * @param dbConnector Database connector 
	 * 
	 * @return The ID affected to the triple if anything went well; <code>-1</code> if not
	 */
	public static int recordGrammaticalGender(final String sLemmaCode, final QuadrifoliumFcts.Gender iGender, DBConnector dbConnector, final SessionElements sessionElements)
	{
		if ((null == dbConnector) || (null == sLemmaCode) || "".equals(sLemmaCode))
			return -1 ;
		
		String sGenderCode = QuadrifoliumFcts.getGrammaticalGenderConceptCode(iGender) ;
		if ("".equals(sGenderCode))
			return -1 ;
		
		return QuadrifoliumServerFcts.recordTriple(dbConnector, sLemmaCode, QuadrifoliumFcts.getConceptCodeForGrammaticalGender(), sGenderCode, sessionElements) ;
	}
	
	/**
	 * Record the grammatical gender category for a lemma
	 * 
	 * @param sLemmaCode    Lemma's code
	 * @param iPoS          Grammatical gender category
	 * @param tripleManager Manager for the triples table 
	 * 
	 * @return The ID affected to the triple if anything went well; <code>-1</code> if not
	 */
	public static int recordGrammaticalGender(final String sLemmaCode, final QuadrifoliumFcts.Gender iGender, TripleManager tripleManager, final SessionElements sessionElements)
	{
		if ((null == tripleManager) || (null == sLemmaCode) || "".equals(sLemmaCode))
			return -1 ;
		
		String sGenderCode = QuadrifoliumFcts.getGrammaticalGenderConceptCode(iGender) ;
		if ("".equals(sGenderCode))
			return -1 ;
		
		return QuadrifoliumServerFcts.recordTriple(tripleManager, sLemmaCode, QuadrifoliumFcts.getConceptCodeForGrammaticalGender(), sGenderCode, sessionElements) ;
	}
	
	/**
	 * Record the grammatical number information for a lemma
	 * 
	 * @param sFlexCode   Flex's code
	 * @param iNumber     Grammatical number category
	 * @param dbConnector Database connector 
	 * 
	 * @return The ID affected to the triple if anything went well; <code>-1</code> if not
	 */
	public static int recordGrammaticalNumber(final String sFlexCode, final QuadrifoliumFcts.Number iNumber, DBConnector dbConnector, final SessionElements sessionElements)
	{
		if ((null == dbConnector) || (null == sFlexCode) || "".equals(sFlexCode))
			return -1 ;
		
		String sNumberCode = QuadrifoliumFcts.getGrammaticalNumberConceptCode(iNumber) ;
		if ("".equals(sNumberCode))
			return -1 ;
		
		return QuadrifoliumServerFcts.recordTriple(dbConnector, sFlexCode, QuadrifoliumFcts.getConceptCodeForGrammaticalNumber(), sNumberCode, sessionElements) ;		
	}
	
	/**
	 * Record the grammatical number information for a lemma
	 * 
	 * @param sFlexCode     Flex's code
	 * @param iNumber       Grammatical number category
	 * @param tripleManager Manager for the triples table 
	 * 
	 * @return The ID affected to the triple if anything went well; <code>-1</code> if not
	 */
	public static int recordGrammaticalNumber(final String sFlexCode, final QuadrifoliumFcts.Number iNumber, TripleManager tripleManager, final SessionElements sessionElements)
	{
		if ((null == tripleManager) || (null == sFlexCode) || "".equals(sFlexCode))
			return -1 ;
		
		String sNumberCode = QuadrifoliumFcts.getGrammaticalNumberConceptCode(iNumber) ;
		if ("".equals(sNumberCode))
			return -1 ;
		
		return QuadrifoliumServerFcts.recordTriple(tripleManager, sFlexCode, QuadrifoliumFcts.getConceptCodeForGrammaticalNumber(), sNumberCode, sessionElements) ;		
	}
	
	/**
	 * Set a lemma as the preferred code for its concept
	 * 
	 * @param sLemmaCode  Lemma's code
	 * @param dbConnector Database connector 
	 * 
	 * @return The ID affected to the triple if anything went well; <code>-1</code> if not
	 */
	protected int recordPreferredTerm(final String sLemmaCode, DBConnector dbConnector)
	{
		if ((null == dbConnector) || (null == sLemmaCode) || "".equals(sLemmaCode))
			return -1 ;
		
		return QuadrifoliumServerFcts.recordTriple(dbConnector, QuadrifoliumFcts.getConceptCode(sLemmaCode), QuadrifoliumFcts.getConceptCodeForPreferredTerm(), sLemmaCode, _sessionElements) ;		
	}
	
	/**
	 * Record a Meaning clarification in the form of a free text and a triple
	 * 
	 * @return
	 */
	protected int recordMeaningClarification(final String sLemmaCode, final String sMeaningClarification, DBConnector dbConnector)
	{
		// Insert text in base
		//
		String sHeaderCode = QuadrifoliumServerFcts.recordFreeText(dbConnector, QuadrifoliumFcts.getConceptCode(sLemmaCode), sMeaningClarification, "fr", _sessionElements) ;
		if ("".equals(sHeaderCode))
			return -1 ;
		
		return QuadrifoliumServerFcts.recordTriple(dbConnector, sLemmaCode, QuadrifoliumFcts.getConceptCodeForMeaningClarification(), sHeaderCode, _sessionElements) ;
	}
}


