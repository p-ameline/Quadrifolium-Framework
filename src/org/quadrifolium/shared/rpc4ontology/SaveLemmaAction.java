package org.quadrifolium.shared.rpc4ontology;

import org.quadrifolium.shared.ontology.Lemma;
import org.quadrifolium.shared.rpc_util.SessionElements;

import net.customware.gwt.dispatch.shared.Action;

/**
 * Object used to save a new definition or update an existing one for a given concept
 */
public class SaveLemmaAction implements Action<SaveLemmaResult> 
{
  private SessionElements _sessionElements ;
  
  /** Concept the lemma to be created/edited belongs to */
  private String _sConceptCode ;
  
  private String _sNewLanguage ;
  private String _sNewText ;
  private String _sNewGrammar ;
  
  /** Lemma being edited (when not a new one, thus can be <code>null</code>) */
	private Lemma  _editedLemma ;

	/**
	 * Plain vanilla constructor<br>
	 * <br>
	 * editedDefinition must be null if the request is to create a new definition
	 */
	public SaveLemmaAction(final SessionElements sessionElements, final String sConceptCode, final String sNewLanguage, final String sNewText, final String sNewGrammar, final Lemma editedLemma) 
	{
		super() ;
		
		_sessionElements = sessionElements ;
		_sConceptCode    = sConceptCode ;
		_sNewLanguage    = sNewLanguage ;
		_sNewText        = sNewText ;
		_sNewGrammar     = sNewGrammar ;
		_editedLemma     = editedLemma ;
	}

	/**
	 * No-args constructor, mandatory for serializable objects
	 */
	public SaveLemmaAction() 
	{
		super() ;
		
		_sessionElements  = null ;
		_sConceptCode     = "" ;
		_sNewLanguage     = "" ;
		_sNewText         = "" ;
		_sNewGrammar      = "" ;
		_editedLemma      = null ;
	}

	public SessionElements getSessionElements() {
		return _sessionElements ;
	}
	
	public String getConceptCode() {
		return _sConceptCode ;
	}
	
	public String getNewLanguage() {
		return _sNewLanguage ;
	}

	public String getNewText() {
		return _sNewText ;
	}
	
	public String getNewGrammar() {
		return _sNewGrammar ;
	}
	
	public Lemma getEditedLemma() {
		return _editedLemma ;
	}
}
