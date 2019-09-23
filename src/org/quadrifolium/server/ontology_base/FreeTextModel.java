package org.quadrifolium.server.ontology_base ;

/**
 * Lemma.java
 *
 * The Flex class represents a specific flexed variation of a lemma
 * 
 * Author: PA
 * 
 */
public class FreeTextModel
{
	private int    _iId ;
	private String _sLabel ;
	private String _sCode ;
	private String _sLanguage ;
	private String _sNext ;
	
	/**
	 * Null constructor
	 */
	public FreeTextModel() {
		reset() ;
	}
		
	/**
	 * Plain vanilla constructor
	 */
	public FreeTextModel(int iId, final String sLabel, final String sCode, final String sLanguage, final String sNext) 
	{
		reset() ;
		
		_iId       = iId ;
		_sLabel    = sLabel ;
		_sCode     = sCode ;
		_sLanguage = sLanguage ;
		_sNext     = sNext ;
	}
	
	/**
	 * Copy constructor
	 */
	public FreeTextModel(final FreeTextModel model) {
		initFromFlex(model) ;
	}
	
	/**
	 * Initialize from an object of the kind 
	 */
	public void initFromFlex(final FreeTextModel model)
	{
		reset() ;
		
		if (null == model)
			return ;
		
		_iId       = model._iId ;
		_sLabel    = model._sLabel ;
		_sCode     = model._sCode ;
		_sLanguage = model._sLanguage ;
		_sNext     = model._sNext ;
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
		_sNext     = "" ;
	}

	/**
	 * Is this free text in the database?
	 * 
	 * @return <code>true</code> if this flex has a database ID, <code>false</code> if not 
	 */
	public boolean isReferenced() {
		return _iId >= 0 ; 
	}
	
	/**
	 * Is there a "next slice"
	 */
	public boolean hasNext() {
		return ((null != _sNext) && (false == "".equals(_sNext))) ;
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
	
	public String getNext() {
		return _sNext ;
	}
	public void setNext(final String sNext) {
		_sNext = sNext ;
	}
}
