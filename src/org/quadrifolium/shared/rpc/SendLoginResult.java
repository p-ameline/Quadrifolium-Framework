package org.quadrifolium.shared.rpc;

import org.quadrifolium.shared.database.Person;
import org.quadrifolium.shared.rpc_util.SessionElements;

import net.customware.gwt.dispatch.shared.Result;

public class SendLoginResult implements Result 
{
	private SessionElements _sessionElements ;
	
	private Person _user ;
	
	private String _sMessage ;
	private String _sVersion ;

	/**
	 * Plain vanilla constructor
	 */
	public SendLoginResult(final SessionElements sessionElements, final String sMessage, final Person user, final String sVersion) 
	{
		super() ;
		
		_sessionElements = sessionElements ;
		_user            = new Person(user) ;
		_sMessage        = sMessage ;
		_sVersion        = sVersion ;
	}

	/**
	 * Empty constructor
	 */
	@SuppressWarnings("unused")
	private SendLoginResult() 
	{
		super() ;
		
		_sessionElements = null ;
		_user            = new Person() ;
		_sMessage        = "" ;
		_sVersion        = "" ;
	}

	public SessionElements getSessionElements() {
		return _sessionElements ;
	}

	public Person getUser() {
		return _user ;
	}
	
	public String getMessage() {
		return _sMessage ;
	}
	
	public String getVersion() {
		return _sVersion ;
	}
}
