package org.quadrifolium.client.mvp;

import org.quadrifolium.client.loc.QuadrifoliumConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import net.customware.gwt.presenter.client.widget.WidgetDisplay;

public class QuadrifoliumBaseDisplay implements WidgetDisplay 
{
	protected final QuadrifoliumConstants constants = GWT.create(QuadrifoliumConstants.class) ;
		
	public static void switchToWaitCursor() {
		RootPanel.getBodyElement().setAttribute("cursor", "wait") ;
	}

	public static void switchToDefaultCursor() {
    RootPanel.getBodyElement().setAttribute("cursor", "default") ;
	}

	@Override
	public Widget asWidget() {
		// TODO Auto-generated method stub
		return null;
	}
}
