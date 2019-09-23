package org.quadrifolium.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface LoginSentEventHandler extends EventHandler 
{
	void onSendLogin(LoginSentEvent event) ;
}
