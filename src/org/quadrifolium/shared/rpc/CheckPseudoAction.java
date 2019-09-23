package org.quadrifolium.shared.rpc;

import net.customware.gwt.dispatch.shared.Action;

public class CheckPseudoAction implements Action<CheckPseudoResult> 
{	
	private String _sPseudo ;
	
	public CheckPseudoAction() 
	{
		super() ;
		
		_sPseudo = "" ;
	}
	
	public CheckPseudoAction(final String sPseudo) 
	{
		_sPseudo = sPseudo ;
	}
	
	public String getPseudo() {
		return _sPseudo ;
	}
	public void setPseudo(final String sPseudo) {
		_sPseudo = sPseudo ;
	}
}
