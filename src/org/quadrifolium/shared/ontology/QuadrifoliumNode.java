package org.quadrifolium.shared.ontology ;

import com.google.gwt.user.client.rpc.IsSerializable ;
import com.ldv.shared.graph.LdvModelNode;

/**
 * QuadrifoliumNode.java
 *
 * The QuadrifoliumNode class represents a node with a label in a given language
 * 
 * Author: PA
 * 
 */
public class QuadrifoliumNode extends LdvModelNode implements IsSerializable
{
	protected String _sLabel ;
	protected String _sLanguage ;
	
	/**
	 * Null constructor
	 */
	public QuadrifoliumNode() {
		init() ;
	}
			
	/**
	 * Copy constructor
	 */
	public QuadrifoliumNode(final QuadrifoliumNode model) {
		initFromNode(model) ;
	}
	
	/**
	 * Constructor from a LdvModelNode
	 */
	public QuadrifoliumNode(final LdvModelNode model) 
	{
		init() ;
		
		if (null == model)
			return ;
		
		if (getClass() == model.getClass())
			initFromNode((QuadrifoliumNode) model) ;
		else
			super.initFromNode(model) ;
	}
	
	/**
	 * Initialize from an object of the kind 
	 */
	public void initFromNode(final QuadrifoliumNode model)
	{
		init() ;
		
		if (null == model)
			return ;
		
		super.initFromNode(model) ;
		
		_sLabel    = model._sLabel ;
		_sLanguage = model._sLanguage ;
	}

	/**
	 * Set all information to void
	 */
	public void init() 
	{
		super.init() ;
		
		_sLabel    = "" ;
		_sLanguage = "" ;
	}

	// getter and setter
	//
	public String getLabel() {
		return _sLabel ;
	}
	public void setLabel(final String sLabel) {
		_sLabel = sLabel ;
	} 

	public String getLanguage() {
		return _sLanguage ;
	}
	public void setLanguage(final String sLanguage) {
		_sLanguage = sLanguage ;
	}	
}
