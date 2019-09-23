package org.quadrifolium.server.database ;

import com.google.gwt.user.client.rpc.IsSerializable ;

/**
 * Person.java
 *
 * The Person class represents object type "sessions" in database
 * 
 * Created: 13 Apr 2019
 *
 * Author: PA
 * 
 */
public class Session implements IsSerializable 
{
	private int    _iSessionId ;
	private int    _iPersonId ;
	private String _sToken ;
	private String _sDateTimeOpen ;
	private String _sDateTimeClose ;
	private String _sDateTimeLastBeat ;
	
	//
	//
	public Session() {
		reset() ;
	}
		
	/**
	 * Default constructor
	 */
	public Session(final int iSessionId, final int iPersonId, final String sToken, final String sDateTimeOpen, final String sDateTimeClose, final String sDateTimeLastBeat) 
	{
		_iSessionId        = iSessionId ;
		_iPersonId         = iPersonId ;
		_sToken            = sToken ;
		_sDateTimeOpen     = sDateTimeOpen ;
		_sDateTimeClose    = sDateTimeClose ;
		_sDateTimeLastBeat = sDateTimeLastBeat ;
	}
	
	/**
	 * Initialize a session from a model session
	 * @param session Model session
	 */
	public void initFromPerson(final Session session)
	{
		reset() ;
		
		if (null == session)
			return ;
		
		_iSessionId        = session._iSessionId ;
		_iPersonId         = session._iPersonId ;
		_sToken            = session._sToken ;
		_sDateTimeOpen     = session._sDateTimeOpen ;
		_sDateTimeClose    = session._sDateTimeClose ;
		_sDateTimeLastBeat = session._sDateTimeLastBeat ;
	}

	/**
	 * Set all information to void
	 */
	public void reset() 
	{
		_iSessionId        = -1 ;
		_iPersonId         = -1 ;
		_sToken            = "" ;
		_sDateTimeOpen     = "" ;
		_sDateTimeClose    = "" ;
		_sDateTimeLastBeat = "" ;
	}
			
	/**
	 * Is this session in the database?
	 * 
	 * @return <code>true</code> if this session has a database ID, <code>false</code> if not 
	 */
	public boolean isReferenced() {
		return _iSessionId >= 0 ; 
	}
	
	// getter and setter
	//
	public int getSessionId() {
		return _iSessionId ;
	}
	public void setSessionId(final int id) {
		_iSessionId = id ;
	}

	public int getPersonId() {
		return _iPersonId ;
	}
	public void setPersonId(final int iPersonId) {
		_iPersonId = iPersonId ;
	}

	public String getToken() {
		return _sToken ;
	}
	public void setToken(final String sToken) {
		_sToken = sToken ;
	}

	public String getDateTimeOpen() {
		return _sDateTimeOpen ;
	}
	public void setDateTimeOpen(final String sDateTimeOpen) {
		_sDateTimeOpen = sDateTimeOpen ;
	}
	
	public String getDateTimeClose() {
		return _sDateTimeClose ;
	}
	public void setDateTimeClose(final String sDateTimeClose) {
		_sDateTimeClose = sDateTimeClose ;
	}
	
	public String getDateTimeLastBeat() {
		return _sDateTimeLastBeat ;
	}
	public void setDateTimeLastBeat(final String sDateTimeLastBeat) {
		_sDateTimeLastBeat = sDateTimeLastBeat ;
	}
}
