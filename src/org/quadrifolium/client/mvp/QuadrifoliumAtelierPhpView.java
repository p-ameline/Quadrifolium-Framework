package org.quadrifolium.client.mvp;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class QuadrifoliumAtelierPhpView extends Composite implements QuadrifoliumAtelierPhpPresenter.Display 
{
	// private final LoginViewConstants constants = GWT.create(LoginViewConstants.class) ;
	
	private final FlowPanel _workspace ;

	public QuadrifoliumAtelierPhpView() 
	{
		// Main table
		//
		// FlexTable mainTable = new FlexTable() ;
		
		// Open in new tab
		// Window.open("https://quadrifolium.org/atelier", "_blank", "") ;
		
		// Workspace
		//		
		// _workspace = new HTMLPanel("<iframe src=\"https://quadrifolium.org/atelier\" style=\"width:600px; height:600px;\" >Unfortunately this content could not be displayed</iframe>") ;
			
		_workspace = new FlowPanel() ; 
		_workspace.addStyleName("atelierWorkspace") ;
		
		initWidget(_workspace) ;
	}

	@Override
	public FlowPanel getWorkspace() {
		return _workspace ;
	}
	
	@Override
	public void openPage(final String sUrl)
	{
		String sIFrameStyle = " style=\"width:100%; height:100%;\"" ;
		
		HTML atelier = new HTML("<iframe src=\"" + sUrl + "\"" + sIFrameStyle + " >Unfortunately this content could not be displayed</iframe>") ;
		atelier.addStyleName("atelierArea") ;
		_workspace.add(atelier) ;
	}
	
	public void reset() {	
	}

	public Widget asWidget() {
		return this;
	}
}
