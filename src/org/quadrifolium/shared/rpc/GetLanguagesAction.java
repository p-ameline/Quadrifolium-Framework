package org.quadrifolium.shared.rpc;

import net.customware.gwt.dispatch.shared.Action;

public class GetLanguagesAction implements Action<GetLanguagesResult> 
{	
	private String _sRoot ;
	
	public GetLanguagesAction() {
		super() ;
		
		_sRoot = "" ;
	}
	
	public GetLanguagesAction(String sRoot) 
	{
		_sRoot = sRoot ;
	}
	
	public String getRoot() {
		return _sRoot ;
	}
	public void setRoot(String sRoot) {
		_sRoot = sRoot ;
	}
}
