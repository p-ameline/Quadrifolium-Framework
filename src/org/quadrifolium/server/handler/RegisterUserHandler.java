package org.quadrifolium.server.handler;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;

import org.quadrifolium.server.DbParameters;
import org.quadrifolium.server.EMailer;
import org.quadrifolium.server.model.PersonManager;
import org.quadrifolium.shared.rpc.RegisterUserAction;
import org.quadrifolium.shared.rpc.RegisterUserResult;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ldv.server.DBConnector;
import com.ldv.server.Logger;
import com.ldv.server.handler.LdvActionHandler;

public class RegisterUserHandler extends LdvActionHandler<RegisterUserAction, RegisterUserResult>
{
	@Inject
	public RegisterUserHandler(final Provider<ServletContext> servletContext,       
                             final Provider<HttpServletRequest> servletRequest)
	{
		super(servletContext, servletRequest) ;
	}

	/**
	  * Constructor dedicated to unit tests 
	  */
	public RegisterUserHandler()
	{
		super() ;
	}
	
	@Override
	public RegisterUserResult execute(final RegisterUserAction action,
       					                    final ExecutionContext context) throws ActionException 
  {	
		try 
		{			
			DBConnector dbConnector = new DBConnector(true, -1, DBConnector.databaseType.databaseCore) ;
			
			// Record information in the clients table
			//
			int iClientId = insertPerson(action, dbConnector) ;
			if (-1 == iClientId)
				return new RegisterUserResult(false) ;
			
			boolean bMailSuccess = sendValidationMail(action) ;
			if (false == bMailSuccess)
			{
				// TODO remove client
				return new RegisterUserResult(false) ;
			}
			
			return new RegisterUserResult(true) ;
		}
		catch (Exception cause) 
		{
			Logger.trace("RegisterUserHandler: Exception " + cause.getMessage(), -1, Logger.TraceLevel.ERROR) ;   
			throw new ActionException(cause);
		}
  }

	/**
	  * Add information in clients database and returns the corresponding client Id
	  */
	private int insertPerson(final RegisterUserAction action, DBConnector dbConnector)
	{
		if ((null == action) || (null == dbConnector))
			return -1 ;
		
		PersonManager personManager = new PersonManager() ;
		personManager.setPseudo(action.getPseudo());
		personManager.setPassword(action.getPassword());
		personManager.setLanguage(action.getLanguage());
		
		return personManager.createNewPerson(dbConnector) ;
	}
	
	/**
	  * Send the registering validation mail
	  */
	public boolean sendValidationMail(RegisterUserAction action)
	{
		if (null == action)
			return false ;
		
		String sToAddr   = action.getEmail() ;
		String sLanguage = action.getLanguage() ; 
		
		Logger.trace("Entering RegisterUserHandler.sendValidationMail: mail= " + sToAddr + " language= " + sLanguage, -1, Logger.TraceLevel.DETAIL) ;
		
		String sRealPath = _servletContext.get().getRealPath("") ;
		
		// Get message title
		//
		String sMailTitle = getMailTitle(sRealPath, sLanguage) ;
		
		// Get message body and insert random string in it
		//
		String sMailBody  = getMailBody(sRealPath, sLanguage) ;
		
		EMailer mailer = new EMailer(sRealPath) ;
		boolean bMailSent = mailer.sendEmail("", sToAddr, sMailTitle, sMailBody) ;
		
		return bMailSent ;
	}
	
	/**
	  * Get registering validation mail's title from file
	  */
	protected String getMailTitle(String sRealPath, String sLanguage)
	{
		return getLanguageDependentFile("registerHeader", sRealPath, sLanguage) ;
	}
	
	/**
	  * Get registering validation mail's body from file
	  */
	protected String getMailBody(String sRealPath, String sLanguage)
	{
		return getLanguageDependentFile("registerBody", sRealPath, sLanguage) ;
	}
	
	/**
	  * Get the content of a language dependent message from file
	  */
	protected String getLanguageDependentFile(String sPrefix, String sRealPath, String sLanguage)
	{
		String sFullFileName = sRealPath + DbParameters.getDirSeparator() + "WEB-INF" + DbParameters.getDirSeparator() + sPrefix ;
		if (false == sLanguage.equals(""))
			sFullFileName += "_" + sLanguage ;
		sFullFileName += ".txt" ;
			
		String sResult = "" ;
		
		InputStream input = null ;
    try 
    {
    	input = new FileInputStream(sFullFileName) ;
    	if (null != input)
    	{
        String NL = System.getProperty("line.separator") ;
        Scanner scanner = new Scanner(input, "UTF-8") ;
        try {
          while (scanner.hasNextLine()){
          	sResult += scanner.nextLine() + NL ;
          }
        }
        finally{
          scanner.close();
        }
    	}
    }
    catch ( IOException ex ){
      System.err.println("Cannot open and load " + sPrefix + " file for language " + sLanguage) ;
    }
    finally {
      try {
        if (input != null) 
        	input.close() ;
      }
      catch ( IOException ex ){
        System.err.println( "Cannot close " + sPrefix + " file for language " + sLanguage) ;
      }
    }
    return sResult ;
	}
	
	@Override
	public void rollback(final RegisterUserAction action,
        							 final RegisterUserResult result,
        final ExecutionContext context) throws ActionException
  {
		// Nothing to do here
  }
 
	@Override
	public Class<RegisterUserAction> getActionType()
	{
		return RegisterUserAction.class ;
	}
}
