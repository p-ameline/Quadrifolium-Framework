package org.quadrifolium.client.mvp;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.place.PlaceRequest;
import net.customware.gwt.presenter.client.widget.WidgetDisplay;
import net.customware.gwt.presenter.client.widget.WidgetPresenter;

import org.quadrifolium.client.event.GoToLoginResponseEvent;
import org.quadrifolium.client.event.LoginPageEvent;
import org.quadrifolium.client.event.LoginPageEventHandler;
import org.quadrifolium.client.event.LoginSentEvent;
import org.quadrifolium.client.event.LoginSentEventHandler;
import org.quadrifolium.client.global.QuadrifoliumSupervisor;
import org.quadrifolium.shared.database.Person;
import org.quadrifolium.shared.rpc.SendLoginAction;
import org.quadrifolium.shared.rpc.SendLoginResult;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasKeyDownHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.inject.Inject;

public class QuadrifoliumLoginHeaderPresenter extends WidgetPresenter<QuadrifoliumLoginHeaderPresenter.Display>
{	
	public interface Display extends WidgetDisplay 
	{	
		public String             getUser() ;
		public String             getPassWord() ;
		public FlexTable          getLoginTable() ;
		public void               showBadVersionMessage(final String sClientVersion, final String sServerVersion) ;
		
		public Button             getSendLogin() ;
		public HasKeyDownHandlers getPassKeyDown() ;
		
		public DialogBox getErrDialogBox() ;
		public Button    getErrDialogBoxOkButton() ;
		public Button    getErrDialogBoxSendIdsButton() ;
		public void      showWaitCursor() ;
		public void      showDefaultCursor() ;
	}

	private final DispatchAsync          _dispatcher ;
	private final QuadrifoliumSupervisor _supervisor ;
	
	@Inject
	public QuadrifoliumLoginHeaderPresenter(final Display                display, 
						                              final EventBus               eventBus,
						                              final DispatchAsync          dispatcher,
						                              final QuadrifoliumSupervisor supervisor) 
	{
		super(display, eventBus) ;
		
		_dispatcher = dispatcher ;
		_supervisor = supervisor ;
		
		bind() ;
	}
	
	/**
	 * Install events answer functions
	 */
	@Override
	protected void onBind() 
	{
		Log.info("Entering CoachingFitLoginHeaderPresenter::onBind()") ;
		
		// Event bus message handlers
		//
		eventBus.addHandler(LoginPageEvent.TYPE, new LoginPageEventHandler() {
	      public void onLogin(LoginPageEvent event) 
	      {
					Log.info("Creating Login Label");
				  event.getHeader().clear() ;
				  FlowPanel Header = event.getHeader() ;
				  Header.add(display.getLoginTable()) ;
					Log.info("Creating Login Label success");
				}
		});
		
		eventBus.addHandler(LoginSentEvent.TYPE, new LoginSentEventHandler() {
	 		public void onSendLogin(LoginSentEvent event) 
	 		{
				Log.info("Sending User and PassWord") ;
			  doSendLogin(event.getName(), event.getPassword()) ;
			}
		});
		
		// Controls notifications
		//
		display.getSendLogin().addClickHandler(new ClickHandler(){
				public void onClick(final ClickEvent event)
				{
					display.showWaitCursor() ;
					eventBus.fireEvent(new LoginSentEvent(display.getUser(),display.getPassWord())) ;
				}
		});
		
		/**
		 * Get key down from password Textbox and start searching when enter key is detected 
		 * */
		display.getPassKeyDown().addKeyDownHandler(new KeyDownHandler() {
			public void onKeyDown(KeyDownEvent event) 
			{
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					display.showWaitCursor() ;
					eventBus.fireEvent(new LoginSentEvent(display.getUser(),display.getPassWord())) ;
				}
			}
		}) ;
		
		display.getErrDialogBoxOkButton().addClickHandler(new ClickHandler(){
				public void onClick(final ClickEvent event)
				{
					display.getErrDialogBox().hide() ;
				}
		});
	}
	
	/**
	 * Ciphers the password, then send login and ciphered password for the server to check login validity
	 * 
	 * @param sUserName User's login
	 * @param sPassword User's password
	 */
	public void doSendLogin(final String sUserName, final String sPassword) 
	{
		_supervisor.initCipherElements(sPassword) ;
		_dispatcher.execute(new SendLoginAction(sUserName, _supervisor.getUserCipherPassword()), new LoginUserCallback()) ;
	}
	
	/**
	 * Callback function called when the server answers to login attempt
	 * 
	 * @author Philippe
	 *
	 */
	public class LoginUserCallback implements AsyncCallback<SendLoginResult>
	{
		public LoginUserCallback() {
			super() ;
		}

		@Override
		public void onFailure(Throwable cause) {
			Log.error("Unhandled error", cause);
			Log.info("error from LoginUserCallback!!");
			display.showDefaultCursor() ;
		}

		@Override
		public void onSuccess(SendLoginResult value) 
		{
			display.showDefaultCursor() ;
			
			// No user found, display an error message
			//
			Person person = value.getUser() ;
			if ((null == person) || (false == person.isReferenced()))
			{
				display.getErrDialogBox().show() ;
				return ;
			}
			
			// Initialize the supervisor with user and server version information
			//
			_supervisor.setUser(person) ;
			_supervisor.setSessionElements(value.getSessionElements()) ;
			_supervisor.setServerVersion(value.getVersion()) ;

			// Tell display to mask login controls
			//
      display.getLoginTable().setVisible(false) ;
      
      // Publish the successful login status to the Event Bus
      //
			eventBus.fireEvent(new GoToLoginResponseEvent()) ;
			
			// If client version and server version are not equal, warn the user that he should reload
			//
			if (false == _supervisor.getClientVersion().equals(_supervisor.getServerVersion()))
				display.showBadVersionMessage(_supervisor.getClientVersion(), _supervisor.getServerVersion());
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
	
	protected void onPlaceRequest(final PlaceRequest request) {
		// this is a popup
	}

	@Override
	protected void onRevealDisplay() {
		// TODO Auto-generated method stub		
	}	
}
