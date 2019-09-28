package org.quadrifolium.client.mvp_components;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.widget.WidgetDisplay;
import net.customware.gwt.presenter.client.widget.WidgetPresenter;

import java.util.ArrayList;

import org.quadrifolium.client.event.GoToWorkshopDefinitionEvent;
import org.quadrifolium.client.event.GoToWorkshopDefinitionEventHandler;
import org.quadrifolium.client.event.UserChangedEvent;
import org.quadrifolium.client.event.UserChangedEventHandler;
import org.quadrifolium.client.event.WorkshopConceptChangedEvent;
import org.quadrifolium.client.event.WorkshopConceptChangedEventHandler;
import org.quadrifolium.client.global.QuadrifoliumSupervisor;
import org.quadrifolium.client.mvp.QuadrifoliumWorkshopPresenterModel;
import org.quadrifolium.client.mvp.QuadrifoliumWorkshopViewModel;
import org.quadrifolium.client.mvp_components.QuadrifoliumDefinitionsView.INTERFACETYPE;
import org.quadrifolium.shared.ontology.LanguageTag;
import org.quadrifolium.shared.ontology.TripleWithLabel;
import org.quadrifolium.shared.rpc4ontology.GetDefinitionsTriplesAction;
import org.quadrifolium.shared.rpc4ontology.GetDefinitionsTriplesResult;
import org.quadrifolium.shared.rpc4ontology.SaveDefinitionAction;
import org.quadrifolium.shared.rpc4ontology.SaveDefinitionResult;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.inject.Inject;

public class QuadrifoliumDefinitionsPresenter extends WidgetPresenter<QuadrifoliumDefinitionsPresenter.Display> 
{	
	public interface Display extends WidgetDisplay 
	{			
		public void             feedDefinitionsTable(final ArrayList<TripleWithLabel> aTriples, final INTERFACETYPE iInterfaceType) ;

		public void                  updateView(final ArrayList<TripleWithLabel> aTriples, final INTERFACETYPE iInterfaceType) ;
		public HasClickHandlers      getEditButtonKeyDown() ;
		public HasClickHandlers      getAddButtonKeyDown() ;
		public ArrayList<PushButton> getButtonsArray() ;
		
		public void             openAddPanel() ;
		public void             closeAddPanel() ;
		public HasClickHandlers getAddOkButtonKeyDown() ;
		public HasClickHandlers getAddCancelButtonKeyDown() ;
		public void             initializeLanguagesList(final ArrayList<LanguageTag> _LanguageTags) ;
		
		public String           getEditedLanguage() ;
		public void             setEditedLanguage(final String sLanguageTag) ;
		public String           getEditedText() ;
		public void             setEditedText(final String sText) ;
		
		public void             openErrDialogBox(final String sErrMsgId) ;
		public void             closeErrDialogBox() ;
		public HasClickHandlers getErrDialogBoxOkButton() ;
	}

	private final DispatchAsync              _dispatcher ;
	private final QuadrifoliumSupervisor     _supervisor ;
	
	protected     QuadrifoliumWorkshopPresenterModel<QuadrifoliumWorkshopViewModel> _parent ;
	
	protected     ArrayList<TripleWithLabel> _aDefinitionsTriples ;
	protected     TripleWithLabel            _editedDefinition ;
	
	protected     boolean _bEditMode ;
	protected     boolean _bAdding ;
	
	@Inject
	public QuadrifoliumDefinitionsPresenter(final Display display, 
			                                    final EventBus eventBus,
			                                    final DispatchAsync dispatcher,
			                                    final QuadrifoliumSupervisor supervisor) 
	{
		super(display, eventBus) ;
		
		_dispatcher = dispatcher ;
		_supervisor = supervisor ;
		
		_parent     = null ;
		
		_bEditMode  = false ;
		_bAdding    = false ;
		
		_aDefinitionsTriples = null ;
		_editedDefinition    = null ;
		
		bind() ;
	}
	
	/**
	 * Binds Event Bus events that are handled by this presenter
	 */
	@Override
	protected void onBind() 
	{
		Log.debug("Entering QuadrifoliumDefinitionsPresenter::onBind()") ;
		
		// Message received when it is time to open the workshop
		//
		eventBus.addHandler(GoToWorkshopDefinitionEvent.TYPE, new GoToWorkshopDefinitionEventHandler() {
			public void onGoToWorkshopDefinition(GoToWorkshopDefinitionEvent event)
			{
				// If this presenter is already connected, discard this message
				//
				if (null != _parent)
					return ;
					
				// Connect this presenter to the workshop
				//
				_parent = event.getParent() ;
					
				Log.debug("Loading Workshop's definitions presenter Page") ;
				initialize(event.getWorkspace()) ;
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
					
				Log.debug("Definitions presenter handling concept change") ;
				UpdateDefinitions() ;
			}
		});
		
		// Message received when the user changed
		//
		eventBus.addHandler(UserChangedEvent.TYPE, new UserChangedEventHandler() {
			public void onUserChanged(UserChangedEvent event)
			{
				adaptToUserChange() ;
			}
		});
		
		/**
		 * Get key down from the button that switches from edit more to read-only mode 
		 */
		display.getEditButtonKeyDown().addClickHandler(new ClickHandler() {
			public void onClick(final ClickEvent event)
			{
				switchEditMode() ;
			}
		});
		
		/**
		 * Get key down from the "add definition" button 
		 */
		display.getAddButtonKeyDown().addClickHandler(new ClickHandler() {
			public void onClick(final ClickEvent event)
			{
				if (_bAdding)
					return ;
				
				addNewDefinition() ;
			}
		});
		
		/**
		 * Get key down from the OK button from the "add definition" panel 
		 */
		display.getAddOkButtonKeyDown().addClickHandler(new ClickHandler() {
			public void onClick(final ClickEvent event)
			{
				if (false == _bAdding)
					return ;
				
				saveEditedDefinition() ;
			}
		});
		
		/**
		 * Get key down from the OK button from the error box
		 */
		display.getErrDialogBoxOkButton().addClickHandler(new ClickHandler() {
			public void onClick(final ClickEvent event)
			{
				display.closeErrDialogBox() ;
			}
		});
		
		scheduleRefresh() ;
	}

	/**
	 * Create a timer to ensure that the definitions list remains up to date
	 */
	protected void scheduleRefresh()
	{
		// Create a new timer that updates the definitions list
		//
		Timer t = new Timer() {
	    public void run() {
	    	UpdateDefinitions() ;
	    }
	  };
	  
	  // Schedule the timer to run once in 10 seconds.
	  //
	  t.schedule(10000) ;
	}
  
	
	/**
	 * Get current interface mode
	 */
	protected INTERFACETYPE getInterfaceType() 
	{
		INTERFACETYPE iInterfaceType = INTERFACETYPE.readOnlyMode ;
		if (_supervisor.isUserAnEditor())
		{
			if (_bEditMode)
				iInterfaceType = INTERFACETYPE.editMode ;
			else
				iInterfaceType = INTERFACETYPE.editableMode ;
		}
		return iInterfaceType ;
	}
	
	/**
	 * Install view inside workspace
	 */
	protected void initialize(Panel workspace)
	{
		if (null == workspace)
			return ;
		
		workspace.clear() ;
		workspace.add(display.asWidget()) ;
		
		if (_supervisor.isUserAnEditor())
			updateView(INTERFACETYPE.editableMode) ;
	}
	
	protected void UpdateDefinitions()
	{
		// Since the process is asynchronous, we better clear the display first so definitions don't remain out-of-date for some times   
		//
		display.feedDefinitionsTable(null, INTERFACETYPE.readOnlyMode) ;
		
		String sConcept = _supervisor.getConcept() ;
		
		if ((null == sConcept) || "".equals(sConcept))
			return ;
		
		// The query language is set to "" meaning that all languages are to be displayed
		//
		_dispatcher.execute(new GetDefinitionsTriplesAction(_supervisor.getUserId(), "", sConcept), new GetDefinitionsTriplesForConceptCallback()) ;
	}
	
	/**
	 * Callback function called when the server answers to a request for a list of definitions
	 * 
	 * @author Philippe
	 *
	 */
	public class GetDefinitionsTriplesForConceptCallback implements AsyncCallback<GetDefinitionsTriplesResult>
	{
		public GetDefinitionsTriplesForConceptCallback() {
			super() ;
		}

		@Override
		public void onFailure(Throwable cause) {
			Log.error("Unhandled error", cause);
			Log.info("error from GetDefinitionsTriplesForConceptCallback!!");
		}

		@Override
		public void onSuccess(GetDefinitionsTriplesResult value) 
		{
			if (null == _aDefinitionsTriples)
				_aDefinitionsTriples = new ArrayList<TripleWithLabel>() ;
			else
				_aDefinitionsTriples.clear() ;
			
			if (false == value.getTriplesArray().isEmpty())
				_aDefinitionsTriples.addAll(value.getTriplesArray())  ;
			
			refreshDefinitionsList() ;
		}
	}
	
	/**
	 * Set what is needed when user changed (or there is no longer a logged user)
	 */
	protected void adaptToUserChange()
	{
		// Constant is that we are never editing
		//
		_bEditMode = false ;
		
		updateView() ;
	}
	
	/**
	 * Switch from edit mode to read-only mode or vice-versa
	 */
	protected void switchEditMode()
	{
		_bEditMode = !_bEditMode ;
		
		// Change view state
		//
		updateView() ;
	}
	
	/**
	 * Open the "add definition" panel
	 */
	protected void addNewDefinition() 
	{
		_bEditMode        = true ;
		_editedDefinition = null ;
		
		display.initializeLanguagesList(_supervisor.getLanguageTags()) ;
		
		_bAdding = true ;
		
		display.openAddPanel() ;
	}
	
	/**
	 * Close the editing session
	 */
	protected void closeEditingSession()
	{
		display.closeAddPanel() ;
		
		_bEditMode        = true ;
		_editedDefinition = null ;
	
		_bAdding = false ;
	}
	
	/**
	 * Save currently edited definition
	 */
	protected void saveEditedDefinition()
	{
		String sText = display.getEditedText() ;
		if ("".equals(sText))
		{
			display.openErrDialogBox("definitionErrEmptyLabel") ;
			return ;
		}
		
		String sLanguage = display.getEditedLanguage() ;
		if ("".equals(sLanguage))
		{
			display.openErrDialogBox("definitionErrNoLanguage") ;
			return ;
		}
		
		// If editing an existing definition, check if something changed
		//
		if (null != _editedDefinition)
		{
			if ((sText.equals(_editedDefinition.getObjectLabel())) && (sLanguage.equals(_editedDefinition.getLanguage())))
			{
				closeEditingSession() ;
				return ;
			}
		}
		
		// Check if this language is already attributed to another definition
		//
		if (false == _aDefinitionsTriples.isEmpty())
			for (TripleWithLabel triple : _aDefinitionsTriples)
				if (sLanguage.equals(triple.getLanguage()))
					if ((null == _editedDefinition) || (_editedDefinition.getId() != triple.getId()))
					{
						display.openErrDialogBox("definitionErrLanguageExists") ;
						return ;
					}
		
		// Send the save query to the server
		//
		_dispatcher.execute(new SaveDefinitionAction(_supervisor.getSessionElements(), _supervisor.getConcept(), sLanguage, sText, _editedDefinition), new SaveDefinitionCallback()) ;
	}
	
	/**
	 * Callback function called when the server answers to a request to save a definition
	 * 
	 * @author Philippe
	 *
	 */
	public class SaveDefinitionCallback implements AsyncCallback<SaveDefinitionResult>
	{
		public SaveDefinitionCallback() {
			super() ;
		}

		@Override
		public void onFailure(Throwable cause) {
			Log.error("Unhandled error", cause);
			Log.info("error from GetDefinitionsTriplesForConceptCallback!!");
		}

		@Override
		public void onSuccess(SaveDefinitionResult value) 
		{
			// If the returned definition is null, it means that the server didn't succeed
			//
			if (null == value.getSavedDefinition())
			{
				signalSaveProblem(value.getMessage(), value.getSentLanguage(), value.getSentText(), value.getUpdatedTripleId()) ;
				return ;
			}
			
			// If things went well, just update the view
			//
			int iUpdatedTripleId = value.getUpdatedTripleId() ;
			if (iUpdatedTripleId < 0)
				_aDefinitionsTriples.add(value.getSavedDefinition())  ;
			else
			{
				for (TripleWithLabel triple : _aDefinitionsTriples)
					if (triple.getId() == iUpdatedTripleId)
					{
						triple.initFromLabelModel(value.getSavedDefinition()) ;
						break ;
					}
			}
			
			closeEditingSession() ;
			
			display.feedDefinitionsTable(_aDefinitionsTriples, getInterfaceType()) ;
		}
	}
	
	/**
	 * Connect the buttons that control definitions edition and deletion actions 
	 */
	protected void connectButtonsClickHandlers()
	{
		ArrayList<PushButton> aButtons = display.getButtonsArray() ;
		
		if ((null == aButtons) || aButtons.isEmpty())
			return ;
		
		for (PushButton button : aButtons)
		{
			String sId = button.getElement().getId() ;
			if ((null != sId) && (false == sId.equals("")))
			{
				String[] decomposition = sId.split("_") ;
				
				final String sAction       = decomposition[0] ;
				final String sDefinitionId = decomposition[1] ;
			
				button.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event)
					{
						definitionAction(sAction, sDefinitionId) ;
					}
				}) ;
			}
		}
	}
	
	/**
	 * An action button was clicked for this definition 
	 */
	protected void definitionAction(final String sAction, final String sDefinitionID)
	{
		if ((null == sAction) || "".equals(sAction) || (null == sDefinitionID) || "".equals(sDefinitionID))
			return ;
		
		TripleWithLabel tripleToEdit = getDefinitionFromId(sDefinitionID) ;
		if (null == tripleToEdit)
		{
			// If clicked definition not found, better refresh the list
			//
			refreshDefinitionsList() ;
			return ;
		}
		
		// Edit
		//
		if ("edt".equals(sAction))
		{
			_bEditMode        = true ;
			_editedDefinition = tripleToEdit ;
			
			display.initializeLanguagesList(_supervisor.getLanguageTags()) ;
			
			display.setEditedText(_editedDefinition.getObjectLabel()) ;
			display.setEditedLanguage(_editedDefinition.getLanguage()) ;
			
			_bAdding = true ;
			
			display.openAddPanel() ;
			
			return ;
		}
		
		// Edit
		//
		if ("del".equals(sAction))
		{
			
		}
	}
	
	/**
	 * Find a definition in the array from its ID
	 * 
	 * @return The definition if found, <code>null</code> if not
	 */
	protected TripleWithLabel getDefinitionFromId(final String sDefinitionID)
	{
		if (_aDefinitionsTriples.isEmpty() || (null == sDefinitionID) || "".equals(sDefinitionID))
			return null ;
		
		for (TripleWithLabel triple : _aDefinitionsTriples)
			if (sDefinitionID.equals(triple.getObject()))
				return triple ;
		
		return null ;
	}

	/**
	 * Refresh the whole view
	 * 
	 * @param iInterfaceType Forced interface type, or computed one if <code>undefined</code>
	 */
	protected void updateView(final INTERFACETYPE iInterfaceType)
	{
		// Find the interface type if undefined
		//
		INTERFACETYPE iProperInterfaceType = iInterfaceType ;
		if (INTERFACETYPE.undefined == iInterfaceType)
			iProperInterfaceType = getInterfaceType() ;
		
		// Update view
		//
		display.updateView(_aDefinitionsTriples, iProperInterfaceType) ;
		
		// Connect buttons
		//
		connectButtonsClickHandlers() ;
	}
	
	/**
	 * Refresh the whole view
	 */
	protected void updateView() {
		updateView(INTERFACETYPE.undefined) ;
	}
	
	/**
	 * Ask the display to refresh the definition list and, if they exist, connect action buttons
	 */
	protected void refreshDefinitionsList() 
	{
		display.feedDefinitionsTable(_aDefinitionsTriples, getInterfaceType()) ;
		
		connectButtonsClickHandlers() ;
	}
	
	protected void signalSaveProblem(final String sServerMessage, final String sSentLanguage, final String sSentText, final int iUpdatedTripleId)
	{
		
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
