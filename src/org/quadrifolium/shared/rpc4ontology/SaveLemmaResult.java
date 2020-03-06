package org.quadrifolium.shared.rpc4ontology;

import org.quadrifolium.shared.ontology.Lemma;

import net.customware.gwt.dispatch.shared.Result;

/**
 * Object used to provide feedback when a new lemma was saved or an existing lemma updated
 * 
 * @author Philippe
 */
public class SaveLemmaResult implements Result
{
  private Lemma    _savedLemma ;
  
	private String  _sMessage ;
	
	private int     _iUpdatedLemmaId ;
	
	/** Information that was sent to create/edit the lemma */
  private SaveLemmaContent _actionContent ;
		
	/**
	 * No-args constructor, mandatory for serializable objects
	 */
	public SaveLemmaResult()
	{
		super() ;
		
		_savedLemma      = null ;
		_sMessage        = "" ;
		
		_iUpdatedLemmaId = -1 ;
		
		_actionContent   = new SaveLemmaContent() ;
	}

	/**
	 * Plain vanilla constructor
	 */
	public SaveLemmaResult(final String sMessage, Lemma savedLemma, final SaveLemmaContent content, final int iUpdatedLemmaId)
	{
		super() ;
		
		_sMessage        = sMessage ;
		_savedLemma      = savedLemma ;
		
		_iUpdatedLemmaId = iUpdatedLemmaId ;
		
		_actionContent   = new SaveLemmaContent(content) ;
	}
	
	/**
	 * Full variables constructor
	 */
	public SaveLemmaResult(final String sMessage, Lemma savedLemma, final String sConceptCode, final String sSentLanguage, final String sSentGrammar, final String sSentText, final boolean bFromOtherPanel, final int iUpdatedLemmaId)
	{
		super() ;
		
		_sMessage        = sMessage ;
		_savedLemma      = savedLemma ;
		
		_iUpdatedLemmaId = iUpdatedLemmaId ;
		
		_actionContent   = new SaveLemmaContent(sConceptCode, sSentLanguage, sSentText, sSentGrammar, bFromOtherPanel) ;
	}
	
	public String getMessage() {
		return _sMessage ;
	}
	public void setMessage(final String sMessage) {
		_sMessage = sMessage ;
	}
	
	public Lemma getSavedLemma() {
		return _savedLemma ;
	}
	public void setSavedLemma(Lemma savedLemma) {
		_savedLemma = savedLemma ;
	}
	
	public String getConceptCode() {
		return _actionContent.getConceptCode() ;
	}
	public void setConceptCode(final String sConceptCode) {
		_actionContent.setConceptCode(sConceptCode) ;
	}
	
	public SaveLemmaContent getActionContent() {
		return _actionContent ;
	}
	public void setActionContent(final SaveLemmaContent actionContent) {
		_actionContent.initFrom(actionContent) ;
	}
	
	public String getSentLanguage() {
		return _actionContent.getNewLanguage() ;
	}
	public void setSentLanguage(final String sLanguage) {
		_actionContent.setNewLanguage(sLanguage) ;
	}
	
	public String getSentGrammar() {
		return _actionContent.getNewGrammar() ;
	}
	public void setSentGrammar(final String sGrammar) {
		_actionContent.setNewGrammar(sGrammar) ;
	}
	
	public String getSentText() {
		return _actionContent.getNewText() ;
	}
	public void setSentText(final String sText) {
		_actionContent.setNewText(sText) ;
	}
	
	public int getUpdatedLemmaId() {
		return _iUpdatedLemmaId ;
	}
	public void setUpdatedLemmaId(int iUpdatedLemmaId) {
		_iUpdatedLemmaId = iUpdatedLemmaId ;
	}
	
	public boolean isFromOtherPanel() {
		return _actionContent.isFromOtherPanel() ;
	}
}
