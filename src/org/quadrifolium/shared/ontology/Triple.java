package org.quadrifolium.shared.ontology ;

import com.google.gwt.user.client.rpc.IsSerializable ;

/**
 * Triple.java
 *
 * The Triple class represents a subject, predicate, object triple as recorded in the triple table
 * 
 * Author: PA
 * 
 */
public class Triple implements IsSerializable 
{
	protected int    _iId ;
	protected String _sSubject ;
	protected String _sPredicate ;
	protected String _sObject ;
	
	/**
	 * Null constructor
	 */
	public Triple() {
		reset() ;
	}
		
	/**
	 * Plain vanilla constructor
	 */
	public Triple(int iId, final String sSubject, final String sPredicate, final String sObject) 
	{
		reset() ;
		
		_iId        = iId ;
		_sSubject   = sSubject ;
		_sPredicate = sPredicate ;
		_sObject    = sObject ;
	}
	
	/**
	 * Copy constructor
	 */
	public Triple(final Triple model) {
		initFromModel(model) ;
	}
	
	/**
	 * Initialize from an object of the kind 
	 */
	public void initFromModel(final Triple model)
	{
		reset() ;
		
		if (null == model)
			return ;
		
		_iId        = model._iId ;
		_sSubject   = model._sSubject ;
		_sPredicate = model._sPredicate ;
		_sObject    = model._sObject ;
	}

	/**
	 * Set all information to void
	 */
	public void reset() 
	{
		_iId        = -1 ;
		_sSubject   = "" ;
		_sPredicate = "" ;
		_sObject    = "" ;
	}

	/**
	 * Is this flex in the database?
	 * 
	 * @return <code>true</code> if this flex has a database ID, <code>false</code> if not 
	 */
	public boolean isReferenced() {
		return _iId >= 0 ; 
	}
	
	// getter and setter
	//
	public int getId() {
		return _iId ;
	}
	public void setId(final int id) {
		_iId = id ;
	}

	public String getSubject() {
		return _sSubject ;
	}
	public void setSubject(final String sSubject) {
		_sSubject = sSubject ;
	} 

	public String getPredicate() {
		return _sPredicate ;
	}
	public void setPredicate(final String sPredicate) {
		_sPredicate = sPredicate ;
	}

	public String getObject() {
		return _sObject ;
	}
	public void setObject(final String sObject) {
		_sObject = sObject ;
	}
}
