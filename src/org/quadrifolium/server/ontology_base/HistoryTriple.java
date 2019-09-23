package org.quadrifolium.server.ontology_base ;

/**
 * Lemma.java
 *
 * The Lemma class represents a word and its flexed variations
 * 
 * Author: PA
 * 
 */
public class HistoryTriple
{
	private int    _iId ;
	private int    _iTripleId ;
	private String _sSubject ;
	private String _sPredicate ;
	private String _sObject ;
	
	/**
	 * Null constructor
	 */
	public HistoryTriple() {
		reset() ;
	}
		
	/**
	 * Plain vanilla constructor
	 */
	public HistoryTriple(int iId, int iTripleId, final String sSubject, final String sPredicate, final String sObject) 
	{
		reset() ;
		
		_iId        = iId ;
		_iTripleId  = iTripleId ;
		_sSubject   = sSubject ;
		_sPredicate = sPredicate ;
		_sObject    = sObject ;
	}
	
	/**
	 * Copy constructor
	 */
	public HistoryTriple(final HistoryTriple model) {
		initFromModel(model) ;
	}
	
	/**
	 * Initialize from an object of the kind 
	 */
	public void initFromModel(final HistoryTriple model)
	{
		reset() ;
		
		if (null == model)
			return ;
		
		_iId        = model._iId ;
		_iTripleId  = model._iTripleId ;
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
		_iTripleId  = -1 ;
		_sSubject   = "" ;
		_sPredicate = "" ;
		_sObject    = "" ;
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

	public int getTripleId() {
		return _iTripleId ;
	}
	public void setTripleId(final int iTripleId) {
		_iTripleId = iTripleId ;
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
