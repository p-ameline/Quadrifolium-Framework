package org.quadrifolium.shared.rpc4ontology;

import net.customware.gwt.dispatch.shared.Action;

/**
 * Object used to get a list of Lemma records from a code 
 */
public class GetFullSynonymsForConceptAction implements Action<GetFullSynonymsForConceptResult> 
{
  private int    _iUserId ;
  private String _sDisplayLanguage ;
  
	private String _sConceptCode ;
	private String _sQueryLanguage ;

	/**
	 * Plain vanilla constructor
	 */
	public GetFullSynonymsForConceptAction(final int iUserId, final String sDisplayLanguage, final String sConceptCode, final String sQueryLanguage) 
	{
		super() ;
		
		_iUserId          = iUserId ;
		_sDisplayLanguage = sDisplayLanguage ;
		
		_sConceptCode     = sConceptCode ;
		_sQueryLanguage   = sQueryLanguage ;
	}

	/**
	 * No-args constructor, mandatory for serializable objects
	 */
	public GetFullSynonymsForConceptAction() 
	{
		super() ;
		
		_iUserId          = -1 ;
		_sDisplayLanguage = "" ;
		
		_sConceptCode     = "" ;
		_sQueryLanguage   = "" ;
	}

	public int getUserId() {
		return _iUserId ;
	}
	
	public String getDisplayLanguage() {
		return _sDisplayLanguage ;
	}

	public String getConceptCode() {
		return _sConceptCode ;
	}
	
	public String getQueryLanguage() {
		return _sQueryLanguage ;
	}
}
