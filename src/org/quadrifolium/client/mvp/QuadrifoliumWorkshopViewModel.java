package org.quadrifolium.client.mvp;


import org.quadrifolium.client.loc.QuadrifoliumConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class QuadrifoliumWorkshopViewModel extends Composite implements WorkshopInterfaceModel 
{
	private final QuadrifoliumConstants constants = GWT.create(QuadrifoliumConstants.class) ;
	
	private final FlowPanel        _workspace ;
	
	// Concept search area
	//
	private       SimplePanel      _commandPanel ;
	
	// Concept display area
	//
	private final SplitLayoutPanel _displayPanel ;
	
	private       SimplePanel      _lemmasPanel ;
	private       SimplePanel      _semanticsPanel ;
	private       SimplePanel      _definitionsPanel ;
		
	public QuadrifoliumWorkshopViewModel() 
	{
		// Workspace
		//
		_workspace = new FlowPanel() ;
		_workspace.addStyleName("worshopWorkspace") ;
				
		// Concept search panel
		//
		_commandPanel = new SimplePanel() ;
		_commandPanel.addStyleName("commandPanel") ;
		
		_workspace.add(_commandPanel) ;
			
		// Display area
		//
		_displayPanel = new SplitLayoutPanel() ;
		_displayPanel.addStyleName("displayPanel") ;
			
		// Prepare the lemmas panel
		//
		_lemmasPanel = new SimplePanel() ;
		_displayPanel.addWest(_lemmasPanel, 500) ;
		
		// Prepare the definitions panel
		//
		_definitionsPanel = new SimplePanel() ;
		_displayPanel.addNorth(_definitionsPanel, 250) ;
		
		// Prepare the semantics panel
		//
		_semanticsPanel = new SimplePanel() ;
		_displayPanel.add(_semanticsPanel) ;
		
		_workspace.add(_displayPanel) ;
			
		initWidget(_workspace) ;
	}
	
	@Override
	public void addCommandView(Panel commandView) {
		_commandPanel.add(commandView) ;
	}
	
	@Override
	public void addLemmasView(Panel lemmasView) {
		_lemmasPanel.add(lemmasView) ;
	}
	
	@Override
	public void addSemanticsView(Panel semanticsView) {
		_semanticsPanel.add(semanticsView) ;
	}
	
	@Override
	public void addDefinitionsView(Panel semanticsView) {
		_definitionsPanel.add(semanticsView) ;
	}
	
	@Override
	public FlowPanel getWorkspace() {
		return _workspace ;
	}
	
	@Override
	public SimplePanel getLemmasWorkspace() {
		return _lemmasPanel ;
	}
	
	@Override
	public SimplePanel getSemanticsWorkspace() {
		return _semanticsPanel ;
	}
	
	@Override
	public SimplePanel getDefinitionsWorkspace() {
		return _definitionsPanel ;
	}
	
	public void reset() {	
	}

	public Widget asWidget() {
		return this;
	}
}
