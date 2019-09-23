package org.quadrifolium.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Event that informs that a user successfully logged in
 * 
 * @author Philippe
 *
 */
public class LoginSuccessEvent extends GwtEvent<LoginSuccessEventHandler> 
{	
	public static Type<LoginSuccessEventHandler> TYPE = new Type<LoginSuccessEventHandler>();
	
	private FlowPanel _workspace ;
	
	public static Type<LoginSuccessEventHandler> getType() 
	{
		if (null == TYPE)
			TYPE = new Type<LoginSuccessEventHandler>();
		return TYPE;
	}
	
	public LoginSuccessEvent(final FlowPanel flowPanel) {
		_workspace = flowPanel ;
	}
	
	public FlowPanel getWorkspace(){
		return _workspace ;
	}
		
	@Override
	protected void dispatch(LoginSuccessEventHandler handler) {
		handler.onLoginSuccess(this);
	}

	@Override
	public Type<LoginSuccessEventHandler> getAssociatedType() {
		// TODO Auto-generated method stub
		return TYPE;
	}
}
