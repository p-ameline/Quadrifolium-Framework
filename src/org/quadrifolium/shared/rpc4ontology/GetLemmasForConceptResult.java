package org.quadrifolium.shared.rpc4ontology;

import java.util.ArrayList;

import org.quadrifolium.shared.ontology.Lemma;

import net.customware.gwt.dispatch.shared.Result;

public class GetLemmasForConceptResult implements Result
{
  private ArrayList<Lemma> _aLemmaList = new ArrayList<Lemma>() ;
	private String           _sMessage ;
	
	/**
	 * */
	public GetLemmasForConceptResult()
	{
		super() ;
		
		_sMessage = "" ;
	}

	/**
	 * @param sMessage
	 * */
	public GetLemmasForConceptResult(final String sMessage)
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
	
	public ArrayList<Lemma> getLemmaArray() {
		return _aLemmaList ;
	}
	
	public void addLemma(final Lemma lemma) {
		_aLemmaList.add(new Lemma(lemma)) ;
	}
}
