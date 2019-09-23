package org.quadrifolium.client.mvp;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.widget.WidgetDisplay;
import net.customware.gwt.presenter.client.widget.WidgetPresenter;

import org.quadrifolium.client.event.WelcomeTextEvent;
import org.quadrifolium.client.event.WelcomeTextEventHandler;
import org.quadrifolium.client.global.QuadrifoliumSupervisor;
import org.quadrifolium.shared.rpc.GetWelcomeTextAction;
import org.quadrifolium.shared.rpc.GetWelcomeTextResult;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.inject.Inject;

public class QuadrifoliumWelcomeTextPresenter extends WidgetPresenter<QuadrifoliumWelcomeTextPresenter.Display> 
{	
	public interface Display extends WidgetDisplay 
	{			
		public FlowPanel getWorkspace() ;
		public void      displayText(final String sText) ;
	}

	private final DispatchAsync          _dispatcher ;
	private final QuadrifoliumSupervisor _supervisor ;
	
	@Inject
	public QuadrifoliumWelcomeTextPresenter(final Display display, 
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
		Log.debug("Entering WelcomeTextPresenter::onBind()") ;
		
		eventBus.addHandler(WelcomeTextEvent.TYPE, new WelcomeTextEventHandler() {
			public void onWelcomeText(WelcomeTextEvent event)
			{
				Log.debug("Loading Welcome Page") ;
				// RootPanel.get().clear();
				// RootPanel.get().add(display.asWidget());
				event.getWorkspace().clear() ;
				FlowPanel WelcomeWorkspace = event.getWorkspace() ;
				WelcomeWorkspace.add(display.asWidget()) ;
				
				loadText(event.getLanguage()) ;
			}
		});
	}
	
	/**
	 * Asks the server for the welcome text for a given language 
	 * 
	 * @param sLanguage
	 */
	public void loadText(final String sLanguage)
	{
		_dispatcher.execute(new GetWelcomeTextAction(sLanguage), new LoadTextCallback()) ;
	}
		
	/**
	 * Callback function called when the server answers to login attempt
	 * 
	 * @author Philippe
	 *
	 */
	public class LoadTextCallback implements AsyncCallback<GetWelcomeTextResult>
	{
		public LoadTextCallback() {
			super() ;
		}

		@Override
		public void onFailure(Throwable cause) {
			Log.error("Unhandled error", cause);
			Log.info("error from LoadTextCallback!!");
		}

		@Override
		public void onSuccess(GetWelcomeTextResult value) 
		{
			display.displayText(value.getWelcomeText()) ;
			
			String sDetectedLanguage = value.getServerDetectedLanguage() ;
			
			if (false == "".equals(sDetectedLanguage))
				_supervisor.setUserLanguage(sDetectedLanguage) ;
		}
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
