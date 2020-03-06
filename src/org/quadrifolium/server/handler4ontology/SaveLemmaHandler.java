package org.quadrifolium.server.handler4ontology;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;

import org.quadrifolium.server.DBConnector;
import org.quadrifolium.server.Logger;
import org.quadrifolium.server.handler.QuadrifoliumActionHandler;
import org.quadrifolium.server.model.SessionsManager;
import org.quadrifolium.server.ontology.FlexManager;
import org.quadrifolium.server.ontology.LemmaExtendedManager;
import org.quadrifolium.server.ontology.LemmaManager;
import org.quadrifolium.server.ontology.LexiconToLemmaManager;
import org.quadrifolium.server.ontology.TripleManager;
import org.quadrifolium.shared.ontology.Flex;
import org.quadrifolium.shared.ontology.FlexWithTraits;
import org.quadrifolium.shared.ontology.Lemma;
import org.quadrifolium.shared.ontology.LemmaWithInflections;
import org.quadrifolium.shared.ontology.OntologyLexicon;
import org.quadrifolium.shared.ontology.Triple;
import org.quadrifolium.shared.ontology.TripleWithLabel;
import org.quadrifolium.shared.rpc4ontology.SaveLemmaAction;
import org.quadrifolium.shared.rpc4ontology.SaveLemmaResult;
import org.quadrifolium.shared.rpc_util.SessionElements;
import org.quadrifolium.shared.util.QuadrifoliumFcts;
import org.quadrifolium.shared.util.QuadrifoliumFcts.Gender;
import org.quadrifolium.shared.util.QuadrifoliumFcts.Number;
import org.quadrifolium.shared.util.QuadrifoliumFcts.PartOfSpeech;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class SaveLemmaHandler extends QuadrifoliumActionHandler<SaveLemmaAction, SaveLemmaResult>
{
	protected SessionElements _sessionElements ;

	@Inject
	public SaveLemmaHandler(final Logger logger,
                          final Provider<ServletContext> servletContext,       
                          final Provider<HttpServletRequest> servletRequest)
	{
		super(logger, servletContext, servletRequest) ;
		
		_sessionElements = null ;
	}
	
	/**
	  * Constructor dedicated to unit tests 
	  */
	public SaveLemmaHandler()
	{
		super() ;
		
		_sessionElements = null ;
	}

	@Override
	public SaveLemmaResult execute(final SaveLemmaAction action, final ExecutionContext context) throws ActionException 
  {
		String sFunctionName = "SaveLemmaHandler.execute" ;
		
		if (null == action)
		{
			Logger.trace(sFunctionName + ": Error, no input information.", -1, Logger.TraceLevel.ERROR) ;
			return new SaveLemmaResult("Error, no input information.", null, null, -1) ;
		}
		
		// To make certain that the connection will be closed, a "finally" block must be added to secure the call to closeAll
		//
		DBConnector dbConnector = null ;
		
		/** Concept the lemma to be created/edited belongs to */
		String sConceptCode = action.getConceptCode() ;
		
		String sLanguageTag = action.getNewLanguage() ;
 		String sText        = action.getNewText() ;
 		String sGrammar     = action.getNewGrammar() ;
 	
		try 
		{
			_sessionElements = action.getSessionElements() ;
			if (null == _sessionElements)
			{
				Logger.trace(sFunctionName + ": Error, no session elements.", -1, Logger.TraceLevel.ERROR) ;
				return new SaveLemmaResult("No session elements.", null, action.getContent(), -1) ;
			}
			
			// Check token validity
   		//
   		SessionsManager sessionManager = new SessionsManager() ;
   		boolean bValidSession = sessionManager.isValidToken(_sessionElements.getPersonId(), _sessionElements.getToken()) ;
   		if (false == bValidSession)
   		{
   			Logger.trace(sFunctionName + ": Error, invalid session elements.", _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
   			return new SaveLemmaResult("Invalid session elements.", null, action.getContent(), -1) ;
   		}

			// Do we have to update an existing definition?
			//
			Lemma updatedLemma = action.getEditedLemma() ;
   		
   		int iUpdatedLemmaId = -1 ;
   		if (null != updatedLemma)
   			iUpdatedLemmaId = updatedLemma.getId() ;
   		
   		// Trace operation to be done
   		//
   		String sTraceText = sFunctionName + ": For concept " + sConceptCode ;
   		if (-1 == iUpdatedLemmaId)
   			sTraceText += " add lemma " ;
   		else
   			sTraceText += " update lemma " + iUpdatedLemmaId + " " ;
   		sTraceText += sLanguageTag + ", \"" + sText + "\"" ;
   		
   		Logger.trace(sTraceText, _sessionElements.getPersonId(), Logger.TraceLevel.STEP) ;
   		
   		// Check if language tag and text are valid
   		//
   		if ((null == sConceptCode) || "".equals(sConceptCode))
   		{
   			Logger.trace(sFunctionName + ": Error, no concept code.", _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
   			return new SaveLemmaResult("Error, no concept code.", null, action.getContent(), iUpdatedLemmaId) ;
   		}
   		
   		// Check for consistency of concept code when an existing lemma is to be updated
   		//
   		if (null != updatedLemma)
   		{
   			String sLemmaConceptCode = QuadrifoliumFcts.getConceptCode(updatedLemma.getCode()) ;
   		
   			if (false == sConceptCode.equals(sLemmaConceptCode))
   			{
   				Logger.trace(sFunctionName + ": Error, wrong concept code for lemma to update.", _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
   				return new SaveLemmaResult("Error, wrong concept code for lemma to update.", null, action.getContent(), iUpdatedLemmaId) ;
   			}
   		}
   		
   		// Check if language tag and text are valid
   		//
   		if ((null == sLanguageTag) || ("".equals(sLanguageTag)) || (null == sText) || ("".equals(sText)) || (null == sGrammar) || ("".equals(sGrammar)))
   		{
   			Logger.trace(sFunctionName + ": Error, empty language and/or text and/or grammatical information.", _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
   			return new SaveLemmaResult("Error, empty language and/or text.", null, action.getContent(), iUpdatedLemmaId) ;
   		}
   		
   		// Check if concept code is valid
   		//
   		String sVerifiedCode = QuadrifoliumFcts.getConceptCode(sConceptCode) ;
   		if (false == sVerifiedCode.equals(sConceptCode))
   		{
   			Logger.trace(sFunctionName + ": Error, wrong concept code.", _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
   			return new SaveLemmaResult("Error, wrong concept code.", null, action.getContent(), iUpdatedLemmaId) ;
   		}
   		
   		// Creates a connector to the Ontology database
   		//
   		dbConnector = new DBConnector(true, -1, DBConnector.databaseType.databaseOntology) ;

   		// Do the job
   		//
   		SaveLemmaResult saveLemmaResult = new SaveLemmaResult("", null, action.getContent(), iUpdatedLemmaId) ;
   		
   		if (null != updatedLemma)
   			updateLemma(updatedLemma, sLanguageTag, sText, sGrammar, dbConnector, saveLemmaResult) ;
   		else
   			saveLemma(sConceptCode, sLanguageTag, sText, sGrammar, dbConnector, saveLemmaResult) ;
   		
   		return saveLemmaResult ;
		}
		catch (Exception cause) 
		{
			Logger.trace(sFunctionName + ": exception ; cause: " + cause.getMessage(), _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
   
			throw new ActionException(cause);
		}
		finally
		{
			if (null != dbConnector)
				dbConnector.closeAll() ;
		}
  }

	/**
	 * Update an existing lemma
	 */
	protected void updateLemma(Lemma updatedLemma, final String sLanguageTag, final String sText, final String sGrammar, DBConnector dbConnector, SaveLemmaResult saveLemmaResult) throws NullPointerException
	{
		// We should never enter there
		//
		if (null == saveLemmaResult)
			throw new NullPointerException() ;
		
		String sFunctionName = "SaveLemmaHandler.updateLemma" ;
		
		if ((null == dbConnector) || (null == updatedLemma) || (null == sLanguageTag) || "".equals(sLanguageTag) || (null == sText) || "".equals(sText))
		{
			saveLemmaResult.setMessage("Server internal error") ;
			return ;
		}
		
		// Get lemma's inflections
		//
		LemmaWithInflections lemmaWithInflections = GetLemmaWithInflections(dbConnector, sLanguageTag, updatedLemma) ;
		if (null == lemmaWithInflections)
		{
			saveLemmaResult.setMessage("Server internal error") ;
			return ;
		}
		
		// Get the grammar string
		//
		String sGrammarString = OntologyLexicon.getGrammarString(lemmaWithInflections) ;
		
		// By the way, is there something to do?
		//
		if (sLanguageTag.equals(updatedLemma.getLanguage()) && sText.equals(updatedLemma.getLabel())  && sGrammarString.equals(sGrammar))
		{
			Logger.trace(sFunctionName + ": Looks like there is nothing to do.", _sessionElements.getPersonId(), Logger.TraceLevel.WARNING) ;
			saveLemmaResult.setMessage("Looks like there is nothing to do.") ;
			return ;
		}
		
		// First, check that updating this definition will not break something
		//
		String sLemmaConceptCode = QuadrifoliumFcts.getConceptCode(updatedLemma.getCode()) ;
		
		if (false == isOkToInjectAfterUpdate(updatedLemma, sLanguageTag, sText, sGrammar, dbConnector))
		{
			Logger.trace(sFunctionName + ": Such a lemma already exists.", _sessionElements.getPersonId(), Logger.TraceLevel.WARNING) ;
			saveLemmaResult.setMessage("Such a lemma already exists.") ;
			return ;
		}
		
		// It is now possible to update
		//
		
		// Should the lemma itself be updated?
		//
		if ((false == sLanguageTag.equals(updatedLemma.getLanguage())) || (false == sText.equals(updatedLemma.getLabel())))
		{
			LemmaManager lemmaManager = new LemmaManager(_sessionElements, dbConnector) ;
		
			Lemma newLemma = new Lemma(updatedLemma) ;
			newLemma.setLabel(sText) ;
			newLemma.setLanguage(sLanguageTag) ;
		
			// Ask database to do the job
			//
			if (false == lemmaManager.updateData(newLemma))
			{
				saveLemmaResult.setMessage("Internal server error.") ;
				return ;
			}
			
			// If there, it means that all went well, so we fill the result
			//
			saveLemmaResult.setSavedLemma(updatedLemma) ;
		}
		
		// Update traits
		//
		updateTraits(lemmaWithInflections, sLanguageTag, sText, sGrammar, dbConnector, saveLemmaResult) ;
		updateInflections(lemmaWithInflections, sLanguageTag, sText, sGrammar, dbConnector, saveLemmaResult) ;
		
		saveLemmaResult.setSavedLemma(updatedLemma) ;
	}
	
	/**
	 * Update traits linked to an existing lemma
	 */
	protected void updateTraits(LemmaWithInflections lemmaWithInflections, final String sLanguageTag, final String sText, final String sGrammar, DBConnector dbConnector, SaveLemmaResult saveLemmaResult) throws NullPointerException
	{
		// We should never enter there
		//
		if ((null == saveLemmaResult) || (null == lemmaWithInflections))
			throw new NullPointerException() ;
		
		// First trait to check: Part of Speech
		//
		PartOfSpeech PrevPoS = lemmaWithInflections.getPartOfSpeech() ;
		PartOfSpeech NewPoS  = OntologyLexicon.getPoSFromGrammarString(sGrammar) ;
		
		if (PrevPoS != NewPoS)
			updateTraitPartOfSpeech(lemmaWithInflections, NewPoS, dbConnector, saveLemmaResult) ;
		
		//
		//
		if (PartOfSpeech.commonNoun == NewPoS)
		{
			// Is grammatical gender to be created/updated?
			//
			Gender PrevGender = lemmaWithInflections.getGrammaticalGender() ;
			Gender NewGender  = OntologyLexicon.getGenderFromGrammarString(sGrammar) ;
			
			if (PrevGender != NewGender)
				updateTraitGender(lemmaWithInflections, NewGender, dbConnector, saveLemmaResult) ;
			
			// Is grammatical number to be created/updated? (in known languages, grammatical number is specific to flexed forms
			//
/*
			Number PrevNumber = lemmaWithInflections.getGrammaticalNumber() ;
			Number NewNumber  = OntologyLexicon.getNumberFromGrammarString(sGrammar) ;
					
			if (PrevNumber != NewNumber)
				updateTraitNumber(lemmaWithInflections, NewNumber, dbConnector, saveLemmaResult) ;
*/
		}
		else
		{
			// Remove grammatical gender and grammatical number traits (if exist)
			//
			updateTraitGender(lemmaWithInflections, Gender.nullGender, dbConnector, saveLemmaResult) ;
			updateTraitNumber(lemmaWithInflections, Number.nullNumber, dbConnector, saveLemmaResult) ;
		}
		
	}
	
	/**
	 * Update/Create/Delete a lemma's Part of Speech trait
	 * 
	 * @param lemmaWithInflections Previous state to be updated
	 * @param newPoS               Part of Speech to set (pass <code>nullPoS</code> to delete existing trait)
	 */
	protected void updateTraitPartOfSpeech(LemmaWithInflections lemmaWithInflections, final PartOfSpeech newPoS, DBConnector dbConnector, SaveLemmaResult saveLemmaResult) throws NullPointerException
	{
		// We should never enter there
		//
		if ((null == saveLemmaResult) || (null == lemmaWithInflections))
			throw new NullPointerException() ;
		
		String sFunctionName = "SaveLemmaHandler.updateTraitPartOfSpeech" ; 
		
		TripleWithLabel PoSTriple = lemmaWithInflections.getPartOfSpeechTriple() ;

		// No existing Part of Speech trait, then create one
		//
		if (null == PoSTriple)
		{
			// In case this trait would have been to be deleted, there is nothing to do if it doesn't exist
			//
			if (PartOfSpeech.nullPoS == newPoS)
				return ;
			
			LexiconToLemmaManager.recordPartOfSpeech(lemmaWithInflections.getCode(), newPoS, dbConnector, _sessionElements) ;
			return ;
		}
		
		// A Part of Speech trait already exists, update/delete it 
		//
		TripleManager tripleManager = new TripleManager(_sessionElements, dbConnector) ;
		
		// If triple is to be deleted
		//
		if (PartOfSpeech.nullPoS == newPoS)
		{
			tripleManager.deleteRecord(PoSTriple) ;
			return ;
		}
		
		// Get the code for the new PoS
		//
		String sPoSCode = QuadrifoliumFcts.getPartOfSpeechConceptCode(newPoS) ;
		if ("".equals(sPoSCode))
		{
			Logger.trace(sFunctionName + ": Unknown part of speech \"" + QuadrifoliumFcts.getPartOfSpeechAsText(newPoS) + "\"", _sessionElements.getPersonId(), Logger.TraceLevel.WARNING) ;
			saveLemmaResult.setMessage("Internal server error (Unknown part of speech).") ;
			return ;
		}
			
		// Update record in database
		//	
		Triple updateTriple = new Triple(PoSTriple) ;
		updateTriple.setSubject(sPoSCode) ;
			
		tripleManager.updateData(updateTriple) ;
	}
	
	/**
	 * Update/Create/Delete a lemma's grammatical gender trait
	 * 
	 * @param lemmaWithInflections Previous state to be updated
	 * @param newGender            Grammatical gender to set (pass <code>nullGender</code> to delete existing trait)
	 */
	protected void updateTraitGender(LemmaWithInflections lemmaWithInflections, final Gender newGender, DBConnector dbConnector, SaveLemmaResult saveLemmaResult) throws NullPointerException
	{
		// We should never enter there
		//
		if ((null == saveLemmaResult) || (null == lemmaWithInflections))
			throw new NullPointerException() ;
		
		String sFunctionName = "SaveLemmaHandler.updateTraitGender" ; 
		
		TripleWithLabel genderTriple = lemmaWithInflections.getGrammaticalGenderTriple() ;

		// No existing grammatical gender trait, then create one
		//
		if (null == genderTriple)
		{
			// In case this trait would have been to be deleted, there is nothing to do if it doesn't exist
			//
			if (Gender.nullGender == newGender)
				return ;
			
			LexiconToLemmaManager.recordGrammaticalGender(lemmaWithInflections.getCode(), newGender, dbConnector, _sessionElements) ;
			return ;
		}
		
		// A grammatical gender trait already exists, update/delete it 
		//
		
		TripleManager tripleManager = new TripleManager(_sessionElements, dbConnector) ;
		
		// If triple is to be deleted
		//
		if (Gender.nullGender == newGender)
		{
			tripleManager.deleteRecord(genderTriple) ;
			return ;
		}
			
		// In case the triple is to be updated
		//
	
		// Get the code for the new grammatical gender
		//
		String sGenderCode = QuadrifoliumFcts.getGrammaticalGenderConceptCode(newGender) ;
		if ("".equals(sGenderCode))
		{
			Logger.trace(sFunctionName + ": Unknown grammatical gender \"" + newGender + "\"", _sessionElements.getPersonId(), Logger.TraceLevel.WARNING) ;
			saveLemmaResult.setMessage("Internal server error (Unknown grammatical gender).") ;
			return ;
		}
			
		// Update record in database
		//
		Triple updateTriple = new Triple(genderTriple) ;
		updateTriple.setSubject(sGenderCode) ;
			
		tripleManager.updateData(updateTriple) ;
	}
	
	/**
	 * Update/Create/Delete a lemma's grammatical number trait
	 * 
	 * @param lemmaWithInflections Previous state to be updated
	 * @param newNumber            Grammatical number to set (pass <code>nullNumber</code> to delete existing trait)
	 */
	protected void updateTraitNumber(LemmaWithInflections lemmaWithInflections, final Number newNumber, DBConnector dbConnector, SaveLemmaResult saveLemmaResult) throws NullPointerException
	{
		// We should never enter there
		//
		if ((null == saveLemmaResult) || (null == lemmaWithInflections))
			throw new NullPointerException() ;
		
		String sFunctionName = "SaveLemmaHandler.updateTraitNumber" ; 
		
		TripleWithLabel numberTriple = lemmaWithInflections.getGrammaticalNumberTriple() ;

		// No existing grammatical gender trait, then create one
		//
		if (null == numberTriple)
		{
			// In case this trait would have been to be deleted, there is nothing to do if it doesn't exist
			//
			if (Number.nullNumber == newNumber)
				return ;
			
			LexiconToLemmaManager.recordGrammaticalNumber(lemmaWithInflections.getCode(), newNumber, dbConnector, _sessionElements) ;
			return ;
		}
		
		// A grammatical number trait already exists, update/delete it 
		//
		TripleManager tripleManager = new TripleManager(_sessionElements, dbConnector) ;
		
		// If triple is to be deleted
		//
		if (Number.nullNumber == newNumber)
		{
			tripleManager.deleteRecord(numberTriple) ;
			return ;
		}
		
		// Get the code for the new grammatical number
		//
		String sNumberCode = QuadrifoliumFcts.getGrammaticalNumberConceptCode(newNumber) ;
		if ("".equals(sNumberCode))
		{
			Logger.trace(sFunctionName + ": Unknown grammatical number \"" + newNumber + "\"", _sessionElements.getPersonId(), Logger.TraceLevel.WARNING) ;
			saveLemmaResult.setMessage("Internal server error (Unknown grammatical number).") ;
			return ;
		}
			
		// Update record in database
		//
		Triple updateTriple = new Triple(numberTriple) ;
		updateTriple.setSubject(sNumberCode) ;
			
		tripleManager.updateData(updateTriple) ;
	}
	
	/**
	 * Update inflections linked to an existing lemma
	 */
	protected void updateInflections(LemmaWithInflections lemmaWithInflections, final String sLanguageTag, final String sText, final String sGrammar, DBConnector dbConnector, SaveLemmaResult saveLemmaResult) throws NullPointerException
	{
		// We should never enter there
		//
		if ((null == saveLemmaResult) || (null == lemmaWithInflections))
			throw new NullPointerException() ;
		
		// First check if Part of Speech changed; if true we have to delete existing inflections and create new ones
		// since inflections are specific to the part of speech 
		//
		PartOfSpeech PrevPoS = lemmaWithInflections.getPartOfSpeech() ;
		PartOfSpeech NewPoS  = OntologyLexicon.getPoSFromGrammarString(sGrammar) ;
		
		if (PrevPoS != NewPoS)
		{
			deleteExistingInflections(lemmaWithInflections, dbConnector, saveLemmaResult) ;
			createInflections(lemmaWithInflections, sLanguageTag, sText, sGrammar, dbConnector, saveLemmaResult) ;
		}
		else
			checkInflections(lemmaWithInflections, sLanguageTag, sText, sGrammar, dbConnector, saveLemmaResult) ;
	}
	
	/**
	 * Record an new lemma
	 */
	protected void saveLemma(final String sConceptCode, final String sLanguageTag, final String sText, final String sGrammar, DBConnector dbConnector, SaveLemmaResult saveLemmaResult) throws NullPointerException
	{
		// We should never enter there
		//
		if (null == saveLemmaResult)
			throw new NullPointerException() ;
		
		String sFunctionName = "SaveLemmaHandler.saveLemma" ;
		
		if ((null == dbConnector) || (null == sConceptCode) || "".equals(sConceptCode) || (null == sLanguageTag) || "".equals(sLanguageTag) || (null == sText) || "".equals(sText) || (null == sGrammar) || "".equals(sGrammar))
		{
			saveLemmaResult.setMessage("Server internal error") ;
			return ;
		}
		
		// Build a "lexicon" object
		//
		OntologyLexicon lexicon = new OntologyLexicon(sText, "", sGrammar, "", "") ;
		
		// Process it
		//
		LexiconToLemmaManager lexManager = new LexiconToLemmaManager(_sessionElements, dbConnector) ;
		String sNewLemmaCode = lexManager.processLemmaFromLexique(sConceptCode, lexicon, sLanguageTag, false) ;
		
		if ("".equals(sNewLemmaCode))
			saveLemmaResult.setMessage("Internal server error.") ;
		
		// Get new lemma from database
		//
		Lemma newLemma = new Lemma() ;
		
		LemmaManager lemmaManager = new LemmaManager(_sessionElements, dbConnector) ;
		if (false == lemmaManager.existData(sNewLemmaCode, newLemma))
			saveLemmaResult.setMessage("Internal server error.") ;
		
		saveLemmaResult.setSavedLemma(newLemma) ;
	}

	/**
	 * Delete inflections (and their traits)
	 */
	protected void deleteExistingInflections(LemmaWithInflections lemmaWithInflections, DBConnector dbConnector, SaveLemmaResult saveLemmaResult) throws NullPointerException
	{
		// We should never enter there
		//
		if ((null == saveLemmaResult) || (null == dbConnector) || (null == lemmaWithInflections))
			throw new NullPointerException() ;
		
		ArrayList<FlexWithTraits> aInflections = lemmaWithInflections.getInflections() ;
		if ((null == aInflections) || aInflections.isEmpty())
			return ;
		
		FlexManager   flexManager   = new FlexManager(_sessionElements, dbConnector) ;
		TripleManager tripleManager = new TripleManager(_sessionElements, dbConnector) ;
		
		for (FlexWithTraits flex : aInflections)
			deleteExistingInflection(flex, flexManager, tripleManager, saveLemmaResult) ;
	}

	/**
	 * Delete an inflection (and its traits)
	 */
	protected void deleteExistingInflection(FlexWithTraits flex, FlexManager flexManager, TripleManager tripleManager, SaveLemmaResult saveLemmaResult) throws NullPointerException
	{
		// We should never enter there
		//
		if ((null == saveLemmaResult) || (null == flexManager) || (null == tripleManager) || (null == flex))
			throw new NullPointerException() ;
		
		// Delete traits
		//
		ArrayList<TripleWithLabel> aTraits = flex.getTraits() ;
				
		if ((null != aTraits) && (false == aTraits.isEmpty()))
			for (TripleWithLabel triple : aTraits)
				tripleManager.deleteRecord(triple) ;

		// Delete the inflection record
		//
		flexManager.deleteRecord(flex) ;
	}
	
	/**
	 * Create inflections and their traits
	 * 
	 * @param lemmaWithInflections Lemma to update 
	 * @param sLanguageTag         Language to create inflections for
	 * @param sText                Text inflections have to be extracted from
	 * @param sGrammar             Grammar (PoS, grammatical gender, grammatical number)
	 * @param dbConnector          Database connector
	 * @param saveLemmaResult      Handler return structure
	 * 
	 * @throws NullPointerException
	 */
	protected void createInflections(LemmaWithInflections lemmaWithInflections, final String sLanguageTag, final String sText, final String sGrammar, DBConnector dbConnector, SaveLemmaResult saveLemmaResult) throws NullPointerException
	{
		// We should never enter there
		//
		if ((null == saveLemmaResult) || (null == dbConnector) || (null == lemmaWithInflections))
			throw new NullPointerException() ;
		
		OntologyLexicon lexicon = new OntologyLexicon(sText, "", sGrammar, "", "") ;
		
		PartOfSpeech NewPoS  = OntologyLexicon.getPoSFromGrammarString(sGrammar) ;
		
		if      (PartOfSpeech.commonNoun == NewPoS)
			LexiconToLemmaManager.createFlexedFormsForNoun(lemmaWithInflections.getCode(), lexicon, sLanguageTag, dbConnector, _sessionElements) ;
		else if (PartOfSpeech.adjective == NewPoS)
			LexiconToLemmaManager.createFlexedFormsForAdjective(lemmaWithInflections.getCode(), lexicon, sLanguageTag, dbConnector, _sessionElements) ;
	}
	
	/**
	 * Check inflections and their traits
	 * 
	 * @param lemmaWithInflections Lemma to update 
	 * @param sLanguageTag         Language to create inflections for
	 * @param sText                Text inflections have to be extracted from
	 * @param sGrammar             Grammar (PoS, grammatical gender, grammatical number)
	 * @param dbConnector          Database connector
	 * @param saveLemmaResult      Handler return structure
	 * 
	 * @throws NullPointerException
	 */
	protected void checkInflections(LemmaWithInflections lemmaWithInflections, final String sLanguageTag, final String sText, final String sGrammar, DBConnector dbConnector, SaveLemmaResult saveLemmaResult) throws NullPointerException
	{
		// We should never enter there
		//
		if ((null == saveLemmaResult) || (null == dbConnector) || (null == lemmaWithInflections))
			throw new NullPointerException() ;
		
		PartOfSpeech NewPoS  = OntologyLexicon.getPoSFromGrammarString(sGrammar) ;
		
		if      (PartOfSpeech.commonNoun == NewPoS)
			checkInflectionsForNoon(lemmaWithInflections, sLanguageTag, sText, sGrammar, dbConnector, saveLemmaResult) ;
		else if (PartOfSpeech.adjective == NewPoS)
			checkInflectionsForAdjective(lemmaWithInflections, sLanguageTag, sText, sGrammar, dbConnector, saveLemmaResult) ;
	}
	
	/**
	 * Check inflections and their traits for a common noun
	 * 
	 * @param lemmaWithInflections Lemma to update 
	 * @param sLanguageTag         Language to create inflections for
	 * @param sText                Text inflections have to be extracted from
	 * @param sGrammar             Grammar (PoS, grammatical gender, grammatical number)
	 * @param dbConnector          Database connector
	 * @param saveLemmaResult      Handler return structure
	 * 
	 * @throws NullPointerException
	 */
	protected void checkInflectionsForNoon(LemmaWithInflections lemmaWithInflections, final String sLanguageTag, final String sText, final String sGrammar, DBConnector dbConnector, SaveLemmaResult saveLemmaResult) throws NullPointerException
	{
		// We should never enter there
		//
		if ((null == saveLemmaResult) || (null == dbConnector) || (null == lemmaWithInflections))
			throw new NullPointerException() ;
		
		// Languages which noons have a singular and a plural flexed forms
		//
		if (("fr".equals(sLanguageTag)) || 
				("en".equals(sLanguageTag)) || 
				("it".equals(sLanguageTag)) || 
				("es".equals(sLanguageTag)) ||
				("pt".equals(sLanguageTag)))
		{
			QuadrifoliumFcts.Number[] aAllowedNumbers = new QuadrifoliumFcts.Number[2] ;
			aAllowedNumbers[0] = QuadrifoliumFcts.Number.singular ; 
			aAllowedNumbers[1] = QuadrifoliumFcts.Number.plural ;
			
			// The only allowed situation for noun inflections is not having a gender trait 
			//
			QuadrifoliumFcts.Gender[] aAllowedGenders = new QuadrifoliumFcts.Gender[1] ;
			aAllowedGenders[0] = QuadrifoliumFcts.Gender.nullGender ; 
			
			checkDeclinations(lemmaWithInflections, sLanguageTag, sText, sGrammar, QuadrifoliumFcts.PartOfSpeech.commonNoun, aAllowedNumbers, aAllowedGenders, dbConnector, saveLemmaResult) ;
		}
	}
	
	/**
	 * Check inflections and their traits for an adjective
	 * 
	 * @param lemmaWithInflections Lemma to update 
	 * @param sLanguageTag         Language to create inflections for
	 * @param sText                Text inflections have to be extracted from
	 * @param sGrammar             Grammar (PoS, grammatical gender, grammatical number)
	 * @param dbConnector          Database connector
	 * @param saveLemmaResult      Handler return structure
	 * 
	 * @throws NullPointerException
	 */
	protected void checkInflectionsForAdjective(LemmaWithInflections lemmaWithInflections, final String sLanguageTag, final String sText, final String sGrammar, DBConnector dbConnector, SaveLemmaResult saveLemmaResult) throws NullPointerException
	{
		// We should never enter there
		//
		if ((null == saveLemmaResult) || (null == dbConnector) || (null == lemmaWithInflections))
			throw new NullPointerException() ;
		
		// Languages which adjectives have a singular and a plural flexed forms combined to masculine and feminine genders
		//
		if (("fr".equals(sLanguageTag)) || 
				("it".equals(sLanguageTag)) || 
				("es".equals(sLanguageTag)) ||
				("pt".equals(sLanguageTag)))
		{
			QuadrifoliumFcts.Number[] aAllowedNumbers = new QuadrifoliumFcts.Number[2] ;
			aAllowedNumbers[0] = QuadrifoliumFcts.Number.singular ; 
			aAllowedNumbers[1] = QuadrifoliumFcts.Number.plural ;
			
			QuadrifoliumFcts.Gender[] aAllowedGenders = new QuadrifoliumFcts.Gender[2] ;
			aAllowedGenders[0] = QuadrifoliumFcts.Gender.feminine ; 
			aAllowedGenders[1] = QuadrifoliumFcts.Gender.masculine ;
			
			checkDeclinations(lemmaWithInflections, sLanguageTag, sText, sGrammar, QuadrifoliumFcts.PartOfSpeech.adjective, aAllowedNumbers, aAllowedGenders, dbConnector, saveLemmaResult) ;
		}
		
		// Languages which adjectives are invariable
		//
		if ("en".equals(sLanguageTag))
		{
			QuadrifoliumFcts.Number[] aAllowedNumbers = new QuadrifoliumFcts.Number[1] ;
			aAllowedNumbers[0] = QuadrifoliumFcts.Number.nullNumber ; 
				
			QuadrifoliumFcts.Gender[] aAllowedGenders = new QuadrifoliumFcts.Gender[1] ;
			aAllowedGenders[0] = QuadrifoliumFcts.Gender.nullGender ; 
				
			checkDeclinations(lemmaWithInflections, sLanguageTag, sText, sGrammar, QuadrifoliumFcts.PartOfSpeech.adjective, aAllowedNumbers, aAllowedGenders, dbConnector, saveLemmaResult) ;
		}
	}
	
	/**
	 * Check inflections and their traits
	 * 
	 * @param lemmaWithInflections Lemma to update 
	 * @param sLanguageTag         Language to create inflections for
	 * @param sText                Text inflections have to be extracted from
	 * @param sGrammar             Grammar (PoS, grammatical gender, grammatical number)
	 * @param PoS                  Lemma's updated part of speech
	 * @param aAllowedNumbers      Array of allowed grammatical numbers (pass <code>null</code> if not to check, pass only <code>nullNumber</code> if the only valid situation is not to have a grammatical number trait)  
	 * @param aAllowedGenders      Array of allowed grammatical genders (pass <code>null</code> if not to check, pass only <code>nullGender</code> if the only valid situation is not to have a grammatical gender trait)
	 * @param dbConnector          Database connector
	 * @param saveLemmaResult      Handler return structure
	 * 
	 * @throws NullPointerException
	 */
	protected void checkDeclinations(LemmaWithInflections lemmaWithInflections, final String sLanguageTag, final String sText, final String sGrammar, QuadrifoliumFcts.PartOfSpeech PoS, QuadrifoliumFcts.Number[] aAllowedNumbers, QuadrifoliumFcts.Gender[] aAllowedGenders, DBConnector dbConnector, SaveLemmaResult saveLemmaResult) throws NullPointerException
	{
		// We should never enter there
		//
		if ((null == saveLemmaResult) || (null == dbConnector) || (null == lemmaWithInflections))
			throw new NullPointerException() ;
		
		FlexManager   flexManager   = new FlexManager(_sessionElements, dbConnector) ;
		TripleManager tripleManager = new TripleManager(_sessionElements, dbConnector) ;
		
		ArrayList<FlexWithTraits> aFlexedForms = lemmaWithInflections.getInflections() ;
		
		// Delete non allowed flexed forms
		//
		for (FlexWithTraits flex : aFlexedForms)
		{
			boolean bIsAllowed = true ;
			
			// Not the proper language, better remove
			//
			if (false == flex.getLanguage().equals(sLanguageTag))
				bIsAllowed = false ;
			
			if (bIsAllowed && (null != aAllowedNumbers))
			{
				// Check if it is attached to a grammatical number that is valid for this language
				//
				// If the flexed form is not attached to a grammatical number, getGrammaticalNumber() returns <code>nullNumber</code>,
				// if this situation is enabled, don't forget to add <code>nullNumber</code> inside aAllowedNumbers 
				//
				boolean bKeepSearching = true ;
				for (int i = 0 ; (i < aAllowedNumbers.length) && bKeepSearching ; i++)
					if (flex.getGrammaticalNumber() == aAllowedNumbers[i])
						bKeepSearching = false ;
					
				if (bKeepSearching)
					bIsAllowed = false ;
			}
			
			if (bIsAllowed && (null != aAllowedGenders))
			{
				// Check if it is attached to a grammatical gender that is valid for this language
				//
				// If the flexed form is not attached to a grammatical gender, getGrammaticalGender() returns <code>nullGender</code>,
				// if this situation is enabled, don't forget to add <code>nullGender</code> inside aAllowedGenders 
				//
				boolean bKeepSearching = true ;
				for (int i = 0 ; (i < aAllowedGenders.length) && bKeepSearching ; i++)
					if (flex.getGrammaticalGender() == aAllowedGenders[i])
						bKeepSearching = false ;
					
				if (bKeepSearching)
					bIsAllowed = false ;
			}
			
			if (false == bIsAllowed)
				deleteExistingInflection(flex, flexManager, tripleManager, saveLemmaResult) ;
		}
		
		// Then create or update allowed flexed forms
		//
		if (null != aAllowedNumbers)
		{
			for (int i = 0 ; i < aAllowedNumbers.length ; i++)
			{
				QuadrifoliumFcts.Number grammaticalNumber = aAllowedNumbers[i] ;
				
				if (null != aAllowedGenders)
				{
					for (int j = 0 ; j < aAllowedGenders.length ; j++)
					{
						QuadrifoliumFcts.Gender grammaticalGender = aAllowedGenders[j] ;	
						checkAllowedSlot(lemmaWithInflections, sLanguageTag, sText, sGrammar, PoS, grammaticalNumber, grammaticalGender, flexManager, tripleManager, saveLemmaResult) ;
					}
				}
				else
					checkAllowedSlot(lemmaWithInflections, sLanguageTag, sText, sGrammar, PoS, grammaticalNumber, QuadrifoliumFcts.Gender.nullGender, flexManager, tripleManager, saveLemmaResult) ;
			}
		}
		else
		{
			if (null != aAllowedGenders)
			{
				for (int j = 0 ; j < aAllowedGenders.length ; j++)
				{
					QuadrifoliumFcts.Gender grammaticalGender = aAllowedGenders[j] ;	
					checkAllowedSlot(lemmaWithInflections, sLanguageTag, sText, sGrammar, PoS, QuadrifoliumFcts.Number.nullNumber, grammaticalGender, flexManager, tripleManager, saveLemmaResult) ;
				}
			}
			// Don't treat this case, since it mistakes null arrays with arrays containing nullNumber and nullGender
			// Typically, it would create an invariable flexed forms even in case with just want to check that there is none 
			//
			// else
			//	checkAllowedSlot(lemmaWithInflections, sLanguageTag, sText, sGrammar, PoS, QuadrifoliumFcts.Number.nullNumber, QuadrifoliumFcts.Gender.nullGender, flexManager, tripleManager, saveLemmaResult) ;
		}
	}
	
	/**
	 * Check a slot (Part of Speech, Gender, Number) that could host a flexed form, update/create/delete according to context 
	 * 
	 * @param lemmaWithInflections Lemma to update 
	 * @param sLanguageTag         Language to create inflections for
	 * @param sText                Text inflections have to be extracted from
	 * @param sGrammar             Grammar (PoS, grammatical gender, grammatical number)
	 * @param PoS                  Lemma's updated part of speech
	 * @param aAllowedNumbers      Array of allowed grammatical numbers (pass <code>null</code> if not to check, pass only <code>nullNumber</code> if the only valid situation is not to have a grammatical number trait)  
	 * @param aAllowedGenders      Array of allowed grammatical genders (pass <code>null</code> if not to check, pass only <code>nullGender</code> if the only valid situation is not to have a grammatical gender trait)
	 * @param dbConnector          Database connector
	 * @param saveLemmaResult      Handler return structure
	 * 
	 * @throws NullPointerException
	 */
	protected void checkAllowedSlot(LemmaWithInflections lemmaWithInflections, final String sLanguageTag, final String sText, final String sGrammar, QuadrifoliumFcts.PartOfSpeech PoS, QuadrifoliumFcts.Number grammaticalNumber, QuadrifoliumFcts.Gender grammaticalGender, FlexManager flexManager, TripleManager tripleManager, SaveLemmaResult saveLemmaResult) throws NullPointerException
	{
		// We should never enter there
		//
		if ((null == saveLemmaResult) || (null == flexManager)  || (null == tripleManager) || (null == lemmaWithInflections))
			throw new NullPointerException() ;
		
		// Get the flexed form for this slot (it can remain "" if there is none for this specific lemma)
		//
		String sLabel = "" ;
		
		OntologyLexicon.Declination declination = OntologyLexicon.getDeclinationFromGrammaticalNumber(grammaticalNumber) ; 
		OntologyLexicon.Gender      gender      = OntologyLexicon.getGenderFromNumberAndGender(grammaticalNumber, grammaticalGender) ;

		OntologyLexicon lexicon = new OntologyLexicon(sText, "", sGrammar, "", "") ;
		
		if      (QuadrifoliumFcts.PartOfSpeech.commonNoun == PoS)
			sLabel = lexicon.getDisplayLabelForNoon(declination, sLanguageTag) ;
		else if (QuadrifoliumFcts.PartOfSpeech.adjective == PoS)
			sLabel = lexicon.getDisplayLabelForAdjective(gender, sLanguageTag) ;
		
		// Check if this slot already contains a flexed form
		//
		FlexWithTraits flexInSlot = null ;
		
		ArrayList<FlexWithTraits> aFlexedForms = lemmaWithInflections.getInflections() ;
		if ((null != aFlexedForms) && (false == aFlexedForms.isEmpty()))
			for (FlexWithTraits flex : aFlexedForms)
				if ((flex.getGrammaticalNumber() == grammaticalNumber) && (flex.getGrammaticalGender().equals(grammaticalGender)))
					flexInSlot = flex ;
		
		// There is no existing flexed form inside this slot...
		//
		if (null == flexInSlot)
		{
			// But there should be one, so we have to create it
			//
			if (false == "".equals(sLabel))
			{
				if      (QuadrifoliumFcts.PartOfSpeech.commonNoun == PoS)
					LexiconToLemmaManager.createDeclinationForNoun(lemmaWithInflections.getCode(), lexicon, sLanguageTag, grammaticalNumber, flexManager, tripleManager, _sessionElements) ;
				else if (QuadrifoliumFcts.PartOfSpeech.adjective == PoS)
					LexiconToLemmaManager.createDeclinationForAdjective(lemmaWithInflections.getCode(), lexicon, sLanguageTag, grammaticalNumber, grammaticalGender, flexManager, tripleManager, _sessionElements) ;
			}
				
			return ;
		}
		
		// There is already a flexed form inside this slot...
		//
			
		// If the flex form doesn't exist for this grammatical number, we must delete the existing one
		//
		if ((null == sLabel) || "".equals(sLabel))
		{
			deleteExistingInflection(flexInSlot, flexManager, tripleManager, saveLemmaResult) ;
			return ;
		}
				
		// If the label changed, then update it
		//
		if (false == sLabel.equals(flexInSlot.getLabel()))
		{
			Flex updatedFlex = new Flex(flexInSlot) ;
			updatedFlex.setLabel(sLabel) ;
			flexManager.updateData(updatedFlex) ;
		}
	}
	
	/**
	 * Does a lemma already exist for this concept, this language tag and this text?
	 */
	protected boolean isOkToInject(final String sConceptCode, final String sLanguageTag, final String sText, final String sgrammar, DBConnector dbConnector)
	{
		// Can we work?
		//
		if ((null == sConceptCode) || "".equals(sConceptCode) || (null == sLanguageTag) || "".equals(sLanguageTag) || (null == sText) || "".equals(sText))
			return false ;
		
		if (null == dbConnector)
			return false ;
		
		// First, get all lemmas for the concept
		//
		ArrayList<Lemma> aLemmas = new ArrayList<Lemma>() ;
		if (false == getLemmasFromConcept(dbConnector, sConceptCode, aLemmas))
			return false ;
		
		if (aLemmas.isEmpty())
			return true ;
				
		// Now, try to find if the proposed text, for the proposed language, already exists among lemmas
		//
		for (Lemma lemma : aLemmas)
			if (sLanguageTag.equals(lemma.getLanguage()) && sText.equals(lemma.getLabel()))
				return false ;
			
		return true ;
	}
	
	/**
	 * Does another lemma already exist for this concept, this language tag and this text?
	 */
	protected boolean isOkToInjectAfterUpdate(final Lemma lemmaToUpdate, final String sLanguageTag, final String sText, final String sgrammar, DBConnector dbConnector) throws NullPointerException
	{
		if (null == lemmaToUpdate)
			throw new NullPointerException() ;
		
		// Can we work?
		//
		if ((null == sLanguageTag) || "".equals(sLanguageTag) || (null == sText) || "".equals(sText))
			return false ;
		
		String sLemmaCode = lemmaToUpdate.getCode() ;
		
		String sConceptCode = QuadrifoliumFcts.getConceptCode(sLemmaCode) ;
		if ((null == sConceptCode) || "".equals(sConceptCode))
			return false ;
		
		if (null == dbConnector)
			return false ;
		
		// First, get all lemmas for the concept
		//
		ArrayList<Lemma> aLemmas = new ArrayList<Lemma>() ;
		if (false == getLemmasFromConcept(dbConnector, sConceptCode, aLemmas))
			return false ;
		
		if (aLemmas.isEmpty())
			return true ;
				
		// Now, try to find if the proposed text, for the proposed language, already exists among lemmas
		//
		for (Lemma lemma : aLemmas)
			if ((false == sLemmaCode.equals(lemma.getCode())) && sLanguageTag.equals(lemma.getLanguage()) && sText.equals(lemma.getLabel()))
				return false ;
			
		return true ;
	}
	
	/**
	 * Get all lemmas for a given concept
	 * 
	 * @param dbconnector Database connector
	 * @param sCode       Code of the concepts to be looked for
	 * @param aLemmas     Array to be filled
	 * 
	 * @return <code>true</code> if all went well and <code>false</code> if not
	 **/	
	private boolean getLemmasFromConcept(DBConnector dbConnector, final String sCode, ArrayList<Lemma> aLemmas)
	{
		String sFctName = "SaveLemmaHandler.getLemmasFromConcept" ;
		
		if ((null == dbConnector) || (null == sCode) || "".equals(sCode))
		{
			Logger.trace(sFctName + ": bad parameter", _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		// SQL query to get all lemmas for a language and a concept
		//
		String sqlText = "SELECT * FROM lemma WHERE code LIKE ?" ;
		
		dbConnector.prepareStatememt(sqlText, Statement.NO_GENERATED_KEYS) ;
		dbConnector.setStatememtString(1, sCode + "%") ;
		
		if (false == dbConnector.executePreparedStatement())
		{
			Logger.trace(sFctName + ": failed query " + sqlText + " for code = " + sCode, _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		int iNbRecords = 0 ;
		
		ResultSet rs = dbConnector.getResultSet() ;
		try
		{
			// Browse results
			//
			while (rs.next())
			{
				Lemma lemma = new Lemma() ;
				LemmaManager.fillDataFromResultSet(rs, lemma, _sessionElements.getPersonId()) ;
				
				aLemmas.add(lemma) ;
				
				iNbRecords++ ;
			}
			
			if (0 == iNbRecords)
			{
				Logger.trace(sFctName + ": no lemma found for object \"" + sCode + "\".", _sessionElements.getPersonId(), Logger.TraceLevel.SUBSTEP) ;
				dbConnector.closePreparedStatement() ;
				return true ;
			}
		}
		catch(SQLException ex)
		{
			Logger.trace(sFctName + ": DBConnector.dbSelectPreparedStatement: executeQuery failed for preparedStatement " + sqlText, -1, Logger.TraceLevel.ERROR) ;
			Logger.trace(sFctName + ": SQLException: " + ex.getMessage(), -1, Logger.TraceLevel.ERROR) ;
			Logger.trace(sFctName + ": SQLState: " + ex.getSQLState(), -1, Logger.TraceLevel.ERROR) ;
			Logger.trace(sFctName + ": VendorError: " +ex.getErrorCode(), -1, Logger.TraceLevel.ERROR) ;        
		}
		
		Logger.trace(sFctName + ": found " + iNbRecords + " lemmas for concept " + sCode, _sessionElements.getPersonId(), Logger.TraceLevel.SUBDETAIL) ;
		
		dbConnector.closeResultSet() ;
		dbConnector.closePreparedStatement() ;
		
		return true ;
	}
	
	/**
	 * Get a LemmaWithInflections from a Lemma
	 * 
	 * @param dbConnector Database connector
	 * @param sLanguage   Language the flexed forms must be expressed into
	 * @param lemma       The Lemma to get traits and flex forms for
	 * 
	 * @return <code>null</code> if something went wrong
	 */
	protected LemmaWithInflections GetLemmaWithInflections(final DBConnector dbConnector, final String sLanguage, final Lemma lemma)
	{
		LemmaExtendedManager lexManager = new LemmaExtendedManager(_sessionElements, dbConnector) ;
		
		return lexManager.fillTraitsAndInflections(sLanguage, lemma) ;
	}
	
	@Override
	public void rollback(final SaveLemmaAction action,
        							 final SaveLemmaResult result,
                       final ExecutionContext context) throws ActionException
  {
		// Nothing to do here
  }
 
	@Override
	public Class<SaveLemmaAction> getActionType()
	{
		return SaveLemmaAction.class ;
	}
}
