package org.quadrifolium.shared.database ;

import com.google.gwt.user.client.rpc.IsSerializable ;

/**
 * Person.java
 *
 * The Person class represents object type user in database
 * 
 * Created: 19 Jul 2011
 *
 * Author: PA
 * 
 */
public class Person implements IsSerializable 
{
	public enum USERTYPE { unknown, visitor, editor, administrator, unactive } ;
	
	private int      _iPersonId ;
	private String   _sPseudo ;
	private String   _sPassword ;
	private String   _sLanguage ;
	private USERTYPE _iUserType ;
	private String   _sBio ;
	
	//
	//
	public Person() {
		reset() ;
	}
		
	public Person(int iPersonId, final String sPseudo, final String sPassword, final String sLanguage, final String sBio, final USERTYPE iUserType) 
	{
		reset() ;
		
		_iPersonId = iPersonId ;
		_sPseudo   = sPseudo ;
		_sPassword = sPassword ;
		_sLanguage = sLanguage ;
		_sBio      = sBio ;
		_iUserType = iUserType ;
	}
	
	public Person(final Person modelPerson) {
		initFromPerson(modelPerson) ;
	}
	
	public void initFromPerson(final Person person)
	{
		reset() ;
		
		if (null == person)
			return ;
		
		_iPersonId        = person._iPersonId ;
		_sPseudo          = person._sPseudo ;
		_sPassword        = person._sPassword ;
		_sLanguage        = person._sLanguage ;
		_sBio             = person._sBio ;
		_iUserType        = person._iUserType ;
	}
			
	public void reset() 
	{
		_iPersonId        = -1 ;
		_sPseudo          = "" ;
		_sPassword        = "" ;
		_sLanguage        = "" ;
		_sBio             = "" ;
		_iUserType        = USERTYPE.unknown ;
	}

	/**
	 * Is this person in the database?
	 * 
	 * @return <code>true</code> if this person has a database ID, <code>false</code> if not 
	 */
	public boolean isReferenced() {
		return _iPersonId >= 0 ; 
	}
	
	// getter and setter
	//
	public int getPersonId() {
		return _iPersonId ;
	}
	public void setPersonId(final int id) {
		_iPersonId = id ;
	}

	public String getPseudo() {
		return _sPseudo ;
	}
	public void setPseudo(final String sPseudo) {
		_sPseudo = sPseudo ;
	} 

	public String getPassword() {
		return _sPassword ;
	}
	public void setPassword(final String sPassword) {
		_sPassword = sPassword ;
	}

	public String getLanguage() {
		return _sLanguage ;
	}
	public void setLanguage(final String sLanguage) {
		_sLanguage = sLanguage ;
	}
	
	public String getBio() {
		return _sBio ;
	}
	public void setBio(final String sBio) {
		_sBio = sBio ;
	}
	
	public USERTYPE getUserType() {
		return _iUserType ;
	}
	public void setUserType(final USERTYPE iUserType) {
		_iUserType = iUserType ;
	}	
}
