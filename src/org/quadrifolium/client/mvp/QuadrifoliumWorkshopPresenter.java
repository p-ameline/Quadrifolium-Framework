package org.quadrifolium.client.mvp;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;

import org.quadrifolium.client.event.GoToWorkshopEvent;
import org.quadrifolium.client.event.GoToWorkshopEventHandler;
import org.quadrifolium.client.global.QuadrifoliumSupervisor;
import org.quadrifolium.shared.rpc_special.LexiqueTo4foliumAction;
import org.quadrifolium.shared.rpc_special.LexiqueTo4foliumResult;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Panel;
import com.google.inject.Inject;

public class QuadrifoliumWorkshopPresenter extends QuadrifoliumWorkshopPresenterModel<QuadrifoliumWorkshopPresenter.Display> 
{	
	public interface Display extends WorkshopInterfaceModel 
	{
		public HasClickHandlers getNewConceptButton() ;
	}

	@Inject
	public QuadrifoliumWorkshopPresenter(final Display display, 
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
		Log.debug("Entering QuadrifoliumWorkshopPresenter::onBind()") ;
		super.onBind() ;
		
		// Message received when it is time to open the workshop
		//
		eventBus.addHandler(GoToWorkshopEvent.TYPE, new GoToWorkshopEventHandler() {
			public void onGoToWorkshop(GoToWorkshopEvent event)
			{
				Log.debug("Loading Workshop Page") ;

				Panel WelcomeWorkspace = event.getWorkspace() ;
				WelcomeWorkspace.clear() ;
				WelcomeWorkspace.add(display.asWidget()) ;
			}
		});
	}

/*
	protected void ConnectComponents()
	{
		// Create lemmas workshop
		//
		QuadrifoliumLemmasView lemmasView = new QuadrifoliumLemmasView() ;
		_lemmasPresenter = new QuadrifoliumLemmasPresenter(lemmasView, eventBus, _dispatcher, _supervisor, this) ;
		display.addLemmasView(lemmasView) ;
		
		// Create semantics workshop
		//
		QuadrifoliumSemanticsView semanticsView = new QuadrifoliumSemanticsView() ;
		_semanticsPresenter = new QuadrifoliumSemanticsPresenter(semanticsView, eventBus, _dispatcher, _supervisor, this) ;
		display.addSemanticsView(semanticsView) ;
		
		// Create definitions workshop
		//
		QuadrifoliumDefinitionsView definitionsView = new QuadrifoliumDefinitionsView() ;
		_definitionsPresenter = new QuadrifoliumDefinitionsPresenter(definitionsView, eventBus, _dispatcher, _supervisor, this) ;
		display.addDefinitionsView(definitionsView) ;
	}
*/
		
	protected void UpdateRelationsLeaf()
	{	
	}
	
	protected void UpdateJobLeaf()
	{
	}
	
	protected void lexiqueToQuadrifolium()
	{
		_dispatcher.execute(new LexiqueTo4foliumAction(_supervisor.getSessionElements()), new startProcessingCallback()) ;
	}
	
	/**
	 * Callback function called when the server answers to login attempt
	 * 
	 * @author Philippe
	 *
	 */
	public class startProcessingCallback implements AsyncCallback<LexiqueTo4foliumResult>
	{
		public startProcessingCallback() {
			super() ;
		}

		@Override
		public void onFailure(Throwable cause) {
			Log.error("Unhandled error", cause);
			Log.info("error from startProcessingCallback!!");
		}

		@Override
		public void onSuccess(LexiqueTo4foliumResult value) 
		{
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
