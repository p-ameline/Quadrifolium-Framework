package org.quadrifolium.client.mvp;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;

import org.quadrifolium.client.event.CommandDisplayTitleEvent;
import org.quadrifolium.client.event.WorkshopConceptChangedEvent;
import org.quadrifolium.client.event.WorkshopConceptSynonymsLoadedEvent;
import org.quadrifolium.client.event.WorkshopConceptSynonymsLoadedEventHandler;
import org.quadrifolium.client.global.QuadrifoliumSupervisor;
import org.quadrifolium.client.mvp_components.QuadrifoliumDefinitionsPresenter;
import org.quadrifolium.client.mvp_components.QuadrifoliumDefinitionsView;
import org.quadrifolium.client.mvp_components.QuadrifoliumLemmasPresenter;
import org.quadrifolium.client.mvp_components.QuadrifoliumLemmasView;
import org.quadrifolium.client.mvp_components.QuadrifoliumSemanticsPresenter;
import org.quadrifolium.client.mvp_components.QuadrifoliumSemanticsView;
import org.quadrifolium.client.mvp_components.QuadrifoliumStemmaPresenter;
import org.quadrifolium.client.mvp_components.QuadrifoliumStemmaView;
import org.quadrifolium.shared.ontology.Flex;
import org.quadrifolium.shared.rpc_special.LexiqueTo4foliumAction;
import org.quadrifolium.shared.rpc_special.LexiqueTo4foliumResult;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

public abstract class QuadrifoliumWorkshopPresenterModel<D extends WorkshopInterfaceModel> extends QuadrifoliumBasePresenter<D>
{	
	protected QuadrifoliumLemmasPresenter      _lemmasPresenter ;
	protected QuadrifoliumSemanticsPresenter   _semanticsPresenter ;
	protected QuadrifoliumDefinitionsPresenter _definitionsPresenter ;
	protected QuadrifoliumStemmaPresenter      _stemmaPresenter ;
	
	@Inject
	public QuadrifoliumWorkshopPresenterModel(final D                      display, 
			                                      final EventBus               eventBus,
			                                      final DispatchAsync          dispatcher,
			                                      final QuadrifoliumSupervisor supervisor) 
	{
		super(display, eventBus, dispatcher, supervisor) ;
		
		
		_lemmasPresenter      = null ;
		_semanticsPresenter   = null ;
		_definitionsPresenter = null ;
		_stemmaPresenter      = null ;
	}
	
	/**
	 * Binds Event Bus events that are handled by this presenter
	 */
	@Override
	protected void onBind() 
	{
		Log.debug("Entering QuadrifoliumWorkshopPresenterModel::onBind()") ;
		
		// Message received when the linguistic leaf just loaded its synonyms
		//
		eventBus.addHandler(WorkshopConceptSynonymsLoadedEvent.TYPE, new WorkshopConceptSynonymsLoadedEventHandler() {
			public void onWorkshopConceptSynonymsLoaded(WorkshopConceptSynonymsLoadedEvent event)
			{
				UpdateSynonymsDependantInformation() ;
			}
		});	
		
		ConnectComponents() ;
		InitializeComponents() ;
	}
		
	protected void ConnectComponents()
	{
		// Create lemmas workshop
		//
		QuadrifoliumLemmasView lemmasView = new QuadrifoliumLemmasView() ;
		_lemmasPresenter = new QuadrifoliumLemmasPresenter(lemmasView, eventBus, _dispatcher, _supervisor) ;
		_lemmasPresenter.setParent((QuadrifoliumWorkshopPresenterModel<QuadrifoliumWorkshopViewModel>) this) ;
		display.addLemmasView(lemmasView) ;
		
		// Create semantics workshop
		//
		QuadrifoliumSemanticsView semanticsView = new QuadrifoliumSemanticsView() ;
		_semanticsPresenter = new QuadrifoliumSemanticsPresenter(semanticsView, eventBus, _dispatcher, _supervisor) ;
		_semanticsPresenter.setParent((QuadrifoliumWorkshopPresenterModel<QuadrifoliumWorkshopViewModel>) this) ;
		display.addSemanticsView(semanticsView) ;
		
		// Create definitions workshop
		//
		QuadrifoliumDefinitionsView definitionsView = new QuadrifoliumDefinitionsView() ;
		_definitionsPresenter = new QuadrifoliumDefinitionsPresenter(definitionsView, eventBus, _dispatcher, _supervisor) ;
		_definitionsPresenter.setParent((QuadrifoliumWorkshopPresenterModel<QuadrifoliumWorkshopViewModel>) this) ;
		display.addDefinitionsView(definitionsView) ;
		
		// Create stemma workshop
		//
		QuadrifoliumStemmaView stemmaView = new QuadrifoliumStemmaView() ;
		_stemmaPresenter = new QuadrifoliumStemmaPresenter(stemmaView, eventBus, _dispatcher, _supervisor) ;
		_stemmaPresenter.setParent((QuadrifoliumWorkshopPresenterModel<QuadrifoliumWorkshopViewModel>) this) ;
		display.addStemmaView(stemmaView) ;
	}
	
	/**
	 * Initialize existing components, typically feed the languages selection box with referenced languages
	 */
	protected void InitializeComponents()
	{
	}
	
	/**
	 * After the linguistic leaf finished loading its synonyms, it is time for some updates 
	 */
	protected void UpdateSynonymsDependantInformation()
	{
		if (null == _lemmasPresenter)
			return ;
		
		Flex preferredTerm = _lemmasPresenter.getPreferredInflexion() ;
		
		// Ask the command panel to display the preferred term as a title
		//
		eventBus.fireEvent(new CommandDisplayTitleEvent(preferredTerm.getLabel())) ;
	}
		
	public void UpdateDisplay()
	{
		eventBus.fireEvent(new WorkshopConceptChangedEvent(this)) ;
		
		UpdateRelationsLeaf() ;
		UpdateJobLeaf() ;
	}
		
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
