package org.quadrifolium.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event sent when the user changed (for example when it just logged in)
 * 
 * @author Philippe
 *
 */
public class UserChangedEvent extends GwtEvent<UserChangedEventHandler> {
	
	public static Type<UserChangedEventHandler> TYPE = new Type<UserChangedEventHandler>();
	
	public static Type<UserChangedEventHandler> getType() 
	{
		if (null == TYPE)
			TYPE = new Type<UserChangedEventHandler>();
		return TYPE;
	}
	
	public UserChangedEvent(){	
	}
		
	@Override
	protected void dispatch(UserChangedEventHandler handler) {
		handler.onUserChanged(this) ;
	}

	@Override
	public Type<UserChangedEventHandler> getAssociatedType() {
		return TYPE;
	}
}
