package org.quadrifolium.client.mvp;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.widget.WidgetDisplay;
import net.customware.gwt.presenter.client.widget.WidgetPresenter;

import org.quadrifolium.client.event.AtelierPhpEvent;
import org.quadrifolium.client.event.AtelierPhpEventHandler;
import org.quadrifolium.client.global.QuadrifoliumSupervisor;
import org.quadrifolium.shared.rpc_util.SessionElements;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.inject.Inject;

public class QuadrifoliumAtelierPhpPresenter extends WidgetPresenter<QuadrifoliumAtelierPhpPresenter.Display> 
{	
	public interface Display extends WidgetDisplay 
	{			
		public FlowPanel getWorkspace() ;
		public void      openPage(final String sUrl) ;
	}

	private final DispatchAsync          _dispatcher ;
	private final QuadrifoliumSupervisor _supervisor ;
	
	@Inject
	public QuadrifoliumAtelierPhpPresenter(final Display display, 
			                                   final EventBus eventBus,
			                                   final DispatchAsync dispatcher,
			                                   final QuadrifoliumSupervisor supervisor) 
	{
		super(display, eventBus) ;
		
		_dispatcher = dispatcher ;
		_supervisor = supervisor ;
		
		bind() ;
	}
	
	/**
	 * Try to send the greeting message
	 */
	@Override
	protected void onBind() 
	{
		Log.debug("Entering AtelierPhpPresenter::onBind()") ;
		
		eventBus.addHandler(AtelierPhpEvent.TYPE, new AtelierPhpEventHandler() {
			public void onOpenAtelierPhp(AtelierPhpEvent event)
			{
				Log.debug("Loading Atelier Php Page") ;
				// RootPanel.get().clear();
				// RootPanel.get().add(display.asWidget());
				event.getWorkspace().clear() ;
				FlowPanel WelcomeWorkspace = event.getWorkspace() ;
				WelcomeWorkspace.add(display.asWidget()) ;
				
				openAtelier() ;
			}
		});
	}
		
	protected void openAtelier()
	{
		SessionElements sessionElements = _supervisor.getSessionElements() ; 
		if (null == sessionElements)
			return ;
		
		String sUrl = "https://quadrifolium.org/atelier" ;
		sUrl += "?token=" + sessionElements.getToken() + "&session=" + sessionElements.getSessionId() ;
		
		display.openPage(sUrl) ;
	}
	
	@Override
	protected void onUnbind() {
		// Add unbind functionality here for more complex presenters.
	}

	public void refreshDisplay() {
		// This is called when the presenter should pull the latest data
		// from the server, etc. In this case.	
	}

	public void revealDisplay() {
		// Nothing to do. This is more useful in UI which may be buried
		// in a tab bar, tree, etc.
	}

	@Override
	protected void onRevealDisplay() {
		// TODO Auto-generated method stub
	}
}
