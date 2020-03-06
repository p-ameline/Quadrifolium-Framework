package org.quadrifolium.client.mvp_components;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;

import java.util.ArrayList;

import org.quadrifolium.client.event.GoToWorkshopDefinitionEvent;
import org.quadrifolium.client.event.GoToWorkshopDefinitionEventHandler;
import org.quadrifolium.client.global.QuadrifoliumSupervisor;
import org.quadrifolium.client.mvp_components.QuadrifoliumComponentBaseDisplayModel.INTERFACETYPE;
import org.quadrifolium.shared.ontology.TripleWithLabel;
import org.quadrifolium.shared.rpc4ontology.GetDefinitionsTriplesResult;
import org.quadrifolium.shared.rpc4ontology.SaveDefinitionAction;
import org.quadrifolium.shared.rpc4ontology.SaveDefinitionResult;

import com.allen_sauer.gwt.log.client.Log;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ButtonBase;
import com.google.gwt.user.client.ui.PushButton;
import com.google.inject.Inject;

public class QuadrifoliumStemmaPresenter extends QuadrifoliumComponentBasePresenter<QuadrifoliumStemmaPresenter.Display> 
{	
	public interface Display extends QuadrifoliumComponentInterface 
	{
		public void             feedStemma(final INTERFACETYPE iInterfaceType) ;

		public void             updateView(final ArrayList<TripleWithLabel> aTriples, final INTERFACETYPE iInterfaceType) ;		
	}

	protected     ArrayList<TripleWithLabel> _aDefinitionsTriples ;
	protected     TripleWithLabel            _editedDefinition ;
	
	@Inject
	public QuadrifoliumStemmaPresenter(final Display display, 
			                                    final EventBus eventBus,
			                                    final DispatchAsync dispatcher,
			                                    final QuadrifoliumSupervisor supervisor) 
	{
		super(display, eventBus, dispatcher, supervisor) ;
		
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
		super.onBind() ;
		
		// Message received when it is time to open the workshop
		//
		eventBus.addHandler(GoToWorkshopDefinitionEvent.TYPE, new GoToWorkshopDefinitionEventHandler() {
			public void onGoToWorkshopDefinition(GoToWorkshopDefinitionEvent event) {
				connectToWorkshop(event.getContent()) ;
			}
		});
	}

	/**
	 * Update stemma from server
	 */
	protected void UpdateContent()
	{
/*
		// Since the process is asynchronous, we better clear the display first so definitions don't remain out-of-date for some times   
		//
		display.feedDefinitionsTable(null, INTERFACETYPE.readOnlyMode) ;
		
		String sConcept = _supervisor.getConcept() ;
		
		if ((null == sConcept) || "".equals(sConcept))
			return ;
		
		// The query language is set to "" meaning that all languages are to be displayed
		//
		_dispatcher.execute(new GetDefinitionsTriplesAction(_supervisor.getUserId(), "", sConcept), new GetDefinitionsTriplesForConceptCallback()) ;
*/
	}
	
	/**
	 * Refresh view
	 */
	protected void UpdateDisplay(final INTERFACETYPE iInterfaceType) {
		display.updateView(_aDefinitionsTriples, iInterfaceType) ;
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
	 * Open the "add definition" panel
	 * 
	 * Nothing to do there since the stemma is a singleton
	 */
	protected void addNewElement() {
	}
	
	/**
	 * Close the editing session
	 */
	protected void closeEditingSession()
	{
		super.closeEditingSession() ;
		
		_editedDefinition = null ;
	}
	
	/**
	 * Save currently edited definition
	 */
	protected void saveEditedElement()
	{
		String sText = "" ;
		
		String sLanguage = "" ;
		
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
			
			display.feedStemma(getInterfaceType()) ;
		}
	}
	
	/**
	 * Connect the buttons that control definitions edition and deletion actions 
	 */
	protected void connectButtonsClickHandlers()
	{
		ArrayList<ButtonBase> aButtons = display.getButtonsArray() ;
		
		if ((null == aButtons) || aButtons.isEmpty())
			return ;
		
		for (ButtonBase buttonBase : aButtons)
		{
			buttonControlInformation ctrlInfo = getControlInformation(buttonBase) ;
			if (null != ctrlInfo)
			{
				PushButton button = (PushButton) buttonBase ;
				button.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event)
					{
						definitionAction(ctrlInfo.getAction(), ctrlInfo.getId()) ;
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
	 * Ask the display to refresh the definition list and, if they exist, connect action buttons
	 */
	protected void refreshDefinitionsList() 
	{
		display.feedStemma(getInterfaceType()) ;
		
		connectButtonsClickHandlers() ;
	}
	
	protected void signalSaveProblem(final String sServerMessage, final String sSentLanguage, final String sSentText, final int iUpdatedTripleId)
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
