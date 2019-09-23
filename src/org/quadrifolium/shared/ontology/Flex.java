package org.quadrifolium.shared.ontology ;

import com.google.gwt.user.client.rpc.IsSerializable ;

/**
 * Flex.java
 *
 * The Flex class represents a specific inflection of a lemma
 * 
 * Author: PA
 * 
 */
public class Flex implements IsSerializable, Comparable<Flex>
{
	protected int    _iId ;
	protected String _sLabel ;
	protected String _sCode ;
	protected String _sLanguage ;
	
	/**
	 * Null constructor
	 */
	public Flex() {
		reset() ;
	}
		
	/**
	 * Plain vanilla constructor
	 */
	public Flex(int iId, final String sLabel, final String sCode, final String sLanguage) 
	{
		reset() ;
		
		_iId       = iId ;
		_sLabel    = sLabel ;
		_sCode     = sCode ;
		_sLanguage = sLanguage ;
	}
	
	/**
	 * Copy constructor
	 */
	public Flex(final Flex model) {
		initFromFlex(model) ;
	}
	
	/**
	 * Initialize from an object of the kind 
	 */
	public void initFromFlex(final Flex model)
	{
		reset() ;
		
		if (null == model)
			return ;
		
		_iId       = model._iId ;
		_sLabel    = model._sLabel ;
		_sCode     = model._sCode ;
		_sLanguage = model._sLanguage ;
	}

	/**
	 * Set all information to void
	 */
	public void reset() 
	{
		_iId       = -1 ;
		_sLabel    = "" ;
		_sCode     = "" ;
		_sLanguage = "" ;
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

	public String getLabel() {
		return _sLabel ;
	}
	public void setLabel(final String sLabel) {
		_sLabel = sLabel ;
	} 

	public String getCode() {
		return _sCode ;
	}
	public void setCode(final String sCode) {
		_sCode = sCode ;
	}

	public String getLanguage() {
		return _sLanguage ;
	}
	public void setLanguage(final String sLanguage) {
		_sLanguage = sLanguage ;
	}
	
	/**
	 * Sorting is based on code
	 */
	@Override
	public int compareTo(Flex other)
	{
		if (null == other)
			return 1 ;
		
		return this.getCode().compareTo(other.getCode()) ;
	}
}
