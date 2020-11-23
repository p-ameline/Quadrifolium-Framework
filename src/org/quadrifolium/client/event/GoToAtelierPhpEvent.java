package org.quadrifolium.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Message sent when login just occurred and it is time to open the working environment
 * 
 * @author Philippe
 *
 */
public class GoToAtelierPhpEvent extends GwtEvent<GoToAtelierPhpEventHandler> {
	
	public static Type<GoToAtelierPhpEventHandler> TYPE = new Type<GoToAtelierPhpEventHandler>();
	
	public static Type<GoToAtelierPhpEventHandler> getType() 
	{
		if (null == TYPE)
			TYPE = new Type<GoToAtelierPhpEventHandler>();
		return TYPE;
	}
	
	public GoToAtelierPhpEvent(){	
	}
		
	@Override
	protected void dispatch(GoToAtelierPhpEventHandler handler) {
		handler.onGoToAtelierPhp(this) ;
	}

	@Override
	public Type<GoToAtelierPhpEventHandler> getAssociatedType() {
		return TYPE;
	}
}
