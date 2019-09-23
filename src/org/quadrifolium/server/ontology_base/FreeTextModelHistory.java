package org.quadrifolium.server.ontology_base ;

/**
 * Lemma.java
 *
 * The Flex class represents a specific flexed variation of a lemma
 * 
 * Author: PA
 * 
 */
public class FreeTextModelHistory
{
	private int    _iId ;
	private int    _iFreeTextId ;
	private String _sLabel ;
	private String _sCode ;
	private String _sLanguage ;
	private String _sNext ;
	
	/**
	 * Null constructor
	 */
	public FreeTextModelHistory() {
		reset() ;
	}
		
	/**
	 * Plain vanilla constructor
	 */
	public FreeTextModelHistory(int iId, int iFreeTextId, final String sLabel, final String sCode, final String sLanguage, final String sNext) 
	{
		reset() ;
		
		_iId         = iId ;
		_iFreeTextId = iFreeTextId ;
		_sLabel      = sLabel ;
		_sCode       = sCode ;
		_sLanguage   = sLanguage ;
		_sNext       = sNext ;
	}
	
	/**
	 * Copy constructor
	 */
	public FreeTextModelHistory(final FreeTextModelHistory model) {
		initFromFlex(model) ;
	}
	
	/**
	 * Initialize from an object of the kind 
	 */
	public void initFromFlex(final FreeTextModelHistory model)
	{
		reset() ;
		
		if (null == model)
			return ;
		
		_iId         = model._iId ;
		_iFreeTextId = model._iFreeTextId ;
		_sLabel      = model._sLabel ;
		_sCode       = model._sCode ;
		_sLanguage   = model._sLanguage ;
		_sNext       = model._sNext ;
	}

	/**
	 * Set all information to void
	 */
	public void reset() 
	{
		_iId         = -1 ;
		_iFreeTextId = -1 ;
		_sLabel      = "" ;
		_sCode       = "" ;
		_sLanguage   = "" ;
		_sNext       = "" ;
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

	public int getFreeTextId() {
		return _iFreeTextId ;
	}
	public void setFreeTextId(final int iFreeTextId) {
		_iFreeTextId = iFreeTextId ;
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
	
	public String getNext() {
		return _sNext ;
	}
	public void setNext(final String sNext) {
		_sNext = sNext ;
	}
}
