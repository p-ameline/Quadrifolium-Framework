package org.quadrifolium.server.handler4ontology;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;

// import org.quadrifolium.server.DBConnector;
// import org.quadrifolium.server.Logger;
import org.quadrifolium.server.ontology.FlexManager;
import org.quadrifolium.shared.ontology.Flex;
import org.quadrifolium.shared.rpc4ontology.GetFlexListFromTextAction;
import org.quadrifolium.shared.rpc4ontology.GetFlexListFromTextResult;

import com.google.inject.Inject;
import com.google.inject.Provider;

import com.ldv.server.DBConnector;
import com.ldv.server.Logger;
import com.ldv.server.handler.LdvActionHandler;

public class GetFlexListFromTextHandler extends LdvActionHandler<GetFlexListFromTextAction, GetFlexListFromTextResult>
{
	protected int _iUserId ;
	
	@Inject
	public GetFlexListFromTextHandler(final Provider<ServletContext> servletContext,       
                                    final Provider<HttpServletRequest> servletRequest)
	{
		super(servletContext, servletRequest) ;
		
		_iUserId        = -1 ; 
	}
	
	/**
	  * Constructor dedicated to unit tests 
	  */
	public GetFlexListFromTextHandler()
	{
		super() ;
		
		_iUserId = -1 ; 
	}

	@Override
	public GetFlexListFromTextResult execute(final GetFlexListFromTextAction action, final ExecutionContext context) throws ActionException 
  {	
		try 
		{			
   		String sText = action.getStartingText() ;
   		String sLang = action.getLanguage() ;
   		
   		_iUserId = action.getUserId() ;
   		
   		// Creates a connector to Ontology database
   		//
   		DBConnector dbconnector = new DBConnector(true, -1, DBConnector.databaseType.databaseOntology) ;
   		
   		GetFlexListFromTextResult FlexListResult = new GetFlexListFromTextResult("", action.getBoxIndex()) ;

   		boolean bGotResult = false ;
   		
   		String[] aLangPart = sLang.split("-") ;
   		String sSubLang = aLangPart[0] ; 
   		for (int i = 0 ; i < aLangPart.length ; i++)
   		{
   			if (getFlexListFromText(dbconnector, sSubLang, sText, FlexListResult))
   				bGotResult = true ;
   			
   			if (i < aLangPart.length - 1)
   				sSubLang += "-" + aLangPart[i + 1] ; 
   		}
   		
   		if (bGotResult)
   			return FlexListResult ;
   		
			return new GetFlexListFromTextResult("Error", action.getBoxIndex()) ;
		}
		catch (Exception cause) 
		{
			Logger.trace("GetFlexListFromTextHandler.execute: ; cause: " + cause.getMessage(), _iUserId, Logger.TraceLevel.DETAIL) ;
   
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
	private boolean getFlexListFromText(DBConnector dbConnector, final String sLanguage, final String sText, GetFlexListFromTextResult flexListResult)
	{
		String sFctName = "GetFlexListFromTextHandler.getLexiconListFromText" ;
		
		if ((null == dbConnector) || (null == sText) || sText.equals(""))
		{
			Logger.trace(sFctName + ": bad parameter", -1, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		String sqlText = "SELECT * FROM flex WHERE lang = ? AND label LIKE ?" ;
		
		dbConnector.prepareStatememt(sqlText, Statement.NO_GENERATED_KEYS) ;
		dbConnector.setStatememtString(1, sLanguage) ;
		dbConnector.setStatememtString(2, sText + "%") ;
				
		if (false == dbConnector.executePreparedStatement())
		{
			Logger.trace(sFctName + ": failed query " + sqlText + " and text = " + sText, _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		int iNbRecords = 0 ;
		
		ResultSet rs = dbConnector.getResultSet() ;
		try
		{        
			while (rs.next())
			{
				Flex flex = new Flex() ;
				FlexManager.fillDataFromResultSet(rs, flex, _iUserId) ;
				
				flexListResult.addFlex(flex) ;
				
				iNbRecords++ ;
			}
			
			if (0 == iNbRecords)
			{
				Logger.trace(sFctName + ": nothing found for \"" + sText + "\" in flex", _iUserId, Logger.TraceLevel.SUBSTEP) ;
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
		
		Logger.trace(sFctName + ": found " + iNbRecords + " entries for text \"" + sText + "\"", _iUserId, Logger.TraceLevel.SUBDETAIL) ;
		
		dbConnector.closePreparedStatement() ;
		
		return true ;
	}
		
	@Override
	public void rollback(final GetFlexListFromTextAction action,
        							 final GetFlexListFromTextResult result,
                       final ExecutionContext context) throws ActionException
  {
		// Nothing to do here
  }
 
	@Override
	public Class<GetFlexListFromTextAction> getActionType()
	{
		return GetFlexListFromTextAction.class ;
	}
}
