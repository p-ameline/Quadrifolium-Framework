package org.quadrifolium.shared.rpc4ontology;

import org.quadrifolium.shared.rpc_util.SessionElements;

import net.customware.gwt.dispatch.shared.Action;

/**
 * Object used to get a list of semantic network triples from a concept 
 */
public class GetSemanticTriplesAction implements Action<GetSemanticTriplesResult> 
{
	private SessionElements _sessionElements ;
	
  private String          _sDisplayLanguage ;
	private String          _sConceptCode ;

	/**
	 * Plain vanilla constructor
	 */
	public GetSemanticTriplesAction(final SessionElements sessionElements, final String sDisplayLanguage, final String sConceptCode) 
	{
		super() ;
		
		_sessionElements  = sessionElements ;
		_sDisplayLanguage = sDisplayLanguage ;
		_sConceptCode     = sConceptCode ;
	}

	/**
	 * No-args constructor, mandatory for serializable objects
	 */
	public GetSemanticTriplesAction() 
	{
		super() ;
		
		_sessionElements  = null ;
		_sDisplayLanguage = "" ;
		_sConceptCode     = "" ;
	}

	public SessionElements getSessionElements() {
		return _sessionElements ;
	}
	
	public String getDisplayLanguage() {
		return _sDisplayLanguage ;
	}

	public String getConceptCode() {
		return _sConceptCode ;
	}
}
