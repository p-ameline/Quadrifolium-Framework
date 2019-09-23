package org.quadrifolium.shared.rpc4ontology;

import net.customware.gwt.dispatch.shared.Action;

/**
 * Object used to get a list of semantic network triples from a concept 
 */
public class GetSemanticTriplesAction implements Action<GetSemanticTriplesResult> 
{
  private int    _iUserId ;
  private String _sDisplayLanguage ;
	private String _sConceptCode ;

	/**
	 * Plain vanilla constructor
	 */
	public GetSemanticTriplesAction(final int iUserId, final String sDisplayLanguage, final String sConceptCode) 
	{
		super() ;
		
		_iUserId          = iUserId ;
		_sDisplayLanguage = sDisplayLanguage ;
		_sConceptCode     = sConceptCode ;
	}

	/**
	 * No-args constructor, mandatory for serializable objects
	 */
	public GetSemanticTriplesAction() 
	{
		super() ;
		
		_iUserId          = -1 ;
		_sDisplayLanguage = "" ;
		_sConceptCode     = "" ;
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
}
