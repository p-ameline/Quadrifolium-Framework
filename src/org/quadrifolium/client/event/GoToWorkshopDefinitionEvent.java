package org.quadrifolium.client.event;

import org.quadrifolium.client.mvp.QuadrifoliumWorkshopPresenterModel;
import org.quadrifolium.client.mvp.QuadrifoliumWorkshopViewModel;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Panel;

/**
 * Message sent when a workshop wants to enroll a definition management component
 * 
 * @author Philippe
 *
 */
public class GoToWorkshopDefinitionEvent extends GwtEvent<GoToWorkshopDefinitionEventHandler> {
	
	public static Type<GoToWorkshopDefinitionEventHandler> TYPE = new Type<GoToWorkshopDefinitionEventHandler>() ;
	
	private Panel                                                             _workspace ;
	private QuadrifoliumWorkshopPresenterModel<QuadrifoliumWorkshopViewModel> _parent ;
	
	public static Type<GoToWorkshopDefinitionEventHandler> getType() 
	{
		if (null == TYPE)
			TYPE = new Type<GoToWorkshopDefinitionEventHandler>() ;
		return TYPE ;
	}
	
	public GoToWorkshopDefinitionEvent(QuadrifoliumWorkshopPresenterModel<QuadrifoliumWorkshopViewModel> parent, Panel workspace) 
	{	
		_workspace = workspace ;
		_parent    = parent ;
	}
	
	public Panel getWorkspace() {
		return _workspace ;
	}
		
	public QuadrifoliumWorkshopPresenterModel<QuadrifoliumWorkshopViewModel> getParent() {
		return _parent ;
	}
	
	@Override
	protected void dispatch(GoToWorkshopDefinitionEventHandler handler) {
		handler.onGoToWorkshopDefinition(this) ;
	}

	@Override
	public Type<GoToWorkshopDefinitionEventHandler> getAssociatedType() {
		return TYPE;
	}
}
