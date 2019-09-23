package org.quadrifolium.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Message sent to workshop components when the concept at work changed
 * 
 * @author Philippe
 *
 */
public class CommandLoadLanguagesEvent extends GwtEvent<CommandLoadLanguagesEventHandler>
{	
	public static Type<CommandLoadLanguagesEventHandler> TYPE = new Type<CommandLoadLanguagesEventHandler>() ;
	
	public static Type<CommandLoadLanguagesEventHandler> getType() 
	{
		if (null == TYPE)
			TYPE = new Type<CommandLoadLanguagesEventHandler>() ;
		return TYPE ;
	}
	
	public CommandLoadLanguagesEvent() {	
	}
	
	@Override
	protected void dispatch(CommandLoadLanguagesEventHandler handler) {
		handler.onCommandLoadLanguages(this) ;
	}

	@Override
	public Type<CommandLoadLanguagesEventHandler> getAssociatedType() {
		return TYPE ;
	}
}
