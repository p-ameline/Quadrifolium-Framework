package org.quadrifolium.shared.model;

import org.quadrifolium.shared.util.MiscellanousFcts;

import com.google.gwt.user.client.rpc.IsSerializable;

public class QuadrifoliumModelLexicon implements IsSerializable
{
	protected String _sCode ;
	protected String _sLabel ;

	/**
	 * Default constructor 
	 * 
	 **/
	public QuadrifoliumModelLexicon()
	{
		init() ; 
	}
				
	/**
	 * Full constructor  
	 * 
	 **/
	public QuadrifoliumModelLexicon(final String sCode, final String sLabel)
	{
		init() ;
		
		_sCode      = sCode ;
		_sLabel     = sLabel ;
	}
	
	/**
	 * Copy constructor  
	 * 
	 * @param sourceNode Model node
	 * 
	 **/
	public QuadrifoliumModelLexicon(final QuadrifoliumModelLexicon sourceNode)
	{
		initFromLexique(sourceNode) ;
	}
	
	/**
	 * Reset all information
	 * 
	 **/
	public void init()
	{
		_sCode      = "" ;
		_sLabel     = "" ;
	}
	
	/**
	 * Sets all information by copying other node content   
	 * 
	 * @param otherNode Model node
	 * @return void
	 * 
	 **/
	public void initFromLexique(final QuadrifoliumModelLexicon otherNode)
	{
		init() ;
		
		if (null == otherNode)
			return ;
		
		_sCode      = otherNode._sCode ;
		_sLabel     = otherNode._sLabel ;
	}
			
	public String getCode() {
		return _sCode ;
	}

	public void setCode(final String sCode) {
		_sCode = sCode ;
	}

	public String getLabel() {
		return _sLabel ;
	}

	public void setLabel(final String sLabel) {
		_sLabel = sLabel ;
	}

	/**
	  * Determine whether two lexicon objects are exactly similar
	  * 
	  * @return true if all data are the same, false if not
	  * @param node LdvModelLexique to compare to
	  * 
	  */
	public boolean equals(final QuadrifoliumModelLexicon lexicon)
	{
		if (this == lexicon) {
			return true ;
		}
		if (null == lexicon) {
			return false ;
		}
		
		return (MiscellanousFcts.areIdenticalStrings(_sCode, lexicon._sCode) && 
				    MiscellanousFcts.areIdenticalStrings(_sLabel, lexicon._sLabel)) ;
	}
   
	/**
	  * Determine whether two lexicon objects are exactly similar
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

		final QuadrifoliumModelLexicon node = (QuadrifoliumModelLexicon) o ;

		return (this.equals(node)) ;
	}
}
