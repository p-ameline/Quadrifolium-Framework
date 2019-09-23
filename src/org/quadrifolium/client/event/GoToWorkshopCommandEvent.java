package org.quadrifolium.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Panel;

/**
 * Message sent when a workshop wants to enroll a definition management component
 * 
 * @author Philippe
 *
 */
public class GoToWorkshopCommandEvent extends GwtEvent<GoToWorkshopCommandEventHandler> {
	
	public static Type<GoToWorkshopCommandEventHandler> TYPE = new Type<GoToWorkshopCommandEventHandler>() ;
	
	private Panel  _workspace ;
	private String _sConcept ;
	
	public static Type<GoToWorkshopCommandEventHandler> getType() 
	{
		if (null == TYPE)
			TYPE = new Type<GoToWorkshopCommandEventHandler>() ;
		return TYPE ;
	}
	
	public GoToWorkshopCommandEvent(Panel workspace, final String sConcept)
	{	
		_workspace = workspace ;
		_sConcept  = sConcept ;
	}
	
	public Panel getWorkspace() {
		return _workspace ;
	}
		
	public String getConcept() {
		return _sConcept ;
	}
	
	@Override
	protected void dispatch(GoToWorkshopCommandEventHandler handler) {
		handler.onGoToWorkshopCommand(this) ;
	}

	@Override
	public Type<GoToWorkshopCommandEventHandler> getAssociatedType() {
		return TYPE;
	}
}
