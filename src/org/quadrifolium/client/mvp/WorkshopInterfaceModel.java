package org.quadrifolium.client.mvp;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;

import net.customware.gwt.presenter.client.widget.WidgetDisplay;

public interface WorkshopInterfaceModel extends WidgetDisplay
{
	public FlowPanel   getWorkspace() ;
	public SimplePanel getLemmasWorkspace() ;
	public SimplePanel getSemanticsWorkspace() ;
	public SimplePanel getDefinitionsWorkspace() ;
	public SimplePanel getStemmaWorkspace() ;
	
	public void        addCommandView(Panel commandView) ;
	public void        addLemmasView(Panel lemmasView) ;
	public void        addSemanticsView(Panel semanticsView) ;
	public void        addDefinitionsView(Panel definitionsView) ;
	public void        addStemmaView(Panel stemmaView) ;
}
