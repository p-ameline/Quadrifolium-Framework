package org.quadrifolium.shared.rpc4ontology;

import com.ldv.shared.graph.LdvModelTree;

import net.customware.gwt.dispatch.shared.Result;

public class GetStemmaForConceptResult implements Result
{
  private LdvModelTree _stemma = new LdvModelTree() ;
	private String       _sMessage ;
	
	/**
	 * */
	public GetStemmaForConceptResult()
	{
		super() ;
		
		_sMessage = "" ;
	}

	/**
	 * @param sMessage
	 * */
	public GetStemmaForConceptResult(final String sMessage)
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
	
	public LdvModelTree getStemma() {
		return _stemma ;
	}
	
	public void setStemma(final LdvModelTree stemma) {
		_stemma.initFromModelTree(stemma) ;
	}
}
