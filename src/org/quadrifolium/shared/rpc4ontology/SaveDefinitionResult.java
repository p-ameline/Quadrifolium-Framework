package org.quadrifolium.shared.rpc4ontology;

import org.quadrifolium.shared.ontology.TripleWithLabel;

import net.customware.gwt.dispatch.shared.Result;

/**
 * Object used to save a new definition or update an existing one
 * 
 * @author Philippe
 */
public class SaveDefinitionResult implements Result
{
  private TripleWithLabel _savedDefinition ;
  
	private String          _sMessage ;
	
	private int             _iUpdatedTripleId ;
	private String          _sConceptCode ;
	private String          _sSentLanguage ;
	private String          _sSentText ;
	
	/**
	 * No-args constructor, mandatory for serializable objects
	 */
	public SaveDefinitionResult()
	{
		super() ;
		
		_savedDefinition  = null ;
		_sMessage         = "" ;
		
		_iUpdatedTripleId = -1 ;
		_sConceptCode     = "" ;
		_sSentLanguage    = "" ;
		_sSentText        = "" ;
	}

	/**
	 * Plain vanilla constructor
	 * */
	public SaveDefinitionResult(final String sMessage, TripleWithLabel savedDefinition, final String sConceptCode, final String sSentLanguage, final String sSentText, final int iUpdatedTripleId)
	{
		super() ;
		
		_sMessage         = sMessage ;
		_savedDefinition  = savedDefinition ;
		
		_iUpdatedTripleId = iUpdatedTripleId ;
		_sConceptCode     = sConceptCode ;
		_sSentLanguage    = sSentLanguage ;
		_sSentText        = sSentText ;
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
	
	public TripleWithLabel getSavedDefinition() {
		return _savedDefinition ;
	}
	public void setSavedDefinition(TripleWithLabel savedDefinition) {
		_savedDefinition = savedDefinition ;
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
	
	public int getUpdatedTripleId() {
		return _iUpdatedTripleId ;
	}
	public void setUpdatedTripleId(int iUpdatedTripleId) {
		_iUpdatedTripleId = iUpdatedTripleId ;
	}
}
