package org.quadrifolium.server.handler4ontology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;

import org.quadrifolium.server.ontology.TripleManager;
import org.quadrifolium.server.util.QuadrifoliumServerFcts;
import org.quadrifolium.shared.ontology.Triple;
import org.quadrifolium.shared.ontology.TripleWithLabel;
import org.quadrifolium.shared.rpc4ontology.GetSemanticTriplesAction;
import org.quadrifolium.shared.rpc4ontology.GetSemanticTriplesResult;
import org.quadrifolium.shared.rpc_util.SessionElements;
import org.quadrifolium.shared.util.QuadrifoliumFcts;

import com.google.inject.Inject;
import com.google.inject.Provider;

import com.ldv.server.DBConnector;
import com.ldv.server.Logger;
import com.ldv.server.handler.LdvActionHandler;

public class GetSemanticTriplesForConceptHandler extends LdvActionHandler<GetSemanticTriplesAction, GetSemanticTriplesResult>
{
	protected SessionElements _sessionElements ;
	protected int             _iUserId ;

	/**
	 * Buffer used to avoid looking twice for labels in the database 
	 */
  protected final HashMap<String, String> _aLabelsForCodes = new HashMap<String, String>() ;
	
	@Inject
	public GetSemanticTriplesForConceptHandler(final Provider<ServletContext> servletContext,       
                                             final Provider<HttpServletRequest> servletRequest)
	{
		super(servletContext, servletRequest) ;
		
		_sessionElements = null ;
		_iUserId         = -1 ;
	}
	
	/**
	  * Constructor dedicated to unit tests 
	  */
	public GetSemanticTriplesForConceptHandler()
	{
		super() ;
		
		_sessionElements = null ;
		_iUserId         = -1 ;
	}

	@Override
	public GetSemanticTriplesResult execute(final GetSemanticTriplesAction action, final ExecutionContext context) throws ActionException 
  {	
		_sessionElements    = action.getSessionElements() ;
 		
 		// This function is read only, hence it doesn't need a registered user
		//
		_iUserId = -1 ;
		if (null != _sessionElements)
			_iUserId = _sessionElements.getPersonId() ;
		
		// To make certain that the connection will be closed, a "finally" block must be added to secure the call to closeAll
		//
		DBConnector dbconnector = null ;
		
		try 
		{			
   		String sCode        = action.getConceptCode() ;
   		String sDisplayLang = action.getDisplayLanguage() ;
   		
   		// Check if it really is a concept code
   		//
   		String sVerifiedCode = QuadrifoliumFcts.getConceptCode(sCode) ;
   		if (false == sVerifiedCode.equals(sCode))
   			return new GetSemanticTriplesResult("Error, wrong concept code") ;
   		
   		// Creates a connector to the Ontology database
   		//
   		dbconnector = new DBConnector(true, -1, DBConnector.databaseType.databaseOntology) ;

   		GetSemanticTriplesResult SemanticTriplesListResult = new GetSemanticTriplesResult("") ;
   		
   		// Fill the structure to be returned
   		//
   		boolean bGotTriples = getSemanticTriplesFromConcept(dbconnector, sDisplayLang, sCode, SemanticTriplesListResult) ; 
   		if (bGotTriples)
   			return SemanticTriplesListResult ;
   		
			return new GetSemanticTriplesResult("Error") ;
		}
		catch (Exception cause) 
		{
			Logger.trace("GetSemanticTriplesForConceptHandler.execute: exception ; cause: " + cause.getMessage(), _iUserId, Logger.TraceLevel.DETAIL) ;
   
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
	 * @param sLanguage         Language to get all synonyms for
	 * @param sCode             Code of the concepts to be looked for
	 * @param triplesListResult Result structure to be filled
	 * 
	 * @return <code>true</code> if all went well and <code>false</code> if not
	 **/	
	private boolean getSemanticTriplesFromConcept(DBConnector dbConnector, final String sLanguage, final String sCode, GetSemanticTriplesResult triplesListResult)
	{
		String sFctName = "GetSemanticTriplesForConceptHandler.getLeftSemanticTriplesFromConcept" ;
		
		if ((null == dbConnector) || (null == sCode) || "".equals(sCode))
		{
			Logger.trace(sFctName + ": bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		Logger.trace(sFctName + ": entering for concept = " + sCode, _iUserId, Logger.TraceLevel.STEP) ;
		
		// We need a TripleManager
		//
		TripleManager tripleManager = new TripleManager(_sessionElements, dbConnector) ;
		
		// First step, get all triples for the concept as object
		//
		ArrayList<TripleWithLabel> aLeftTriples = new ArrayList<TripleWithLabel>() ; 
		
		boolean bLIsA  = getTriplesFromPredicateAndConceptAsSubject(tripleManager, sLanguage, sCode, QuadrifoliumFcts.getConceptCodeForIsA(), aLeftTriples) ;
		boolean bLIsA0 = getTriplesFromPredicateAndConceptAsSubject(tripleManager, sLanguage, sCode, QuadrifoliumFcts.getConceptCodeForIntransitiveIsA(), aLeftTriples) ;
		boolean bLAt   = getTriplesFromPredicateAndConceptAsSubject(tripleManager, sLanguage, sCode, QuadrifoliumFcts.getConceptCodeForAt(), aLeftTriples) ;
		boolean bLMe   = getTriplesFromPredicateAndConceptAsSubject(tripleManager, sLanguage, sCode, QuadrifoliumFcts.getConceptCodeForHasUnit(), aLeftTriples) ;
		
		fillLabels(dbConnector, sLanguage, aLeftTriples) ;
		
		triplesListResult.addListOfTriplesForConceptAsObject(aLeftTriples) ;
		
		// Second step, get all triples for the concept as subject
		//
		ArrayList<TripleWithLabel> aRightTriples = new ArrayList<TripleWithLabel>() ; 
			
		boolean bRIsA  = getTriplesFromPredicateAndConceptAsObject(tripleManager, sLanguage, sCode, QuadrifoliumFcts.getConceptCodeForIsA(), aRightTriples) ;
		boolean bRIsA0 = getTriplesFromPredicateAndConceptAsObject(tripleManager, sLanguage, sCode, QuadrifoliumFcts.getConceptCodeForIntransitiveIsA(), aRightTriples) ;
		boolean bRAt   = getTriplesFromPredicateAndConceptAsObject(tripleManager, sLanguage, sCode, QuadrifoliumFcts.getConceptCodeForAt(), aRightTriples) ;
		boolean bRMe   = getTriplesFromPredicateAndConceptAsObject(tripleManager, sLanguage, sCode, QuadrifoliumFcts.getConceptCodeForHasUnit(), aRightTriples) ;
			
		fillLabels(dbConnector, sLanguage, aRightTriples) ;
			
		triplesListResult.addListOfTriplesForConceptAsSubject(aRightTriples) ;
		
		Logger.trace(sFctName + ": leaving for concept = " + sCode, _iUserId, Logger.TraceLevel.STEP) ;
		
		return bLIsA && bLIsA0 && bLAt && bLMe && bRIsA && bRIsA0 && bRAt && bRMe ;
	}

	/**
	 * Get all triples for a given predicate, with the concept as the object
	 * 
	 * @param tripleManager     TripleManager used for database query
	 * @param sLanguage         Language to get triples labels for
	 * @param sObject           Code as object of the triples to look for
	 * @param sPredicate        Predicate of the triples to look for
	 * @param triplesListResult Result structure to be filled
	 * 
	 * @return <code>true</code> if all went well and <code>false</code> if not 
	 */
	protected boolean getTriplesFromPredicateAndConceptAsObject(TripleManager tripleManager, final String sLanguage, final String sObject, final String sPredicate, ArrayList<TripleWithLabel> aResultList)
	{
		String sFctName = "GetSemanticTriplesForConceptHandler.getTriplesFromPredicateAndConceptAsObject" ;
			
		if ((null == tripleManager) || (null == sObject) || "".equals(sObject))
		{
			Logger.trace(sFctName + ": bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		ArrayList<Triple> aResults = new ArrayList<Triple>() ;
		
		if (false == tripleManager.getSubjects(sObject, sPredicate, "", aResults))
		  return false ;
		
		if (false == aResults.isEmpty())
		  for (Triple triple : aResults)
		    aResultList.add(new TripleWithLabel(triple, sLanguage, null, null, null)) ;
		
		return true ;
	}
	
	 /**
   * Get all triples for a given predicate, with the concept as the subject
   * 
   * @param tripleManager     TripleManager used for database query
   * @param sLanguage         Language to get triples labels for
   * @param sObject           Code as subject of the triples to look for
   * @param sPredicate        Predicate of the triples to look for
   * @param triplesListResult Result structure to be filled
   * 
   * @return <code>true</code> if all went well and <code>false</code> if not 
   */
  protected boolean getTriplesFromPredicateAndConceptAsSubject(TripleManager tripleManager, final String sLanguage, final String sSubject, final String sPredicate, ArrayList<TripleWithLabel> aResultList)
  {
    String sFctName = "GetSemanticTriplesForConceptHandler.getTriplesFromPredicateAndConceptAsSubject" ;
      
    if ((null == tripleManager) || (null == sSubject) || "".equals(sSubject) || (null == sPredicate) || "".equals(sPredicate))
    {
      Logger.trace(sFctName + ": bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
      return false ;
    }
    
    ArrayList<Triple> aResults = new ArrayList<Triple>() ;
    
    if (false == tripleManager.getObjects(sSubject, sPredicate, "", aResults))
      return false ;
    
    if (false == aResults.isEmpty())
      for (Triple triple : aResults)
        aResultList.add(new TripleWithLabel(triple, sLanguage, null, null, null)) ;
    
    return true ;
  }
	
	/**
	 * Fill a list of triples with their labels
	 * 
	 * @param dbconnector Database connector
	 * @param sLanguage   Language to get triples labels for
	 * @param aTriples    List of triples to be provided with their labels
	 */
	protected void fillLabels(DBConnector dbConnector, final String sLanguage, ArrayList<TripleWithLabel> aTriples)
	{
		if ((null == aTriples) || aTriples.isEmpty())
			return ;
		
		// Provide the traits with their labels
		//
		for (Iterator<TripleWithLabel> it = aTriples.iterator() ; it.hasNext() ; )
			QuadrifoliumServerFcts.fillTraitWithLabels(dbConnector, _sessionElements, sLanguage, it.next(), _aLabelsForCodes) ;
	}

	@Override
	public void rollback(final GetSemanticTriplesAction action,
        							 final GetSemanticTriplesResult result,
                       final ExecutionContext context) throws ActionException
  {
		// Nothing to do here
  }
 
	@Override
	public Class<GetSemanticTriplesAction> getActionType()
	{
		return GetSemanticTriplesAction.class ;
	}
}
