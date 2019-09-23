package org.quadrifolium.client.mvp;

import net.customware.gwt.presenter.client.widget.WidgetDisplay;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class QuadrifoliumMainView extends Composite implements QuadrifoliumMainPresenter.Display 
{
	//private final TextBox name;
	private FlowPanel _body ;
	private FlowPanel _header ;
	private FlowPanel _command ;
	private FlowPanel _workspace ;
	private FlowPanel _footer ;

	public QuadrifoliumMainView() 
	{
		_body = new FlowPanel() ;
		_body.addStyleName("quadrifoliumBody") ;
		
		initHeader() ;
		initCommand() ;
		initWorkspace() ;
		initMainFooter() ;
		
		initWidget(_body) ;
	}	
	
	public void initHeader()
	{
		_header = new FlowPanel() ;
		_header.add(new Label("header")) ;
		_header.addStyleName("header") ;
		_body.add(_header) ;
	}
		
	public void initCommand()
	{
		_command = new FlowPanel() ;
		_command.addStyleName("header") ;
		_body.add(_command) ;
	}
	
	public void initWorkspace()
	{
		_workspace = new FlowPanel();
		_workspace.addStyleName("workspace");
		_workspace.add(new Label("workspace"));
		_body.add(_workspace) ;
	}
		
	public void initMainFooter()
	{
		HTML footerContent = new HTML("") ;
		_footer = new FlowPanel();
		_footer.addStyleName("footer");
		_footer.add(footerContent);
		_body.add(_footer) ;
	}
	
	@Override
	public FlowPanel getHeader() {
		return _header ;
	}
	
	@Override
	public FlowPanel getCommand() {
		return _command ;
	}
	
	@Override
	public FlowPanel getWorkspace() {
		return _workspace ;
	}
	
	@Override
	public FlowPanel getFooter() {
		return _footer ;
	}

	public void reset() {
		// Focus the cursor on the name field when the app loads
	}
	
	/**
	 * Returns this widget as the {@link WidgetDisplay#asWidget()} value.
	 */
	public Widget asWidget() {
		return this ;
	}
}
