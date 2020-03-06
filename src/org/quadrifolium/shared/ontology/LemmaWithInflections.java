package org.quadrifolium.shared.ontology ;

import java.util.ArrayList;
import java.util.Iterator;

import org.quadrifolium.shared.util.QuadrifoliumFcts;
import org.quadrifolium.shared.util.QuadrifoliumFcts.Gender;
import org.quadrifolium.shared.util.QuadrifoliumFcts.PartOfSpeech;

import com.google.gwt.user.client.rpc.IsSerializable ;

/**
 * LemmaWithInflections.java
 *
 * The LemmaWithInflections class represents a lemma and its inflections (along with their traits=
 * 
 * Author: PA
 * 
 */
public class LemmaWithInflections extends Lemma implements IsSerializable 
{
	protected boolean                    _isPreferred ;
	protected ArrayList<FlexWithTraits>  _aInflections = new ArrayList<FlexWithTraits>() ;
	protected ArrayList<TripleWithLabel> _aTraits      = new ArrayList<TripleWithLabel>() ;
	
	/**
	 * Null constructor
	 */
	public LemmaWithInflections() {
		resetGlobal() ;
	}
		
	/**
	 * Plain vanilla constructor
	 */
	public LemmaWithInflections(int iId, final String sLabel, final String sCode, final String sLanguage, final ArrayList<FlexWithTraits> aInflections, final ArrayList<TripleWithLabel> aTraits) 
	{
		super(iId, sLabel, sCode, sLanguage) ;
		
		resetLocal() ;
		
		initInflectionsFromModel(aInflections) ;
		initTraitsFromModel(aTraits) ;
	}
	
	/**
	 * Constructor from a lemma
	 */
	public LemmaWithInflections(final Lemma lemma, final ArrayList<FlexWithTraits> aInflections, final ArrayList<TripleWithLabel> aTraits) 
	{
		super(lemma) ;
		
		resetLocal() ;
		
		initInflectionsFromModel(aInflections) ;
		initTraitsFromModel(aTraits) ;
	}
	
	/**
	 * Copy constructor
	 */
	public LemmaWithInflections(final LemmaWithInflections model) {
		initFromGlobalModel(model) ;
	}
	
	/**
	 * Initialize from an object of the kind 
	 */
	public void initFromGlobalModel(final LemmaWithInflections model)
	{
		resetGlobal() ;
		
		if (null == model)
			return ;
		
		initFromModel((Lemma) model) ;
		
		initInflectionsFromModel(model._aInflections) ;
		initTraitsFromModel(model._aTraits) ;
	}

	/**
	 * Set local information to void
	 */
	public void resetLocal() 
	{
		_isPreferred = false ;
		_aInflections.clear() ;
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
	 * Return the name as the name of the first inflection (or, if none, as lemma's label)
	 */
	public String getName() 
	{
		String sName = "" ; 
		
		// If no inflections available, get lemma's name, else get first inflection
		//
		if (_aInflections.isEmpty())
			sName = _sLabel ;
		else
		{
			Iterator<FlexWithTraits> it = _aInflections.iterator() ;		
			sName = it.next().getName() ;
		}
		
		// Add language
		//
		sName += " (" + getLanguage() ;
		
		// Add a '*' if the preferred term for this language
		//
		if (isPreferred())
			sName += "*" ;
    
		sName += ")" ;
		
    return sName ;
  }
	
	// getter and setter
	//
	public boolean isPreferred() {
		return _isPreferred ;
	}
	public void setPreferred(boolean isPreferred) {
		_isPreferred = isPreferred ;
	}
	
	public ArrayList<FlexWithTraits> getInflections() {
		return _aInflections ;
	}
	public void initInflectionsFromModel(final ArrayList<FlexWithTraits> aInflections)
	{
		_aInflections.clear() ;
		
		if ((null == aInflections) || aInflections.isEmpty())
			return ;
		
		for (FlexWithTraits inflection : aInflections)
			_aInflections.add(new FlexWithTraits(inflection)) ;
	}
	public void addInflection(final FlexWithTraits inflection)
	{
		if (null == inflection)
			return ;
		
		_aInflections.add(new FlexWithTraits(inflection)) ;
	}
	
	public ArrayList<TripleWithLabel> getTraits() {
		return _aTraits ;
	}
	public void initTraitsFromModel(final ArrayList<TripleWithLabel> aTraits)
	{
		_aTraits.clear() ;
		
		if ((null == aTraits) || aTraits.isEmpty())
			return ;
		
		for (TripleWithLabel trait : aTraits)
			_aTraits.add(new TripleWithLabel(trait)) ;
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
	 * Get the part of speech triple for this lemma
	 * 
	 * @return The trait if found, <code>null</code> if not
	 */
	public TripleWithLabel getPartOfSpeechTriple()
	{
		// Get the concept code for "part of speech"
		//
		String sConceptCode = QuadrifoliumFcts.getConceptCodeForPartOfSpeech() ;
		if ((null == sConceptCode) || "".equals(sConceptCode))
			return null ;
		
		// Get the triple (if any) which predicate is the concept code for "part of speech"
		//
		return getTripleForTrait(sConceptCode) ;
	}
	
	/**
	 * Get the part of speech of this lemma (from traits)
	 * 
	 * @return The part of speech if found, <code>nullPoS</code> if not
	 */
	public PartOfSpeech getPartOfSpeech()
	{
		// Get the triple for "part of speech"
		//
		TripleWithLabel triple = getPartOfSpeechTriple() ;
		
		if (null == triple)
			return QuadrifoliumFcts.PartOfSpeech.nullPoS ;
		
		return QuadrifoliumFcts.getPartOfSpeechFromConceptCode(triple.getObject()) ;
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
