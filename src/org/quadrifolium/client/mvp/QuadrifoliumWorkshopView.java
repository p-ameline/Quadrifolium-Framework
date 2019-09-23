package org.quadrifolium.client.mvp;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Widget;

public class QuadrifoliumWorkshopView extends QuadrifoliumWorkshopViewModel implements QuadrifoliumWorkshopPresenter.Display 
{
	// private final QuadrifoliumConstants constants = GWT.create(QuadrifoliumConstants.class) ;
			
	public QuadrifoliumWorkshopView() 
	{
		super() ;
	}
	
	@Override
	public HasClickHandlers getNewConceptButton() {
		return null ;
	}
	
	public void reset() {	
	}

	public Widget asWidget() {
		return this;
	}
}
