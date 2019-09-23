package org.quadrifolium.shared.rpc;

import net.customware.gwt.dispatch.shared.Result;

public class GetWelcomeTextResult implements Result 
{
	private String _sUserLanguage ;
	private String _sServerDetectedLanguage ;
	private String _sReturnedTextLanguage ;
	private String _sWelcomeText ;
	
	/**
	 * Void constructor, for serialization purposes
	 */
	public GetWelcomeTextResult() {
		this("", "", "", "") ;
	}
	
	/**
	 * Plain vanilla contructor 
	 */
	public GetWelcomeTextResult(final String sWelcomeText, final String sReturnedTextLanguage, final String sServerDetectedLanguage, final String sUserLanguage)
	{
		super() ;
		
		_sUserLanguage           = sUserLanguage ;
		_sServerDetectedLanguage = sServerDetectedLanguage ;
		_sReturnedTextLanguage   = sReturnedTextLanguage ;
		_sWelcomeText            = sWelcomeText ;
	}
	
	public void setUserLanguage(final String sUserLanguage) {
		_sUserLanguage = sUserLanguage ;
	}
	public String getUserLanguage() {
		return _sUserLanguage ; 
	}
	
	public void setWelcomeText(final String sWelcomeText) {
		_sWelcomeText = sWelcomeText ;
	}
	public String getWelcomeText() {
		return _sWelcomeText ; 
	}
	
	public void setServerDetectedLanguage(final String sServerDetectedLanguage) {
		_sServerDetectedLanguage = sServerDetectedLanguage ;
	}
	public String getServerDetectedLanguage() {
		return _sServerDetectedLanguage ; 
	}
	
	public void setReturnedTextLanguage(final String sReturnedTextLanguage) {
		_sReturnedTextLanguage = sReturnedTextLanguage ;
	}
	public String getReturnedTextLanguage() {
		return _sReturnedTextLanguage ; 
	}
}
