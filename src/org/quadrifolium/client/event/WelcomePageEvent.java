package org.quadrifolium.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Event sent to the Welcome page to tell it to load
 * 
 * @author Philippe
 *
 */
public class WelcomePageEvent extends GwtEvent<WelcomePageEventHandler> {
	
	public static Type<WelcomePageEventHandler> TYPE = new Type<WelcomePageEventHandler>();
	
	private FlowPanel _workspace ;
	
	public static Type<WelcomePageEventHandler> getType() 
	{
		if (null == TYPE)
			TYPE = new Type<WelcomePageEventHandler>();
		return TYPE;
	}
	
	public WelcomePageEvent(FlowPanel flowPanel) {
		_workspace = flowPanel ;
	}
	
	public FlowPanel getWorkspace() {
		return _workspace ;
	}
	
	@Override
	protected void dispatch(WelcomePageEventHandler handler) {
		handler.onWelcome(this);
	}

	@Override
	public Type<WelcomePageEventHandler> getAssociatedType() {
		return TYPE;
	}
}
