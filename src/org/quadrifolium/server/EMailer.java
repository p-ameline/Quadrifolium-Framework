package org.quadrifolium.server;

import java.util.*;
import java.io.*;

import javax.mail.*;
import javax.mail.internet.*;

import com.ldv.server.Logger;

public class EMailer 
{
	//PRIVATE //

	private static String     _sPropertiesPath ;
  private static Properties _fMailServerConfig = new Properties() ;
  private static boolean    _bPropertiesInitialized ;
  
/*
  static {
    fetchConfig();
  }
*/
	
/*
	public static void main( String... aArguments ){
    Emailer emailer = new Emailer();
    //the domains of these email addresses should be valid,
    //or the example will fail:
    emailer.sendEmail(
      "fromblah@blah.com", "toblah@blah.com",
       "Testing 1-2-3", "blah blah blah"
    );
   }
*/

  public EMailer(String sPropertiesPath)
  {
  	_sPropertiesPath        = sPropertiesPath ;
  	_bPropertiesInitialized = fetchConfig() ;
  }
  
  /**
  * Send a single email.
  */
  public boolean sendEmail(String aFromEmailAddr, String aToEmailAddr, String aSubject, String aBody)
  {
  	Logger.trace("Entering EMailer.sendEmail: from= " + aFromEmailAddr + " to= " + aToEmailAddr + " subject= " + aSubject, -1, Logger.TraceLevel.DETAIL) ;
  	
  	if (false == _bPropertiesInitialized)
  	{
  		Logger.trace("EMailer.sendEmail: properties not initialized, leaving", -1, Logger.TraceLevel.WARNING) ;
  		return false ;
  	}
  	
    // Here, no Authenticator argument is used (it is null).
    // Authenticators are used to prompt the user for user
    // name and password.
  	//
    Session session = Session.getDefaultInstance(_fMailServerConfig, null) ;
    MimeMessage message = new MimeMessage(session) ;
    try 
    {
      //the "from" address may be set in code, or set in the
      //config file under "mail.from" ; here, the latter style is used
    	if (false == aFromEmailAddr.equals(""))
    		message.setFrom( new InternetAddress(aFromEmailAddr) );
    	
      // message.addRecipient(Message.RecipientType.TO, new InternetAddress(aToEmailAddr)) ;
      message.setRecipient(Message.RecipientType.TO, new InternetAddress(aToEmailAddr)) ;
      message.setSubject(aSubject) ;
      message.setText(aBody) ;
    }
    catch (MessagingException ex)
    {
    	Logger.trace("EMailer.sendEmail: cannot build message for " + aToEmailAddr + " " + ex.getMessage(), -1, Logger.TraceLevel.ERROR) ;
      // System.err.println("Cannot send email. " + ex);
      return false ;
    }
    
    try 
    {
    	Transport.send(message) ;
    }
    catch (MessagingException ex)
    {
    	MessagingException nested = ex ;
      while (null != nested) 
      {
      	Logger.trace("EMailer.sendEmail: cannot send mail to " + aToEmailAddr + " " + nested.getMessage(), -1, Logger.TraceLevel.WARNING) ;
      	
      	if (ex instanceof SendFailedException) 
      	{
      		SendFailedException sfex = (SendFailedException) nested ;
      		
          Address[] invalid = sfex.getInvalidAddresses() ;
          if (null != invalid) 
          {
          	String sError = "EMailer.sendEmail: Invalid Addresses" ;
            for (int i = 0; i < invalid.length; i++)
            	sError += " " + invalid[i] ;
            Logger.trace(sError, -1, Logger.TraceLevel.WARNING) ;
          }
          Address[] validUnsent = sfex.getValidUnsentAddresses();
          if (null != validUnsent) 
          {
          	String sError = "EMailer.sendEmail: ValidUnsent Addresses" ;
          	for (int i = 0; i < validUnsent.length; i++)
            	sError += " " + validUnsent[i] ;
          	Logger.trace(sError, -1, Logger.TraceLevel.WARNING) ;
          }
          Address[] validSent = sfex.getValidSentAddresses();
          if (null != validSent) 
          {
          	String sError = "EMailer.sendEmail: ValidSent Addresses" ;
          	for (int i = 0; i < validSent.length; i++)
            	sError += " " + validSent[i] ;
          	Logger.trace(sError, -1, Logger.TraceLevel.WARNING) ;
          }
      	}
      	nested = (MessagingException) nested.getNextException();
      }
    	
      return false ;
    }
    
    Logger.trace("Leaving EMailer.sendEmail: from= " + aFromEmailAddr + " to= " + aToEmailAddr + " subject= " + aSubject, -1, Logger.TraceLevel.DETAIL) ;
    return true ;
  }

  /**
  * Allows the config to be refreshed at runtime, instead of
  * requiring a restart.
  */
  public static void refreshConfig() 
  {
    _fMailServerConfig.clear() ;
    fetchConfig() ;
  }

  /**
  * Open a specific text file containing mail server
  * parameters, and populate a corresponding Properties object.
  */
  private static boolean fetchConfig() 
  {
  	Logger.trace("Entering EMailer.fetchConfig", -1, Logger.TraceLevel.SUBDETAIL) ;
  	
  	boolean     bSuccess = false ;
  	InputStream input    = null ;
    try {
      //If possible, one should try to avoid hard-coding a path in this
      //manner; in a web application, one should place such a file in
      //WEB-INF, and access it using ServletContext.getResourceAsStream.
      //Another alternative is Class.getResourceAsStream.
      //This file contains the javax.mail config properties mentioned above.
      // input = new FileInputStream( "C:\\Temp\\MyMailServer.txt" );
    	// input = context.getResourceAsStream("/yourfilename.cnf");
    	
    	input = new FileInputStream(_sPropertiesPath + "/WEB-INF/MailServer.properties") ;
    	if (null != input)
    	{
    		_fMailServerConfig.load(input) ;
    		bSuccess = true ;
    	}
    }
    catch ( IOException ex )
    {
    	Logger.trace("EMailer.fetchConfig, cannot read MailServer.properties: " + ex.getMessage(), -1, Logger.TraceLevel.ERROR) ;
      // System.err.println("Cannot open and load mail server properties file.") ;
      bSuccess = false ;
    }
    finally {
      try {
        if (input != null) 
        	input.close() ;
      }
      catch (IOException ex) {
      	Logger.trace("EMailer.fetchConfig, cannot close MailServer.properties: " + ex.getMessage(), -1, Logger.TraceLevel.ERROR) ;
        // System.err.println( "Cannot close mail server properties file." );
      }
    }
    return bSuccess ;
  }
  
  public boolean arePropertiesInilialized() {
  	return _bPropertiesInitialized ; 
  }
}
