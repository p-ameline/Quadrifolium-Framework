package org.quadrifolium.shared.ontology ;

import com.google.gwt.user.client.rpc.IsSerializable ;

/**
 * TripleWithLabel.java
 *
 * The TripleWithLabel class represents a Triple extended with labels for the subject, predicate and object variables 
 * 
 * Author: PA
 * 
 */
public class TripleWithLabel extends Triple implements IsSerializable 
{
	protected String _sLanguage ;
	
	protected String _sSubjectLabel ;
	protected String _sPredicateLabel ;
	protected String _sObjectLabel ;
	
	/**
	 * Null constructor
	 */
	public TripleWithLabel() {
		resetGlobal() ;
	}
		
	/**
	 * Plain vanilla constructor
	 */
	public TripleWithLabel(int iId, final String sSubject, final String sPredicate, final String sObject, final String sLanguage, final String sSubjectLabel, final String sPredicateLabel, final String sObjectLabel) 
	{
		super(iId, sSubject, sPredicate, sObject) ;
		
		resetLocal() ;
		
		_sLanguage       = sLanguage ;
		
		_sSubjectLabel   = sSubjectLabel ;
		_sPredicateLabel = sPredicateLabel ;
		_sObjectLabel    = sObjectLabel ;
	}
	
	/**
	 * Constructor from a Triple and labels
	 */
	public TripleWithLabel(Triple triple, final String sLanguage, final String sSubjectLabel, final String sPredicateLabel, final String sObjectLabel)
  {
		super(triple) ;
		
		_sLanguage       = sLanguage ;
		
		_sSubjectLabel   = sSubjectLabel ;
		_sPredicateLabel = sPredicateLabel ;
		_sObjectLabel    = sObjectLabel ;
  }
	
	/**
	 * Copy constructor
	 */
	public TripleWithLabel(final TripleWithLabel model) {
		initFromLabelModel(model) ;
	}
	
	/**
	 * Initialize from an object of the kind 
	 */
	public void initFromLabelModel(final TripleWithLabel model)
	{
		reset() ;
		
		if (null == model)
			return ;
		
		initFromModel((Triple) model) ;
		
		_sLanguage       = model._sLanguage ;
		_sSubjectLabel   = model._sSubjectLabel ;
		_sPredicateLabel = model._sPredicateLabel ;
		_sObjectLabel    = model._sObjectLabel ;
	}

	/**
	 * Set specific information to void
	 */
	public void resetLocal() 
	{
		_sLanguage       = "" ;
		_sSubjectLabel   = "" ;
		_sPredicateLabel = "" ;
		_sObjectLabel    = "" ;
	}

	/**
	 * Set information to void
	 */
	public void resetGlobal() 
	{
		reset() ;
		resetLocal() ;
	}
	
	/**
	 * Return a label in the form 'object (predicate)"
	 */
	public String getName() {
    return _sObjectLabel + " (" + _sPredicateLabel + ")" ;
  }
	
	public String getLanguage() {
		return _sLanguage ;
	}
	public void setLanguage(final String sLanguage) {
		_sLanguage = sLanguage ;
	}
	
	public String getSubjectLabel() {
		return _sSubjectLabel ;
	}
	public void setSubjectLabel(final String sSubjectLabel) {
		_sSubjectLabel = sSubjectLabel ;
	} 

	public String getPredicateLabel() {
		return _sPredicateLabel ;
	}
	public void setPredicateLabel(final String sPredicateLabel) {
		_sPredicateLabel = sPredicateLabel ;
	}

	public String getObjectLabel() {
		return _sObjectLabel ;
	}
	public void setObjectLabel(final String sObjectLabel) {
		_sObjectLabel = sObjectLabel ;
	}
}
