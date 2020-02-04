package org.quadrifolium.server.ontology;

import org.quadrifolium.server.DBConnector;
import org.quadrifolium.server.Logger;
import org.quadrifolium.server.util.QuadrifoliumServerFcts;
import org.quadrifolium.shared.ontology.Flex;
import org.quadrifolium.shared.ontology.Lemma;
import org.quadrifolium.shared.ontology.OntologyLexicon;
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
	protected void processLemmaFromLexique(final String sQConceptCode, final OntologyLexicon lexicon, final String sLanguageTag)
	{
		String sFunctionName = "LexiqueTo4foliumHandler.processLexiqueLemma" ;
		
		// First step, just treat nouns and adjectives
		//
		if ((false == lexicon.isAdjective()) && (false == lexicon.isNoon())) 
			return ;
		
		// To make certain that the connection will be closed, a "finally" block must be added to secure the call to closeAll
		//
		DBConnector dbConnector2 = null ; 
		
		try
		{
			dbConnector2 = new DBConnector(false, _sessionElements.getPersonId()) ;
		
			if ((null == dbConnector2) || (null == lexicon))
			{
				Logger.trace(sFunctionName +  ": bad parameters, leaving", _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
				return ;
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
			String sLemmaCode = lemmaManager.getNextLemmaCode(sQConceptCode) ;
			
			if ("".equals(sLemmaCode))
			{
				Logger.trace(sFunctionName +  ": lemma \"" + lexicon.getLabel() + "\" could not be inserted since no code could be affected to it, leaving", _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
				return ;
			}
		
			// Add the new lemma in database
			//
			lemma.setCode(sLemmaCode) ;
			if (false == lemmaManager.insertData(lemma))
				return ;
		
			// Update the lexicon
			//
			LexiconManager lexiconManager = new LexiconManager(_sessionElements.getPersonId(), dbConnector2) ;
			lexicon.setLemma(sLemmaCode) ;
			lexiconManager.updateData(lexicon) ;
		
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
		recordPartOfSpeech(sLemmaCode, QuadrifoliumFcts.PartOfSpeech.commonNoun, dbConnector) ;
		
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
			recordGrammaticalGender(sLemmaCode, iGender, dbConnector) ;
		
		// Get inflections for singular and plural
		//
		String sSingularLabel = lexicon.getDisplayLabelForNoon(OntologyLexicon.Declination.singular, "fr") ;
		String sPluralLabel   = lexicon.getDisplayLabelForNoon(OntologyLexicon.Declination.plural, "fr") ;
		
		FlexManager flexManager = new FlexManager(_sessionElements, dbConnector) ;
		
		// If singular inflection exists, then add it to the flex table
		//
		if (false == "".equals(sSingularLabel))
		{
			Flex singularFlex = new Flex() ;
			singularFlex.setLabel(sSingularLabel) ;
			singularFlex.setLanguage("fr");
			
			String sFlexCode = flexManager.getNextFlexCode(sLemmaCode) ;
			if (false == "".equals(sFlexCode))
			{
				singularFlex.setCode(sFlexCode) ;
				flexManager.insertData(singularFlex) ;
			}
			
			// Record the grammatical number for this inflection
			//
			recordGrammaticalNumber(sFlexCode, QuadrifoliumFcts.Number.singular, dbConnector) ;
		}
		
		// If plural inflection exists, then add it to the flex table
		//
		if (false == "".equals(sPluralLabel))
		{
			Flex pluralFlex = new Flex() ;
			pluralFlex.setLabel(sPluralLabel) ;
			pluralFlex.setLanguage("fr");
				
			String sFlexCode = flexManager.getNextFlexCode(sLemmaCode) ;
			if (false == "".equals(sFlexCode))
			{
				pluralFlex.setCode(sFlexCode) ;
				flexManager.insertData(pluralFlex) ;
			}
				
			// Record the grammatical number for this inflection
			//
			recordGrammaticalNumber(sFlexCode, QuadrifoliumFcts.Number.plural, dbConnector) ;
		}
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
		recordPartOfSpeech(sLemmaCode, QuadrifoliumFcts.PartOfSpeech.adjective, dbConnector) ;
				
		// Get inflections for singular and plural, masculine and feminine
		//
		String sSingularMasculine = lexicon.getDisplayLabelForAdjective(OntologyLexicon.Gender.MSGender, "fr") ;
		String sSingularFeminine  = lexicon.getDisplayLabelForAdjective(OntologyLexicon.Gender.FSGender, "fr") ;
		String sPluralMasculine   = lexicon.getDisplayLabelForAdjective(OntologyLexicon.Gender.MPGender, "fr") ;
		String sPluralFeminine    = lexicon.getDisplayLabelForAdjective(OntologyLexicon.Gender.FPGender, "fr") ;
		
		FlexManager flexManager = new FlexManager(_sessionElements, dbConnector) ;
		
		// If singular inflections exist, then add them to the flex table
		//
		if (false == "".equals(sSingularMasculine))
		{
			Flex singularFlex = new Flex() ;
			singularFlex.setLabel(sSingularMasculine) ;
			singularFlex.setLanguage("fr");
			
			String sFlexCode = flexManager.getNextFlexCode(sLemmaCode) ;
			if (false == "".equals(sFlexCode))
			{
				singularFlex.setCode(sFlexCode) ;
				flexManager.insertData(singularFlex) ;
			}
			
			// Record grammatical information for this inflection
			//
			recordGrammaticalGender(sFlexCode, QuadrifoliumFcts.Gender.masculine, dbConnector) ;
			recordGrammaticalNumber(sFlexCode, QuadrifoliumFcts.Number.singular, dbConnector) ;
		}
		if (false == "".equals(sSingularFeminine))
		{
			Flex singularFlex = new Flex() ;
			singularFlex.setLabel(sSingularFeminine) ;
			singularFlex.setLanguage("fr");
			
			String sFlexCode = flexManager.getNextFlexCode(sLemmaCode) ;
			if (false == "".equals(sFlexCode))
			{
				singularFlex.setCode(sFlexCode) ;
				flexManager.insertData(singularFlex) ;
			}
			
			// Record grammatical information for this inflection
			//
			recordGrammaticalGender(sFlexCode, QuadrifoliumFcts.Gender.feminine, dbConnector) ;
			recordGrammaticalNumber(sFlexCode, QuadrifoliumFcts.Number.singular, dbConnector) ;
		}
		
		// If plural inflections exist, then add them to the flex table
		//
		if (false == "".equals(sPluralMasculine))
		{
			Flex pluralFlex = new Flex() ;
			pluralFlex.setLabel(sPluralMasculine) ;
			pluralFlex.setLanguage("fr");
				
			String sFlexCode = flexManager.getNextFlexCode(sLemmaCode) ;
			if (false == "".equals(sFlexCode))
			{
				pluralFlex.setCode(sFlexCode) ;
				flexManager.insertData(pluralFlex) ;
			}
				
			// Record the grammatical information for this inflection
			//
			recordGrammaticalGender(sFlexCode, QuadrifoliumFcts.Gender.masculine, dbConnector) ;
			recordGrammaticalNumber(sFlexCode, QuadrifoliumFcts.Number.plural, dbConnector) ;
		}
		if (false == "".equals(sPluralFeminine))
		{
			Flex pluralFlex = new Flex() ;
			pluralFlex.setLabel(sPluralFeminine) ;
			pluralFlex.setLanguage("fr");
				
			String sFlexCode = flexManager.getNextFlexCode(sLemmaCode) ;
			if (false == "".equals(sFlexCode))
			{
				pluralFlex.setCode(sFlexCode) ;
				flexManager.insertData(pluralFlex) ;
			}
				
			// Record the grammatical information for this inflection
			//
			recordGrammaticalGender(sFlexCode, QuadrifoliumFcts.Gender.feminine, dbConnector) ;
			recordGrammaticalNumber(sFlexCode, QuadrifoliumFcts.Number.plural, dbConnector) ;
		}
	}
	
	/**
	 * Record the part of speech information for a lemma
	 * 
	 * @param sLemmaCode  Lemma's code
	 * @param iPoS        Part of speech category
	 * @param dbConnector Database connector 
	 * 
	 * @return The ID affected to the triple if anything went well; <code>-1</code> if not
	 */
	protected int recordPartOfSpeech(final String sLemmaCode, final QuadrifoliumFcts.PartOfSpeech iPoS, DBConnector dbConnector)
	{
		if ((null == dbConnector) || (null == sLemmaCode) || "".equals(sLemmaCode))
			return -1 ;
		
		String sPoSCode = QuadrifoliumFcts.getPartOfSpeechConceptCode(iPoS) ;
		if ("".equals(sPoSCode))
			return -1 ;
		
		return QuadrifoliumServerFcts.recordTriple(dbConnector, sLemmaCode, QuadrifoliumFcts.getConceptCodeForPartOfSpeech(), sPoSCode, _sessionElements) ;
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
	protected int recordGrammaticalGender(final String sLemmaCode, final QuadrifoliumFcts.Gender iGender, DBConnector dbConnector)
	{
		if ((null == dbConnector) || (null == sLemmaCode) || "".equals(sLemmaCode))
			return -1 ;
		
		String sGenderCode = QuadrifoliumFcts.getGrammaticalGenderConceptCode(iGender) ;
		if ("".equals(sGenderCode))
			return -1 ;
		
		return QuadrifoliumServerFcts.recordTriple(dbConnector, sLemmaCode, QuadrifoliumFcts.getConceptCodeForGrammaticalGender(), sGenderCode, _sessionElements) ;
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
	protected int recordGrammaticalNumber(final String sFlexCode, final QuadrifoliumFcts.Number iNumber, DBConnector dbConnector)
	{
		if ((null == dbConnector) || (null == sFlexCode) || "".equals(sFlexCode))
			return -1 ;
		
		String sNumberCode = QuadrifoliumFcts.getGrammaticalNumberConceptCode(iNumber) ;
		if ("".equals(sNumberCode))
			return -1 ;
		
		return QuadrifoliumServerFcts.recordTriple(dbConnector, sFlexCode, QuadrifoliumFcts.getConceptCodeForGrammaticalNumber(), sNumberCode, _sessionElements) ;		
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


