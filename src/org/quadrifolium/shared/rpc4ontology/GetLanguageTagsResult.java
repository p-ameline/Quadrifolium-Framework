package org.quadrifolium.shared.rpc4ontology;

import java.util.ArrayList;

import org.quadrifolium.shared.ontology.LanguageTag;

import net.customware.gwt.dispatch.shared.Result;

public class GetLanguageTagsResult implements Result
{
  private ArrayList<LanguageTag> _aTagsList = new ArrayList<LanguageTag>() ;
	private String                 _sMessage ;
	
	/**
	 * */
	public GetLanguageTagsResult()
	{
		super() ;
		
		_sMessage = "" ;
	}

	/**
	 * @param sMessage
	 * */
	public GetLanguageTagsResult(final String sMessage)
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
	
	public ArrayList<LanguageTag> getTagsArray() {
		return _aTagsList ;
	}
	
	public void addTag(LanguageTag tag) {
		_aTagsList.add(new LanguageTag(tag)) ;
	}
	
	public boolean isEmpty() {
		return _aTagsList.isEmpty() ;
	}
}
