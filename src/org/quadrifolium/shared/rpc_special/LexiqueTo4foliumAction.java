package org.quadrifolium.shared.rpc_special;

import org.quadrifolium.shared.rpc_util.SessionElements;

import net.customware.gwt.dispatch.shared.Action;

public class LexiqueTo4foliumAction implements Action<LexiqueTo4foliumResult> 
{	
	private SessionElements _sessionElements ;

	public LexiqueTo4foliumAction(final int iPersonId, final String sToken) 
	{
		super() ;

		_sessionElements = new SessionElements(iPersonId, sToken) ;
	}

  protected LexiqueTo4foliumAction() 
	{
  	super() ;

		_sessionElements = new SessionElements() ;
	}

  public SessionElements getSessionElements() {
		return _sessionElements ;
	}
}
