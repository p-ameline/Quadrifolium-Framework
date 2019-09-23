package org.quadrifolium.client.mvp_components;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.widget.WidgetDisplay;
import net.customware.gwt.presenter.client.widget.WidgetPresenter;

import java.util.ArrayList;

import org.quadrifolium.client.event.GoToWorkshopLemmaEvent;
import org.quadrifolium.client.event.GoToWorkshopLemmaEventHandler;
import org.quadrifolium.client.event.WorkshopConceptChangedEvent;
import org.quadrifolium.client.event.WorkshopConceptChangedEventHandler;
import org.quadrifolium.client.event.WorkshopConceptSynonymsLoadedEvent;
import org.quadrifolium.client.global.QuadrifoliumSupervisor;
import org.quadrifolium.client.mvp.QuadrifoliumWorkshopPresenterModel;
import org.quadrifolium.client.mvp.QuadrifoliumWorkshopViewModel;
import org.quadrifolium.shared.ontology.Flex;
import org.quadrifolium.shared.ontology.LemmaWithInflections;
import org.quadrifolium.shared.rpc4ontology.GetFullSynonymsForConceptAction;
import org.quadrifolium.shared.rpc4ontology.GetFullSynonymsForConceptResult;
import org.quadrifolium.shared.util.ParsedLanguageTag;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Panel;
import com.google.inject.Inject;

/**
 * This class in in charge of the "linguistic leaf". All linguistic related functions should be located there.
 * 
 * @author Philippe
 *
 */
public class QuadrifoliumLemmasPresenter extends WidgetPresenter<QuadrifoliumLemmasPresenter.Display> 
{	
	public interface Display extends WidgetDisplay 
	{			
		public void             feedLinguisticTree(final ArrayList<LemmaWithInflections> aSynonyms) ;
		public void             feedLinguisticTreeForOthers(final ArrayList<LemmaWithInflections> aSynonyms) ;
		
		public void             initCommandPanelForEditing() ;
		public HasClickHandlers getNewLemmaButton() ;
	}

	private final DispatchAsync                   _dispatcher ;
	private final QuadrifoliumSupervisor          _supervisor ;
	
	protected     QuadrifoliumWorkshopPresenterModel<QuadrifoliumWorkshopViewModel> _parent ;
	
	// Is it a read-only component?
	//
	protected     boolean                         _bReadOnly ;
	
	protected     ArrayList<LemmaWithInflections> _aSynonyms = new ArrayList<LemmaWithInflections>() ;
	protected     Flex                            _preferredInflexion ;
	
	@Inject
	public QuadrifoliumLemmasPresenter(final Display display, 
			                               final EventBus eventBus,
			                               final DispatchAsync dispatcher,
			                               final QuadrifoliumSupervisor supervisor) 
	{
		super(display, eventBus) ;
		
		_dispatcher        = dispatcher ;
		_supervisor        = supervisor ;
		
		_bReadOnly         = true ;
		_parent            = null ;
		
		_preferredInflexion = null ;
		
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
		eventBus.addHandler(GoToWorkshopLemmaEvent.TYPE, new GoToWorkshopLemmaEventHandler() {
			public void onGoToWorkshopLemma(GoToWorkshopLemmaEvent event)
			{
				// If this presenter is already connected, discard this message
				//
				if (null != _parent)
					return ;
				
				// Connect this presenter to the workshop
				//
				_parent = event.getParent() ;
				
				Log.debug("Loading Workshop's lemma presenter Page") ;
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
				
				Log.debug("Lemma presenter handling concept change") ;
				UpdateLinguisticLeaf() ;
			}
		});		
	}
	
	/**
	 * Ask the view to create editing controls and connect corresponding handlers
	 */
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
	
	/**
	 * User wants to create a new lemma
	 */
	protected void addNewLemma()
	{
		
	}
	
	protected void UpdateLinguisticLeaf()
	{
		// Since the process is asynchronous, we better clear the list first so it doesn't remain out-of-date for some times   
		//
		display.feedLinguisticTree(null) ;
		display.feedLinguisticTreeForOthers(null) ;
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
		
		display.feedLinguisticTree(aUserLanguage) ;
		display.feedLinguisticTreeForOthers(aOtherLanguages) ;
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
	
	public void setParent(QuadrifoliumWorkshopPresenterModel<QuadrifoliumWorkshopViewModel> parent) {
		_parent = parent ;
	}
	
	public void setReadOnly(boolean bReadOnly) {
		_bReadOnly = bReadOnly ;
		
		if (false == _bReadOnly)
			initCommandControls() ;
	}
	public boolean isReadOnly() {                           
		return _bReadOnly ;
	}
	
	public Flex getPreferredInflexion() {
		return _preferredInflexion ;
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
