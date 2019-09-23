package org.quadrifolium.client.mvp;

import org.quadrifolium.client.util.FieldVerifier;
import org.quadrifolium.shared.rpc.CheckPseudoAction;
import org.quadrifolium.shared.rpc.CheckPseudoResult;
import org.quadrifolium.shared.util.MiscellanousFcts;

import com.allen_sauer.gwt.log.client.Log;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.widget.WidgetDisplay;
import net.customware.gwt.presenter.client.widget.WidgetPresenter;

/**
 * Abstract super-class for user parameters presenters
 * 
 * @author Philippe Ameline
 * 
 */
public abstract class QuadrifoliumUserEditPresenter<D extends QuadrifoliumUserEditPresenter.DisplayModel> extends WidgetPresenter<D> 
{
	public interface DisplayModel extends WidgetDisplay
	{
		HasText          getUserPseudo() ;
		HasText          getUserPassword() ;
		HasText          getUserConfirmedPassword() ;
		
		TextBox          getPseudo() ;
		PasswordTextBox  getPassword () ;
		PasswordTextBox  getConfirmedPassword() ;
		TextBox          getEmail() ;
		TextBox          getConfirmedEmail() ;
		Button           getSubmitButton() ;
		
		HasText          getUserNameMsg() ;
		void             emptyPseudoMsg() ;
		void             setPseudoMsgInvalid() ;
		void             setPseudoCheckError() ;
		void             setPseudoMsgNotAvailable() ;
		
		HasText          getPasswordMsg() ;
		void             emptyPasswordMsg() ;
		void             setPasswordMsgInvalid() ;
		
		HasText          getConfirmedPassMsg() ;
		void             emptyConfirmedMsg() ;
		void             setConfirmedMsgAreDifferent() ;
		
		HasText          getEmailMsg() ;
		void             emptyEmailMsg() ;
		void             setEmailMsgInvalid() ;
		
		HasText          getConfirmedMailMsg() ;
		void             emptyConfirmedMailMsg() ;
		void             setConfirmedMailsAreDifferent() ;
	}
	
	protected boolean             _bAvailablePseudo ;
	protected final DispatchAsync _dispatcher ;
	
	public QuadrifoliumUserEditPresenter(final D             display, 
			                                 final EventBus      eventBus,
			                                 final DispatchAsync dispatcher) 
	{
		super(display, eventBus) ;
		
		_dispatcher       = dispatcher ;
		_bAvailablePseudo = false ;
	}
	
	@Override
	protected void onBind() 
	{
		/**
		 * verify userName
		 * invoked when lost focus of the field
		 * implementation derived from BlurHandler
		 * 
		 */
		display.getPseudo().addBlurHandler(new UsernameCheckHandler()) ;
		
		/**
		 * verify password
		 * invoked when lost focus of the field
		 * implementation derived from BlurHandler
		 * 
		 */
		display.getPassword().addBlurHandler(new PasswordCheckHandler() ) ;
		
		/**
		 * verify 2 password
		 * invoked when lost focus of the field
		 * implementation derived from BlurHandler
		 * */
		display.getConfirmedPassword().addBlurHandler(new ConfirmPasswordHandler()) ;//end 2 passwords'identification check
		
		/**
		 * verify email
		 * invoked when lost focus of the field
		 * implementation derived form BlurHandler
		 * */
		display.getEmail().addBlurHandler(new EmailCheckHandler()) ;//end email check
		
		/**
		 * verify 2 mails
		 * */
		if (null != display.getConfirmedEmail())
			display.getConfirmedEmail().addBlurHandler(new ConfirmMailHandler()) ;
	}
    
	/**
	 * Verification of username field
	 * not null, length larger than 3
	 * TODO illegal character
	 * TODO comparison with existed username in database
	 * 
	 * */
	private class UsernameCheckHandler implements BlurHandler 
	{
		@Override
  	public void onBlur(BlurEvent event) 
  	{
			managePseudoValidity() ;
  	}		
  }
	
	/**
	 * Checks if entered pseudo doesn't already exist in database
	 * 
	 * */
	protected void managePseudoValidity()
	{
		_bAvailablePseudo = false ;
		
		String _pseudo = display.getUserPseudo().getText() ;
		
		if (false == FieldVerifier.isValidName(_pseudo)) {
			display.setPseudoMsgInvalid() ;
		} 
		else 
		{
			display.emptyPseudoMsg() ;
			checkPseudoAvailability(_pseudo) ;
		}
		
		manageSubmitButtonActivation() ;
	}
	
	/**
	 * Asynchronous function called to check is the entered pseudo is available
	 * 
	 * @param pseudo Pseudo entered by the user
	 */
	private void checkPseudoAvailability(final String pseudo)
	{	
		_dispatcher.execute(new CheckPseudoAction(pseudo), new PseudoAvailabilityCallback()) ;
	}
	
	protected class PseudoAvailabilityCallback implements AsyncCallback<CheckPseudoResult> 
	{
		public PseudoAvailabilityCallback() {
			super() ;
		}

		@Override
		public void onFailure(Throwable cause) {
			Log.error("Unhandled error", cause);
		}

		@Override
		public void onSuccess(CheckPseudoResult value) 
		{
			if (value.wasRequestSuccessful())
			{
				_bAvailablePseudo = (false == value.doesAlreadyExist()) ;
				if (_bAvailablePseudo)
					display.emptyPseudoMsg() ;
				else
					display.setPseudoMsgNotAvailable() ;
			}
			else
			{
				_bAvailablePseudo = false ;
				display.setPseudoCheckError() ;
			}
		}
	}
	
	/**
	 * Verification of password field
	 * not null, length larger than 3
	 * TODO illegal character
	 * TODO mix of number and character
	 * 
	 * */
	private class PasswordCheckHandler implements BlurHandler 
	{	
		private String _password ;
		@Override
		public void onBlur(BlurEvent event) 
		{
			_password = display.getUserPassword().getText() ;
			if (false == FieldVerifier.isValidName(_password)) {
				//Window.alert("password length must be larger than 3") ;
				display.setPasswordMsgInvalid() ;
			} else {
				display.emptyPasswordMsg() ;
			}
			
			manageSubmitButtonActivation() ;
		}
	}//end PasswordCheckHandler
	
	/**
	 * Verification of 2 password fields 
	 * invoked when mouse lost focus in
	 * confirmed password field
	 * TODO either password in 2 fields inserted doesnt invoke check mechanism 
	 * TODO only invoked when 2 fields are both inserted 
	 * */
	private class ConfirmPasswordHandler implements BlurHandler 
	{
		private String _password ; 
		private String _confirmedPassword ; 
		
		@Override
		public void onBlur(BlurEvent event) 
		{
			_password = display.getUserPassword().getText() ;
			_confirmedPassword = display.getUserConfirmedPassword().getText() ;
			if (false == MiscellanousFcts.areIdenticalStrings(_password, _confirmedPassword)) 
			{
				//Window.alert("Two Different Password") ;
				display.setConfirmedMsgAreDifferent() ;	
			} else {
				display.getConfirmedPassMsg().setText("") ;
			}
			
			manageSubmitButtonActivation() ;
		}
	}//end ConfirmPasswordHandler
	
	/**
	 * Verification of email field
	 * invoked when mouse lost focus
	 * in email field
	 * TODO the format check of email
	 * 
	 * */
	private class EmailCheckHandler implements BlurHandler 
	{	
		private String _email ;
		
		@Override
		public void onBlur(BlurEvent event) 
		{
			_email = display.getEmail().getText() ;
			
			if (false == MiscellanousFcts.isValidMailAddress(_email)) {
				display.setEmailMsgInvalid() ;
			} else {
				display.emptyEmailMsg() ;
			}
			
			manageSubmitButtonActivation() ;
		}
	}
	
	/**
	 * Verification of the 2 e-mail fields 
	 * invoked when mouse lost focus in confirmed password field
	 * TODO either password in 2 fields inserted doesnt invoke check mechanism 
	 * TODO only invoked when 2 fields are both inserted 
	 * */
	private class ConfirmMailHandler implements BlurHandler 
	{
		private String _mail ; 
		private String _confirmedMail ; 
		
		@Override
		public void onBlur(BlurEvent event) 
		{
			_mail = display.getEmail().getText() ;
			_confirmedMail = display.getConfirmedEmail().getText() ;
			if (false == MiscellanousFcts.areIdenticalStrings(_mail, _confirmedMail)) 
			{
				//Window.alert("Two Different Password") ;
				display.setConfirmedMailsAreDifferent() ;	
			} else {
				display.emptyConfirmedMailMsg() ;
			}
			
			manageSubmitButtonActivation() ;
		}
	}
	
	protected void manageSubmitButtonActivation()
	{
		if (_bAvailablePseudo && isPasswordOk() && isMailOk() /* && isBirthdateOk() */)
			display.getSubmitButton().setEnabled(true) ;
		else
			display.getSubmitButton().setEnabled(false) ;
	}
	
	protected boolean isPasswordOk() 
	{
		String password = display.getUserPassword().getText() ;
		if (false == FieldVerifier.isValidName(password))
			return false ;
		
		String confirmedPassword = display.getUserConfirmedPassword().getText() ;
		if (false == MiscellanousFcts.areIdenticalStrings(password, confirmedPassword))
			return false ;
		
		return true ;
	}
	
	protected boolean isMailOk() 
	{
		String email = display.getEmail().getText() ;
		if (false == MiscellanousFcts.isValidMailAddress(email))
			return false ;
		
		if (null != display.getConfirmedEmail())
		{
			String confirmedMail = display.getConfirmedEmail().getText() ;
			if (false == MiscellanousFcts.areIdenticalStrings(email, confirmedMail))
				return false ;
		}
		
		return true ;
	}
}
