package org.quadrifolium.server.ontology_base ;

import org.quadrifolium.shared.ontology.Flex;

/**
 * HistoryFlex.java
 *
 * The HistoryFlex class represents a versioned instance of a Flex
 * 
 * Author: PA
 * 
 */
public class HistoryFlex
{
	private int    _iId ;
	
	private int    _iFlexId ;
	private String _sLabel ;
	private String _sCode ;
	private String _sLanguage ;
	
	/**
	 * Null constructor
	 */
	public HistoryFlex() {
		reset() ;
	}
		
	/**
	 * Plain vanilla constructor
	 */
	public HistoryFlex(int iId, int iFlexId, final String sLabel, final String sCode, final String sLanguage) 
	{
		reset() ;
		
		_iId       = iId ;
		_iFlexId   = iFlexId ;
		_sLabel    = sLabel ;
		_sCode     = sCode ;
		_sLanguage = sLanguage ;
	}
	
	/**
	 * Constructor from a Flex
	 */
	public HistoryFlex(final Flex flex) 
	{
		reset() ;

		if (null == flex)
			return ;
		
		_iFlexId   = flex.getId() ;
		_sLabel    = flex.getLabel() ;
		_sCode     = flex.getCode() ;
		_sLanguage = flex.getLanguage() ;
	}
	
	/**
	 * Copy constructor
	 */
	public HistoryFlex(final HistoryFlex model) {
		initFromHistoryFlex(model) ;
	}
	
	/**
	 * Initialize from an object of the kind 
	 */
	public void initFromHistoryFlex(final HistoryFlex model)
	{
		reset() ;
		
		if (null == model)
			return ;
		
		_iId       = model._iId ;
		_iFlexId   = model._iFlexId ;
		_sLabel    = model._sLabel ;
		_sCode     = model._sCode ;
		_sLanguage = model._sLanguage ;
	}

	/**
	 * Initialize from a Flex object 
	 */
	public void initFromFlex(final Flex flex)
	{
		reset() ;
		
		if (null == flex)
			return ;
		
		_iId       = -1 ;
		_iFlexId   = flex.getId() ;
		_sLabel    = flex.getLabel() ;
		_sCode     = flex.getCode() ;
		_sLanguage = flex.getLanguage() ;
	}
	
	/**
	 * Set all information to void
	 */
	public void reset() 
	{
		_iId       = -1 ;
		_iFlexId   = -1 ;
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

	public int getFlexId() {
		return _iFlexId ;
	}
	public void setFlexId(final int iFlexId) {
		_iFlexId = iFlexId ;
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
