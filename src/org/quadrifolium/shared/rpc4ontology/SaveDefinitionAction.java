package org.quadrifolium.shared.rpc4ontology;

import org.quadrifolium.shared.ontology.TripleWithLabel;
import org.quadrifolium.shared.rpc_util.SessionElements;

import net.customware.gwt.dispatch.shared.Action;

/**
 * Object used to save a new definition or update an existing one for a given concept
 */
public class SaveDefinitionAction implements Action<SaveDefinitionResult> 
{
  private SessionElements _sessionElements ;
  
  private String _sConceptCode ;
  private String _sNewLanguage ;
  private String _sNewText ;
  
	private TripleWithLabel _editedDefinition ;

	/**
	 * Plain vanilla constructor<br>
	 * <br>
	 * editedDefinition must be null if the request is to create a new definition
	 */
	public SaveDefinitionAction(final SessionElements sessionElements, final String sConceptCode, final String sNewLanguage, final String sNewText, final TripleWithLabel editedDefinition) 
	{
		super() ;
		
		_sessionElements  = sessionElements ;
		_sConceptCode     = sConceptCode ;
		_sNewLanguage     = sNewLanguage ;
		_sNewText         = sNewText ;
		_editedDefinition = editedDefinition ;
	}

	/**
	 * No-args constructor, mandatory for serializable objects
	 */
	public SaveDefinitionAction() 
	{
		super() ;
		
		_sessionElements  = null ;
		_sConceptCode     = "" ;
		_sNewLanguage     = "" ;
		_sNewText         = "" ;
		_editedDefinition = null ;
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
	
	public TripleWithLabel getEditedDefinition() {
		return _editedDefinition ;
	}
}
