package org.quadrifolium.shared.ontology ;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.gwt.user.client.rpc.IsSerializable ;

/**
 * FlexWithTraits.java
 *
 * The FlexWithTraits class represents a specific inflection of a lemma with its traits (triple + labels)
 * 
 * Author: PA
 * 
 */
public class FlexWithTraits extends Flex implements IsSerializable 
{
	protected ArrayList<TripleWithLabel> _aTraits = new ArrayList<TripleWithLabel>() ;
	
	/**
	 * Null constructor
	 */
	public FlexWithTraits() {
		resetGlobal() ;
	}
		
	/**
	 * Plain vanilla constructor
	 */
	public FlexWithTraits(int iId, final String sLabel, final String sCode, final String sLanguage, final ArrayList<TripleWithLabel> aTraits) 
	{
		super(iId, sLabel, sCode, sLanguage) ;
		
		resetLocal() ;
		
		initTraitsFromModel(aTraits) ;
	}
	
	/**
	 * Constructor from a Flex
	 */
	public FlexWithTraits(final Flex inflection) 
	{
		super(inflection) ;
		resetLocal() ;
	}
	
	/**
	 * Copy constructor
	 */
	public FlexWithTraits(final FlexWithTraits model) {
		initFromFlexWithTraits(model) ;
	}
	
	/**
	 * Initialize from an object of the kind 
	 */
	public void initFromFlexWithTraits(final FlexWithTraits model)
	{
		resetGlobal() ;
		
		if (null == model)
			return ;
		
		initFromFlex((Flex) model) ;
		
		initTraitsFromModel(model._aTraits) ;
	}

	/**
	 * Set specific information to void
	 */
	public void resetLocal() 
	{
		_aTraits.clear() ;
	}

	/**
	 * Set all information to void
	 */
	public void resetGlobal() 
	{
		reset() ;
		resetLocal() ;
	}
		
	/**
	 * Return the label as name
	 */
	public String getName() {
    return getLabel() ;
  }
	
	// getter and setter
	//
	public int getId() {
		return _iId ;
	}
	public void setId(final int id) {
		_iId = id ;
	}

	public ArrayList<TripleWithLabel> getTraits() {
		return _aTraits ;
	}
	public void initTraitsFromModel(final ArrayList<TripleWithLabel> aTraits)
	{
		_aTraits.clear() ;
		
		if ((null == aTraits) || aTraits.isEmpty())
			return ;
		
		for (Iterator<TripleWithLabel> it = aTraits.iterator() ; it.hasNext() ; )
			_aTraits.add(new TripleWithLabel(it.next())) ;
	}
	public void addTrait(final TripleWithLabel trait)
	{
		if (null == trait)
			return ;
		
		_aTraits.add(new TripleWithLabel(trait)) ;
	}
}
