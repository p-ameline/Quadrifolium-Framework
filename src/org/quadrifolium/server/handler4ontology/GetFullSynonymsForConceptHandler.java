package org.quadrifolium.server.handler4ontology;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;

import org.quadrifolium.server.ontology.LemmaExtendedManager;
import org.quadrifolium.server.ontology.LemmaManager;
import org.quadrifolium.server.ontology.TripleManager;
import org.quadrifolium.server.util.QuadrifoliumServerFcts;
import org.quadrifolium.shared.ontology.Lemma;
import org.quadrifolium.shared.ontology.LemmaWithInflections;
import org.quadrifolium.shared.ontology.Triple;
import org.quadrifolium.shared.ontology.TripleWithLabel;
import org.quadrifolium.shared.rpc4ontology.GetFullSynonymsForConceptAction;
import org.quadrifolium.shared.rpc4ontology.GetFullSynonymsForConceptResult;
import org.quadrifolium.shared.rpc_util.SessionElements;
import org.quadrifolium.shared.util.QuadrifoliumFcts;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ldv.server.DBConnector;
import com.ldv.server.Logger;
import com.ldv.server.handler.LdvActionHandler;

public class GetFullSynonymsForConceptHandler extends LdvActionHandler<GetFullSynonymsForConceptAction, GetFullSynonymsForConceptResult>
{
	protected SessionElements _sessionElements ;
	protected int             _iUserId ;
	
	protected String _sQueryLanguage ;

	// "Global traits" i.e. Traits with the concept as the subject
	//
	protected ArrayList<TripleWithLabel> _aTraitsForConcept = new ArrayList<TripleWithLabel>() ;
	
	/**
	 * Buffer used to avoid looking twice for labels in the database 
	 */
  protected final HashMap<String, String> _aLabelsForCodes = new HashMap<String, String>() ;
	
	@Inject
	public GetFullSynonymsForConceptHandler(final Provider<ServletContext> servletContext,       
                                          final Provider<HttpServletRequest> servletRequest)
	{
		super(servletContext, servletRequest) ;
		
		_sessionElements = null ;
		_iUserId         = -1 ;
		_sQueryLanguage  = "" ;
	}
	
	/**
	  * Constructor dedicated to unit tests 
	  */
	public GetFullSynonymsForConceptHandler()
	{
		super() ;
		
		_sessionElements = null ;
		_iUserId         = -1 ;
		_sQueryLanguage  = "" ;
	}

	@Override
	public GetFullSynonymsForConceptResult execute(final GetFullSynonymsForConceptAction action, final ExecutionContext context) throws ActionException 
  {	
		// To make certain that the connection will be closed, a "finally" block must be added to secure the call to closeAll
		//
		DBConnector dbconnector = null ;
		
		try 
		{			
   		String sCode        = action.getConceptCode() ;
   		String sDisplayLang = action.getDisplayLanguage() ;
   		
   		_iUserId        = action.getUserId() ;
   		_sQueryLanguage = action.getQueryLanguage() ;
   		
   		// Check if it really is a concept code
   		//
   		String sVerifiedCode = QuadrifoliumFcts.getConceptCode(sCode) ;
   		if (false == sVerifiedCode.equals(sCode))
   			return new GetFullSynonymsForConceptResult("Error, wrong concept code") ;
   		
   		// Creates a connector to the Ontology database
   		//
   		dbconnector = new DBConnector(true, -1, DBConnector.databaseType.databaseOntology) ;

   		// Get global traits
   		//
   		fillGlobalTraits(dbconnector, sDisplayLang, sCode) ;
   		
   		GetFullSynonymsForConceptResult SynonymsListResult = new GetFullSynonymsForConceptResult("") ;
   		
   		// Fill the structure to be returned
   		//
   		boolean bGotSynonyms = getSynonymsListFromConcept(dbconnector, sDisplayLang, sCode, SynonymsListResult) ; 
   		if (true == bGotSynonyms)
   			return SynonymsListResult ;
   		
			return new GetFullSynonymsForConceptResult("Error") ;
		}
		catch (Exception cause) 
		{
			Logger.trace("GetFullSynonymsForConceptHandler.execute: exception ; cause: " + cause.getMessage(), _iUserId, Logger.TraceLevel.DETAIL) ;
   
			throw new ActionException(cause);
		}
		finally
		{
			if (null != dbconnector)
				dbconnector.closeAll() ;
		}
  }

	/**
	 * Look for an entry with a given code in the "flex" table 
	 * 
	 * @param dbconnector        Database connector
	 * @param sLanguage          Language to get all synonyms for
	 * @param sCode              Code of the concepts to be looked for
	 * @param synonymsListResult Result structure to be filled
	 * 
	 **/	
	private boolean getSynonymsListFromConcept(DBConnector dbConnector, final String sLanguage, final String sCode, GetFullSynonymsForConceptResult synonymsListResult)
	{
		String sFctName = "GetFullSynonymsForConceptHandler.getSynonymsListFromConcept" ;
		
		if ((null == dbConnector) || (null == sCode) || "".equals(sCode))
		{
			Logger.trace(sFctName + ": bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		Logger.trace(sFctName + ": entering for concept = " + sCode, _iUserId, Logger.TraceLevel.STEP) ;
		
		boolean bQueryForLang = true ;
		if ("".equals(_sQueryLanguage))
			bQueryForLang = false ;
		
		// SQL query to get all lemmas for a language and a concept
		//
		String sqlText = "SELECT * FROM lemma WHERE code LIKE ?" ;
		
		if (bQueryForLang)
			sqlText += " AND lang = ?" ;
		
		dbConnector.prepareStatememt(sqlText, Statement.NO_GENERATED_KEYS) ;
		dbConnector.setStatememtString(1, sCode + "%") ;
		
		if (bQueryForLang)
			dbConnector.setStatememtString(2, _sQueryLanguage) ;
		
		if (false == dbConnector.executePreparedStatement())
		{
			Logger.trace(sFctName + ": failed query " + sqlText + " and code = " + sCode, _iUserId, Logger.TraceLevel.ERROR) ;
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
				LemmaManager.fillDataFromResultSet(rs, lemma, _iUserId) ;
				
				synonymsListResult.addSynonym(new LemmaWithInflections(lemma, null, null)) ;
				
				iNbRecords++ ;
			}
			
			if (0 == iNbRecords)
			{
				Logger.trace(sFctName + ": nothing found for concept \"" + sCode + "\" in lemma", _iUserId, Logger.TraceLevel.SUBSTEP) ;
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
		
		Logger.trace(sFctName + ": found " + iNbRecords + " lemmas for concept " + sCode, _iUserId, Logger.TraceLevel.SUBDETAIL) ;
		
		dbConnector.closeResultSet() ;
		dbConnector.closePreparedStatement() ;
		
		boolean bAllWentWell = true ;
		
		// Add other information (inflections and triples)
		//
		ArrayList<LemmaWithInflections> aSynonyms = synonymsListResult.getSynonymsArray() ;
		for (LemmaWithInflections synonym : aSynonyms)
		{
			boolean bSuccess = fillTraitsAndInflections(dbConnector, sLanguage, synonym) ;
			if (false == bSuccess)
				bAllWentWell = false ;
			
			synonym.setPreferred(isLemmaAPreferredTerm(synonym.getCode())) ;
		}
		
		Logger.trace(sFctName + ": leaving for concept = " + sCode, _iUserId, Logger.TraceLevel.STEP) ;
		
		return bAllWentWell ;
	}
		
	/**
	 * Fill traits and inflections for a given lemma
	 * 
	 * @param dbconnector Database connector
	 * @param sLanguage   Language to get all synonyms for
	 * @param synonym     Object to be completed
	 * @return
	 */
	private boolean fillTraitsAndInflections(DBConnector dbConnector, final String sLanguage, LemmaWithInflections synonym)
	{
		LemmaExtendedManager LExManager = new LemmaExtendedManager(_sessionElements, dbConnector, _aLabelsForCodes) ;
		
		boolean bGotTraits      = LExManager.fillTraits(sLanguage, synonym) ;
		boolean bGotInflections = LExManager.fillInflections(sLanguage, synonym) ;
		
		return bGotTraits && bGotInflections ;
	}
		
	/**
	 * Fill "global traits": traits with the concept as subject
	 * 
	 * @param dbconnector Database connector
	 * @param sLanguage   Language to get all synonyms for
	 * @param sConcept    Code of the concept to get traits for
	 * 
	 * @return <code>true</code> if all went well, <code>false</code> if not
	 */
	private boolean fillGlobalTraits(DBConnector dbConnector, final String sLanguage, final String sConcept)
	{
		String sFctName = "GetFullSynonymsForConceptHandler.fillGlobalTraits" ;
		
		if ((null == dbConnector) || (null == sConcept) || "".equals(sConcept))
		{
			Logger.trace(sFctName + ": bad parameter", -1, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		// SQL query to get all triples the concept is subject of
		//
		String sqlText = "SELECT * FROM triple WHERE subject = ?" ;
		
		dbConnector.prepareStatememt(sqlText, Statement.NO_GENERATED_KEYS) ;
		dbConnector.setStatememtString(1, sConcept) ;
				
		if (false == dbConnector.executePreparedStatement())
		{
			Logger.trace(sFctName + ": failed query " + sqlText + " and code = " + sConcept, _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		int iNbRecords = 0 ;
		
		ResultSet rs = dbConnector.getResultSet() ;
		try
		{
			// Browse resulting triples
			//
			while (rs.next())
			{
				Triple triple = new Triple() ;
				TripleManager.fillDataFromResultSet(rs, triple, _iUserId) ;
				
				_aTraitsForConcept.add(new TripleWithLabel(triple, sLanguage, "", "", "")) ;
				
				iNbRecords++ ;
			}
			
			if (0 == iNbRecords)
			{
				Logger.trace(sFctName + ": no triple found for concept \"" + sConcept + "\"", _iUserId, Logger.TraceLevel.SUBSTEP) ;
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
		
		Logger.trace(sFctName + ": found " + iNbRecords + " triples for concept " + sConcept, _iUserId, Logger.TraceLevel.SUBDETAIL) ;
		
		dbConnector.closeResultSet() ;
		dbConnector.closePreparedStatement() ;
		
		for (Iterator<TripleWithLabel> itTrait = _aTraitsForConcept.iterator() ; itTrait.hasNext() ; )
		{
			TripleWithLabel trait = itTrait.next() ;
			QuadrifoliumServerFcts.fillTraitWithLabels(dbConnector, _sessionElements, sLanguage, trait, _aLabelsForCodes) ;
		}
		
		return true ;
	}
	
	/**
	 * Is a lemma a preferred term for this concept
	 * 
	 * @param sLemmaCode Code of lemma to check
	 * 
	 * @return <code>true</code> if a global trait defines this lemma as a preferred term, <code>false</code> if not   
	 */
	protected boolean isLemmaAPreferredTerm(final String sLemmaCode)
	{
		if ((null == sLemmaCode) || "".equals(sLemmaCode) || _aTraitsForConcept.isEmpty())
			return false ;
		
		String sCodeForPreferredTerm = QuadrifoliumFcts.getConceptCodeForPreferredTerm() ;
		if ((null == sCodeForPreferredTerm) || "".equals(sCodeForPreferredTerm))
			return false ;
		
		for (Iterator<TripleWithLabel> itTrait = _aTraitsForConcept.iterator() ; itTrait.hasNext() ; )
		{
			TripleWithLabel globalTrait = itTrait.next() ; 
			
			if (sLemmaCode.equals(globalTrait.getObject()) && sCodeForPreferredTerm.equals(globalTrait.getPredicate()))
				return true ;
		}
		
		return false ;
	}

	@Override
	public void rollback(final GetFullSynonymsForConceptAction action,
        							 final GetFullSynonymsForConceptResult result,
                       final ExecutionContext context) throws ActionException
  {
		// Nothing to do here
  }
 
	@Override
	public Class<GetFullSynonymsForConceptAction> getActionType()
	{
		return GetFullSynonymsForConceptAction.class ;
	}
}
