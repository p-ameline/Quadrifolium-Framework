package org.quadrifolium.shared.ontology ;

import java.util.ArrayList;
import java.util.Iterator;

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
		
		for (Iterator<FlexWithTraits> it = aInflections.iterator() ; it.hasNext() ; )
			_aInflections.add(new FlexWithTraits(it.next())) ;
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
