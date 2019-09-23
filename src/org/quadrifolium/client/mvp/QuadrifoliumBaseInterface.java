package org.quadrifolium.client.mvp;

import org.quadrifolium.client.widgets.FlexTextBox;

import com.google.gwt.event.dom.client.HasClickHandlers;

import net.customware.gwt.presenter.client.widget.WidgetDisplay;

public interface QuadrifoliumBaseInterface extends WidgetDisplay
{
	public FlexTextBox      getTermChangeTextBox() ;
	public HasClickHandlers getTermChangeButton() ;
}
