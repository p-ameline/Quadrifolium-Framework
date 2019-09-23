package org.quadrifolium.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Message send by the login component in order to assert that login and password have been entered
 * 
 * @author Philippe
 *
 */
public class LoginSentEvent extends GwtEvent<LoginSentEventHandler> 
{	
	public static Type<LoginSentEventHandler> TYPE = new Type<LoginSentEventHandler>();
	
	private final String      _sName ;
	private final String      _sPassword ;
	private       SimplePanel _workspace ;
	private       FlexTable   _logintable ;
	
	public static Type<LoginSentEventHandler> getType() 
	{
		if (null == TYPE)
			TYPE = new Type<LoginSentEventHandler>() ;
		return TYPE ;
	}
	
	public LoginSentEvent(final String name, final String password) 
	{
		_sName     = name ;
		_sPassword = password ;
	}
	
	public FlexTable getLoginTable(){
		return _logintable ;
	}
	
	public SimplePanel getWorkspace(){
		return _workspace ;
	}
	public String getName() {
		return _sName ;
	}
	
	public String getPassword() {
		return _sPassword ;
	}
	
	@Override
	protected void dispatch(LoginSentEventHandler handler) {
		handler.onSendLogin(this) ;
	}

	@Override
	public Type<LoginSentEventHandler> getAssociatedType() {
		// TODO Auto-generated method stub
		return TYPE ;
	}
}
