package org.quadrifolium.shared.rpc4ontology;

import org.quadrifolium.shared.ontology.Lemma;
import org.quadrifolium.shared.rpc_util.SessionElements;

import net.customware.gwt.dispatch.shared.Action;

/**
 * Object used to save a new definition or update an existing one for a given concept
 */
public class SaveLemmaAction implements Action<SaveLemmaResult> 
{
  private SessionElements  _sessionElements ;
  
  /** Lemma being edited when this is the case, <code>null</code> when creating a new lemma */
	private Lemma            _editedLemma ;
	
	/** Information sent to create/edit the lemma */
  private SaveLemmaContent _content ;

	/**
	 * Plain vanilla constructor<br>
	 * <br>
	 * editedDefinition must be null if the request is to create a new definition
	 */
	public SaveLemmaAction(final SessionElements sessionElements, final String sConceptCode, final String sNewLanguage, final String sNewText, final String sNewGrammar, final Lemma editedLemma, final boolean bFromOtherPanel) 
	{
		super() ;
		
		_sessionElements = sessionElements ;
		_editedLemma     = editedLemma ;
		
		_content         = new SaveLemmaContent(sConceptCode, sNewLanguage, sNewText, sNewGrammar, bFromOtherPanel) ;
	}

	/**
	 * No-args constructor, mandatory for serializable objects
	 */
	public SaveLemmaAction() 
	{
		super() ;
		
		_sessionElements = null ;
		_editedLemma     = null ;
		
		_content         = new SaveLemmaContent() ;
	}

	public SessionElements getSessionElements() {
		return _sessionElements ;
	}
	
	public SaveLemmaContent getContent() {
		return _content ;
	}
	
	public String getConceptCode() {
		return _content.getConceptCode() ;
	}
	
	public String getNewLanguage() {
		return _content.getNewLanguage() ;
	}

	public String getNewText() {
		return _content.getNewText() ;
	}
	
	public String getNewGrammar() {
		return _content.getNewGrammar() ;
	}
	
	public Lemma getEditedLemma() {
		return _editedLemma ;
	}
}
