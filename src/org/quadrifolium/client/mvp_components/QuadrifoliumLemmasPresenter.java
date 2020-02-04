package org.quadrifolium.client.mvp_components;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;

import java.util.ArrayList;

import org.quadrifolium.client.event.GoToWorkshopLemmaEvent;
import org.quadrifolium.client.event.GoToWorkshopLemmaEventHandler;
import org.quadrifolium.client.event.WorkshopConceptSynonymsLoadedEvent;
import org.quadrifolium.client.global.QuadrifoliumSupervisor;
import org.quadrifolium.client.mvp_components.QuadrifoliumComponentBaseDisplayModel.INTERFACETYPE;
import org.quadrifolium.shared.ontology.Flex;
import org.quadrifolium.shared.ontology.LanguageTag;
import org.quadrifolium.shared.ontology.LemmaWithInflections;
import org.quadrifolium.shared.rpc4ontology.GetFullSynonymsForConceptAction;
import org.quadrifolium.shared.rpc4ontology.GetFullSynonymsForConceptResult;
import org.quadrifolium.shared.rpc4ontology.SaveLemmaAction;
import org.quadrifolium.shared.rpc4ontology.SaveLemmaResult;
import org.quadrifolium.shared.util.ParsedLanguageTag;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.PushButton;
import com.google.inject.Inject;

/**
 * This class in in charge of the "linguistic leaf". All linguistic related functions should be located there.
 * 
 * @author Philippe
 *
 */
public class QuadrifoliumLemmasPresenter extends QuadrifoliumComponentBasePresenter<QuadrifoliumLemmasPresenter.Display> 
{	
	public interface Display extends QuadrifoliumComponentInterface 
	{			
		public void                  feedLinguisticTree(final ArrayList<LemmaWithInflections> aSynonyms, final INTERFACETYPE iInterfaceType) ;
		public void                  feedLinguisticTreeForOthers(final ArrayList<LemmaWithInflections> aSynonyms, final INTERFACETYPE iInterfaceType) ;
		
		public void                  updateView(final INTERFACETYPE iInterfaceType) ;
		public void                  updateViewForOthers(final INTERFACETYPE iInterfaceType) ;

		public void                  initializeLanguagesList(final ArrayList<LanguageTag> aLanguages) ;

		public String                getEditedText() ;
		public void                  setEditedText(final String sText) ;
		public String                getEditedGrammar() ;
		public void                  setEditedGrammar(final String sGrammar) ;
		public String                getEditedLanguage() ;
		public void                  setEditedLanguage(final String sLanguageTag) ;
		public String                get2ndEditedText() ;
		public void                  set2ndEditedText(final String sText) ;
		public String                get2ndEditedGrammar() ;
		public void                  set2ndEditedGrammar(final String sGrammar) ;
		
		public HasClickHandlers      get2ndEditButtonKeyDown() ;
		public HasClickHandlers      get2ndAddButtonKeyDown() ;
		public ArrayList<PushButton> get2ndButtonsArray() ;
		
		public void                  open2ndAddPanel() ;
		public void                  close2ndAddPanel() ;
		public HasClickHandlers      get2ndAddOkButtonKeyDown() ;
		public HasClickHandlers      get2ndAddCancelButtonKeyDown() ;

	}

	protected ArrayList<LemmaWithInflections> _aSynonyms = new ArrayList<LemmaWithInflections>() ;
	protected Flex                            _preferredInflexion ;
	
	protected LemmaWithInflections            _editedLemma ;
	protected LemmaWithInflections            _2ndEditedLemma ;
	
	protected boolean                         _b2ndEditMode ;
	protected boolean                         _b2ndAdding ;
	
	@Inject
	public QuadrifoliumLemmasPresenter(final Display display, 
			                               final EventBus eventBus,
			                               final DispatchAsync dispatcher,
			                               final QuadrifoliumSupervisor supervisor) 
	{
		super(display, eventBus, dispatcher, supervisor) ;
		
		_preferredInflexion = null ;
		_editedLemma        = null ;
		_2ndEditedLemma     = null ;
		
		_b2ndEditMode       = false ;
		_b2ndAdding         = false ;		
		
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
		eventBus.addHandler(GoToWorkshopLemmaEvent.TYPE, new GoToWorkshopLemmaEventHandler() {
			public void onGoToWorkshopLemma(GoToWorkshopLemmaEvent event) {
				connectToWorkshop(event.getContent()) ;
			}
		});
		
		/**
		 * Get key down from the button that switches from edit more to read-only mode 
		 */
		display.get2ndEditButtonKeyDown().addClickHandler(new ClickHandler() {
			public void onClick(final ClickEvent event) {
				switch2ndEditMode() ;
			}
		});
			
		/**
		 * Get key down from the "add definition" button 
		 */
		display.get2ndAddButtonKeyDown().addClickHandler(new ClickHandler() {
			public void onClick(final ClickEvent event)
			{
				if (_b2ndAdding)
					return ;
					
				add2ndNewElement() ;
			}
		});
			
		/**
		 * Get key down from the OK button from the "add definition" panel 
		 */
		display.get2ndAddOkButtonKeyDown().addClickHandler(new ClickHandler() {
			public void onClick(final ClickEvent event)
			{
				if (false == _b2ndAdding)
					return ;
					
				save2ndEditedElement() ;
			}
		});
	}
	
	
	
	/**
	 * Ask the view to create editing controls and connect corresponding handlers
	 */
/*
	protected void initCommandControls()
	{
		display.initCommandPanelForEditing() ;
		
		// Click handler for the "new lemma" button
		//
		HasClickHandlers newLemmaClickHandler = display.getNewLemmaButton() ;
		if (null != newLemmaClickHandler)
			newLemmaClickHandler.addClickHandler(new ClickHandler(){
					@Override
					public void onClick(final ClickEvent event) {
						addNewLemma() ;
					}
				});
	}
*/
	
	/**
	 * Refresh view
	 */
	protected void UpdateDisplay(final INTERFACETYPE iInterfaceType) 
	{
		display.updateView(iInterfaceType) ;
		display.updateViewForOthers(iInterfaceType) ;
		
		feedLinguisticTrees() ;
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
						lemmaAction(sAction, sDefinitionId) ;
					}
				}) ;
			}
		}
	}
	
	/**
	 * An action button was clicked for this lemma 
	 */
	protected void lemmaAction(final String sAction, final String sLemmaCode)
	{
		if ((null == sAction) || "".equals(sAction) || (null == sLemmaCode) || "".equals(sLemmaCode))
			return ;
		
		LemmaWithInflections lemmaToEdit = getLemmaFromCode(sLemmaCode) ;
		if (null == lemmaToEdit)
		{
			// If clicked definition not found, better refresh the list
			//
			refreshLemmasList() ;
			return ;
		}
		
		// Edit
		//
		if ("edt".equals(sAction))
		{
			_bEditMode   = true ;
			_editedLemma = lemmaToEdit ;
			
			display.initializeLanguagesList(_supervisor.getLanguageTags()) ;
			
			display.setEditedText(_editedLemma.getLabel()) ;
			// display.setEditedLanguage(_editedDefinition.getLanguage()) ;
			
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
	 * Find a lemma in the array from its code
	 * 
	 * @return The lemma if found, <code>null</code> if not
	 */
	protected LemmaWithInflections getLemmaFromCode(final String sLemmaCode)
	{
		if (_aSynonyms.isEmpty() || (null == sLemmaCode) || "".equals(sLemmaCode))
			return null ;
		
		for (LemmaWithInflections lemma : _aSynonyms)
			if (sLemmaCode.equals(lemma.getCode()))
				return lemma ;
		
		return null ;
	}
	
	/**
	 * Ask the display to refresh the definition list and, if they exist, connect action buttons
	 */
	protected void refreshLemmasList() 
	{
		feedLinguisticTrees() ;
		
		connectButtonsClickHandlers() ;
	}
	
	protected void UpdateContent()
	{
		// Since the process is asynchronous, we better clear the list first so it doesn't remain out-of-date for some times   
		//
		display.feedLinguisticTree(null, getInterfaceType()) ;
		display.feedLinguisticTreeForOthers(null, getInterfaceType()) ;
		
		_aSynonyms.clear() ;
		_preferredInflexion = null ;
		
		String sConcept = _supervisor.getConcept() ;
		
		if ((null == sConcept) || "".equals(sConcept))
			return ;
		
		// The query language is set to "" meaning that all languages are to be displayed
		//
		_dispatcher.execute(new GetFullSynonymsForConceptAction(_supervisor.getUserId(), _supervisor.getUserLanguage(), sConcept, ""), new getSynonymsCallback()) ;
	}
	
	/**
	 * Callback function called when the server answers to login attempt
	 * 
	 * @author Philippe
	 *
	 */
	public class getSynonymsCallback implements AsyncCallback<GetFullSynonymsForConceptResult>
	{
		public getSynonymsCallback() {
			super() ;
		}

		@Override
		public void onFailure(Throwable cause) {
			Log.error("Unhandled error", cause);
			Log.info("error from getSynonymsCallback!!");
		}

		@Override
		public void onSuccess(GetFullSynonymsForConceptResult value) 
		{
			// Load synonyms
			//
			_aSynonyms = value.getSynonymsArray() ;
			if (false == _aSynonyms.isEmpty())
				feedLinguisticTrees() ;
			
			selectPreferredInflexion() ;
			
			// Tell other components that the new synonym list is ready
			//
			eventBus.fireEvent(new WorkshopConceptSynonymsLoadedEvent(_parent)) ;
		}
	}
	
	/**
	 * Feed the interface components (user language and other languages)
	 */
	public void feedLinguisticTrees()
	{
		ArrayList<LemmaWithInflections> aUserLanguage   = new ArrayList<LemmaWithInflections>() ;
		ArrayList<LemmaWithInflections> aOtherLanguages = new ArrayList<LemmaWithInflections>() ;

		String sSelectedLanguage = _supervisor.getUserLanguage() ;
		
		for (LemmaWithInflections lemma : _aSynonyms)
		{
			if (isCompatibleLanguage(lemma.getLanguage(), sSelectedLanguage))
				aUserLanguage.add(lemma) ;
			else
				aOtherLanguages.add(lemma) ;
		}
		
		display.feedLinguisticTree(aUserLanguage, getInterfaceType()) ;
		display.feedLinguisticTreeForOthers(aOtherLanguages, getInterfaceType()) ;
	}
	
	/**
	 * Open the "add lemma" panel
	 */
	protected void addNewElement() 
	{
		_bEditMode   = true ;
		_editedLemma = null ;
		
		_bAdding = true ;
		
		display.openAddPanel() ;
	}
	
	/**
	 * Open the second "add lemma" panel
	 */
	protected void add2ndNewElement() 
	{
		_b2ndEditMode   = true ;
		_2ndEditedLemma = null ;
		
		display.initializeLanguagesList(_supervisor.getLanguageTags()) ;
		
		_b2ndAdding = true ;
		
		display.open2ndAddPanel() ;
	}
	
	/**
	 * Select the preferred inflexion (as the preferred one for current language) from the list of synonyms 
	 */
	protected void selectPreferredInflexion()
	{
		_preferredInflexion = null ;
		
		if (_aSynonyms.isEmpty())
			return ;
		
		String sSelectedLanguage = _supervisor.getUserLanguage() ;
		
		LemmaWithInflections bestCandidate = null ;
		
		// Look for the preferred term for a synonym from currently selected language
		//
		for (LemmaWithInflections lemma : _aSynonyms)
			if (lemma.isPreferred() && isBetterCompatibleLanguage(lemma, bestCandidate, sSelectedLanguage))
				bestCandidate = lemma ;
		
		// If nothing found among the "preferred terms", try with ordinay ones
		//
		if (null == bestCandidate)
			for (LemmaWithInflections lemma : _aSynonyms)
				if (false == lemma.isPreferred() && isBetterCompatibleLanguage(lemma, bestCandidate, sSelectedLanguage))
					bestCandidate = lemma ;
		
		if (null != bestCandidate)
			_preferredInflexion = bestCandidate.getInflections().get(0) ;
		else
			_preferredInflexion = null ;
	}
	
	/**
	 * Is a given language compatible with a reference language, means can this language been displayed for a user 
	 * whose language is the reference language.<br><br>
	 * For example a user whose language is "en-GB" is compatible with "en" and with "en-GB-slang" but not with "en-US"
	 * 
	 * @param sLanguage    The language to be considered compatible or not
	 * @param sRefLanguage The reference language (usually user's language)
	 * 
	 * @return <code>true</code> if 
	 */
	protected boolean isCompatibleLanguage(final String sLanguage, final String sRefLanguage)
	{
		ParsedLanguageTag language = new ParsedLanguageTag(sLanguage) ;
		ParsedLanguageTag refernce = new ParsedLanguageTag(sRefLanguage) ;
		
		return language.isCompatibleWith(refernce) ;
	}
	
	/**
	 * Is a language tag closer than another tag from the reference tag
	 * 
	 * @param candidate    Language tag to be evaluated
	 * @param reference    Current closest tag that is to be challenged
	 * @param sRefLanguage Actual user's language
	 * 
	 * @return <code>true</code> if the candidate is closer from user's language than the current reference one, <code>false</code> if not
	 */
	protected boolean isBetterCompatibleLanguage(final LemmaWithInflections candidate, final LemmaWithInflections reference, final String sRefLanguage)
	{
		// Null or empty variables
		//
		if (null == candidate)
			return false ;
		if (null == reference)
			return true ;
		if ((null == sRefLanguage) || "".equals(sRefLanguage))
			return false ;
		
		// Check if candidate is compatible with the reference language
		//
		ParsedLanguageTag candidateTag = new ParsedLanguageTag(candidate.getLanguage()) ;
		ParsedLanguageTag refTag       = new ParsedLanguageTag(sRefLanguage) ;
		
		if (false == candidateTag.isCompatibleWith(refTag))
			return false ;
		
		// Check if candidate is better than champion
		//
		// See "Matching of Language Tags" http://www.rfc-editor.org/rfc/rfc4647.txt
		//
		ParsedLanguageTag championTag  = new ParsedLanguageTag(reference.getLanguage()) ;
		
		return (candidateTag.getInformationCount() > championTag.getInformationCount()) ;
	} 
		
	public Flex getPreferredInflexion() {
		return _preferredInflexion ;
	}

	/**
	 * Switch from edit mode to read-only mode or vice-versa
	 */
	protected void switch2ndEditMode()
	{
		_b2ndEditMode = !_b2ndEditMode ;
		
		// Change view state
		//
		updateView() ;
	}
	
	/**
	 * Close the editing session
	 */
	protected void closeEditingSession()
	{
		display.closeAddPanel() ;
		
		_bEditMode   = true ;
		_editedLemma = null ;
	
		_bAdding = false ;
	}
	
	/**
	 * Save currently edited lemma
	 */
	protected void saveEditedElement()
	{
		// Check that no information is missing
		//
		String sText = display.getEditedText() ;
		if ("".equals(sText))
		{
			display.openErrDialogBox("lemmaErrEmptyLabel") ;
			return ;
		}
		
		String sGrammar = display.getEditedGrammar() ;
		if ("".equals(sGrammar))
		{
			display.openErrDialogBox("lemmaErrEmptyGrammar") ;
			return ;
		}
		
/*
		String sLanguage = display.getEditedLanguage() ;
		if ("".equals(sLanguage))
		{
			display.openErrDialogBox("definitionErrNoLanguage") ;
			return ;
		}
*/
		
		// If editing an existing definition, check if something changed
		//
		if (null != _editedLemma)
		{
			// if ((sText.equals(_editedLemma.getLabel())) && (sLanguage.equals(_editedLemma.getLanguage())))
			if ((sText.equals(_editedLemma.getLabel())) && (sGrammar.equals(org.quadrifolium.shared.ontology.OntologyLexicon.getGrammarString(_editedLemma))))
			{
				closeEditingSession() ;
				return ;
			}
		}
		
		// No need to Check if this language is already attributed to another lemma since synonyms are accepted
		//
		
		// Send the save query to the server
		//
		_dispatcher.execute(new SaveLemmaAction(_supervisor.getSessionElements(), _supervisor.getConcept(), _supervisor.getUserLanguage(), sText, sGrammar, _editedLemma), new SaveLemmaCallback()) ;
	}
	
	/**
	 * Save currently edited lemma
	 */
	protected void save2ndEditedElement()
	{
		// Check that no information is missing
		//
		String sText = display.get2ndEditedText() ;
		if ("".equals(sText))
		{
			display.openErrDialogBox("lemmaErrEmptyLabel") ;
			return ;
		}
		
		String sGrammar = display.get2ndEditedGrammar() ;
		if ("".equals(sGrammar))
		{
			display.openErrDialogBox("lemmaErrEmptyGrammar") ;
			return ;
		}
		
		String sLanguage = display.getEditedLanguage() ;
		if ("".equals(sLanguage))
		{
			display.openErrDialogBox("lemmaErrNoLanguage") ;
			return ;
		}
		
		// If editing an existing definition, check if something changed
		//
		if (null != _editedLemma)
		{
			if ((sText.equals(_editedLemma.getLabel())) && (sLanguage.equals(_editedLemma.getLanguage())) && (sGrammar.equals(org.quadrifolium.shared.ontology.OntologyLexicon.getGrammarString(_editedLemma))))
			{
				closeEditingSession() ;
				return ;
			}
		}
		
		// No need to Check if this language is already attributed to another lemma since synonyms are accepted
		//
		
		// Send the save query to the server
		//
		_dispatcher.execute(new SaveLemmaAction(_supervisor.getSessionElements(), _supervisor.getConcept(), sLanguage, sText, sGrammar, _editedLemma), new SaveLemmaCallback()) ;
	}
	
	/**
	 * Callback function called when the server answers to a request to save a definition
	 * 
	 * @author Philippe
	 *
	 */
	public class SaveLemmaCallback implements AsyncCallback<SaveLemmaResult>
	{
		public SaveLemmaCallback() {
			super() ;
		}

		@Override
		public void onFailure(Throwable cause) {
			Log.error("Unhandled error", cause);
			Log.info("error from GetDefinitionsTriplesForConceptCallback!!");
		}

		@Override
		public void onSuccess(SaveLemmaResult value) 
		{
			// If the returned definition is null, it means that the server didn't succeed
			//
			if (null == value.getSavedLemma())
			{
				signalSaveProblem(value.getMessage(), value.getSentLanguage(), value.getSentText(), value.getUpdatedLemmaId()) ;
				return ;
			}
			
			// If things went well, just update the view
			//
			
			
			closeEditingSession() ;
			
			// display.feedDefinitionsTable(_aDefinitionsTriples, getInterfaceType()) ;
		}
	}
	
	protected void signalSaveProblem(final String sServerMessage, final String sSentLanguage, final String sSentText, final int iUpdatedLemmaId)
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
