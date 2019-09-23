package org.quadrifolium.shared.rpc4ontology;

import java.util.ArrayList;

import org.quadrifolium.shared.ontology.LemmaWithInflections;

import net.customware.gwt.dispatch.shared.Result;

public class GetFullSynonymsForConceptResult implements Result
{
  private ArrayList<LemmaWithInflections> _aSynonymsList = new ArrayList<LemmaWithInflections>() ;
	private String                          _sMessage ;
	
	/**
	 * */
	public GetFullSynonymsForConceptResult()
	{
		super() ;
		
		_sMessage = "" ;
	}

	/**
	 * @param sMessage
	 * */
	public GetFullSynonymsForConceptResult(final String sMessage)
	{
		super() ;
		
		_sMessage = sMessage ;
	}
	
	public String getMessage() {
		return _sMessage ;
	}
	public void setMessage(String sMessage) {
		_sMessage = sMessage ;
	}
	
	public ArrayList<LemmaWithInflections> getSynonymsArray() {
		return _aSynonymsList ;
	}
	
	public void addSynonym(final LemmaWithInflections synonym) {
		_aSynonymsList.add(new LemmaWithInflections(synonym)) ;
	}
}
