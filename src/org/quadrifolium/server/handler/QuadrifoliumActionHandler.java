package org.quadrifolium.server.handler;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.Action;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.Result;

import org.quadrifolium.server.Logger;
import org.quadrifolium.server.QuadrifoliumClientSocket;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class QuadrifoliumActionHandler<A extends Action<R>,R extends Result> implements ActionHandler<A, R>
{
	protected final Logger                       _logger ;
	protected final Provider<ServletContext>     _servletContext ;
	protected final Provider<HttpServletRequest> _servletRequest ;
	
	protected String _sServerAnswer ;
	protected String _sServerRequest ;

	/**
	  * Constructor dedicated to unit tests 
	  */
	public QuadrifoliumActionHandler()
	{
		_logger         = null ;
		_servletContext = null ;
		_servletRequest = null ;
	}
	
	@Inject
	public QuadrifoliumActionHandler(final Logger logger,
                                   final Provider<ServletContext> servletContext,       
                                   final Provider<HttpServletRequest> servletRequest)
	{
		_logger = logger ;
		_servletContext = servletContext ;
		_servletRequest = servletRequest ;
	}

	public void CallServer() throws ActionException 
  {
		if (_sServerRequest.equals(""))
			return ;
		
		try 
		{
			// String serverInfo = servletContext.get().getServerInfo();
   		// String userAgent = servletRequest.get().getHeader("User-Agent");
   
   		QuadrifoliumClientSocket clientSocket = new QuadrifoliumClientSocket() ;
			
			_sServerAnswer = clientSocket.runSocket(_sServerRequest) ;
			
			// final String message = "Hello, " + name + "!<br><br>I am running " + serverInfo
			// + ".<br><br>It looks like you are using:<br>" + userAgent + " Status : " + clientSocket.get_sStatus() + sServerAnswer ;

   		//final String message = "Hello " + action.getName(); 
   
		}
		catch (Exception cause) 
		{
			Logger.trace("CallServer: Exception " + cause.getMessage(), -1, Logger.TraceLevel.ERROR) ;
   
			throw new ActionException(cause) ;
		}
  }
 
	public String getInfoString(String sTagName)
	{
		if ((null == _sServerAnswer) || (_sServerAnswer.equals("")))
			return "" ;
		
		String sStartTag = "<" + sTagName + ">" ;	
		int iStartPosition = _sServerAnswer.indexOf(sStartTag) ;
		if (-1 == iStartPosition)
			return "" ;
		
		String sEndTag = "</" + sTagName + ">" ;
		int iEndPosition = _sServerAnswer.indexOf(sEndTag) ;
		if (-1 == iEndPosition)
			return "" ;
		
		return _sServerAnswer.substring(iStartPosition + sStartTag.length(), iEndPosition) ; 
	}
	
	public boolean isSuccess()
	{
		String sResult = getInfoString("result") ;
		return sResult.equals("Ok") ;
	}

	@Override
  public R execute(A arg0, ExecutionContext arg1) throws ActionException
  {
	  // TODO Auto-generated method stub
	  return null;
  }

	@Override
  public void rollback(A arg0, R arg1, ExecutionContext arg2)
      throws ActionException
  {
	  // TODO Auto-generated method stub
	}

	@Override
  public Class<A> getActionType()
  {
	  // TODO Auto-generated method stub
	  return null;
  }
}
