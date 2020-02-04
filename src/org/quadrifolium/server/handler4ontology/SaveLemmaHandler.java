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
import org.quadrifolium.server.ontology.LemmaManager;
import org.quadrifolium.shared.ontology.Lemma;
import org.quadrifolium.shared.rpc4ontology.SaveLemmaAction;
import org.quadrifolium.shared.rpc4ontology.SaveLemmaResult;
import org.quadrifolium.shared.rpc_util.SessionElements;
import org.quadrifolium.shared.util.QuadrifoliumFcts;

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
			return new SaveLemmaResult("Error, no input information.", null, "", "", "", -1) ;
		}
		
		// To make certain that the connection will be closed, a "finally" block must be added to secure the call to closeAll
		//
		DBConnector dbconnector = null ;
		
		/** Concept the lemma to be created/edited belongs to */
		String sConceptCode = action.getConceptCode() ;
		
		String sLanguageTag = action.getNewLanguage() ;
 		String sText        = action.getNewText() ;
 	
		try 
		{
			_sessionElements = action.getSessionElements() ;
			if (null == _sessionElements)
			{
				Logger.trace(sFunctionName + ": Error, no session elements.", -1, Logger.TraceLevel.ERROR) ;
				return new SaveLemmaResult("No session elements.", null, sConceptCode, sLanguageTag, sText, -1) ;
			}
			
			// Check token validity
   		//
   		SessionsManager sessionManager = new SessionsManager() ;
   		boolean bValidSession = sessionManager.isValidToken(_sessionElements.getPersonId(), _sessionElements.getToken()) ;
   		if (false == bValidSession)
   		{
   			Logger.trace(sFunctionName + ": Error, invalid session elements.", _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
   			return new SaveLemmaResult("Invalid session elements.", null, sConceptCode, sLanguageTag, sText, -1) ;
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
   			return new SaveLemmaResult("Error, no concept code.", null, sConceptCode, sLanguageTag, sText, iUpdatedLemmaId) ;
   		}
   		
   		// Check for consistency of concept code
   		//
   		if (null != updatedLemma)
   		{
   			String sLemmaConceptCode = QuadrifoliumFcts.getConceptCode(updatedLemma.getCode()) ;
   		
   			if (false == sConceptCode.equals(sLemmaConceptCode))
   			{
   				Logger.trace(sFunctionName + ": Error, wrong concept code for definition to update.", _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
   				return new SaveLemmaResult("Error, wrong concept code for lemma to update.", null, sConceptCode, sLanguageTag, sText, iUpdatedLemmaId) ;
   			}
   		}
   		
   		// Check if language tag and text are valid
   		//
   		if ((null == sLanguageTag) || ("".equals(sLanguageTag)) || (null == sText) || ("".equals(sText)))
   		{
   			Logger.trace(sFunctionName + ": Error, empty language and/or text.", _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
   			return new SaveLemmaResult("Error, empty language and/or text.", null, sConceptCode, sLanguageTag, sText, iUpdatedLemmaId) ;
   		}
   		
   		// Check if concept code is valid
   		//
   		String sVerifiedCode = QuadrifoliumFcts.getConceptCode(sConceptCode) ;
   		if (false == sVerifiedCode.equals(sConceptCode))
   		{
   			Logger.trace(sFunctionName + ": Error, wrong concept code.", _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
   			return new SaveLemmaResult("Error, wrong concept code.", null, sConceptCode, sLanguageTag, sText, iUpdatedLemmaId) ;
   		}
   		
   		// Creates a connector to the Ontology database
   		//
   		dbconnector = new DBConnector(true, -1, DBConnector.databaseType.databaseOntology) ;

   		// Do the job
   		//
   		SaveLemmaResult saveLemmaResult = new SaveLemmaResult("", null, sConceptCode, sLanguageTag, sText, iUpdatedLemmaId) ;
   		
   		if (null != updatedLemma)
   			updateLemma(updatedLemma, sLanguageTag, sText, dbconnector, saveLemmaResult) ;
   		else
   			saveLemma(sConceptCode, sLanguageTag, sText, dbconnector, saveLemmaResult) ;
   		
   		return saveLemmaResult ;
		}
		catch (Exception cause) 
		{
			Logger.trace(sFunctionName + ": exception ; cause: " + cause.getMessage(), _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
   
			throw new ActionException(cause);
		}
		finally
		{
			if (null != dbconnector)
				dbconnector.closeAll() ;
		}
  }

	/**
	 * Update an existing lemma
	 */
	protected void updateLemma(Lemma updatedLemma, final String sLanguageTag, final String sText, DBConnector dbConnector, SaveLemmaResult saveLemmaResult) throws NullPointerException
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
		
		// By the way, is there something to do?
		//
		if (sLanguageTag.equals(updatedLemma.getLanguage()) && sText.equals(updatedLemma.getLabel()))
		{
			Logger.trace(sFunctionName + ": Looks like there is nothing to do.", _sessionElements.getPersonId(), Logger.TraceLevel.WARNING) ;
			saveLemmaResult.setMessage("Looks like there is nothing to do.") ;
			return ;
		}
		
		// First, check that updating this definition will not break something
		//
		String sLemmaConceptCode = QuadrifoliumFcts.getConceptCode(updatedLemma.getCode()) ;
		
		if (false == isOkToInject(sLemmaConceptCode, sLanguageTag, sText, dbConnector))
		{
			Logger.trace(sFunctionName + ": Such a definition already exists.", _sessionElements.getPersonId(), Logger.TraceLevel.WARNING) ;
			saveLemmaResult.setMessage("Such a lemma already exists.") ;
			return ;
		}
		
		// It is now possible to update
		//
		LemmaManager lemmaManager = new LemmaManager(_sessionElements, dbConnector) ;
		
		// Create the modified version of lemma to update
		//
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
		saveLemmaResult.setSavedLemma(newLemma) ;
	}
	
	/**
	 * Record an new lemma
	 */
	protected void saveLemma(final String sConceptCode, final String sLanguageTag, final String sText, DBConnector dbConnector, SaveLemmaResult saveLemmaResult) throws NullPointerException
	{
		// We should never enter there
		//
		if (null == saveLemmaResult)
			throw new NullPointerException() ;
		
		String sFunctionName = "SaveLemmaHandler.saveLemma" ;
		
		if ((null == dbConnector) || (null == sConceptCode) || "".equals(sConceptCode) || (null == sLanguageTag) || "".equals(sLanguageTag) || (null == sText) || "".equals(sText))
		{
			saveLemmaResult.setMessage("Server internal error") ;
			return ;
		}
				
		// First, check that adding this definition will not break something
		//
		if (false == isOkToInject(sConceptCode, sLanguageTag, sText, dbConnector))
		{
			Logger.trace(sFunctionName + ": Such a lemma already exists.", _sessionElements.getPersonId(), Logger.TraceLevel.WARNING) ;
			saveLemmaResult.setMessage("Such a lemma already exists.") ;
			return ;
		}
		
		// It is now possible to add in database
		//
		LemmaManager lemmaManager = new LemmaManager(_sessionElements, dbConnector) ;
		
		// Get next available lemma code for this concept
		//
		String sLemmaCode = lemmaManager.getNextLemmaCode(sConceptCode) ;
		if ("".equals(sLemmaCode))
			saveLemmaResult.setMessage("Internal server error.") ;
		
		// Create the lemma to insert
		//
		Lemma newLemma = new Lemma() ;
		
		newLemma.setCode(sLemmaCode) ;
		newLemma.setLabel(sText) ;
		newLemma.setLanguage(sLanguageTag) ;
		
		// Insert new lemma in database
		//
		if (false == lemmaManager.insertData(newLemma))
			saveLemmaResult.setMessage("Internal server error.") ;
		
		saveLemmaResult.setSavedLemma(newLemma) ;
	}
	
	/**
	 * Does a definition already exist for this concept, this language tag and this text?
	 */
	protected boolean isOkToInject(final String sConceptCode, final String sLanguageTag, final String sText, DBConnector dbConnector)
	{
		// Can we work?
		//
		if ((null == sConceptCode) || "".equals(sConceptCode) || (null == sLanguageTag) || "".equals(sLanguageTag) || (null == sText) || "".equals(sText))
			return false ;
		
		if (null == dbConnector)
			return false ;
		
		// First, get all triples that are definitions for the concept
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
