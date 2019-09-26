package org.quadrifolium.client.mvp_components;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.widget.WidgetDisplay;
import net.customware.gwt.presenter.client.widget.WidgetPresenter;

import java.util.ArrayList;

import org.quadrifolium.client.event.GoToWorkshopSemanticEvent;
import org.quadrifolium.client.event.GoToWorkshopSemanticEventHandler;
import org.quadrifolium.client.event.WorkshopConceptChangedEvent;
import org.quadrifolium.client.event.WorkshopConceptChangedEventHandler;
import org.quadrifolium.client.global.QuadrifoliumSupervisor;
import org.quadrifolium.client.mvp.QuadrifoliumWorkshopPresenterModel;
import org.quadrifolium.client.mvp.QuadrifoliumWorkshopViewModel;
import org.quadrifolium.shared.ontology.TripleWithLabel;
import org.quadrifolium.shared.rpc4ontology.GetSemanticTriplesAction;
import org.quadrifolium.shared.rpc4ontology.GetSemanticTriplesResult;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Panel;
import com.google.inject.Inject;

public class QuadrifoliumSemanticsPresenter extends WidgetPresenter<QuadrifoliumSemanticsPresenter.Display> 
{	
	public interface Display extends WidgetDisplay 
	{			
		public void feedLeftSemanticTable(final ArrayList<TripleWithLabel> aTriples) ;
		public void feedRightSemanticTable(final ArrayList<TripleWithLabel> aTriples) ;
	}

	private final DispatchAsync                      _dispatcher ;
	private final QuadrifoliumSupervisor             _supervisor ;
	
	protected     QuadrifoliumWorkshopPresenterModel<QuadrifoliumWorkshopViewModel> _parent ;
	
	@Inject
	public QuadrifoliumSemanticsPresenter(final Display display, 
			                                  final EventBus eventBus,
			                                  final DispatchAsync dispatcher,
			                                  final QuadrifoliumSupervisor supervisor) 
	{
		super(display, eventBus) ;
		
		_dispatcher = dispatcher ;
		_supervisor = supervisor ;
		
		_parent     = null ;
		
		bind() ;
	}
	
	/**
	 * Binds Event Bus events that are handled by this presenter
	 */
	@Override
	protected void onBind() 
	{
		Log.debug("Entering QuadrifoliumWorkshopPresenter::onBind()") ;
		
		// Message received when it is time to open the workshop
		//
		eventBus.addHandler(GoToWorkshopSemanticEvent.TYPE, new GoToWorkshopSemanticEventHandler() {
			public void onGoToWorkshopSemantic(GoToWorkshopSemanticEvent event)
			{
				// If this presenter is already connected, discard this message
				//
				if (null != _parent)
					return ;
					
				// Connect this presenter to the workshop
				//
				_parent = event.getParent() ;
					
				Log.debug("Loading Workshop's semantic presenter Page") ;
				Panel WelcomeWorkspace = event.getWorkspace() ;
				WelcomeWorkspace.clear() ;
				WelcomeWorkspace.add(display.asWidget()) ;
			}
		});
		
		// Message received when the concept at work changed
		//
		eventBus.addHandler(WorkshopConceptChangedEvent.TYPE, new WorkshopConceptChangedEventHandler() {
			public void onWorkshopConceptChanged(WorkshopConceptChangedEvent event)
			{
				// If the "concept changed" targets another workshop, discard it
				if (event.getTargetWorkshop() != _parent)
					return ;
					
				Log.debug("Semantic network presenter handling concept change") ;
				UpdateSemanticLeaf() ;
			}
		});
	}
	
	protected void UpdateSemanticLeaf()
	{
		// Since the process is asynchronous, we better clear both tables first so they don't remain out-of-date for some times   
		//
		display.feedLeftSemanticTable(null) ;
		display.feedRightSemanticTable(null) ;
		
		String sConcept = _supervisor.getConcept() ;
		
		if ((null == sConcept) || "".equals(sConcept))
			return ;
		
		// The query language is set to "" meaning that all languages are to be displayed
		//
		_dispatcher.execute(new GetSemanticTriplesAction(_supervisor.getSessionElements(), _supervisor.getUserLanguage(), sConcept), new GetSemanticTriplesForConceptCallback()) ;
	}
	
	/**
	 * Callback function called when the server answers to login attempt
	 * 
	 * @author Philippe
	 *
	 */
	public class GetSemanticTriplesForConceptCallback implements AsyncCallback<GetSemanticTriplesResult>
	{
		public GetSemanticTriplesForConceptCallback() {
			super() ;
		}

		@Override
		public void onFailure(Throwable cause) {
			Log.error("Unhandled error", cause);
			Log.info("error from GetSemanticTriplesForConceptCallback!!");
		}

		@Override
		public void onSuccess(GetSemanticTriplesResult value) 
		{
			ArrayList<TripleWithLabel> aObjectTriples = value.getObjectArray() ;
			display.feedLeftSemanticTable(aObjectTriples) ;
			
			ArrayList<TripleWithLabel> aSubjectTriples = value.getSubjectArray() ;
			display.feedRightSemanticTable(aSubjectTriples) ;
		}
	}

	public void setParent(QuadrifoliumWorkshopPresenterModel<QuadrifoliumWorkshopViewModel> parent) {
		_parent = parent ;
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
