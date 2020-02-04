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
	private String  _sConceptCode ;
	private String  _sSentLanguage ;
	private String  _sSentText ;
	
	/**
	 * No-args constructor, mandatory for serializable objects
	 */
	public SaveLemmaResult()
	{
		super() ;
		
		_savedLemma      = null ;
		_sMessage        = "" ;
		
		_iUpdatedLemmaId = -1 ;
		_sConceptCode    = "" ;
		_sSentLanguage   = "" ;
		_sSentText       = "" ;
	}

	/**
	 * Plain vanilla constructor
	 * */
	public SaveLemmaResult(final String sMessage, Lemma savedLemma, final String sConceptCode, final String sSentLanguage, final String sSentText, final int iUpdatedLemmaId)
	{
		super() ;
		
		_sMessage        = sMessage ;
		_savedLemma      = savedLemma ;
		
		_iUpdatedLemmaId = iUpdatedLemmaId ;
		_sConceptCode    = sConceptCode ;
		_sSentLanguage   = sSentLanguage ;
		_sSentText       = sSentText ;
	}
	
	public String getMessage() {
		return _sMessage ;
	}
	public void setMessage(final String sMessage) {
		_sMessage = sMessage ;
	}
	
	public String getConceptCode() {
		return _sConceptCode ;
	}
	public void setConceptCode(final String sConceptCode) {
		_sConceptCode = sConceptCode ;
	}
	
	public Lemma getSavedLemma() {
		return _savedLemma ;
	}
	public void setSavedLemma(Lemma savedLemma) {
		_savedLemma = savedLemma ;
	}
	
	public String getSentLanguage() {
		return _sSentLanguage ;
	}
	public void setSentLanguage(final String sSentLanguage) {
		_sSentLanguage = sSentLanguage ;
	}
	
	public String getSentText() {
		return _sSentText ;
	}
	public void setSentText(final String sSentText) {
		_sSentText = sSentText ;
	}
	
	public int getUpdatedLemmaId() {
		return _iUpdatedLemmaId ;
	}
	public void setUpdatedLemmaId(int iUpdatedLemmaId) {
		_iUpdatedLemmaId = iUpdatedLemmaId ;
	}
}
