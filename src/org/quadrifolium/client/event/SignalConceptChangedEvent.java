package org.quadrifolium.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Message sent to the main presenter when the concept at work changed.
 * This message exists since, depending on context, it may be the moment to switch from "welcome text" to workshop mode
 * 
 * @author Philippe
 *
 */
public class SignalConceptChangedEvent extends GwtEvent<SignalConceptChangedEventHandler>
{	
	public static Type<SignalConceptChangedEventHandler> TYPE = new Type<SignalConceptChangedEventHandler>() ;
	
	public static Type<SignalConceptChangedEventHandler> getType() 
	{
		if (null == TYPE)
			TYPE = new Type<SignalConceptChangedEventHandler>() ;
		return TYPE ;
	}
	
	public SignalConceptChangedEvent() {	
	}
	
	@Override
	protected void dispatch(SignalConceptChangedEventHandler handler) {
		handler.onConceptChanged(this) ;
	}

	@Override
	public Type<SignalConceptChangedEventHandler> getAssociatedType() {
		return TYPE;
	}
}
