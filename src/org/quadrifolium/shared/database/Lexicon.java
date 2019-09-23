package org.quadrifolium.shared.database ;

import org.quadrifolium.shared.model.QuadrifoliumModelLexicon;

import com.google.gwt.user.client.rpc.IsSerializable ;

/**
 * Lexicon.java
 *
 * The Lexicon class represents a object of type QuadrifoliumModelLexicon that can potentially be processed for display purposes
 * 
 * Created: 10 Apr 2019
 *
 * Author: PA
 * 
 */
public class Lexicon implements IsSerializable 
{
	private String _sLabel ;
	private String _sCode ;
	
	public enum LabelType   { rawLabel, selectionLabel, displayLabel} ;
	
	//
	//
	public Lexicon() {
		reset() ;
	}
		
	/**
	 * Standard constructor
	 */
	public Lexicon(final String sLabel, final String sCode) 
	{
		_sLabel = sLabel ;
		_sCode  = sCode ;
	}
	
	/**
	 * Copy constructor
	 */
	public Lexicon(final Lexicon model) 
	{
		reset() ;
		
		if (null == model)
			return ;
		
		_sLabel = model._sLabel ;
		_sCode  = model._sCode ;
	}
			
	public Lexicon(final QuadrifoliumModelLexicon model) 
	{
		reset() ;
		
		if (null == model)
			return ;
		
		_sLabel = model.getLabel() ;
		_sCode  = model.getCode() ;
	}
	
	public void reset() 
	{
		_sLabel = "" ;
		_sCode  = "" ;
	}
	
	public String getLabel() {
  	return _sLabel ;
  }
	public void setLabel(String sLabel) {
  	_sLabel = sLabel ;
  }

	public String getCode() {
  	return _sCode ;
  }
	public void setCode(String sCode) {
  	_sCode = sCode ;
  }
	
	/**
	  * Determine whether two Lexicon objects are exactly similar
	  * 
	  * @return true if all data are the same, false if not
	  * @param  lexicon Other Lexicon to compare to
	  * 
	  */
	public boolean equals(final Lexicon lexicon)
	{
		if (this == lexicon) {
			return true ;
		}
		if (null == lexicon) {
			return false ;
		}
		
		return (_sLabel.equals(lexicon._sLabel) && 
				    _sCode.equals(lexicon._sCode)) ;
	}
  
	/**
	  * Determine whether an object is exactly similar to this Lexicon object
	  * 
	  * designed for ArrayList.contains(Obj) method
		* because by default, contains() uses equals(Obj) method of Obj class for comparison
	  * 
	  * @return true if all data are the same, false if not
	  * @param node LdvModelNode to compare to
	  * 
	  */
	public boolean equals(final Object o) 
	{
		if (this == o) {
			return true ;
		}
		if (null == o || getClass() != o.getClass()) {
			return false;
		}

		final Lexicon lexicon = (Lexicon) o ;

		return (this.equals(lexicon)) ;
	}
}
