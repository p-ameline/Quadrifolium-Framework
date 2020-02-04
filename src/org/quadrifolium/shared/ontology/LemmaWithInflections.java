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
	 * Get the part of speech of this lemma (from traits)
	 * 
	 * @return The part of sppech if found, <code>nullPoS</code> if not
	 */
	public PartOfSpeech getPartOfSpeech()
	{
		// Get the concept code for "part of speech"
		//
		String sConceptCode = QuadrifoliumFcts.getConceptCodeForPartOfSpeech() ;
		if ((null == sConceptCode) || "".equals(sConceptCode))
			return QuadrifoliumFcts.PartOfSpeech.nullPoS ;
		
		// Get the triple (if any) which predicate is the concept code for "part of speech"
		//
		for (TripleWithLabel trait : _aTraits)
			if (sConceptCode.equals(trait.getPredicate()))
				return QuadrifoliumFcts.getPartOfSpeechFromConceptCode(trait.getObject()) ;
		
		return QuadrifoliumFcts.PartOfSpeech.nullPoS ;
	}
	
	/**
	 * Get the grammatical gender of this lemma (from traits)
	 * 
	 * @return The grammatical gender if found, <code>nullGender</code> if not
	 */
	public Gender getGramaticalGender()
	{
		// Get the concept code for "grammatical gender"
		//
		String sConceptCode = QuadrifoliumFcts.getConceptCodeForGrammaticalGender() ;
		if ((null == sConceptCode) || "".equals(sConceptCode))
			return QuadrifoliumFcts.Gender.nullGender ;
		
		// Get the triple (if any) which predicate is the concept code for "grammatical gender"
		//
		for (TripleWithLabel trait : _aTraits)
			if (sConceptCode.equals(trait.getPredicate()))
				return QuadrifoliumFcts.getGrammaticalGenderFromConceptCode(trait.getObject()) ;
		
		return QuadrifoliumFcts.Gender.nullGender ;
	}
	
	/**
	 * Get the grammatical number of this lemma (from traits)
	 * 
	 * @return The grammatical number if found, <code>nullNumber</code> if not
	 */
	public QuadrifoliumFcts.Number getGramaticalNumber()
	{
		// Get the concept code for "grammatical number"
		//
		String sConceptCode = QuadrifoliumFcts.getConceptCodeForGrammaticalNumber() ;
		if ((null == sConceptCode) || "".equals(sConceptCode))
			return QuadrifoliumFcts.Number.nullNumber ;
		
		// Get the triple (if any) which predicate is the concept code for "grammatical number"
		//
		for (TripleWithLabel trait : _aTraits)
			if (sConceptCode.equals(trait.getPredicate()))
				return QuadrifoliumFcts.getGrammaticalNumberFromConceptCode(trait.getObject()) ;
		
		return QuadrifoliumFcts.Number.nullNumber ;
	}
}
