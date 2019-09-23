package org.quadrifolium.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Event bus message that tells the login page that it is time to load
 * 
 * @author Philippe
 *
 */
public class LoginPageEvent extends GwtEvent<LoginPageEventHandler> {
	
	public static Type<LoginPageEventHandler> TYPE = new Type<LoginPageEventHandler>();
	
	private FlowPanel Header;
	private FlowPanel Workspace;
	private FlowPanel Footer;

	public static Type<LoginPageEventHandler> getType() 
	{
		if (null == TYPE)
			TYPE = new Type<LoginPageEventHandler>();
		return TYPE;
	}
	
	public LoginPageEvent(FlowPanel flowPanel){
		this.Header = flowPanel;
	}
	
	public FlowPanel getHeader(){
		return Header ;
	}
	public FlowPanel getWorkspace(){
		return Workspace ;
	}
	public FlowPanel getFooter(){
		return Footer ;
	}
	
	@Override
	protected void dispatch(LoginPageEventHandler handler) {
		handler.onLogin(this);
	}

	@Override
	public Type<LoginPageEventHandler> getAssociatedType() {
		return TYPE;
	}
}
