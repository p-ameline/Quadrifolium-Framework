package org.quadrifolium.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Event sent to the Welcome text page to tell it to load
 * 
 * @author Philippe
 *
 */
public class WelcomeTextEvent extends GwtEvent<WelcomeTextEventHandler> {
	
	public static Type<WelcomeTextEventHandler> TYPE = new Type<WelcomeTextEventHandler>() ;
	
	private FlowPanel _workspace ;
	private String    _sLanguage ;
	
	public static Type<WelcomeTextEventHandler> getType() 
	{
		if (null == TYPE)
			TYPE = new Type<WelcomeTextEventHandler>() ;
		return TYPE ;
	}
	
	public WelcomeTextEvent(FlowPanel flowPanel, final String sLanguage)
	{
		_workspace = flowPanel ;
		_sLanguage = sLanguage ;
	}
	
	public FlowPanel getWorkspace() {
		return _workspace ;
	}

	public String getLanguage() {
		return _sLanguage ;
	}
	
	@Override
	protected void dispatch(WelcomeTextEventHandler handler) {
		handler.onWelcomeText(this) ;
	}

	@Override
	public Type<WelcomeTextEventHandler> getAssociatedType() {
		return TYPE ;
	}
}
