package org.quadrifolium.shared.rpc_special;

import net.customware.gwt.dispatch.shared.Result;

public class LexiqueTo4foliumResult implements Result 
{	
	private String _sResult ;
	
	public LexiqueTo4foliumResult(final String sResult)
	{
		super() ;
		
		_sResult = sResult ;
	}
		
	public String getResult() {
		return _sResult ;
	}
}
