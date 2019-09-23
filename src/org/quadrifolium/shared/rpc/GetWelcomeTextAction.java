package org.quadrifolium.shared.rpc;

import net.customware.gwt.dispatch.shared.Action;

public class GetWelcomeTextAction implements Action<GetWelcomeTextResult> 
{	
	protected String _sLanguage ;
	
	/**
	 * Void constructor - for serialization process only
	 */
	public GetWelcomeTextAction() {
		this("") ;
	}
	
	/**
	 * Plain vanilla constructor
	 */
	public GetWelcomeTextAction(final String sLanguage) 
	{
		super() ;
		_sLanguage = sLanguage ;
	}
	
	public String getLanguage() {
		return _sLanguage ;
	}
	public void setLanguage(final String sLanguage) {
		_sLanguage = sLanguage ;
	}
}
