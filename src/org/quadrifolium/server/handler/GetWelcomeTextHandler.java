package org.quadrifolium.server.handler;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;

import org.apache.commons.io.FileUtils;

import org.quadrifolium.server.Logger;
import org.quadrifolium.shared.rpc.GetWelcomeTextAction;
import org.quadrifolium.shared.rpc.GetWelcomeTextResult;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class GetWelcomeTextHandler extends QuadrifoliumActionHandler<GetWelcomeTextAction, GetWelcomeTextResult>
{
	@Inject
	public GetWelcomeTextHandler(final Logger logger,
                               final Provider<ServletContext> servletContext,       
                               final Provider<HttpServletRequest> servletRequest)
	{
		super(logger, servletContext, servletRequest) ;
	}
	
	/**
	  * Constructor dedicated to unit tests 
	  */
	public GetWelcomeTextHandler()
	{
		super() ;
	}

	@Override
	public GetWelcomeTextResult execute(final GetWelcomeTextAction action,
       					                      final ExecutionContext context) throws ActionException 
  {
		String sLanguage = action.getLanguage() ;
		
		String sRequestLanguage = sLanguage ; 
		
		try 
		{			
			Logger.trace("GetWelcomeTextHandler.execute: entering (language = \"" + sLanguage + "\").", -1, Logger.TraceLevel.DETAIL) ;
			
			String sRealPath         = _servletContext.get().getRealPath("") ;
			
			String sAcceptedLanguage = "" ;
			String sNoRegionLanguage = "" ;
			
			// If no language specified, get user accepted locale from the HTTP Accept-Language header
			//
			// Take care that if the client request doesn't provide an Accept-Language header, this method returns 
			// the default locale for the server.
			//
			if ("".equals(sLanguage))
			{
				Locale locale = _servletRequest.get().getLocale() ;
				
				if (null != locale)
				{
					sNoRegionLanguage = locale.getLanguage() ;
					if ((null != sNoRegionLanguage) && (false == "".equals(sNoRegionLanguage)))
					{
						String sRegion = locale.getCountry() ;
						if ((null != sRegion) && (false == "".equals(sRegion)))
							sAcceptedLanguage = sNoRegionLanguage + "-" + sRegion ;
					}
				}
				
				if (false == "".equals(sAcceptedLanguage))
					sRequestLanguage = sAcceptedLanguage ;
				else
				{
					sRequestLanguage  = sNoRegionLanguage ;
					sAcceptedLanguage = sNoRegionLanguage ;
				}
				
				Logger.trace("GetWelcomeTextHandler.execute: detected user language = \"" + sAcceptedLanguage + "\".", -1, Logger.TraceLevel.DETAIL) ;
			}
			
			// Check user's language 
			//
			if (false == "".equals(sRequestLanguage))
			{
				String sFileContent = getWelcomeTextForLanguage(sRealPath, sRequestLanguage) ;
			
				if (false == "".equals(sFileContent))
					return new GetWelcomeTextResult(sFileContent, sRequestLanguage, sAcceptedLanguage, sLanguage)  ;
				
				// Check if a file exists for user's language, but not region specific
				//
				if (false == sRequestLanguage.equals(sNoRegionLanguage))
				{
					sFileContent = getWelcomeTextForLanguage(sRealPath, sNoRegionLanguage) ;
					
					if (false == "".equals(sFileContent))
						return new GetWelcomeTextResult(sFileContent, sNoRegionLanguage, sAcceptedLanguage, sLanguage)  ;
				}
			}
			
			// Check the English version as default
			//
			String sFileContent = getWelcomeTextForLanguage(sRealPath, "en") ;
			
			return new GetWelcomeTextResult(sFileContent, "en", "", sLanguage)  ;
		}
		catch (Exception cause) 
		{
			Logger.trace("GetWelcomeTextHandler.execute: exception ; cause: " + cause.getMessage(), -1, Logger.TraceLevel.ERROR) ;
			throw new ActionException(cause) ;
		}
  }
	
	protected String getWelcomeTextForLanguage(final String sBasePath, final String sLanguage)
	{
		String sFullName = sBasePath + "/WEB-INF/Welcome_" + sLanguage + ".txt" ;
		 
		File file = new File(sFullName) ;
		
		String sFileContent = "" ;
		
		try 
		{
      if (file.exists() && file.canRead())
      	sFileContent = FileUtils.readFileToString(file) ;
    }
    catch (IOException e) {
    	Logger.trace("GetWelcomeTextHandler.execute: no text found for language " + sLanguage, -1, Logger.TraceLevel.ERROR) ;
    	return "" ;
    }
		
		return sFileContent ;
	}
	
	@Override
	public void rollback(final GetWelcomeTextAction action,
        							 final GetWelcomeTextResult result,
        final ExecutionContext context) throws ActionException
  {
		// Nothing to do here
  }
 
	@Override
	public Class<GetWelcomeTextAction> getActionType() {
		return GetWelcomeTextAction.class ;
	}
}
