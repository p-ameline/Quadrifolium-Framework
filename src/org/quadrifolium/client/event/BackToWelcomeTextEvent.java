package org.quadrifolium.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event sent when the user wants to get back to the welcome text page
 * 
 * @author Philippe
 *
 */
public class BackToWelcomeTextEvent extends GwtEvent<BackToWelcomeTextEventHandler> {
	
	public static Type<BackToWelcomeTextEventHandler> TYPE = new Type<BackToWelcomeTextEventHandler>() ;
	
	public static Type<BackToWelcomeTextEventHandler> getType() 
	{
		if (null == TYPE)
			TYPE = new Type<BackToWelcomeTextEventHandler>() ;
		return TYPE ; 
	}
	
	public BackToWelcomeTextEvent(){	
	}
		
	@Override
	protected void dispatch(BackToWelcomeTextEventHandler handler) {
		handler.onBackToWelcomeText(this) ;
	}

	@Override
	public Type<BackToWelcomeTextEventHandler> getAssociatedType() {
		return TYPE ;
	}
}
