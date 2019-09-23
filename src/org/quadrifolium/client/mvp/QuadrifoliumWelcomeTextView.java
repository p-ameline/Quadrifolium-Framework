package org.quadrifolium.client.mvp;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class QuadrifoliumWelcomeTextView extends Composite implements QuadrifoliumWelcomeTextPresenter.Display 
{
	// private final LoginViewConstants constants = GWT.create(LoginViewConstants.class) ;
	
	private final FlowPanel _workspace;

	public QuadrifoliumWelcomeTextView() 
	{
		// Main table
		//
		// FlexTable mainTable = new FlexTable() ;
		
		// Workspace
		//
		_workspace = new FlowPanel() ;
		_workspace.addStyleName("welcomeWorkspace") ;
		
		// HTML logo = new HTML("<img src=\"" + CoachingFitResources.INSTANCE.welcomeImg().getSafeUri() + "\">") ;
		HTML logo = new HTML("<img src=\"logo_Quadrifolium.gif\">") ;
		logo.addStyleName("logo") ;
		_workspace.add(logo) ;
		
		initWidget(_workspace) ;
	}

	@Override
	public FlowPanel getWorkspace() {
		return _workspace ;
	}

	@Override
	public void displayText(final String sText)
	{
		_workspace.clear() ;
		
		if ((null == sText) || "".equals(sText))
			return ;
		
		HTML welcomeText = new HTML(sText) ;
		welcomeText.addStyleName("welcomeText") ;
		
		_workspace.add(welcomeText) ;
	}
	
	public void reset() {	
	}

	public Widget asWidget() {
		return this;
	}
}
