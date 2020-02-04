package org.quadrifolium.client.event;

import org.quadrifolium.client.mvp.QuadrifoliumWorkshopPresenterModel;
import org.quadrifolium.client.mvp.QuadrifoliumWorkshopViewModel;

import com.google.gwt.user.client.ui.Panel;

/**
 * Content of messages sent when a workshop wants to enroll a component
 * 
 * @author Philippe
 *
 */
public class GoToWorkshopComponentContent
{	
	private Panel                                                             _workspace ;
	private QuadrifoliumWorkshopPresenterModel<QuadrifoliumWorkshopViewModel> _parent ;
	
	public GoToWorkshopComponentContent() 
	{	
		_workspace = null ;
		_parent    = null ;
	}
	
	public GoToWorkshopComponentContent(QuadrifoliumWorkshopPresenterModel<QuadrifoliumWorkshopViewModel> parent, Panel workspace) 
	{	
		_workspace = workspace ;
		_parent    = parent ;
	}
	
	public Panel getWorkspace() {
		return _workspace ;
	}
	public void setWorkspace(Panel workspace) {
		_workspace = workspace ;
	}
		
	public QuadrifoliumWorkshopPresenterModel<QuadrifoliumWorkshopViewModel> getParent() {
		return _parent ;
	}
	public void setParent(QuadrifoliumWorkshopPresenterModel<QuadrifoliumWorkshopViewModel> parent) {
		_parent = parent ;
	}
}
