package org.quadrifolium.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Panel;

public class RegisterSentEvent extends GwtEvent<RegisterSentEventHandler> 
{	
	public static Type<RegisterSentEventHandler> TYPE = new Type<RegisterSentEventHandler>();
	
	private Panel _workspace ;
	
	public static Type<RegisterSentEventHandler> getType() 
	{
		if (null == TYPE)
			TYPE = new Type<RegisterSentEventHandler>();
		return TYPE;
	}
	
	public RegisterSentEvent(Panel workspace) {
		_workspace = workspace ;
	}
	
	public Panel getWorkspace(){
		return _workspace ;
	}
	
	@Override
	protected void dispatch(RegisterSentEventHandler handler) {
		handler.onRegisterSend(this);
	}

	@Override
	public Type<RegisterSentEventHandler> getAssociatedType() {
		return TYPE;
	}
}
