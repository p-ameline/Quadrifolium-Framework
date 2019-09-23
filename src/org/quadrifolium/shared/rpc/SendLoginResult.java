package org.quadrifolium.shared.rpc;

import org.quadrifolium.shared.database.Person;

import net.customware.gwt.dispatch.shared.Result;

public class SendLoginResult implements Result 
{
	private String _sSessionToken ;
	private Person _user ;
	private String _sMessage ;
	private String _sVersion ;

	/**
	 * Plain vanilla constructor
	 */
	public SendLoginResult(final String sSessionToken, final String sMessage, final Person user, final String sVersion) 
	{
		super() ;
		
		_sSessionToken = sSessionToken ;
		_user          = new Person(user) ;
		_sMessage      = sMessage ;
		_sVersion      = sVersion ;
	}

	/**
	 * Empty constructor
	 */
	@SuppressWarnings("unused")
	private SendLoginResult() 
	{
		super() ;
		
		_sSessionToken = "" ;
		_user          = new Person() ;
		_sMessage      = "" ;
		_sVersion      = "" ;
	}

	public String getSessionToken() {
		return _sSessionToken ;
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
