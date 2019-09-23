package org.quadrifolium.server.ontology_base ;

/**
 * Lemma.java
 *
 * The Lemma class represents a word and its flexed variations
 * 
 * Author: PA
 * 
 */
public class HistoryLemma
{
	private int    _iId ;
	private int    _iLemmaId ;
	private String _sLabel ;
	private String _sCode ;
	private String _sLanguage ;
	
	/**
	 * Null constructor
	 */
	public HistoryLemma() {
		reset() ;
	}
		
	/**
	 * Plain vanilla constructor
	 */
	public HistoryLemma(int iId, int iLemmaId, final String sLabel, final String sCode, final String sLanguage) 
	{
		reset() ;
		
		_iId       = iId ;
		_iLemmaId  = iLemmaId ;
		_sLabel    = sLabel ;
		_sCode     = sCode ;
		_sLanguage = sLanguage ;
	}
	
	/**
	 * Copy constructor
	 */
	public HistoryLemma(final HistoryLemma model) {
		initFromModel(model) ;
	}
	
	/**
	 * Initialize from an object of the kind 
	 */
	public void initFromModel(final HistoryLemma model)
	{
		reset() ;
		
		if (null == model)
			return ;
		
		_iId       = model._iId ;
		_iLemmaId  = model._iLemmaId ;
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
		_iLemmaId  = -1 ;
		_sLabel    = "" ;
		_sCode     = "" ;
		_sLanguage = "" ;
	}

	/**
	 * Is this lemma in the database?
	 * 
	 * @return <code>true</code> if this person has a database ID, <code>false</code> if not 
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

	public int getLemmaId() {
		return _iLemmaId ;
	}
	public void setLemmaId(final int iLemmaId) {
		_iLemmaId = iLemmaId ;
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
}
