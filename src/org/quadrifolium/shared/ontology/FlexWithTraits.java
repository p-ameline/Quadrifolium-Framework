package org.quadrifolium.shared.ontology ;

import java.util.ArrayList;
import java.util.Iterator;

import org.quadrifolium.shared.util.QuadrifoliumFcts;
import org.quadrifolium.shared.util.QuadrifoliumFcts.Gender;

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
	
	/**
	 * Get a trait from its predicate
	 * 
	 * @param sPredicate Predicate of the triple to look for
	 * 
	 * @return The trait if found, <code>null</code> if not
	 */
	public TripleWithLabel getTripleForTrait(final String sPredicate) throws NullPointerException
	{
		if (null == sPredicate)
			throw new NullPointerException() ;
		
		if ("".equals(sPredicate))
			return null ;
		
		// Get the triple (if any) which predicate is the concept code for "part of speech"
		//
		for (TripleWithLabel trait : _aTraits)
			if (sPredicate.equals(trait.getPredicate()))
				return trait ;
		
		return null ;
	}
	
	/**
	 * Get the grammatical gender triple for this lemma
	 * 
	 * @return The trait if found, <code>null</code> if not
	 */
	public TripleWithLabel getGrammaticalGenderTriple()
	{
		// Get the concept code for "grammatical gender"
		//
		String sConceptCode = QuadrifoliumFcts.getConceptCodeForGrammaticalGender() ;
		if ((null == sConceptCode) || "".equals(sConceptCode))
			return null ;
		
		// Get the triple (if any) which predicate is the concept code for "part of speech"
		//
		return getTripleForTrait(sConceptCode) ;
	}
	
	/**
	 * Get the grammatical gender of this lemma (from traits)
	 * 
	 * @return The grammatical gender if found, <code>nullGender</code> if not
	 */
	public Gender getGrammaticalGender()
	{
		// Get the triple for "grammatical gender"
		//
		TripleWithLabel genderTriple = getGrammaticalGenderTriple() ;
		
		if (null == genderTriple)
			return QuadrifoliumFcts.Gender.nullGender ;
		
		return QuadrifoliumFcts.getGrammaticalGenderFromConceptCode(genderTriple.getObject()) ;
	}
	
	/**
	 * Get the grammatical number triple for this lemma
	 * 
	 * @return The trait if found, <code>null</code> if not
	 */
	public TripleWithLabel getGrammaticalNumberTriple()
	{
		// Get the concept code for "grammatical number"
		//
		String sConceptCode = QuadrifoliumFcts.getConceptCodeForGrammaticalNumber() ;
		if ((null == sConceptCode) || "".equals(sConceptCode))
			return null ;
		
		// Get the triple (if any) which predicate is the concept code for "part of speech"
		//
		return getTripleForTrait(sConceptCode) ;
	}
	
	/**
	 * Get the grammatical number of this lemma (from traits)
	 * 
	 * @return The grammatical number if found, <code>nullNumber</code> if not
	 */
	public QuadrifoliumFcts.Number getGrammaticalNumber()
	{
		// Get the triple for "grammatical number"
		//
		TripleWithLabel numberTriple = getGrammaticalNumberTriple() ;
		
		if (null == numberTriple)
			return QuadrifoliumFcts.Number.nullNumber ;
		
		return QuadrifoliumFcts.getGrammaticalNumberFromConceptCode(numberTriple.getObject()) ;
	}
}
