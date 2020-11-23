package org.quadrifolium.client.mvp;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.place.PlaceRequest;
import net.customware.gwt.presenter.client.widget.WidgetDisplay;
import net.customware.gwt.presenter.client.widget.WidgetPresenter;

import org.quadrifolium.client.event.AtelierPhpEvent;
import org.quadrifolium.client.event.BackToWelcomePageEvent;
import org.quadrifolium.client.event.BackToWelcomePageEventHandler;
import org.quadrifolium.client.event.BackToWelcomeTextEvent;
import org.quadrifolium.client.event.BackToWelcomeTextEventHandler;
import org.quadrifolium.client.event.CommandLoadLanguagesEvent;
import org.quadrifolium.client.event.GoToAtelierPhpEvent;
import org.quadrifolium.client.event.GoToAtelierPhpEventHandler;
import org.quadrifolium.client.event.GoToLoginResponseEvent;
import org.quadrifolium.client.event.GoToLoginResponseEventHandler;
import org.quadrifolium.client.event.GoToWorkshopCommandEvent;
import org.quadrifolium.client.event.LoginPageEvent;
import org.quadrifolium.client.event.PostLoginHeaderEvent;
import org.quadrifolium.client.event.RegisterSentEvent;
import org.quadrifolium.client.event.SignalConceptChangedEvent;
import org.quadrifolium.client.event.SignalConceptChangedEventHandler;
import org.quadrifolium.client.event.WelcomePageEvent;
import org.quadrifolium.client.event.WelcomeTextEvent;
import org.quadrifolium.client.gin.QuadrifoliumGinjector;
import org.quadrifolium.client.global.QuadrifoliumSupervisor;
import org.quadrifolium.shared.ontology.LanguageTag;
import org.quadrifolium.shared.rpc.GetLanguagesAction;
import org.quadrifolium.shared.rpc.GetLanguagesResult;
import org.quadrifolium.shared.rpc4ontology.GetLanguageTagsAction;
import org.quadrifolium.shared.rpc4ontology.GetLanguageTagsResult;
import org.quadrifolium.shared.rpc_util.SessionElements;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.inject.Inject;

public class QuadrifoliumMainPresenter extends WidgetPresenter<QuadrifoliumMainPresenter.Display> 
{
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
/*private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";
	*/
	public interface Display extends WidgetDisplay 
	{
		public FlowPanel getWorkspace() ;
		public FlowPanel getHeader() ;
		public FlowPanel getCommand() ;
		public FlowPanel getFooter() ; 
	}
	
	private final QuadrifoliumSupervisor _supervisor ;
	private final DispatchAsync          _dispatcher ;
	
	private       boolean          _isWelcomeTextCreated ;
	private       boolean          _isWelcomePageCreated ;
	private       boolean          _isLoginCreated ;
	private       boolean          _isCommandCreated ;
	private       boolean          _isWorkshopPageCreated ;
	private       boolean          _isAtelierPhpCreated ;
	private       boolean          _isPostLoginHeaderCreated ;
	private       boolean          _isRegisterPageCreated ;
	
	private       enum displayMode { displayWorkshop, displayText } ;
	private       displayMode      _iDisplayMode ;
	
	protected     String           _sTargetConcept ;   // Target concept as received from the URL variables
	protected     String           _sTargetLanguage ;  // Target language as received from the URL variables

	private       ScheduledCommand _pendingEvents ;

	@Inject
	public QuadrifoliumMainPresenter(final Display                display, 
						                       final EventBus               eventBus, 
						                       final DispatchAsync          dispatcher,
						                       final QuadrifoliumSupervisor supervisor) 
	{
		super(display, eventBus) ;
		
		_isLoginCreated           = false ;
		_isCommandCreated         = false ;
		_isWorkshopPageCreated    = false ;
		_isAtelierPhpCreated      = false ;
		_isPostLoginHeaderCreated = false ;
		_isWelcomeTextCreated     = false ;
		_isWelcomePageCreated     = false ;
		_isRegisterPageCreated    = false ;
		
		_sTargetConcept           = "" ;
		_sTargetLanguage          = "" ;
		
		_iDisplayMode             = displayMode.displayText ;
		
		_supervisor = supervisor ;
		_dispatcher = dispatcher ;
		
		bind() ;
	}
	
	/**
	 * Connecting events 
	 */
	@Override
	protected void onBind() 
	{
		// Connect events from the Event Bus
		//
		eventBus.addHandler(BackToWelcomePageEvent.TYPE, new BackToWelcomePageEventHandler() {
			@Override
			public void onBackToWelcome(BackToWelcomePageEvent event) 
			{
				Log.info("User wants to go back to welcome page") ;
				doLoad() ;	
			}
		});
		
		eventBus.addHandler(BackToWelcomeTextEvent.TYPE, new BackToWelcomeTextEventHandler() {
			@Override
			public void onBackToWelcomeText(BackToWelcomeTextEvent event) 
			{
				Log.info("User wants to go back to welcome text page") ;
				doLoadText() ;	
			}
		});
		
		eventBus.addHandler(SignalConceptChangedEvent.TYPE, new SignalConceptChangedEventHandler() {
			@Override
			public void onConceptChanged(SignalConceptChangedEvent event) 
			{
				Log.info("Concept changed") ;
				
				String sConcept  = _supervisor.getConcept() ;
				
				if ((null == sConcept) || "".equals(sConcept))
					doLoadText() ;
				else
					doLoadConcept() ;
			}
		});
		
		eventBus.addHandler(GoToLoginResponseEvent.TYPE, new GoToLoginResponseEventHandler() 
		{
			@Override
			public void onGoToLoginResponse(GoToLoginResponseEvent event) 
			{
				Log.info("Call to go to post login page") ;
				goToPostLoginHeader() ;
				doLoadConcept() ;	
			}
		});
				
		eventBus.addHandler(GoToAtelierPhpEvent.TYPE, new GoToAtelierPhpEventHandler() 
		{
			@Override
			public void onGoToAtelierPhp(GoToAtelierPhpEvent event) 
			{
				Log.info("Call to go to atelier PHP") ;
				// doLoadAtelierPhp() ;
				
				SessionElements sessionElements = _supervisor.getSessionElements() ; 
				if (null == sessionElements)
					return ;
				
				if (Storage.isLocalStorageSupported())
				{
					Storage localStorage = Storage.getLocalStorageIfSupported() ;
					if (null != localStorage)
					{
						localStorage.setItem("QuadrifoliumToken",   sessionElements.getToken()) ;
						localStorage.setItem("QuadrifoliumSession", "" + sessionElements.getSessionId()) ;
					}
				}
				
				String sUrl = "https://quadrifolium.org/atelier" ;
				// sUrl += "?token=" + sessionElements.getToken() + "&session=" + sessionElements.getSessionId() ;
				
				Window.open(sUrl, "_blank", "") ;
			}
		});
		
		doLoad() ;
		doLogin() ;
		loadCommand() ;
		initLanguages() ;
	}

	/**
	 * Load the text, either as a description text or a concept display page
	 */
	public void doLoad()
	{
		if ("".equals(_sTargetConcept))
			doLoadText() ;
		else
			doLoadConcept() ;
	}
	
	protected String getCurrentLanguage()
	{
		String sLanguage = _supervisor.getUserLanguage() ;
		if ("".equals(sLanguage))
			sLanguage = _sTargetLanguage ;
		
		return sLanguage ;
	}
	
	/**
	 * Load the description text interface
	 */
	public void doLoadText()
	{
		Log.info("Calling LoadText") ;
		
		_iDisplayMode = displayMode.displayText ;
		
		if ((false == _isWelcomeTextCreated) && (null != _supervisor) && (null != _supervisor.getInjector()))
		{
			QuadrifoliumGinjector injector = _supervisor.getInjector() ;
			injector.getWelcomeTextPresenter() ; 
			_isWelcomeTextCreated = true ;
		}
		display.getWorkspace().clear() ;
		
		// If WelcomeTextEvent is not handled yet, we have to defer fireEvent
		//
		if (false == eventBus.isEventHandled(WelcomeTextEvent.TYPE))
		{
			if (null == _pendingEvents) 
			{
				_pendingEvents = new ScheduledCommand() 
				{
	        public void execute() {
	        	_pendingEvents = null ;
	        	eventBus.fireEvent(new WelcomeTextEvent(display.getWorkspace(), getCurrentLanguage())) ;
	        }
	      };
	      Scheduler.get().scheduleDeferred(_pendingEvents) ;
	    }
		}
		else
			eventBus.fireEvent(new WelcomeTextEvent(display.getWorkspace(), getCurrentLanguage())) ;		
	}
	
	/**
	 * Load Quadrifolium "read only" concept display interface
	 */
	public void doLoadConcept()
	{
		Log.info("Calling Load") ;
		
		_iDisplayMode = displayMode.displayWorkshop ;
		
		if ((false == _isWelcomePageCreated) && (null != _supervisor) && (null != _supervisor.getInjector()))
		{
			QuadrifoliumGinjector injector = _supervisor.getInjector() ;
			injector.getWelcomePagePresenter() ;
			_isWelcomePageCreated = true ;
		}
		display.getWorkspace().clear() ;
		
		// If WelcomePageEvent is not handled yet, we have to defer fireEvent
		//
		if (false == eventBus.isEventHandled(WelcomePageEvent.TYPE))
		{
			if (null == _pendingEvents) 
			{
				_pendingEvents = new ScheduledCommand() 
				{
	        public void execute() {
	        	_pendingEvents = null ;
	        	eventBus.fireEvent(new WelcomePageEvent(display.getWorkspace())) ;
	        }
	      };
	      Scheduler.get().scheduleDeferred(_pendingEvents) ;
	    }
		}
		else
			eventBus.fireEvent(new WelcomePageEvent(display.getWorkspace())) ;
	}
	
	/**
	 * Load Quadrifolium true workshop interface
	 */
/*
	public void goToWorkshopPage()
	{
		Log.info("Going to workshop page") ;
		display.getWorkspace().clear() ;
		if ((false == _isWorkshopPageCreated) && (null != _supervisor) && (null != _supervisor.getInjector()))
		{
			_isWorkshopPageCreated = true ;
			QuadrifoliumGinjector injector = _supervisor.getInjector() ;
			injector.getWorkshopPresenter() ;
		}

		// If GoToWorkshopEvent is not handled yet, we have to defer fireEvent
		//
		if (false == eventBus.isEventHandled(GoToWorkshopEvent.TYPE))
		{
			if (null == _pendingEvents) 
			{
				_pendingEvents = new ScheduledCommand() 
				{
	        public void execute() {
	        	_pendingEvents = null ;
	        	eventBus.fireEvent(new GoToWorkshopEvent(display.getWorkspace())) ;
	        }
	      };
	      Scheduler.get().scheduleDeferred(_pendingEvents) ;
	    }
			else
			{
				// Create a new timer that calls goToPostLoginPage() again later.
		    Timer t = new Timer() {
		      public void run() {
		      	goToWorkshopPage() ;
		      }
		    };
		    // Schedule the timer to run once in 5 seconds.
		    t.schedule(1000);
			}
		}
		else
			eventBus.fireEvent(new GoToWorkshopEvent(display.getWorkspace())) ;	
	}
*/
	
	public void goToNewUserParamsPage(String sId)
	{
		if ((null == sId) || sId.equals(""))
			return ;
		
		// validateNewUser(sId) ;
	}
	
	public void goToPostLoginHeader()
	{
		Log.info("Switch to post-login header") ;
		display.getHeader().clear() ;
		if ((false == _isPostLoginHeaderCreated) && (null != _supervisor) && (null != _supervisor.getInjector()))
		{
			QuadrifoliumGinjector injector = _supervisor.getInjector() ;
			injector.getPostLoginHeaderPresenter() ;
			_isPostLoginHeaderCreated = true ;
		}

		// If UserParamsSentEvent is not handled yet, we have to defer fireEvent
		//
		if (false == eventBus.isEventHandled(PostLoginHeaderEvent.TYPE))
		{
			if (null == _pendingEvents) 
			{
				_pendingEvents = new ScheduledCommand() 
				{
	        public void execute() {
	        	_pendingEvents = null ;
	        	eventBus.fireEvent(new PostLoginHeaderEvent(display.getHeader())) ;
	        }
	      };
	      Scheduler.get().scheduleDeferred(_pendingEvents) ;
	    }
			else
			{
				// Create a new timer that calls goToPostLoginHeader() again later.
		    Timer t = new Timer() {
		      public void run() {
		      	goToPostLoginHeader() ;
		      }
		    };
		    // Schedule the timer to run once in 5 seconds.
		    t.schedule(1000);
			}
		}
		else
			eventBus.fireEvent(new PostLoginHeaderEvent(display.getHeader())) ;	
	}
	
	/**
	 * Load the French preliminary workshop
	 */
	public void doLoadAtelierPhp()
	{
		Log.info("Calling LoadAtelierPhp") ;

		if ((false == _isAtelierPhpCreated) && (null != _supervisor) && (null != _supervisor.getInjector()))
		{
			QuadrifoliumGinjector injector = _supervisor.getInjector() ;
			injector.getAtelierPhpPresenter() ; 
			_isAtelierPhpCreated = true ;
		}
		display.getWorkspace().clear() ;
		
		// If AtelierPhpEvent is not handled yet, we have to defer fireEvent
		//
		if (false == eventBus.isEventHandled(AtelierPhpEvent.TYPE))
		{
			if (null == _pendingEvents) 
			{
				_pendingEvents = new ScheduledCommand() 
				{
	        public void execute() {
	        	_pendingEvents = null ;
	        	eventBus.fireEvent(new AtelierPhpEvent(display.getWorkspace())) ;
	        }
	      };
	      Scheduler.get().scheduleDeferred(_pendingEvents) ;
	    }
		}
		else
			eventBus.fireEvent(new AtelierPhpEvent(display.getWorkspace())) ;		
	}
	
	/**
	 * Go to the person account creation page 
	 */
	public void goToRegisterPage()
	{
		display.getWorkspace().clear() ;
		if ((false == _isRegisterPageCreated) && (null != _supervisor) && (null != _supervisor.getInjector()))
		{
			QuadrifoliumGinjector injector = _supervisor.getInjector() ;
			injector.getRegisterPresenter() ;
			_isRegisterPageCreated = true ;
		}

		// If RegisterSentEvent is not handled yet, we have to defer fireEvent
		//
		if (false == eventBus.isEventHandled(RegisterSentEvent.TYPE))
		{
			if (null == _pendingEvents) 
			{
				_pendingEvents = new ScheduledCommand() 
				{
	        public void execute() {
	        	_pendingEvents = null ;
	        	eventBus.fireEvent(new RegisterSentEvent(display.getWorkspace())) ;
	        }
	      };
	      Scheduler.get().scheduleDeferred(_pendingEvents) ;
	    }
		}
		else
			eventBus.fireEvent(new RegisterSentEvent(display.getWorkspace())) ;	
	}
	
	/**
	 * Load the command panel 
	 */
	public void loadCommand() 
	{
		Log.info("Calling loadCommand") ;
		
		if ((false == _isCommandCreated) && (null != _supervisor) && (null != _supervisor.getInjector()))
		{
			QuadrifoliumGinjector injector = _supervisor.getInjector() ;
			_supervisor.setCommandPanel(injector.getCommandPresenter()) ;
			_isCommandCreated = true ;
		}
		
		// If UserParamsSentEvent is not handled yet, we have to defer fireEvent
		//
		if (false == eventBus.isEventHandled(GoToWorkshopCommandEvent.TYPE))
		{
			if (null == _pendingEvents) 
			{
				_pendingEvents = new ScheduledCommand() 
				{
	        public void execute() {
	        	_pendingEvents = null ;
	        	eventBus.fireEvent(new GoToWorkshopCommandEvent(display.getCommand(), _sTargetConcept)) ;
	        }
	      };
	      Scheduler.get().scheduleDeferred(_pendingEvents) ;
	    }
		}
		else
			eventBus.fireEvent(new GoToWorkshopCommandEvent(display.getCommand(), _sTargetConcept)) ;
				
		if (false == eventBus.isEventHandled(LoginPageEvent.TYPE))
			Log.info("Error in eventBus") ;
	}

	/**
	 * Load the login components 
	 */
	public void doLogin() 
	{
		Log.info("Calling doLogin") ;
		if ((false == _isLoginCreated) && (null != _supervisor) && (null != _supervisor.getInjector()))
		{
			QuadrifoliumGinjector injector = _supervisor.getInjector() ;
			injector.getLoginPresenter() ;
			_isLoginCreated = true ;
		}
		
		// If UserParamsSentEvent is not handled yet, we have to defer fireEvent
		//
		if (false == eventBus.isEventHandled(LoginPageEvent.TYPE))
		{
			if (null == _pendingEvents) 
			{
				_pendingEvents = new ScheduledCommand() 
				{
	        public void execute() {
	        	_pendingEvents = null ;
	        	eventBus.fireEvent(new LoginPageEvent(display.getHeader())) ;
	        }
	      };
	      Scheduler.get().scheduleDeferred(_pendingEvents) ;
	    }
		}
		else
			eventBus.fireEvent(new LoginPageEvent(display.getHeader())) ;
				
		if (false == eventBus.isEventHandled(LoginPageEvent.TYPE))
			Log.info("Error in eventBus") ;
	}
	
	/**
	 * Initialize supervisor's languages and language tags lists
	 */
	protected void initLanguages()
	{
		// Get languages list
		//
		Log.info("Ask server for users languages") ;
		
		_dispatcher.execute(new GetLanguagesAction(), new GetLanguagesCallback()) ;
		
		// Get language tags list
		//
		Log.info("Loading language tags") ;
		
		_dispatcher.execute(new GetLanguageTagsAction(), new GetLanguageTagsCallback()) ;
	}
	
	protected class GetLanguagesCallback implements AsyncCallback<GetLanguagesResult> 
	{
		public GetLanguagesCallback() {
			super() ;
		}

		@Override
		public void onFailure(Throwable cause) {
			Log.error("Unhandled error when getting user languages.", cause.getMessage()) ;
		}

		@Override
		public void onSuccess(GetLanguagesResult value) 
		{
			if (value.isEmpty())
			{
				Log.info("No user language found on server.") ;
				return ;
			}
			
			Log.info("Loading users languages") ;
			
			// Load supervisor's users languages
			//
			_supervisor.setUserLanguages(value.getLanguages()) ;
			
			// Ask the command component to (re)load its languages selection interface 
			//
			eventBus.fireEvent(new CommandLoadLanguagesEvent()) ;
		}
	}
	
	protected class GetLanguageTagsCallback implements AsyncCallback<GetLanguageTagsResult> 
	{
		public GetLanguageTagsCallback() {
			super() ;
		}

		@Override
		public void onFailure(Throwable cause) {
			Log.error("Unhandled error when getting language tags.", cause.getMessage()) ;
		}

		@Override
		public void onSuccess(GetLanguageTagsResult value) 
		{
			if (value.isEmpty())
			{
				Log.info("No language tag found on server.") ;
				return ;
			}
			
			Log.info("Loading language tags") ;
			
			for (LanguageTag tag : value.getTagsArray())
				_supervisor.addLanguageTag(tag) ;
		}
	}

	public void setTargetConcept(final String sTargetConcept) {
		_sTargetConcept = sTargetConcept ;
	}
	
	public void setTargetLanguage(final String sTargetLanguage)
	{          
		_sTargetLanguage = sTargetLanguage ;
		_supervisor.setUserLanguage(sTargetLanguage) ;
	}
	
	@Override
	protected void onUnbind() {
	}

	public void refreshDisplay() {	
	}

	public void revealDisplay() {
	}
		
	protected void onPlaceRequest(final PlaceRequest request) {	
	}

	@Override
	protected void onRevealDisplay()
	{
		// TODO Auto-generated method stub
	}
}
