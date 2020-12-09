package org.quadrifolium.shared.rpc4ontology ;

import com.google.gwt.user.client.rpc.IsSerializable ;

/**
 * SearchAttribute.java
 *
 * The SearchAttribute class represents a predicate, object pair used to find the common subject from set of triples 
 * 
 * Author: PA
 * 
 */
public class SearchAttribute implements IsSerializable 
{
	protected String _sPredicate ;
	protected String _sObject ;
	
	/**
	 * Null constructor
	 */
	public SearchAttribute()
	{
	  super() ;
	  
		reset() ;
	}
		
	/**
	 * Plain vanilla constructor
	 */
	public SearchAttribute(final String sPredicate, final String sObject) 
	{
	  super() ;
	  
		reset() ;
		
		_sPredicate = sPredicate ;
		_sObject    = sObject ;
	}
	
	/**
	 * Copy constructor
	 */
	public SearchAttribute(final SearchAttribute model)
	{
	  super() ;
	  
		initFromModel(model) ;
	}
	
	/**
	 * Initialize from an object of the kind 
	 */
	public void initFromModel(final SearchAttribute model)
	{
		reset() ;
		
		if (null == model)
			return ;
		
		_sPredicate = model._sPredicate ;
		_sObject    = model._sObject ;
	}

	/**
	 * Set all information to void
	 */
	public void reset() 
	{
		_sPredicate = "" ;
		_sObject    = "" ;
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
	
	 /**
    * Determine whether two objects are exactly similar
    * 
    * @param other Other object to compare to
    * 
    * @return true if all data are the same, false if not
    */
 public boolean equals(final SearchAttribute other)
 {
   if (this == other) {
     return true ;
   }
   if (null == other) {
     return false ;
   }
   
   return (_sPredicate.equals(other._sPredicate) && 
           _sObject.equals(other._sObject)) ;
 }
 
 /**
   * Determine whether an object is exactly similar to this object
   * 
   * designed for ArrayList.contains(Obj) method
   * because by default, contains() uses equals(Obj) method of Obj class for comparison
   * 
   * @param o Generic object to compare to
   * 
   * @return true if all data are the same, false if not
   */
 public boolean equals(final Object o) 
 {
   if (this == o) {
     return true ;
   }
   if (null == o || getClass() != o.getClass()) {
     return false;
   }

   final SearchAttribute other = (SearchAttribute) o ;

   return (this.equals(other)) ;
 }
}
