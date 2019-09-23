package org.quadrifolium.client.mvp;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;

import org.quadrifolium.client.event.WelcomePageEvent;
import org.quadrifolium.client.event.WelcomePageEventHandler;
import org.quadrifolium.client.global.QuadrifoliumSupervisor;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.inject.Inject;

/**
 * Presenter for the "read only" interface that displays a concept
 * 
 * @author Philippe
 *
 */
public class QuadrifoliumWelcomePagePresenter extends QuadrifoliumWorkshopPresenterModel<QuadrifoliumWelcomePagePresenter.Display> 
{	
	public interface Display extends WorkshopInterfaceModel 
	{
		public Label             getWelcomeLabel() ;
	}
	
	@Inject
	public QuadrifoliumWelcomePagePresenter(final Display display, 
			                                    final EventBus eventBus,
			                                    final DispatchAsync dispatcher,
			                                    final QuadrifoliumSupervisor supervisor) 
	{
		super(display, eventBus, dispatcher, supervisor) ;
		
		bind() ;
	}
	
	/**
	 * Binds Event Bus events that are handled by this presenter
	 */
	@Override
	protected void onBind() 
	{
		Log.debug("Entering WelcomePagePresenter::onBind()") ;
		super.onBind() ;
		
		eventBus.addHandler(WelcomePageEvent.TYPE, new WelcomePageEventHandler() {
			public void onWelcome(WelcomePageEvent event)
			{
				Log.debug("Loading Welcome Page (read only workshop)") ;
				
				// Insert the display in workspace
				//
				event.getWorkspace().clear() ;
				FlowPanel WelcomeWorkspace = event.getWorkspace() ;
				WelcomeWorkspace.add(display.asWidget()) ;
				
				// Ask components to refresh 
				//
				UpdateDisplay() ;
			}
		});		
	}

	protected void UpdateRelationsLeaf()
	{		
	}
	
	protected void UpdateJobLeaf()
	{
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
