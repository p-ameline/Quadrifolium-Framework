package org.quadrifolium.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Message sent to workshop components when the concept at work changed
 * 
 * @author Philippe
 *
 */
public class CommandDisplayTitleEvent extends GwtEvent<CommandDisplayTitleEventHandler>
{	
	public static Type<CommandDisplayTitleEventHandler> TYPE = new Type<CommandDisplayTitleEventHandler>() ;
	
	private String _sTitle ;
	
	public static Type<CommandDisplayTitleEventHandler> getType() 
	{
		if (null == TYPE)
			TYPE = new Type<CommandDisplayTitleEventHandler>() ;
		return TYPE ;
	}
	
	public CommandDisplayTitleEvent(final String sTitle) {	
		_sTitle = sTitle ;
	}
	
	public String getTitle(){
		return _sTitle ;
	}
		
	@Override
	protected void dispatch(CommandDisplayTitleEventHandler handler) {
		handler.onCommandDisplayTitle(this) ;
	}

	@Override
	public Type<CommandDisplayTitleEventHandler> getAssociatedType() {
		return TYPE;
	}
}
