package org.quadrifolium.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Panel;

/**
 * Message sent when login just occurred and it is time to open the working environment
 * 
 * @author Philippe
 *
 */
public class GoToWorkshopEvent extends GwtEvent<GoToWorkshopEventHandler> {
	
	public static Type<GoToWorkshopEventHandler> TYPE = new Type<GoToWorkshopEventHandler>();
	
	private Panel _workspace ;
	
	public static Type<GoToWorkshopEventHandler> getType() 
	{
		if (null == TYPE)
			TYPE = new Type<GoToWorkshopEventHandler>();
		return TYPE;
	}
	
	public GoToWorkshopEvent(Panel workspace) {	
		_workspace = workspace ;
	}
	
	public Panel getWorkspace(){
		return _workspace ;
	}
		
	@Override
	protected void dispatch(GoToWorkshopEventHandler handler) {
		handler.onGoToWorkshop(this) ;
	}

	@Override
	public Type<GoToWorkshopEventHandler> getAssociatedType() {
		return TYPE;
	}
}
