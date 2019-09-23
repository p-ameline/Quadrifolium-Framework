package org.quadrifolium.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Message sent when login just occurred and it is time to open the working environment
 * 
 * @author Philippe
 *
 */
public class GoToLoginResponseEvent extends GwtEvent<GoToLoginResponseEventHandler> {
	
	public static Type<GoToLoginResponseEventHandler> TYPE = new Type<GoToLoginResponseEventHandler>();
	
	public static Type<GoToLoginResponseEventHandler> getType() 
	{
		if (null == TYPE)
			TYPE = new Type<GoToLoginResponseEventHandler>();
		return TYPE;
	}
	
	public GoToLoginResponseEvent(){	
	}
		
	@Override
	protected void dispatch(GoToLoginResponseEventHandler handler) {
		handler.onGoToLoginResponse(this) ;
	}

	@Override
	public Type<GoToLoginResponseEventHandler> getAssociatedType() {
		return TYPE;
	}
}
