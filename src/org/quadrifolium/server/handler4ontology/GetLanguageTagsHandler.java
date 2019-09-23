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
import org.quadrifolium.server.ontology.LanguageTagManager;
import org.quadrifolium.shared.ontology.LanguageTag;
import org.quadrifolium.shared.rpc4ontology.GetLanguageTagsAction;
import org.quadrifolium.shared.rpc4ontology.GetLanguageTagsResult;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class GetLanguageTagsHandler extends QuadrifoliumActionHandler<GetLanguageTagsAction, GetLanguageTagsResult>
{
	protected int _iUserId ;
	
	@Inject
	public GetLanguageTagsHandler(final Logger logger,
                                final Provider<ServletContext> servletContext,       
                                final Provider<HttpServletRequest> servletRequest)
	{
		super(logger, servletContext, servletRequest) ;
		
		_iUserId = -1 ; 
	}
	
	/**
	  * Constructor dedicated to unit tests 
	  */
	public GetLanguageTagsHandler()
	{
		super() ;
		
		_iUserId = -1 ; 
	}

	@Override
	public GetLanguageTagsResult execute(final GetLanguageTagsAction action, final ExecutionContext context) throws ActionException 
  {	
		try 
		{			
   		_iUserId = action.getUserId() ;
   		
   		// Creates a connector to Ontology database
   		//
   		DBConnector dbconnector = new DBConnector(true, -1, DBConnector.databaseType.databaseOntology) ;
   		
   		GetLanguageTagsResult TagsListResult = new GetLanguageTagsResult("") ;
   		
   		if (true == fillLanguageTagsList(dbconnector, TagsListResult))
   			return TagsListResult ;
   		
			return new GetLanguageTagsResult("Error") ;
		}
		catch (Exception cause) 
		{
			Logger.trace("GetLanguageTagsHandler.execute: ; cause: " + cause.getMessage(), _iUserId, Logger.TraceLevel.DETAIL) ;
   
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
	private boolean fillLanguageTagsList(DBConnector dbConnector, GetLanguageTagsResult tagsListResult)
	{
		String sFctName = "GetLanguageTagsHandler.fillLanguageTagsList" ;
		
		if ((null == dbConnector) || (null == tagsListResult))
		{
			Logger.trace(sFctName + ": bad parameter", -1, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		String sqlText = "SELECT * FROM languageTag" ;
		
		dbConnector.prepareStatememt(sqlText, Statement.NO_GENERATED_KEYS) ;
				
		if (false == dbConnector.executePreparedStatement())
		{
			Logger.trace(sFctName + ": failed query", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		int iNbRecords = 0 ;
		
		ResultSet rs = dbConnector.getResultSet() ;
		try
		{        
			while (rs.next())
			{
				LanguageTag tag = new LanguageTag() ;
				LanguageTagManager.fillDataFromResultSet(rs, tag, _iUserId) ;
				
				tagsListResult.addTag(tag) ;
				
				iNbRecords++ ;
			}
			
			if (0 == iNbRecords)
			{
				Logger.trace(sFctName + ": no language tag found in database.", _iUserId, Logger.TraceLevel.SUBSTEP) ;
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
		
		Logger.trace(sFctName + ": found " + iNbRecords + " language tags in database.", _iUserId, Logger.TraceLevel.SUBDETAIL) ;
		
		dbConnector.closePreparedStatement() ;
		
		return true ;
	}
		
	@Override
	public void rollback(final GetLanguageTagsAction action,
        							 final GetLanguageTagsResult result,
                       final ExecutionContext context) throws ActionException
  {
		// Nothing to do here
  }
 
	@Override
	public Class<GetLanguageTagsAction> getActionType()
	{
		return GetLanguageTagsAction.class ;
	}
}
