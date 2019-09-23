package org.quadrifolium.server;

import java.io.*;
import java.net.*;

public class QuadrifoliumClientSocket
{
	Socket             _requestSocket ;
	PrintWriter        _out ;
	BufferedReader     _in ;
 	
 	String             _sHost = "127.0.0.1" ;
 	int                _iPort = 54321 ;
 	
 	String             _sCommand ;
 	String             _sAnswer ;
 	
 	private String     _sStatus ;

	public QuadrifoliumClientSocket()
 	{
 		_sCommand = "" ;
 		_sAnswer  = "" ;
 	}
 	
 	QuadrifoliumClientSocket(String sCommand)
 	{
 		_sCommand = sCommand ;
 		_sAnswer  = "" ;
 	}
 	
 	public String runSocket(String sCommand)
	{
 		_sCommand = sCommand ;
 		
 		return runSocket() ;
	}
 	
	String runSocket()
	{
		_sAnswer  = "" ;
		
		if (_sCommand.equals(""))
			return _sAnswer ;
		
		// Open communication components
		//
		if (false == openSocket())
		{
			set_sStatus("Impossible d'ouvrir la socket : " + get_sStatus());
			return _sAnswer ;
		}
		
		sendCommand() ;
		
		getServerAnswer() ;
		
		closeSocket() ;
		
		return _sAnswer ;
	}
	
	void sendCommand()
	{
		sendMessage(_sCommand) ;
	}
	
	void sendMessage(String msg)
	{
		_out.println(msg) ;
	}
	
	private void getServerAnswer()
	{
		_sAnswer = "" ;
		
		Timer timer = new Timer(3000) ;
		timer.start() ;
		
		try
		{		
			while (true)
			{			
				String serverInput = (String)_in.readLine() ;
        				
				// Reset timer - timeout can occur on connect
				timer.reset() ;
				
				if (serverInput.equals("bye"))
					break ;
					
				_sAnswer += serverInput ;
			}
		}
		catch (IOException e)
    {
			e.printStackTrace() ;
      System.err.println("Data received from server in unknown format") ;
		}
		finally
		{
			timer.stop() ;
		}
	}

	private boolean openSocket()
	{
		try
		{
			// 1. creating a socket to connect to the server
			//
			_requestSocket = new Socket(_sHost, _iPort) ;
			
			System.out.println("Socket connected") ;
		}
		catch (UnknownHostException e) 
		{
      System.err.println("Socket error: Don't know about host.") ;
      set_sStatus("Socket error: Don't know about host.");
      return false ;
		} 
		catch (IOException e) 
		{
      System.err.println("Socket error: Couldn't get I/O for the connection") ;
      set_sStatus("Socket error: Couldn't get I/O for the connection.");
      return false ;
		}

		try
		{
			// 2. get Input and Output streams
			//
			_out = new PrintWriter(_requestSocket.getOutputStream(), true) ;
			_out.flush() ;
			_in  = new BufferedReader(new InputStreamReader(_requestSocket.getInputStream())) ;
		}
		catch (UnknownHostException e) 
		{
      System.err.println("Socket error: Cannot open streams.") ;
      set_sStatus("Socket error: Cannot open streams.");
      return false ;
		} 
		catch (IOException e) 
		{
      System.err.println("Socket error: Couldn't get I/O for the streams") ;
      set_sStatus("Socket error: Couldn't get I/O for the streams.");
      return false ;
		}
		
		return true ;
	}
	
	private void closeSocket()
	{
		try
		{
			_in.close() ;
			_out.close() ;
			_requestSocket.close() ;
		}
		catch(IOException ioException)
		{
			ioException.printStackTrace() ;
		}			
	}
	
	public String getCommand()
  {
  	return _sCommand ;
  }

	public void setCommand(String sCommand)
  {
  	_sCommand = sCommand ;
  }
	
	public String getAnswer()
  {
  	return _sAnswer ;
  }

	public void setAnswer(String sAnswer)
  {
  	_sAnswer = sAnswer ;
  }

	public void set_sStatus(String _sStatus)
  {
	  this._sStatus = _sStatus;
  }

	public String get_sStatus()
  {
	  return _sStatus;
  }
}
