package org.quadrifolium.shared.rpc;

import net.customware.gwt.dispatch.shared.Action;

public class IsValidPseudoAction implements Action<IsValidPseudoResult> 
{	
	private String _sPseudo ;
	
	public IsValidPseudoAction()
	{
		super() ;
		
		_sPseudo = "" ;
	}
	
	public IsValidPseudoAction(final String sPseudo) {
		_sPseudo = sPseudo ;
	}
	
	public String getPseudo() {
		return _sPseudo ;
	}
	public void setPseudo(final String sPseudo) {
		_sPseudo = sPseudo ;
	}
}
