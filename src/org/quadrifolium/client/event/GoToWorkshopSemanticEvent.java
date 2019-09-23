package org.quadrifolium.client.event;

import org.quadrifolium.client.mvp.QuadrifoliumWorkshopPresenterModel;
import org.quadrifolium.client.mvp.QuadrifoliumWorkshopViewModel;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Panel;

/**
 * Message sent when a workshop wants to enroll a semantic management component
 * 
 * @author Philippe
 *
 */
public class GoToWorkshopSemanticEvent extends GwtEvent<GoToWorkshopSemanticEventHandler> {
	
	public static Type<GoToWorkshopSemanticEventHandler> TYPE = new Type<GoToWorkshopSemanticEventHandler>() ;
	
	private Panel                                                             _workspace ;
	private QuadrifoliumWorkshopPresenterModel<QuadrifoliumWorkshopViewModel> _parent ;
	
	public static Type<GoToWorkshopSemanticEventHandler> getType() 
	{
		if (null == TYPE)
			TYPE = new Type<GoToWorkshopSemanticEventHandler>() ;
		return TYPE ;
	}
	
	public GoToWorkshopSemanticEvent(QuadrifoliumWorkshopPresenterModel<QuadrifoliumWorkshopViewModel> parent, Panel workspace) 
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
	protected void dispatch(GoToWorkshopSemanticEventHandler handler) {
		handler.onGoToWorkshopSemantic(this) ;
	}

	@Override
	public Type<GoToWorkshopSemanticEventHandler> getAssociatedType() {
		return TYPE;
	}
}
