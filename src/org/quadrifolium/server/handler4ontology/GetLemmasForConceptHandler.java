package org.quadrifolium.server.handler4ontology;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;

import org.quadrifolium.server.DBConnector;
import org.quadrifolium.server.Logger;
import org.quadrifolium.server.handler.QuadrifoliumActionHandler;
import org.quadrifolium.server.ontology.LemmaManager;
import org.quadrifolium.shared.ontology.Lemma;
import org.quadrifolium.shared.rpc4ontology.GetLemmasForConceptAction;
import org.quadrifolium.shared.rpc4ontology.GetLemmasForConceptResult;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class GetLemmasForConceptHandler extends QuadrifoliumActionHandler<GetLemmasForConceptAction, GetLemmasForConceptResult>
{
	protected int _iUserId ;
	
	@Inject
	public GetLemmasForConceptHandler(final Logger logger,
                                    final Provider<ServletContext> servletContext,       
                                    final Provider<HttpServletRequest> servletRequest)
	{
		super(logger, servletContext, servletRequest) ;
		
		_iUserId = -1 ; 
	}
	
	/**
	  * Constructor dedicated to unit tests 
	  */
	public GetLemmasForConceptHandler()
	{
		super() ;
		
		_iUserId = -1 ; 
	}

	@Override
	public GetLemmasForConceptResult execute(final GetLemmasForConceptAction action, final ExecutionContext context) throws ActionException 
  {	
		try 
		{			
   		String sCode = action.getConceptCode() ;
   		String sLang = action.getLanguage() ;
   		
   		_iUserId = action.getUserId() ;
   		
   		// Creates a connector to Ontology database
   		//
   		DBConnector dbconnector = new DBConnector(true, -1, DBConnector.databaseType.databaseOntology) ;
   		
   		GetLemmasForConceptResult LemmaListResult = new GetLemmasForConceptResult("") ;
   		
   		if (true == getLemmaListFromConcept(dbconnector, sLang, sCode, LemmaListResult))
   			return LemmaListResult ;
   		
			return new GetLemmasForConceptResult("Error") ;
		}
		catch (Exception cause) 
		{
			Logger.trace("GetLemmasForConceptHandler.execute: ; cause: " + cause.getMessage(), _iUserId, Logger.TraceLevel.DETAIL) ;
   
			throw new ActionException(cause);
		}
  }

	/**
	 * Look for an entry with a given code in the "flex" table 
	 * 
	 * @param dbconnector Database connector
	 * @param sCode       Code to be looked for
	 * @param lexicon     Record content
	 * 
	 **/	
	private boolean getLemmaListFromConcept(DBConnector dbConnector, final String sLanguage, final String sCode, GetLemmasForConceptResult lemmaListResult)
	{
		String sFctName = "GetLemmasForConceptHandler.getLemmaListFromConcept" ;
		
		if ((null == dbConnector) || (null == sCode) || "".equals(sCode))
		{
			Logger.trace(sFctName + ": bad parameter", -1, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		String sqlText = "SELECT * FROM lemma WHERE lang = ? AND code LIKE ?" ;
		
		dbConnector.prepareStatememt(sqlText, Statement.NO_GENERATED_KEYS) ;
		dbConnector.setStatememtString(1, sLanguage) ;
		dbConnector.setStatememtString(2, sCode + "%") ;
				
		if (false == dbConnector.executePreparedStatement())
		{
			Logger.trace(sFctName + ": failed query " + sqlText + " and code = " + sCode, _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		int iNbRecords = 0 ;
		
		ResultSet rs = dbConnector.getResultSet() ;
		try
		{        
			while (rs.next())
			{
				Lemma lemma = new Lemma() ;
				LemmaManager.fillDataFromResultSet(rs, lemma, _iUserId) ;
				
				lemmaListResult.addLemma(lemma) ;
				
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
		
		dbConnector.closePreparedStatement() ;
		
		return true ;
	}
		
	@Override
	public void rollback(final GetLemmasForConceptAction action,
        							 final GetLemmasForConceptResult result,
                       final ExecutionContext context) throws ActionException
  {
		// Nothing to do here
  }
 
	@Override
	public Class<GetLemmasForConceptAction> getActionType()
	{
		return GetLemmasForConceptAction.class ;
	}
}
