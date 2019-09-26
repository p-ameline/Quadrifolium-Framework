package org.quadrifolium.server.handler;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;

import org.quadrifolium.server.DbParameters;
import org.quadrifolium.server.Logger;
import org.quadrifolium.server.model.PersonManager;
import org.quadrifolium.server.model.SessionsManager;
import org.quadrifolium.shared.database.Person;
import org.quadrifolium.shared.rpc.SendLoginAction;
import org.quadrifolium.shared.rpc.SendLoginResult;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class CheckUserHandler extends QuadrifoliumActionHandler<SendLoginAction, SendLoginResult>
{
	@Inject
	public CheckUserHandler(final Logger logger,
                          final Provider<ServletContext> servletContext,
                          final Provider<HttpServletRequest> servletRequest)
	{
		super(logger, servletContext, servletRequest) ;
	}

	/**
	  * Constructor dedicated to unit tests 
	  */
	public CheckUserHandler()
	{
		super() ;
	}
	
	@Override
	public SendLoginResult execute(final SendLoginAction action,
       					                 final ExecutionContext context) throws ActionException 
  {	
		try 
		{			
   		String sIdentifier = action.getIdentifier() ;
   		String sPassword   = action.getEncryptedPassword() ;
   		
   		String sVersion    = DbParameters.getVersion() ;
   		
   		// Get identifier into "persons" database
   		//
   		Person user = getPerson(sIdentifier, sPassword) ;
   		if (null == user)
   			return new SendLoginResult(null, getInfoString("Wrong identifiers"), null, sVersion) ;
   		
   		// Open a session into "sessions" database
   		//
   		SessionsManager sessionManager = new SessionsManager() ;
   		boolean bSessionCreated = sessionManager.createNewSession(user.getPersonId()) ;
   		if (false == bSessionCreated)
   			return new SendLoginResult(null, getInfoString("Cannot create session"), null, sVersion) ;
   				
   		// Return session information
   		//
   		return new SendLoginResult(sessionManager.getSessionElements(), "", user, sVersion) ;
		}
		catch (Exception cause) 
		{
			Logger.trace("CheckUserHandler: Exception " + cause.getMessage(), -1, Logger.TraceLevel.ERROR) ;
   
			throw new ActionException(cause);
		}
  }
	
	private Person getPerson(String sLogin, String sPassword)
	{
		String sFunctionName = "CheckUserHandler.getPerson" ;
		
		if (sLogin.equals("") && sPassword.equals(""))
		{
			Logger.trace(sFunctionName + ": empty parameters", -1, Logger.TraceLevel.ERROR) ;
			return null ;
		}		
		if (sLogin.equals("") || sPassword.equals(""))
		{
			if      (sLogin.equals(""))
				Logger.trace(sFunctionName + ": empty login for password=" + sPassword, -1, Logger.TraceLevel.ERROR) ;
			else if (sPassword.equals(""))
				Logger.trace(sFunctionName + ": empty password for login=" + sLogin, -1, Logger.TraceLevel.ERROR) ;
			return null ;
		}
		
		PersonManager personManager = new PersonManager() ;
		personManager.initFromPseudoAndPassword(sLogin, sPassword) ;
		
		if ((false == sLogin.equals(personManager.getPseudo())) || 
				(false == sPassword.equals(personManager.getPassword())))
		{
			Logger.trace(sFunctionName + ": wrong user found for pseudo " + sLogin + " and pass " + sPassword, -1, Logger.TraceLevel.ERROR) ;
			return null ;
		}
				
		return new Person(personManager.getPerson()) ;
	}
	
	@Override
	public void rollback(final SendLoginAction action,
        							 final SendLoginResult result,
                       final ExecutionContext context) throws ActionException
  {
		// Nothing to do here
  }
 
	@Override
	public Class<SendLoginAction> getActionType()
	{
		return SendLoginAction.class;
	}
}
