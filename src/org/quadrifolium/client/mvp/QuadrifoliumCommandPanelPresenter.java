package org.quadrifolium.client.mvp;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.widget.WidgetDisplay;
import net.customware.gwt.presenter.client.widget.WidgetPresenter;

import java.util.ArrayList;

import org.quadrifolium.client.event.CommandDisplayTitleEvent;
import org.quadrifolium.client.event.CommandDisplayTitleEventHandler;
import org.quadrifolium.client.event.CommandLoadLanguagesEvent;
import org.quadrifolium.client.event.CommandLoadLanguagesEventHandler;
import org.quadrifolium.client.event.GoToWorkshopCommandEvent;
import org.quadrifolium.client.event.GoToWorkshopCommandEventHandler;
import org.quadrifolium.client.event.SignalConceptChangedEvent;
import org.quadrifolium.client.event.UserChangedEvent;
import org.quadrifolium.client.global.QuadrifoliumSupervisor;
import org.quadrifolium.client.widgets.ConceptChangedEvent;
import org.quadrifolium.client.widgets.ConceptChangedHandler;
import org.quadrifolium.client.widgets.FlexTextBox;
import org.quadrifolium.client.widgets.FlexTextBoxManager;
import org.quadrifolium.shared.database.Language;
import org.quadrifolium.shared.ontology.Flex;
import org.quadrifolium.shared.rpc_special.LexiqueTo4foliumAction;
import org.quadrifolium.shared.rpc_special.LexiqueTo4foliumResult;
import org.quadrifolium.shared.util.QuadrifoliumFcts;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Panel;
import com.google.inject.Inject;

public class QuadrifoliumCommandPanelPresenter extends WidgetPresenter<QuadrifoliumCommandPanelPresenter.Display>
{	
	public interface Display extends WidgetDisplay 
	{
		public FlexTextBox       getTermChangeTextBox() ;
		public HasClickHandlers  getTermChangeButton() ;
		
		public void              displayTitle(final String sTitle) ;
		
		public HasChangeHandlers getLanguageChangeHandler() ;
		public void              initLanguages(final ArrayList<Language> aLanguages) ;
		public void              selectLanguage(final String sLanguageCode) ;
		public String            getSelectedLanguageCode() ;
	}

	private final DispatchAsync          _dispatcher ;
	private final QuadrifoliumSupervisor _supervisor ;

	protected     String                 _sConcept ;
	
	protected     FlexTextBoxManager     _flexTextBoxManager ;
		
	@Inject
	public QuadrifoliumCommandPanelPresenter(final Display                display, 
			                                     final EventBus               eventBus,
			                                     final DispatchAsync          dispatcher,
			                                     final QuadrifoliumSupervisor supervisor) 
	{
		super(display, eventBus) ;
		
		_dispatcher         = dispatcher ;
		_supervisor         = supervisor ;
		
		_flexTextBoxManager = new FlexTextBoxManager() ;
		
		_sConcept           = "" ;
		
		bind() ;
	}
	
	/**
	 * Binds Event Bus events that are handled by this presenter
	 */
	@Override
	protected void onBind() 
	{
		Log.debug("Entering QuadrifoliumCommandPanelPresenter::onBind()") ;
		
		// Message received when it is time to display open the command panel
		//
		eventBus.addHandler(GoToWorkshopCommandEvent.TYPE, new GoToWorkshopCommandEventHandler() {
			public void onGoToWorkshopCommand(GoToWorkshopCommandEvent event)
			{
				Log.debug("Loading Workshop's command presenter Page") ;
				Panel commandWorkspace = event.getWorkspace() ;
				commandWorkspace.clear() ;
				commandWorkspace.add(display.asWidget()) ;
				
				String sTargetConcept = event.getConcept() ;
				
				if (null == sTargetConcept)
					_sConcept = "" ;
				else
					_sConcept = sTargetConcept ;
			}
		});
		
		// Message received when the title should be updated
		//
		eventBus.addHandler(CommandDisplayTitleEvent.TYPE, new CommandDisplayTitleEventHandler() {
			public void onCommandDisplayTitle(CommandDisplayTitleEvent event)
			{
				String sNewTitle = event.getTitle() ;
				display.displayTitle(sNewTitle) ;
			}
		});
		
		// Message received when the languages selection control is to be refreshed
		//
		eventBus.addHandler(CommandLoadLanguagesEvent.TYPE, new CommandLoadLanguagesEventHandler() {
			public void onCommandLoadLanguages(CommandLoadLanguagesEvent event)
			{
				Log.info("Loading user languages control") ;
				display.initLanguages(_supervisor.getUserLanguages()) ;
				SetSelectedLanguage() ;
			}
		});
		
		// Text entered in the "change concept" text box changed. Refresh proposed list.
		//
		eventBus.addHandler(ConceptChangedEvent.getType(), new ConceptChangedHandler() {
			public void onConceptChanged(ConceptChangedEvent event)
			{
				SetNewConcept() ;
			}
		});
		
		// Text entered in the "change concept" text box changed. Refresh proposed list.
		//
		display.getTermChangeTextBox().getListBox().addDoubleClickHandler(new DoubleClickHandler() {
			public void onDoubleClick(DoubleClickEvent event) {
				TermSelected(event) ;
				SetNewConcept() ;
			}
		});
		
		// Text entered in the "change concept" text box changed. Refresh proposed list.
		//
		display.getTermChangeTextBox().addKeyUpHandler(new KeyUpHandler() {
			public void onKeyUp(KeyUpEvent event) {
				TermChanged(event) ;
			}
		});
		
		// Click to establish just entered concept as current concept
		//
		display.getTermChangeButton().addClickHandler(new ClickHandler(){
			@Override
			public void onClick(final ClickEvent event)
			{
				SetNewConcept() ;
			}
		});
		
		// Click to establish just entered concept as current concept
		//
		display.getLanguageChangeHandler().addChangeHandler(new ChangeHandler(){
			@Override
			public void onChange(final ChangeEvent event)
			{
				SwitchToOtherLanguage() ;
			}
		});
				
		ConnectFlexTextBoxes() ;
		InitializeComponents() ;
	}
	
	private void ConnectFlexTextBoxes()
	{
		_flexTextBoxManager.addFlexTextBoxToBuffer(display.getTermChangeTextBox(), _supervisor.getUserLanguage()) ;
	}
	
	/**
	 * Initialize existing components, typically feed the languages selection box with referenced languages
	 */
	protected void InitializeComponents()
	{
		// Load the languages selection control (there are probably no languages available yet, the real initialization
		// is done when receiving the CommandLoadLanguagesEvent event). 
		//
		display.initLanguages(_supervisor.getUserLanguages()) ;
		SetSelectedLanguage() ;
	}
		
	/**
	 * The user pressed a key while the flex selection TextBox is focused, we have to react to this event 
	 * 
	 * @param event KeyUpEvent the handler was fired by
	 */
	private void TermChanged(KeyUpEvent event) 
	{
		boolean bMustRefresh = display.getTermChangeTextBox().processKeyUp(event) ;
		if (bMustRefresh)
			_flexTextBoxManager.initFlexBoxList(display.getTermChangeTextBox(), _supervisor.getUserId(), _dispatcher) ;
	}
	
	/**
	 * The user pressed a key while the flex selection TextBox is focused, we have to react to this event 
	 * 
	 * @param event KeyUpEvent the handler was fired by
	 */
	private void TermSelected(DoubleClickEvent event) {
		display.getTermChangeTextBox().complete() ;
	}
	
	/**
	 * The user pressed the concept selection button
	 */
	protected void SetNewConcept() 
	{
		// Get currently displayed flex (if any) in the flex selection TextBox
		//
		Flex flex = display.getTermChangeTextBox().getSelected() ;
		if (null == flex)
			return ;
		
		_sConcept = QuadrifoliumFcts.getConceptCode(flex.getCode()) ; 
		
		UpdateWorkshop() ;
	}

	/**
	 * Concept set from outside (for example the URL)
	 */
	public void SetConcept(final String sConcept) 
	{
		if ((null == sConcept) || "".equals(sConcept))
			return ;
		
		_sConcept = sConcept ; 
		
		UpdateWorkshop() ;
	}
	
	/**
	 * Set this language as the selected one without refreshing the workspace
	 */
	public void SetSelectedLanguage() {
		display.selectLanguage(_supervisor.getUserLanguage()) ;
	}
	
	/**
	 * A new interface language was selected
	 */
	protected void SwitchToOtherLanguage()
	{
		String sSelectedLanguage = display.getSelectedLanguageCode() ;
		if ((null == sSelectedLanguage) || "".equals(sSelectedLanguage))
			return ;
		
		_supervisor.setUserLanguage(sSelectedLanguage) ;
		
		UpdateWorkshop() ;
	}
	
	protected void UpdateWorkshop() {
		eventBus.fireEvent(new SignalConceptChangedEvent()) ;
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

	/**
	 * User changed, tell interface elements
	 */
	public void userChanged()
	{
		eventBus.fireEvent(new UserChangedEvent()) ;
	}
	
	/**
	 * Display command panel's title (should be the preferred term form current concept)
	 */
	public void displayTitle(final String sTitle) {
		display.displayTitle(sTitle) ;
	}
	
	public String getConcept() {
		return _sConcept ;
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
