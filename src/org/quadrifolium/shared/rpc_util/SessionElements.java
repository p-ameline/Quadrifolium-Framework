package org.quadrifolium.shared.rpc_util;

import java.io.Serializable;

/**
 * Object used to transmit session validation elements from client to server
 * 
 * @author Philippe
 *
 */
public class SessionElements implements Serializable
{
	private static final long serialVersionUID = -7683574606211193198L ;
	
	private int    _iPersonId ;
	private int    _iSessionId ;
	private String _sToken ;

	/**
	 * Plain vanilla constructor 
	 */
	public SessionElements(final int iPersonId, final int iSessionId, final String sToken) 
	{
		_iPersonId  = iPersonId ;
		_iSessionId = iSessionId ;
		_sToken     = sToken ;
	}

	/**
	 * Void constructor
	 */
  public SessionElements() 
	{
  	_iPersonId  = -1 ;
  	_iSessionId = -1 ;
  	_sToken     = "" ;
	}

	public int getPersonId() {
		return _iPersonId ;
	}
	
	public int getSessionId() {
		return _iSessionId ;
	}
	
	public String getToken() {
		return _sToken ;
	}	
}
