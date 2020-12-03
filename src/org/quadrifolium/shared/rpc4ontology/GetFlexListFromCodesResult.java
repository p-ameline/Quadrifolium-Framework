package org.quadrifolium.shared.rpc4ontology;

import java.util.ArrayList;

import net.customware.gwt.dispatch.shared.Result;

public class GetFlexListFromCodesResult implements Result
{
  private ArrayList<ResultElement> _aResults = new ArrayList<ResultElement>() ;
	private String                   _sMessage ;
	
	/**
	 * */
	public GetFlexListFromCodesResult()
	{
		super() ;
		
		_sMessage = "" ;
	}

	/**
	 * @param sMessage
	 * */
	public GetFlexListFromCodesResult(final String sMessage, final ArrayList<ResultElement> aResults)
	{
		super() ;
		
		_sMessage = sMessage ;
		
		if ((null != aResults) && (false == aResults.isEmpty()))
		  _aResults.addAll(aResults) ;
	}
	
	public String getMessage() {
		return _sMessage ;
	}
	public void setMessage(String sMessage) {
		_sMessage = sMessage ;
	}
	
	public ArrayList<ResultElement> getResults() {
		return _aResults ;
	}
	
	public void addResult(final ResultElement result)
	{
	  if ((null != result) && (false == _aResults.contains(result)))
	    _aResults.add(result) ;
	}
}
