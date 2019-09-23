package org.quadrifolium.shared.rpc;

import net.customware.gwt.dispatch.shared.Action;

public class RegisterUserAction implements Action<RegisterUserResult> 
{	
	private String _sPseudo ;
	private String _sPassword ;
	private String _sEmail ;
	private String _sLanguage ;
	
	public RegisterUserAction() {
		super() ;
		
		_sPseudo    = "" ;
		_sPassword  = "" ;
		_sEmail     = "" ;
		_sLanguage  = "" ;
	}
	
	public RegisterUserAction(final String sPseudo, final String sPassword, final String sEmail, final String sLanguage) 
	{
		_sPseudo    = sPseudo ;
		_sPassword  = sPassword ;
		_sEmail     = sEmail ;
		_sLanguage  = sLanguage ;
	}
	
	public String getPseudo() {
		return _sPseudo ;
	}
	public void setPseudo(final String sPseudo) {
		_sPseudo = sPseudo ;
	}

	public String getPassword() {
		return _sPassword ;
	}
	public void setPassword(final String sPassword) {
		_sPassword = sPassword ;
	}
	
	public String getEmail() {
		return _sEmail ;
	}
	public void setEmail(final String sEmail) {
		_sEmail = sEmail ;
	}

	public String getLanguage() {
		return _sLanguage ;
	}
	public void setLanguage(final String sLanguage) {
		_sLanguage = sLanguage ;
	}
}
