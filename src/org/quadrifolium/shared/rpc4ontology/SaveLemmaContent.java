package org.quadrifolium.shared.rpc4ontology;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Object that carry information used to save/update a lemma
 */
public class SaveLemmaContent implements IsSerializable 
{
  /** Concept the lemma to be created/edited belongs to */
  private String _sConceptCode ;

  /** Information used to create/edit the lemma */
  private String _sNewLanguage ;
  private String _sNewText ;
  private String _sNewGrammar ;
  
  /** Was the edit interface from the "other languages" panel? (not possible to guess since language can be edited) */
  private boolean _bFromOtherPanel ;

	/**
	 * Plain vanilla constructor<br>
	 * <br>
	 * editedDefinition must be null if the request is to create a new definition
	 */
	public SaveLemmaContent(final String sConceptCode, final String sNewLanguage, final String sNewText, final String sNewGrammar, final boolean bFromOtherPanel) 
	{
		super() ;
		
		_sConceptCode    = sConceptCode ;
		_sNewLanguage    = sNewLanguage ;
		_sNewText        = sNewText ;
		_sNewGrammar     = sNewGrammar ;
		_bFromOtherPanel = bFromOtherPanel ;
	}

	/**
	 * No-args constructor, mandatory for serializable objects
	 */
	public SaveLemmaContent() 
	{
		super() ;
		
		reset() ;
	}

	/**
	 * Copy constructor
	 */
	public SaveLemmaContent(final SaveLemmaContent model) 
	{
		super() ;
		
		initFrom(model) ;
	}
	
	/**
	 * Reset all variables
	 */
	protected void reset()
	{
		_sConceptCode    = "" ;
		_sNewLanguage    = "" ;
		_sNewText        = "" ;
		_sNewGrammar     = "" ;
		_bFromOtherPanel = false ;
	}
	
	/**
	 * Initialize from another object of the same kind (kind of = operator)
	 */
	protected void initFrom(final SaveLemmaContent model)
	{
		reset() ;
		
		if (null == model)
			return ;
		
		_sConceptCode    = model._sConceptCode ;
		_sNewLanguage    = model._sNewLanguage ;
		_sNewText        = model._sNewText ;
		_sNewGrammar     = model._sNewGrammar ;
		_bFromOtherPanel = model._bFromOtherPanel ;
	}
	
	public String getConceptCode() {
		return _sConceptCode ;
	}
	public void setConceptCode(final String sConceptCode) {
		_sConceptCode = sConceptCode ;
	}
	
	public String getNewLanguage() {
		return _sNewLanguage ;
	}
	public void setNewLanguage(final String sNewLanguage) {
		_sNewLanguage = sNewLanguage ;
	}

	public String getNewText() {
		return _sNewText ;
	}
	public void setNewText(final String sNewText) {
		_sNewText = sNewText ;
	}
	
	public String getNewGrammar() {
		return _sNewGrammar ;
	}
	public void setNewGrammar(final String sNewGrammar) {
		_sNewGrammar = sNewGrammar ;
	}
	
	public boolean isFromOtherPanel() {
		return _bFromOtherPanel ;
	}
	public void setFromOtherPanel(final boolean bFromOtherPanel) {
		_bFromOtherPanel = bFromOtherPanel ;
	}
}
