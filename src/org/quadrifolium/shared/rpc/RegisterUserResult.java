package org.quadrifolium.shared.rpc;

import net.customware.gwt.dispatch.shared.Result;

public class RegisterUserResult implements Result 
{
	private boolean _bSuccess ;
	private String  _sMessage ;
	private String  _sGeneratedId ;
	
	public RegisterUserResult(){
		super() ;
		
		_bSuccess     = false ;
		_sMessage     = "" ;
		_sGeneratedId = "" ;
	}
	
	public RegisterUserResult(boolean bSucess) {
		_bSuccess     = bSucess ;
		_sMessage     = "" ;
		_sGeneratedId = "" ;
	}
	
	public void setSuccess(boolean bSuccess) {
		_bSuccess = bSuccess ;
	}
	public boolean wasSuccessful() {
		return _bSuccess ;
	}
	
	public void setMessage(final String sMessage) {
		_sMessage = sMessage ;
	}
	public String getMessage() {
		return _sMessage ;
	}
	
	public void setGeneratedId(final String sId) {
		_sGeneratedId = sId ;
	}
	public String getGeneratedId() {
		return _sGeneratedId ;
	}
}
