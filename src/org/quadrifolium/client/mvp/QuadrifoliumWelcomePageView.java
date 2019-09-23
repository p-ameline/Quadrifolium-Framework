package org.quadrifolium.client.mvp;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;


/**
 * View for the "read only" interface that displays a concept
 * 
 * @author Philippe
 *
 */
public class QuadrifoliumWelcomePageView extends QuadrifoliumWorkshopViewModel implements QuadrifoliumWelcomePagePresenter.Display 
{
	// private final QuadrifoliumConstants constants = GWT.create(QuadrifoliumConstants.class) ;
		
	public QuadrifoliumWelcomePageView() 
	{
		super() ;
	}
	
	@Override
	public Label getWelcomeLabel() {
		return null ;
	}
	
	public void reset() {	
	}

	public Widget asWidget() {
		return this;
	}
}
