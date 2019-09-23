package org.quadrifolium.shared.rpc;

import net.customware.gwt.dispatch.shared.Action;

public class SendLoginAction implements Action<SendLoginResult> 
{
	private String _sIdentifier ;
	private String _sPass ;

	public SendLoginAction(final String id, final String encryptedPass) 
	{
		_sIdentifier = id ;
		_sPass       = encryptedPass ;
	}

  public SendLoginAction() 
	{
  	_sIdentifier = "" ;
  	_sPass       = "" ;
	}

	public String getIdentifier() {
		return _sIdentifier ;
	}
	
	public String getEncryptedPassword() {
		return _sPass ;
	}	
}
