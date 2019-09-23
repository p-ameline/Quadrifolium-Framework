package org.quadrifolium.shared.rpc4ontology;

import org.quadrifolium.shared.ontology.TripleWithLabel;

import net.customware.gwt.dispatch.shared.Action;

/**
 * Object used to save a new definition or update an existing one for a given concept
 */
public class SaveDefinitionAction implements Action<SaveDefinitionResult> 
{
  private int    _iUserId ;
  
  private String _sConceptCode ;
  private String _sNewLanguage ;
  private String _sNewText ;
  
	private TripleWithLabel _editedDefinition ;

	/**
	 * Plain vanilla constructor<br>
	 * <br>
	 * editedDefinition must be null if the request is to create a new definition
	 */
	public SaveDefinitionAction(final int iUserId, final String sConceptCode, final String sNewLanguage, final String sNewText, final TripleWithLabel editedDefinition) 
	{
		super() ;
		
		_iUserId          = iUserId ;
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
		
		_iUserId          = -1 ;
		_sConceptCode     = "" ;
		_sNewLanguage     = "" ;
		_sNewText         = "" ;
		_editedDefinition = null ;
	}

	public int getUserId() {
		return _iUserId ;
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
