package org.quadrifolium.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Event sent to the Welcome text page to tell it to load
 * 
 * @author Philippe
 *
 */
public class AtelierPhpEvent extends GwtEvent<AtelierPhpEventHandler> {
	
	public static Type<AtelierPhpEventHandler> TYPE = new Type<AtelierPhpEventHandler>() ;
	
	private FlowPanel _workspace ;
	
	public static Type<AtelierPhpEventHandler> getType() 
	{
		if (null == TYPE)
			TYPE = new Type<AtelierPhpEventHandler>() ;
		return TYPE ;
	}
	
	public AtelierPhpEvent(FlowPanel flowPanel)
	{
		_workspace = flowPanel ;
	}
	
	public FlowPanel getWorkspace() {
		return _workspace ;
	}

	@Override
	protected void dispatch(AtelierPhpEventHandler handler) {
		handler.onOpenAtelierPhp(this) ;
	}

	@Override
	public Type<AtelierPhpEventHandler> getAssociatedType() {
		return TYPE ;
	}
}
