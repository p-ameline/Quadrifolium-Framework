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
import org.quadrifolium.server.ontology.FreeTextManager;
import org.quadrifolium.server.ontology.TripleManager;
import org.quadrifolium.shared.ontology.FreeText;
import org.quadrifolium.shared.ontology.Triple;
import org.quadrifolium.shared.ontology.TripleWithLabel;
import org.quadrifolium.shared.rpc4ontology.SaveDefinitionAction;
import org.quadrifolium.shared.rpc4ontology.SaveDefinitionResult;
import org.quadrifolium.shared.rpc_util.SessionElements;
import org.quadrifolium.shared.util.QuadrifoliumFcts;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class SaveDefinitionTripleHandler extends QuadrifoliumActionHandler<SaveDefinitionAction, SaveDefinitionResult>
{
	protected SessionElements _sessionElements ;

	@Inject
	public SaveDefinitionTripleHandler(final Logger logger,
                                     final Provider<ServletContext> servletContext,       
                                     final Provider<HttpServletRequest> servletRequest)
	{
		super(logger, servletContext, servletRequest) ;
		
		_sessionElements = null ;
	}
	
	/**
	  * Constructor dedicated to unit tests 
	  */
	public SaveDefinitionTripleHandler()
	{
		super() ;
		
		_sessionElements = null ;
	}

	@Override
	public SaveDefinitionResult execute(final SaveDefinitionAction action, final ExecutionContext context) throws ActionException 
  {	
		if (null == action)
		{
			Logger.trace("SaveDefinitionTripleHandler.execute: Error, no input information.", -1, Logger.TraceLevel.ERROR) ;
			return new SaveDefinitionResult("Error, no input information.", null, "", "", "", -1) ;
		}
		
		// To make certain that the connection will be closed, a "finally" block must be added to secure the call to closeAll
		//
		DBConnector dbconnector = null ;
		
		String sConceptCode = action.getConceptCode() ;
		String sLanguageTag = action.getNewLanguage() ;
 		String sText        = action.getNewText() ;
 	
		try 
		{
			_sessionElements = action.getSessionElements() ;
			if (null == _sessionElements)
			{
				Logger.trace("SaveDefinitionTripleHandler.execute: Error, no session elements.", -1, Logger.TraceLevel.ERROR) ;
				return new SaveDefinitionResult("No session elements.", null, sConceptCode, sLanguageTag, sText, -1) ;
			}
			
			// Check token validity
   		//
   		SessionsManager sessionManager = new SessionsManager() ;
   		boolean bValidSession = sessionManager.isValidToken(_sessionElements.getPersonId(), _sessionElements.getToken()) ;
   		if (false == bValidSession)
   		{
   			Logger.trace("SaveDefinitionTripleHandler.execute: Error, invalid session elements.", _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
   			return new SaveDefinitionResult("Invalid session elements.", null, sConceptCode, sLanguageTag, sText, -1) ;
   		}

			// Do we have to update an existing definition?
			//
			TripleWithLabel updatedTriple = action.getEditedDefinition() ;
   		
   		int iUpdatedTripleId = -1 ;
   		if (null != updatedTriple)
   			iUpdatedTripleId = updatedTriple.getId() ;
   		
   		String sTraceText = "SaveDefinitionTripleHandler.execute: For concept " + sConceptCode ;
   		if (-1 == iUpdatedTripleId)
   			sTraceText += " add definition " ;
   		else
   			sTraceText += " update definition " + iUpdatedTripleId + " " ;
   		sTraceText += sLanguageTag + ", \"" + sText + "\"" ;
   		
   		Logger.trace(sTraceText, _sessionElements.getPersonId(), Logger.TraceLevel.STEP) ;
   		
   		// Check if language tag and text are valid
   		//
   		if ((null == sConceptCode) || "".equals(sConceptCode))
   		{
   			Logger.trace("SaveDefinitionTripleHandler.execute: Error, no concept code.", _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
   			return new SaveDefinitionResult("Error, no concept code.", null, sConceptCode, sLanguageTag, sText, iUpdatedTripleId) ;
   		}
   		
   		if ((null != updatedTriple) && (false == sConceptCode.equals(updatedTriple.getObject())))
   		{
   			Logger.trace("SaveDefinitionTripleHandler.execute: Error, wrong concept code for definition to update.", _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
   			return new SaveDefinitionResult("Error, wrong concept code for definition to update.", null, sConceptCode, sLanguageTag, sText, iUpdatedTripleId) ;
   		}
   		
   		// Check if language tag and text are valid
   		//
   		if ((null == sLanguageTag) || ("".equals(sLanguageTag)) || (null == sText) || ("".equals(sText)))
   		{
   			Logger.trace("SaveDefinitionTripleHandler.execute: Error, empty language and/or text.", _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
   			return new SaveDefinitionResult("Error, empty language and/or text.", null, sConceptCode, sLanguageTag, sText, iUpdatedTripleId) ;
   		}
   		
   		// Check if it really is a concept code
   		//
   		String sVerifiedCode = QuadrifoliumFcts.getConceptCode(sConceptCode) ;
   		if (false == sVerifiedCode.equals(sConceptCode))
   		{
   			Logger.trace("SaveDefinitionTripleHandler.execute: Error, wrong concept code.", _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
   			return new SaveDefinitionResult("Error, wrong concept code.", null, sConceptCode, sLanguageTag, sText, iUpdatedTripleId) ;
   		}
   		
   		// Creates a connector to the Ontology database
   		//
   		dbconnector = new DBConnector(true, -1, DBConnector.databaseType.databaseOntology) ;

   		// Do the job
   		//
   		SaveDefinitionResult saveDefinitionstResult = new SaveDefinitionResult("", null, sConceptCode, sLanguageTag, sText, iUpdatedTripleId) ;
   		
   		if (null != updatedTriple)
   			updateDefinition(updatedTriple, sLanguageTag, sText, dbconnector, saveDefinitionstResult) ;
   		else
   			saveDefinition(sConceptCode, sLanguageTag, sText, dbconnector, saveDefinitionstResult) ;
   		
   		return saveDefinitionstResult ;
		}
		catch (Exception cause) 
		{
			Logger.trace("SaveDefinitionTripleHandler.execute: exception ; cause: " + cause.getMessage(), _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
   
			throw new ActionException(cause);
		}
		finally
		{
			if (null != dbconnector)
				dbconnector.closeAll() ;
		}
  }

	/**
	 * Update an existing definition
	 */
	protected void updateDefinition(TripleWithLabel updatedTriple, final String sLanguageTag, final String sText, DBConnector dbConnector, SaveDefinitionResult saveDefinitionstResult)
	{
		// We should never enter there
		//
		if (null == saveDefinitionstResult)
			return ;
		
		if ((null == dbConnector) || (null == updatedTriple) || (null == sLanguageTag) || "".equals(sLanguageTag) || (null == sText) || "".equals(sText))
		{
			saveDefinitionstResult.setMessage("Server internal error") ;
			return ;
		}
		
		// By the way, is there something to do?
		//
		if (sLanguageTag.equals(updatedTriple.getLanguage()) && sText.equals(updatedTriple.getSubjectLabel()))
		{
			Logger.trace("SaveDefinitionTripleHandler.updateDefinition: Looks like there is nothing to do.", _sessionElements.getPersonId(), Logger.TraceLevel.WARNING) ;
			saveDefinitionstResult.setMessage("Looks like there is nothing to do.") ;
			return ;
		}
		
		// First, check that updating this definition will not break something
		//
		if (false == isOkToInject(updatedTriple.getObject(), sLanguageTag, sText, dbConnector))
		{
			Logger.trace("SaveDefinitionTripleHandler.updateDefinition: Such a definition already exists.", _sessionElements.getPersonId(), Logger.TraceLevel.WARNING) ;
			saveDefinitionstResult.setMessage("Such a definition already exists.") ;
			return ;
		}
		
		// It is now possible to update
		//
		FreeTextManager freeTextManager = new FreeTextManager(_sessionElements, dbConnector) ;
		
		if (false == freeTextManager.updateData(updatedTriple.getSubject(), sText, sLanguageTag))
			saveDefinitionstResult.setMessage("Internal server error.") ;
	}
	
	/**
	 * Record an existing definition
	 */
	protected void saveDefinition(final String sConceptCode, final String sLanguageTag, final String sText, DBConnector dbConnector, SaveDefinitionResult saveDefinitionstResult)
	{
		// We should never enter there
		//
		if (null == saveDefinitionstResult)
			return ;
		
		if ((null == dbConnector) || (null == sConceptCode) || "".equals(sConceptCode) || (null == sLanguageTag) || "".equals(sLanguageTag) || (null == sText) || "".equals(sText))
		{
			saveDefinitionstResult.setMessage("Server internal error") ;
			return ;
		}
				
		// First, check that adding this definition will not break something
		//
		if (false == isOkToInject(sConceptCode, sLanguageTag, sText, dbConnector))
		{
			Logger.trace("SaveDefinitionTripleHandler.updateDefinition: Such a definition already exists.", _sessionElements.getPersonId(), Logger.TraceLevel.WARNING) ;
			saveDefinitionstResult.setMessage("Such a definition already exists.") ;
			return ;
		}
		
		// It is now possible to add in database
		//
		FreeTextManager freeTextManager = new FreeTextManager(_sessionElements, dbConnector) ;
		
		// First, add the text and get its ID
		//
		String sFreeTextCode = freeTextManager.insertData(sConceptCode, sText, sLanguageTag) ;
		if ((null == sFreeTextCode) || "".equals(sFreeTextCode))
			saveDefinitionstResult.setMessage("Internal server error.") ;
		
		// Then add the triple
		//
		TripleManager tripleManager = new TripleManager(_sessionElements, dbConnector) ;
		
		Triple tripleToInsert = new Triple(-1, sConceptCode, QuadrifoliumFcts.getConceptCodeForDefinition(), sFreeTextCode) ;
		if (false == tripleManager.insertData(tripleToInsert))
			saveDefinitionstResult.setMessage("Internal server error.") ;
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
		ArrayList<TripleWithLabel> aTriples = new ArrayList<TripleWithLabel>() ;
		if (false == getDefinitionsTriplesFromConcept(dbConnector, sConceptCode, aTriples))
			return false ;
		
		if (aTriples.isEmpty())
			return true ;
		
		// Then attach definitions labels to the triples
		//
		if (false == getDefinitionsLabelsForTriples(dbConnector, aTriples))
			return false ;
		
		// Now, try to find if the proposed text, for the proposed language, already exists among triples
		//
		for (TripleWithLabel tripleWl : aTriples)
			if (sLanguageTag.equals(tripleWl.getLanguage()) && sText.equals(tripleWl.getObjectLabel()))
				return false ;
			
		return true ;
	}
	
	/**
	 * Get all semantic triples with the concept as an object
	 * 
	 * @param dbconnector       Database connector
	 * @param sLanguage         Language to get the definition for (usually <code>""</code> to get all definitions)
	 * @param sCode             Code of the concepts to be looked for
	 * @param triplesListResult Result structure to be filled
	 * 
	 * @return <code>true</code> if all went well and <code>false</code> if not
	 **/	
	private boolean getDefinitionsTriplesFromConcept(DBConnector dbConnector, final String sCode, ArrayList<TripleWithLabel> aTriples)
	{
		String sFctName = "GetDefinitionsTriplesForConceptHandler.getDefinitionsTriplesFromConcept" ;
		
		if ((null == dbConnector) || (null == sCode) || "".equals(sCode))
		{
			Logger.trace(sFctName + ": bad parameter", _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		String sPredicate = QuadrifoliumFcts.getConceptCodeForDefinition() ;
		
		if ((null == sPredicate) || "".equals(sPredicate))
		{
			Logger.trace(sFctName + ": cannot find the concept for \"definition\".", _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		Logger.trace(sFctName + ": entering for concept = " + sCode, _sessionElements.getPersonId(), Logger.TraceLevel.STEP) ;
				
		// SQL query to get all lemmas for a language and a concept
		//
		String sqlText = "SELECT * FROM triple WHERE subject = ? AND predicate = ?" ;
		
		dbConnector.prepareStatememt(sqlText, Statement.NO_GENERATED_KEYS) ;
		dbConnector.setStatememtString(1, sCode) ;
		dbConnector.setStatememtString(2, sPredicate) ;
		
		if (false == dbConnector.executePreparedStatement())
		{
			Logger.trace(sFctName + ": failed query " + sqlText + " for subject = " + sCode + " and predicate = " + sPredicate, _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
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
				Triple triple = new Triple() ;
				TripleManager.fillDataFromResultSet(rs, triple, _sessionElements.getPersonId()) ;
				
				aTriples.add(new TripleWithLabel(triple, "", null, null, null)) ;
				
				iNbRecords++ ;
			}
			
			if (0 == iNbRecords)
			{
				Logger.trace(sFctName + ": no triple found for object \"" + sCode + "\" and predicate \"" + sPredicate + "\".", _sessionElements.getPersonId(), Logger.TraceLevel.SUBSTEP) ;
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
		
		Logger.trace(sFctName + ": found " + iNbRecords + " triples for concept " + sCode + " and predicate " + sPredicate, _sessionElements.getPersonId(), Logger.TraceLevel.SUBDETAIL) ;
		
		dbConnector.closeResultSet() ;
		dbConnector.closePreparedStatement() ;
		
		return true ;
	}
	
	/**
	 * Fill the list of triples with their definitions labels
	 * 
	 * @param dbconnector Database connector
	 * @param sLanguage   Language to get triples labels for
	 * @param aTriples    List of triples to be provided with their labels
	 */
	protected boolean getDefinitionsLabelsForTriples(DBConnector dbConnector, ArrayList<TripleWithLabel> aTriples)
	{
		String sFctName = "GetDefinitionsTriplesForConceptHandler.getDefinitionsLabelsForTriples" ;
		
		if ((null == dbConnector) || (null == aTriples) || aTriples.isEmpty())
		{
			Logger.trace(sFctName + ": bad parameter", _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		FreeTextManager freeTextManager = new FreeTextManager(_sessionElements, dbConnector) ;
		
		for (TripleWithLabel triple : aTriples) 
		{
			FreeText freeText = freeTextManager.getFreeText(triple.getObject()) ;
			if ((null != freeText) && (false == "".equals(freeText.getLabel())))
			{
				triple.setObjectLabel(freeText.getLabel()) ;
				triple.setLanguage(freeText.getLanguage()) ;
			}
		}
		
		return true ;
	}
	
	@Override
	public void rollback(final SaveDefinitionAction action,
        							 final SaveDefinitionResult result,
                       final ExecutionContext context) throws ActionException
  {
		// Nothing to do here
  }
 
	@Override
	public Class<SaveDefinitionAction> getActionType()
	{
		return SaveDefinitionAction.class ;
	}
}
