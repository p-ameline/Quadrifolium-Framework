package org.quadrifolium.shared.rpc4ontology;

import org.quadrifolium.shared.rpc_util.SessionElements;

import net.customware.gwt.dispatch.shared.Action;

/**
 * Object used to get a list of Lemma records from a code 
 */
public class GetStemmaForConceptAction implements Action<GetStemmaForConceptResult> 
{
	private SessionElements _sessionElements ;
  
	private String          _sLanguage ;
	private String          _sConceptCode ;

	public GetStemmaForConceptAction(final SessionElements sessionElements, final String sLanguage, final String sConceptCode) 
	{
		super() ;
		
		_sessionElements = sessionElements ;
		_sLanguage       = sLanguage ;
		_sConceptCode    = sConceptCode ;
	}

	public GetStemmaForConceptAction() 
	{
		super() ;
		
		_sessionElements = null ;
		_sLanguage       = "" ;
		_sConceptCode    = "" ;
	}

	public String getLanguage() {
		return _sLanguage ;
	}

	public String getConceptCode() {
		return _sConceptCode ;
	}
	
	public SessionElements getSessionElements() {
		return _sessionElements ;
	}
}
