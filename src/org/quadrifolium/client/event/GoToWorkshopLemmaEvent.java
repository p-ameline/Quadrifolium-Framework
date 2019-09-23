package org.quadrifolium.client.event;

import org.quadrifolium.client.mvp.QuadrifoliumWorkshopPresenterModel;
import org.quadrifolium.client.mvp.QuadrifoliumWorkshopViewModel;
import org.quadrifolium.client.mvp.WorkshopInterfaceModel;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Panel;

/**
 * Message sent when a workshop wants to enroll a lemma management component
 * 
 * @author Philippe
 *
 */
public class GoToWorkshopLemmaEvent extends GwtEvent<GoToWorkshopLemmaEventHandler> {
	
	public static Type<GoToWorkshopLemmaEventHandler> TYPE = new Type<GoToWorkshopLemmaEventHandler>() ;
	
	private Panel                                                             _workspace ;
	private QuadrifoliumWorkshopPresenterModel<QuadrifoliumWorkshopViewModel> _parent ;
	
	public static Type<GoToWorkshopLemmaEventHandler> getType() 
	{
		if (null == TYPE)
			TYPE = new Type<GoToWorkshopLemmaEventHandler>() ;
		return TYPE ;
	}
	
	public GoToWorkshopLemmaEvent(QuadrifoliumWorkshopPresenterModel<QuadrifoliumWorkshopViewModel> parent, Panel workspace) 
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
	protected void dispatch(GoToWorkshopLemmaEventHandler handler) {
		handler.onGoToWorkshopLemma(this) ;
	}

	@Override
	public Type<GoToWorkshopLemmaEventHandler> getAssociatedType() {
		return TYPE;
	}
}
