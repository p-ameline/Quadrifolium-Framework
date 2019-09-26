package org.quadrifolium.server.handler4ontology;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;

import org.quadrifolium.server.DBConnector;
import org.quadrifolium.server.Logger;
import org.quadrifolium.server.handler.QuadrifoliumActionHandler;
import org.quadrifolium.server.ontology.FreeTextManager;
import org.quadrifolium.server.ontology.TripleManager;
import org.quadrifolium.shared.ontology.FreeText;
import org.quadrifolium.shared.ontology.Triple;
import org.quadrifolium.shared.ontology.TripleWithLabel;
import org.quadrifolium.shared.rpc4ontology.GetDefinitionsTriplesAction;
import org.quadrifolium.shared.rpc4ontology.GetDefinitionsTriplesResult;
import org.quadrifolium.shared.util.QuadrifoliumFcts;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class GetDefinitionsTriplesForConceptHandler extends QuadrifoliumActionHandler<GetDefinitionsTriplesAction, GetDefinitionsTriplesResult>
{
	protected int    _iUserId ;

	/**
	 * Buffer used to avoid looking twice for labels in the database 
	 */
  protected final HashMap<String, String> _aLabelsForCodes = new HashMap<String, String>() ;
	
	@Inject
	public GetDefinitionsTriplesForConceptHandler(final Logger logger,
                                                final Provider<ServletContext> servletContext,       
                                                final Provider<HttpServletRequest> servletRequest)
	{
		super(logger, servletContext, servletRequest) ;
		
		_iUserId = -1 ;
	}
	
	/**
	  * Constructor dedicated to unit tests 
	  */
	public GetDefinitionsTriplesForConceptHandler()
	{
		super() ;
		
		_iUserId = -1 ;
	}

	@Override
	public GetDefinitionsTriplesResult execute(final GetDefinitionsTriplesAction action, final ExecutionContext context) throws ActionException 
  {	
		// To make certain that the connection will be closed, a "finally" block must be added to secure the call to closeAll
		//
		DBConnector dbconnector = null ;
		
		try 
		{			
   		String sCode      = action.getConceptCode() ;
   		String sQueryLang = action.getDisplayLanguage() ;
   		
   		_iUserId            = action.getUserId() ;
   		
   		// Check if it really is a concept code
   		//
   		String sVerifiedCode = QuadrifoliumFcts.getConceptCode(sCode) ;
   		if (false == sVerifiedCode.equals(sCode))
   			return new GetDefinitionsTriplesResult("Error, wrong concept code") ;
   		
   		// Creates a connector to the Ontology database
   		//
   		dbconnector = new DBConnector(true, -1, DBConnector.databaseType.databaseOntology) ;

   		// Fill the structure to be returned
   		//
   		ArrayList<TripleWithLabel> aTriples = new ArrayList<TripleWithLabel>() ;

   		// Get all the triples that points to a definition for the given concept
   		//
   		boolean bGotTriples = getDefinitionsTriplesFromConcept(dbconnector, sCode, aTriples) ; 
   		if (false == bGotTriples)
   			return new GetDefinitionsTriplesResult("Error") ;
   		
   		// Fill definitions labels
   		//
   		GetDefinitionsTriplesResult definitionsTriplesListResult = new GetDefinitionsTriplesResult("") ;
   		
   		boolean bGotLabels = getDefinitionsLabelsForTriples(dbconnector, sQueryLang, aTriples, definitionsTriplesListResult) ; 
   		if (false == bGotLabels)
   			return new GetDefinitionsTriplesResult("Error") ;
   			
   		return definitionsTriplesListResult ;
		}
		catch (Exception cause) 
		{
			Logger.trace("GetDefinitionsTriplesForConceptHandler.execute: exception ; cause: " + cause.getMessage(), _iUserId, Logger.TraceLevel.DETAIL) ;
   
			throw new ActionException(cause);
		}
		finally
		{
			if (null != dbconnector)
				dbconnector.closeAll() ;
		}
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
			Logger.trace(sFctName + ": bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		String sPredicate = QuadrifoliumFcts.getConceptCodeForDefinition() ;
		
		if ((null == sPredicate) || "".equals(sPredicate))
		{
			Logger.trace(sFctName + ": cannot find the concept for \"definition\".", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		Logger.trace(sFctName + ": entering for concept = " + sCode, _iUserId, Logger.TraceLevel.STEP) ;
				
		// SQL query to get all lemmas for a language and a concept
		//
		String sqlText = "SELECT * FROM triple WHERE subject = ? AND predicate = ?" ;
		
		dbConnector.prepareStatememt(sqlText, Statement.NO_GENERATED_KEYS) ;
		dbConnector.setStatememtString(1, sCode) ;
		dbConnector.setStatememtString(2, sPredicate) ;
		
		if (false == dbConnector.executePreparedStatement())
		{
			Logger.trace(sFctName + ": failed query " + sqlText + " for subject = " + sCode + " and predicate = " + sPredicate, _iUserId, Logger.TraceLevel.ERROR) ;
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
				TripleManager.fillDataFromResultSet(rs, triple, _iUserId) ;
				
				aTriples.add(new TripleWithLabel(triple, "", null, null, null)) ;
				
				iNbRecords++ ;
			}
			
			if (0 == iNbRecords)
			{
				Logger.trace(sFctName + ": no triple found for object \"" + sCode + "\" and predicate \"" + sPredicate + "\".", _iUserId, Logger.TraceLevel.SUBSTEP) ;
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
		
		Logger.trace(sFctName + ": found " + iNbRecords + " triples for concept " + sCode + " and predicate " + sPredicate, _iUserId, Logger.TraceLevel.SUBDETAIL) ;
		
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
	protected boolean getDefinitionsLabelsForTriples(DBConnector dbConnector, final String sQueryLang, ArrayList<TripleWithLabel> aTriples, GetDefinitionsTriplesResult definitionsTriplesListResult)
	{
		String sFctName = "GetDefinitionsTriplesForConceptHandler.getDefinitionsLabelsForTriples" ;
		
		if ((null == dbConnector) || (null == aTriples) || aTriples.isEmpty())
		{
			Logger.trace(sFctName + ": bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		FreeTextManager freeTextManager = new FreeTextManager(null, dbConnector) ;
		
		for (TripleWithLabel triple : aTriples) 
		{
			FreeText freeText = freeTextManager.getFreeText(triple.getObject()) ;
			if ((null != freeText) && (false == "".equals(freeText.getLabel())))
			{
				triple.setObjectLabel(freeText.getLabel()) ;
				triple.setLanguage(freeText.getLanguage()) ;
				
				definitionsTriplesListResult.addTriple(triple) ;
			}
		}
		
		return true ;
	}
	
	@Override
	public void rollback(final GetDefinitionsTriplesAction action,
        							 final GetDefinitionsTriplesResult result,
                       final ExecutionContext context) throws ActionException
  {
		// Nothing to do here
  }
 
	@Override
	public Class<GetDefinitionsTriplesAction> getActionType()
	{
		return GetDefinitionsTriplesAction.class ;
	}
}
