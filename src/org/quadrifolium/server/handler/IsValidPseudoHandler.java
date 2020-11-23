package org.quadrifolium.server.handler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;

import org.quadrifolium.shared.database.Person;
import org.quadrifolium.shared.rpc.IsValidPseudoAction;
import org.quadrifolium.shared.rpc.IsValidPseudoResult;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ldv.server.DBConnector;
import com.ldv.server.Logger;
import com.ldv.server.handler.LdvActionHandler;

public class IsValidPseudoHandler extends LdvActionHandler<IsValidPseudoAction, IsValidPseudoResult>
{
	@Inject
	public IsValidPseudoHandler(final Provider<ServletContext> servletContext,       
                              final Provider<HttpServletRequest> servletRequest)
	{
		super(servletContext, servletRequest) ;
	}
	
	/**
	  * Constructor dedicated to unit tests 
	  */
	public IsValidPseudoHandler()
	{
		super() ;
	}

	@Override
	public IsValidPseudoResult execute(final IsValidPseudoAction action,
       					                        final ExecutionContext context) throws ActionException 
  {
		String sFctName = "GetLdvOtherUsersHandler.execute" ;
		
		try 
		{			
			Logger.trace(sFctName + ": entering.", -1, Logger.TraceLevel.DETAIL) ;
			
			IsValidPseudoResult otherUsersResults = new IsValidPseudoResult() ;
			otherUsersResults.setSuccess(false) ;  // we know this is the default value, but better don't rely on it
			
			if (null == action)
				return otherUsersResults ;
			
			String sPseudo = action.getPseudo() ;
			if ("".equals(sPseudo))
				return otherUsersResults ;
			
			otherUsersResults.setPseudo(action.getPseudo()) ;
			
			DBConnector dbConnector = new DBConnector(true, -1, DBConnector.databaseType.databaseCore) ;
			
			// Prepare sql query
			//
			String sqlText = "SELECT * FROM persons WHERE pseudo = ?" ;
			
			dbConnector.prepareStatememt(sqlText, Statement.NO_GENERATED_KEYS) ;
			dbConnector.setStatememtString(1, sPseudo) ;
					
			// Execute prepared statement 
			//
			boolean bSuccess = dbConnector.executePreparedStatement() ;
			if (false == bSuccess)
			{
				Logger.trace(sFctName + ": query failed for pseudo \"" + sPseudo + "\", leaving.", -1, Logger.TraceLevel.ERROR) ;
				return otherUsersResults ;
			}
			
			ResultSet rs = dbConnector.getResultSet() ;
			if (null == rs)
			{
				Logger.trace(sFctName + ": pseudo \"" + sPseudo + "\" is not a valid pseudo, leaving.", -1, Logger.TraceLevel.SUBDETAIL) ;
				return otherUsersResults ;
			}
			
			// Adding all results from database to the result object
			//
			try
	    {
		    while (rs.next())
		    {
		    	Person newPerson = new Person() ;
		    	
		    	// For confidentiality reason, we only return pseudo and bio
		    	//
		    	newPerson.setPersonId(rs.getInt("personId")) ;
		    	newPerson.setPseudo(rs.getString("pseudo")) ;
		    	newPerson.setBio(rs.getString("bio")) ;
		    	
		    	otherUsersResults.setPerson(newPerson) ;		    	
		    	otherUsersResults.setSuccess(true) ;
		    }
	    } catch (SQLException e)
	    {
	    	Logger.trace(sFctName + ": error parsing results ; stackTrace:" + e.getStackTrace(), -1, Logger.TraceLevel.ERROR) ;
	    }
			
	    dbConnector.closeResultSet() ;
			
	    Logger.trace(sFctName + ": leaving. \"" + sPseudo + "\" is a valid pseudo.", -1, Logger.TraceLevel.DETAIL) ;
	    
			return otherUsersResults ;
		}
		catch (Exception cause) 
		{
			Logger.trace(sFctName + ": exception ; cause: " + cause.getMessage(), -1, Logger.TraceLevel.ERROR) ;
			throw new ActionException(cause) ;
		}
  }
	
	@Override
	public void rollback(final IsValidPseudoAction action,
        							 final IsValidPseudoResult result,
                       final ExecutionContext context) throws ActionException
  {
		// Nothing to do here
  }
 
	@Override
	public Class<IsValidPseudoAction> getActionType()
	{
		return IsValidPseudoAction.class ;
	}
}
