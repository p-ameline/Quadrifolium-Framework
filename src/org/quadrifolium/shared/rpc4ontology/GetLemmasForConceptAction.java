package org.quadrifolium.shared.rpc4ontology;

import net.customware.gwt.dispatch.shared.Action;

/**
 * Object used to get a list of Lemma records from a code 
 */
public class GetLemmasForConceptAction implements Action<GetLemmasForConceptResult> 
{
  private int    _iUserId ;
	private String _sLanguage ;
	private String _sConceptCode ;

	public GetLemmasForConceptAction(final int iUserId, final String sLanguage, final String sConceptCode) 
	{
		super() ;
		
		_iUserId      = iUserId ;
		_sLanguage    = sLanguage ;
		_sConceptCode = sConceptCode ;
	}

	public GetLemmasForConceptAction() 
	{
		super() ;
		
		_iUserId      = -1 ;
		_sLanguage    = "" ;
		_sConceptCode = "" ;
	}

	public String getLanguage() {
		return _sLanguage ;
	}

	public String getConceptCode() {
		return _sConceptCode ;
	}
	
	public int getUserId() {
		return _iUserId ;
	}
}
