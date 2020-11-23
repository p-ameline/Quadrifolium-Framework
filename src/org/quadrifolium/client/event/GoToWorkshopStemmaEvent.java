package org.quadrifolium.client.event;

import org.quadrifolium.client.mvp.QuadrifoliumWorkshopPresenterModel;
import org.quadrifolium.client.mvp.QuadrifoliumWorkshopViewModel;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Panel;

/**
 * Message sent when a workshop wants to enroll a stemma management component
 * 
 * @author Philippe
 *
 */
public class GoToWorkshopStemmaEvent extends GwtEvent<GoToWorkshopStemmaEventHandler> {
	
	public static Type<GoToWorkshopStemmaEventHandler> TYPE = new Type<GoToWorkshopStemmaEventHandler>() ;
	
	private GoToWorkshopComponentContent _content = new GoToWorkshopComponentContent() ;
	
	public static Type<GoToWorkshopStemmaEventHandler> getType() 
	{
		if (null == TYPE)
			TYPE = new Type<GoToWorkshopStemmaEventHandler>() ;
		return TYPE ;
	}
	
	public GoToWorkshopStemmaEvent(QuadrifoliumWorkshopPresenterModel<QuadrifoliumWorkshopViewModel> parent, Panel workspace) 
	{	
		_content.setWorkspace(workspace) ;
		_content.setParent(parent) ;
	}

	public GoToWorkshopComponentContent getContent() {
		return _content ;
	}
	
	public Panel getWorkspace() {
		return _content.getWorkspace() ;
	}
		
	public QuadrifoliumWorkshopPresenterModel<QuadrifoliumWorkshopViewModel> getParent() {
		return _content.getParent() ;
	}
	
	@Override
	protected void dispatch(GoToWorkshopStemmaEventHandler handler) {
		handler.onGoToWorkshopStemma(this) ;
	}

	@Override
	public Type<GoToWorkshopStemmaEventHandler> getAssociatedType() {
		return TYPE;
	}
}
