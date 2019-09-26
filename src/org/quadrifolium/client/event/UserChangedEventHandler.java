package org.quadrifolium.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface UserChangedEventHandler extends EventHandler 
{
	void onUserChanged(UserChangedEvent event) ;
}