package org.quadrifolium.client.mvp_components;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;

import java.util.ArrayList;

import org.quadrifolium.client.event.GoToWorkshopStemmaEvent;
import org.quadrifolium.client.event.GoToWorkshopStemmaEventHandler;
import org.quadrifolium.client.global.QuadrifoliumSupervisor;
import org.quadrifolium.client.mvp_components.QuadrifoliumComponentBaseDisplayModel.INTERFACETYPE;
import org.quadrifolium.shared.ontology.QuadrifoliumNode;
import org.quadrifolium.shared.rpc4ontology.GetStemmaForConceptAction;
import org.quadrifolium.shared.rpc4ontology.GetStemmaForConceptResult;

import com.allen_sauer.gwt.log.client.Log;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ButtonBase;
import com.google.gwt.user.client.ui.PushButton;
import com.google.inject.Inject;
import com.ldv.shared.graph.LdvModelNode;
import com.ldv.shared.graph.LdvModelNodeArray;
import com.ldv.shared.graph.LdvModelTree;

public class QuadrifoliumStemmaPresenter extends QuadrifoliumComponentBasePresenter<QuadrifoliumStemmaPresenter.Display> 
{	
	public interface Display extends QuadrifoliumComponentInterface 
	{
		public void openStemmaViewer() ;
		
		public void feedStemma(final LdvModelTree stemma, final INTERFACETYPE iInterfaceType) ;
		public void updateView(final LdvModelTree stemma, final INTERFACETYPE iInterfaceType) ;		
	}

	protected     LdvModelTree    _stemma ;
	// protected     TripleWithLabel            _editedDefinition ;
	
	@Inject
	public QuadrifoliumStemmaPresenter(final Display display, 
			                               final EventBus eventBus,
			                               final DispatchAsync dispatcher,
			                               final QuadrifoliumSupervisor supervisor) 
	{
		super(display, eventBus, dispatcher, supervisor) ;
		
		_stemma = null ;
		
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
		eventBus.addHandler(GoToWorkshopStemmaEvent.TYPE, new GoToWorkshopStemmaEventHandler() {
			public void onGoToWorkshopStemma(GoToWorkshopStemmaEvent event) {
				connectToWorkshop(event.getContent()) ;
			}
		});
		
		/**
		 * React to resize
		 */
/*
		Window.addResizeHandler(new ResizeHandler()
		{
			@Override
			public void onResize(ResizeEvent event) {
				display.openStemmaViewer() ;
			}
		});
*/
	}

	/**
	 * Update stemma from server
	 */
	protected void UpdateContent()
	{
		// Since the process is asynchronous, we better clear the stemma first so it doesn't remain out-of-date for some times   
		//
		display.feedStemma(null, INTERFACETYPE.readOnlyMode) ;
		
		String sConcept = _supervisor.getConcept() ;
		
		if ((null == sConcept) || "".equals(sConcept))
			return ;
		
		_dispatcher.execute(new GetStemmaForConceptAction(_supervisor.getSessionElements(), _supervisor.getUserLanguage(), sConcept), new GetStemmaForConceptCallback()) ;
	}
	
	/**
	 * Refresh view
	 */
	protected void UpdateDisplay(final INTERFACETYPE iInterfaceType) {
		display.updateView(_stemma, iInterfaceType) ;
	}
	
	/**
	 * Callback function called when the server answers to a request for a list of definitions
	 * 
	 * @author Philippe
	 *
	 */
	public class GetStemmaForConceptCallback implements AsyncCallback<GetStemmaForConceptResult>
	{
		public GetStemmaForConceptCallback() {
			super() ;
		}

		@Override
		public void onFailure(Throwable cause) {
			Log.error("Unhandled error", cause);
			Log.info("error from GetDefinitionsTriplesForConceptCallback!!");
		}

		@Override
		public void onSuccess(GetStemmaForConceptResult value) 
		{
			if (null == _stemma)
				_stemma = new LdvModelTree() ;
			else
				_stemma.reset() ;
			
			LdvModelTree incomingStemma = value.getStemma() ;
			
			if ((null != incomingStemma) && (false == incomingStemma.isEmpty()))
			{
				// Doing it "manually" in order to make certain we really keep an array of QuadrifoliumNode 
				// _stemma.initFromModelTree(incomingStemma) ;
				
				_stemma.setTreeID(incomingStemma.getTreeID()) ;
				
				LdvModelNodeArray aNodes = _stemma.getNodes() ; ;
				for (LdvModelNode node : incomingStemma.getNodes())					
					aNodes.add(new QuadrifoliumNode(node)) ;
			}
			
			refreshStemma() ;
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
		
		// _editedDefinition = null ;
	}
	
	/**
	 * Save modified stemma
	 */
	protected void saveStemma()
	{
		String sText = "" ;
		
		String sLanguage = "" ;
/*
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
*/
	}
	
	/**
	 * Callback function called when the server answers to a request to save a definition
	 * 
	 * @author Philippe
	 *
	 */
/*
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
*/
	
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
		
/*
		TripleWithLabel tripleToEdit = getDefinitionFromId(sDefinitionID) ;
		if (null == tripleToEdit)
		{
			// If clicked definition not found, better refresh the list
			//
			refreshStemma() ;
			return ;
		}
*/		
		// Edit
		//
		if ("edt".equals(sAction))
		{
			_bEditMode        = true ;
			// _editedDefinition = tripleToEdit ;
			
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
	 * Ask the display to refresh the stemma and, if they exist, connect action buttons
	 */
	protected void refreshStemma() 
	{
		display.feedStemma(_stemma, getInterfaceType()) ;
		
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
