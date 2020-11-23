package org.quadrifolium.server.handler_special;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;

import org.quadrifolium.server.model.SessionsManager;
import org.quadrifolium.server.ontology.FlexManager;
import org.quadrifolium.server.ontology.FreeTextManager;
import org.quadrifolium.server.ontology.LemmaManager;
import org.quadrifolium.server.ontology.LexiconManager;
import org.quadrifolium.server.ontology.SavoirManager;
import org.quadrifolium.server.ontology.TripleManager;
import org.quadrifolium.server.ontology_base.OntologySavoir;
import org.quadrifolium.shared.ontology.Flex;
import org.quadrifolium.shared.ontology.Lemma;
import org.quadrifolium.shared.ontology.OntologyLexicon;
import org.quadrifolium.shared.ontology.Triple;
import org.quadrifolium.shared.rpc_special.LexiqueTo4foliumAction;
import org.quadrifolium.shared.rpc_special.LexiqueTo4foliumResult;
import org.quadrifolium.shared.rpc_util.SessionElements;
import org.quadrifolium.shared.util.QuadrifoliumFcts;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ldv.server.DBConnector;
import com.ldv.server.Logger;
import com.ldv.server.handler.LdvActionHandler;

public class LexiqueTo4foliumHandler extends LdvActionHandler<LexiqueTo4foliumAction, LexiqueTo4foliumResult>
{
	private SessionElements _sessionElements ;
	
	@Inject
	public LexiqueTo4foliumHandler(final Provider<ServletContext> servletContext,       
                                 final Provider<HttpServletRequest> servletRequest)
	{
		super(servletContext, servletRequest) ;
	}
	
	/**
	  * Constructor dedicated to unit tests 
	  */
	public LexiqueTo4foliumHandler()
	{
		super() ;
	}

	@Override
	public LexiqueTo4foliumResult execute(final LexiqueTo4foliumAction action,
       					                        final ExecutionContext context) throws ActionException 
  {
		String sFunctionName = "LexiqueTo4foliumHandler.execute" ;
		
		// Check session elements
		//
		_sessionElements = action.getSessionElements() ;
		SessionsManager sessionManager = new SessionsManager() ;
		if (false == sessionManager.checkTokenAndHeartbeat(_sessionElements))
			return new LexiqueTo4foliumResult("Invalid session elements.") ;
		
		// Start processing
		//
		try 
		{
			Logger.trace(sFunctionName + ": entering.", _sessionElements.getPersonId(), Logger.TraceLevel.DETAIL) ;
			
			// String sResult = processLexique() ;
			String sResul2 = processSavoir() ;
			
			Logger.trace(sFunctionName + ": leaving with result = " + sResul2, _sessionElements.getPersonId(), Logger.TraceLevel.DETAIL) ;
			
			return new LexiqueTo4foliumResult(sResul2) ; 
		}
		catch (Exception cause) 
		{
			Logger.trace(sFunctionName + ": exception ; cause: " + cause.getMessage(), _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
			throw new ActionException(cause) ;
		}
  }
	
	/**
	 * Process each and every lemma from the Lexique and inject the lemma and its flexed lexical elements into Quadrifolium
	 * 
	 * @return <code>"OK"</code> if everything went well, an error message if not
	 */
	protected String processLexique()
	{
		String sFunctionName = "LexiqueTo4foliumHandler.processLexique" ;
		
		Logger.trace(sFunctionName + ": entering.", _sessionElements.getPersonId(), Logger.TraceLevel.DETAIL) ;
		
		String sResult = "OK" ;
		
		// To make certain that the connection will be closed, a "finally" block must be added to secure the call to closeAll
		//
		DBConnector dbConnector    = null ;
		DBConnector dbConnectorBis = null ;
		
		try 
		{
			dbConnector = new DBConnector(false, _sessionElements.getPersonId()) ;
			
			// Prepare sql query and execute it
			//
			dbConnector.prepareStatememtForBigResult("SELECT * FROM lexique") ;
			
			if (false == dbConnector.executePreparedStatement())
			{
				Logger.trace(sFunctionName + ": execute statement failed, leaving.", _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
				return "Lexique query failled, leaving" ;
			}
			
			// Process query results 
			//			
			ResultSet rs = dbConnector.getResultSet() ;
			if (null == rs)
			{
				Logger.trace(sFunctionName + ": no result found, leaving.", _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
				return "Lexique is empty" ;
			}
			
			dbConnectorBis = new DBConnector(false, _sessionElements.getPersonId()) ;
			LexiconManager lexiconManager = new LexiconManager(_sessionElements.getPersonId(), dbConnectorBis) ;
			
			long lRowCount = 0 ;
			
			try
	    {
		    while (rs.next())
		    {
		    	lRowCount++ ;
		    	
		    	// Get an OntologyLexicon object from the result
		    	//
		    	OntologyLexicon lexicon = new OntologyLexicon() ;
		    	LexiconManager.fillDataFromResultSet(rs, lexicon, _sessionElements.getPersonId()) ;
		    	
		    	// If this entry from the Lexique has not already been processed
		    	//
		    	if ("".equals(lexicon.getLemma()))
		    	{
		    		// Check if the concept this lemma is attached to already exists in Quadrifolium
		    		//
		    		// Step 1 : get the concept code from Lexique
		    		//
		    		String sLexiqueConceptCode = QuadrifoliumFcts.getLexiqueConceptCode(lexicon.getCode()) ;
		    		//
		    		// Step 2 : find if a lemma is already attached to this concept (from an already existing lemma)
		    		//
		    		String sQConceptCode = lexiconManager.get4foliumConcept(sLexiqueConceptCode) ;
		    	
		    		processLexiqueLemma(sQConceptCode, lexicon) ;
		    	}
		    }
	    }
			catch (SQLException e)
	    {
	    	Logger.trace(sFunctionName + ": error parsing results ; stackTrace:" + e.getStackTrace(), _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
	    	sResult = "Error parsing the Lexique" ;
	    }
			
	    dbConnector.closeResultSet() ;
	    dbConnector.closePreparedStatement() ;
			
	    Logger.trace(sFunctionName + ": leaving. " + lRowCount + " rows found.", _sessionElements.getPersonId(), Logger.TraceLevel.DETAIL) ;
		}
		catch (Exception cause) 
		{
			Logger.trace(sFunctionName +  ": exception ; cause: " + cause.getMessage(), _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
			sResult = "Exception when processing lexique" ;
		}
		finally
		{
			if (null != dbConnector)
				dbConnector.closeAll() ;
			
			if (null != dbConnectorBis)
				dbConnectorBis.closeAll() ;
		}
		
		return sResult ;
  }
	
	/**
	 * Insert a lemma from the Lexique in Quadrifolium
	 * 
	 * @param sQConceptCode Quadrifolium concept code for this lemma, can be <code>""</code> if this is the first lemma for this concept
	 * @param lexicon       Lexique object to process
	 * @param dbConnector   DBConnector
	 */
	protected void processLexiqueLemma(final String sQConceptCode, OntologyLexicon lexicon)
	{
		String sFunctionName = "LexiqueTo4foliumHandler.processLexiqueLemma" ;
		
		// First step, just treat nouns
		//
		if (false == lexicon.isAdjective())
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
			lemma.setLanguage("fr") ;
		
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
	 * @param sQConceptCode Quadrifolium concept code for this lemma
	 * @param lexicon       Lexique object to process
	 * @param dbConnector   DBConnector
	 */
	protected void processNounLemma(final String sLemmaCode, OntologyLexicon lexicon, DBConnector dbConnector)
	{
		String sFunctionName = "LexiqueTo4foliumHandler.processNoonLemma" ;
		
		if ((null == dbConnector) || (null == lexicon))
		{
			Logger.trace(sFunctionName +  ": bad parameters, leaving", -1, Logger.TraceLevel.ERROR) ;
			return ;
		}

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
	 * @param sQConceptCode Quadrifolium concept code for this lemma
	 * @param lexicon       Lexique object to process
	 * @param dbConnector   DBConnector
	 */
	protected void processAdjectiveLemma(final String sLemmaCode, OntologyLexicon lexicon, DBConnector dbConnector)
	{
		String sFunctionName = "LexiqueTo4foliumHandler.processAdjectiveLemma" ;
		
		if ((null == dbConnector) || (null == lexicon))
		{
			Logger.trace(sFunctionName +  ": bad parameters, leaving", -1, Logger.TraceLevel.ERROR) ;
			return ;
		}

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
		
		return recordTriple(sLemmaCode, QuadrifoliumFcts.getConceptCodeForPartOfSpeech(), sPoSCode, dbConnector) ;
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
		
		return recordTriple(sLemmaCode, QuadrifoliumFcts.getConceptCodeForGrammaticalGender(), sGenderCode, dbConnector) ;
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
		
		return recordTriple(sFlexCode, QuadrifoliumFcts.getConceptCodeForGrammaticalNumber(), sNumberCode, dbConnector) ;		
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
		
		return recordTriple(QuadrifoliumFcts.getConceptCode(sLemmaCode), QuadrifoliumFcts.getConceptCodeForPreferredTerm(), sLemmaCode, dbConnector) ;		
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
		String sHeaderCode = recordFreeText(QuadrifoliumFcts.getConceptCode(sLemmaCode), sMeaningClarification, "fr", dbConnector) ;
		if ("".equals(sHeaderCode))
			return -1 ;
		
		return recordTriple(sLemmaCode, QuadrifoliumFcts.getConceptCodeForMeaningClarification(), sHeaderCode, dbConnector) ;
	}
	
	/**
	 * Insert a new triple in database
	 * 
	 * @return ID of created triple
	 */
	protected int recordTriple(final String sSubject, final String sPredicate, final String sObject, DBConnector dbConnector)
	{
		if ((null == dbConnector) || (null == sSubject) || "".equals(sSubject) || (null == sPredicate) || "".equals(sPredicate) || (null == sObject) || "".equals(sObject))
			return -1 ;
		
		Triple newTriple = new Triple(-1, sSubject, sPredicate, sObject) ;
		
		TripleManager tripleManager = new TripleManager(_sessionElements, dbConnector) ;
		if (false == tripleManager.insertData(newTriple))
			return -1 ;
		
		return newTriple.getId() ;
	}
	
	/**
	 * Insert a free text in database
	 * 
	 * @param sText The text to be stored
	 * 
	 * @return The "header code" of the stored text
	 */
	protected String recordFreeText(final String sConceptCode, final String sText, final String sLanguage, DBConnector dbConnector)
	{
		if ((null == dbConnector) || (null == sText) || "".equals(sText))
			return "" ;
		
		FreeTextManager freeTextManager = new FreeTextManager(_sessionElements, dbConnector) ;
		
		return freeTextManager.insertData(sConceptCode, sText, sLanguage) ;
	}
	
	/**
	 * Get the concept code that corresponds to a noon gender 
	 * 
	 * @param iGender Noon gender as defined in the OntologyLexicon class
	 */
	protected String getConceptForGender(OntologyLexicon.Gender iGender)
	{
		switch (iGender)
		{
			case MSGender :
			case MPGender :
				return "0000003" ;
			case FSGender :
			case FPGender :
				return "0000004" ;
			case NSGender :
			case NPGender :
				return "0000005" ;
			default :
				return "" ;
		}
	}

	/**
	 * Process each and every triple from Savoir and inject the triple into Quadrifolium
	 * 
	 * @return <code>"OK"</code> if everything went well, an error message if not
	 */
	protected String processSavoir()
	{
		String sFunctionName = "LexiqueTo4foliumHandler.processSavoir" ;
		
		Logger.trace(sFunctionName + ": entering.", _sessionElements.getPersonId(), Logger.TraceLevel.DETAIL) ;
		
		String sResult = "OK" ;
		
		// To make certain that the connection will be closed, a "finally" block must be added to secure the call to closeAll
		//
		DBConnector dbConnector    = null ;
		DBConnector dbConnectorBis = null ;
		
		try 
		{
			dbConnector = new DBConnector(false, _sessionElements.getPersonId()) ;
			
			// Prepare sql query and execute it
			//
			dbConnector.prepareStatememtForBigResult("SELECT * FROM savoir") ;
			
			if (false == dbConnector.executePreparedStatement())
			{
				Logger.trace(sFunctionName + ": execute statement failed, leaving.", _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
				return "Savoir query failled, leaving" ;
			}
			
			// Process query results 
			//			
			ResultSet rs = dbConnector.getResultSet() ;
			if (null == rs)
			{
				Logger.trace(sFunctionName + ": no result found, leaving.", _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
				return "Savoir is empty" ;
			}
			
			dbConnectorBis = new DBConnector(false, _sessionElements.getPersonId()) ;
			
			
			TripleManager tripleManager = new TripleManager(_sessionElements, dbConnectorBis) ;
			
			long lRowCount = 0 ;
			
			try
	    {
		    while (rs.next())
		    {
		    	lRowCount++ ;
		    	
		    	// Get an OntologyLexicon object from the result
		    	//
		    	OntologySavoir triple = new OntologySavoir() ;
		    	SavoirManager.fillDataFromResultSet(rs, triple, _sessionElements.getPersonId()) ;
		    	
		    	Triple newTriple = new Triple() ;
		    	
		    	// First, check if subject and object are known lemmas
		    	//
		    	if (canPrepareTriple(triple, newTriple, dbConnectorBis))
		    		tripleManager.insertData(newTriple) ;
		    }
	    }
			catch (SQLException e)
	    {
	    	Logger.trace(sFunctionName + ": error parsing results ; stackTrace:" + e.getStackTrace(), _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
	    	sResult = "Error parsing Savoir" ;
	    }
			
	    dbConnector.closeResultSet() ;
	    dbConnector.closePreparedStatement() ;
			
	    Logger.trace(sFunctionName + ": leaving. " + lRowCount + " rows found.", _sessionElements.getPersonId(), Logger.TraceLevel.DETAIL) ;
		}
		catch (Exception cause) 
		{
			Logger.trace(sFunctionName +  ": exception ; cause: " + cause.getMessage(), _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
			sResult = "Exception when processing Savoir" ;
		}
		finally
		{
			if (null != dbConnector)
				dbConnector.closeAll() ;
			
			if (null != dbConnectorBis)
				dbConnectorBis.closeAll() ;
		}
		
		return sResult ;
  }
	
	/**
	 * Initialize a Quadrifolium triple from a Lexique triple  
	 * 
	 * @return <code>true</code> if the new triple could be plainly initialized, <code>false</code> if not
	 */
	protected boolean canPrepareTriple(final OntologySavoir triple, Triple newTriple, DBConnector dbConnector)
	{
		if ((null == dbConnector) || (null == triple) || (null == newTriple))
			return false ;
		
		// First, check if both concepts that correspond to qualifie and qualifiant can be found as Quadrifolium concepts 
		//
		LexiconManager lexiconManager = new LexiconManager(_sessionElements.getPersonId(), dbConnector) ;
		
		String sSubject = lexiconManager.get4foliumConcept(triple.getQualifie()) ;
		if ("".equals(sSubject))
			return false ;
		
		String sObject = lexiconManager.get4foliumConcept(triple.getQualifiant()) ;
		if ("".equals(sObject))
			return false ;
		
		// Get the predicate as a Quadrifolium concept
		//
		String sLien      = triple.getLien() ;
		String sPredicate = "" ;
		
		if      ("ES".equalsIgnoreCase(sLien))
			sPredicate = QuadrifoliumFcts.getConceptCodeForIsA() ;
		else if ("AT".equalsIgnoreCase(sLien))
			sPredicate = QuadrifoliumFcts.getConceptCodeForAt() ;
		else if ("ME".equalsIgnoreCase(sLien))
			sPredicate = QuadrifoliumFcts.getConceptCodeForHasUnit() ;
		else if ("E0".equalsIgnoreCase(sLien))
			sPredicate = QuadrifoliumFcts.getConceptCodeForIntransitiveIsA() ;
		
		if ("".equals(sPredicate))
			return false ;
		
		newTriple.setSubject(sSubject) ;
		newTriple.setPredicate(sPredicate) ;
		newTriple.setObject(sObject) ;
		
		return true ;
	}
		
	@Override
	public void rollback(final LexiqueTo4foliumAction action,
        							 final LexiqueTo4foliumResult result,
                       final ExecutionContext context) throws ActionException
  {
		// Nothing to do here
  }
 
	@Override
	public Class<LexiqueTo4foliumAction> getActionType()
	{
		return LexiqueTo4foliumAction.class ;
	}
}
