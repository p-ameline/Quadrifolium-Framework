package org.quadrifolium.client.mvp;

import org.quadrifolium.client.loc.QuadrifoliumConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class QuadrifoliumUserEditView extends Composite
{	
	private final QuadrifoliumConstants constants = GWT.create(QuadrifoliumConstants.class) ;
	
	protected Label _pseudoLabel ;
	protected Label _passwdLabel ;
	protected Label _confirmLabel ;
	protected Label _emailLabel ;
	protected Label _emailConfirmLabel ;
	protected Label _languageLabel ;
	
	protected Label _pseudoMsg ;
	protected Label _passwordMsg ;
	protected Label _confirmedMsg ;
	protected Label _emailMsg ;
	protected Label _confirmedEmailMsg ;
	protected Label _languageMsg ;
	
	protected TextBox         _pseudoBox ;
	protected PasswordTextBox _passwordBox ;
	protected PasswordTextBox _confirmedBox ;
	protected TextBox         _emailBox ;
	protected TextBox         _emailConfirmedBox ;
	protected ListBox         _languageListBox ;		
	
	/**
	 * It's better to make a constant class
	 * to hold these static variables
	 * 
	 * */
/*
	public static int[] get30days = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,
		20,21,22,23,24,25,26,27,28,29,30} ;
	
	public static int[] get31days = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,
		20,21,22,23,24,25,26,27,28,29,30,31} ;
	
	public static int[] get29days = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,
		20,21,22,23,24,25,26,27,28,29} ;
	
	public static int[] get28days = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,
		20,21,22,23,24,25,26,27,28} ;
*/
			
	public QuadrifoliumUserEditView()
	{
	// init all labels
		//
		_pseudoLabel       = new Label(uppercaseFirstLetter(constants.generalPseudo())) ;
		_pseudoLabel.addStyleName("registerPageLabel") ;
		_passwdLabel       = new Label(uppercaseFirstLetter(constants.generalPassword())) ;
		_passwdLabel.addStyleName("registerPageLabel") ;
		_confirmLabel      = new Label(uppercaseFirstLetter(constants.generalPasswordConf())) ;
		_confirmLabel.addStyleName("registerPageLabel") ;
		_emailLabel        = new Label(uppercaseFirstLetter(constants.generalEmail())) ;
		_emailLabel.addStyleName("registerPageLabel") ;
		_emailConfirmLabel = new Label(uppercaseFirstLetter(constants.generalEmailConfirmed())) ;
		_emailConfirmLabel.addStyleName("registerPageLabel") ;
		_languageLabel     = new Label(uppercaseFirstLetter(constants.generalLanguage())) ;
		_languageLabel.addStyleName("registerPageLabel") ;
		
		// init all TextBoxs
		//
		_pseudoBox         = new TextBox() ;
		_pseudoBox.addStyleName("registerPageTextBox") ;
		_passwordBox       = new PasswordTextBox() ;
		_passwordBox.addStyleName("registerPageTextBox") ;
		_confirmedBox      = new PasswordTextBox() ;
		_confirmedBox.addStyleName("registerPageTextBox") ;
		_emailBox          = new TextBox() ;
		_emailBox.addStyleName("registerPageTextBox") ;
		_emailBox.setName("email") ;
		_emailConfirmedBox = new TextBox() ;
		_emailConfirmedBox.addStyleName("registerPageTextBox") ;
		
		_languageListBox = new ListBox() ;
				
		// init verification messages
		//
		_pseudoMsg  = new Label("") ;
		_pseudoMsg.addStyleName("registerPageError") ;
		_passwordMsg  = new Label("") ;
		_passwordMsg.addStyleName("registerPageError") ;
		_confirmedMsg = new Label("") ;
		_confirmedMsg.addStyleName("registerPageError") ;
		_emailMsg     = new Label("") ;
		_emailMsg.addStyleName("registerPageError") ;
		_confirmedEmailMsg = new Label("") ;
		_confirmedEmailMsg.addStyleName("registerPageError") ;
		_languageMsg  = new Label("") ;
		_languageMsg.addStyleName("registerPageError") ;
	}
			
	@Override
	public Widget asWidget() {
		return this;
	}
	
	public HasText getUserConfirmedPassword() {
		return _confirmedBox ;
	}

	public HasText getUserPseudo() {
		return _pseudoBox ;
	}
	public void setUserPseudo(String sPseudo) {
		_pseudoBox.setText(sPseudo) ;
	}
	public void emptyPseudoMsg() {
		_pseudoMsg.setText("") ;
	}
	public void setPseudoMsgInvalid() {
		_pseudoMsg.setText(constants.registerErrPseudoInvalid()) ;
	}
	public void setPseudoCheckError() {
		_pseudoMsg.setText(constants.registerErrPseudoCheck()) ;
	}
	public void setPseudoMsgNotAvailable() {
		_pseudoMsg.setText(constants.registerErrPseudoNotAvailable()) ;
	}

	public HasText getUserPassword() {
		return _passwordBox ;
	}
	public void setUserPassword(String sPass) {
		_passwordBox.setText(sPass) ;
	}
	
	public PasswordTextBox getPassword() {
		return _passwordBox ;
	}
	
	public PasswordTextBox getConfirmedPassword() {
		return _confirmedBox ;
	}

	public HasText getPasswordMsg() {
		return _passwordMsg ;
	}
	public void emptyPasswordMsg() {
		_passwordMsg.setText("") ;
	}
	public void setPasswordMsgInvalid() {
		_passwordMsg.setText(constants.registerErrPassInvalid()) ;
	}

	public HasText getConfirmedPassMsg() {
		return _confirmedMsg ;
	}
	public void emptyConfirmedMsg() {
		_confirmedMsg.setText("") ;
	}
	public void setConfirmedMsgAreDifferent() {
		_confirmedMsg.setText(constants.registerErrDifPass()) ;
	}

	public HasText getUserEmail() {
		return _emailBox ;
	}
	public void setUserEmail(String sEmail) {
		_emailBox.setText(sEmail) ;
	}
	
	public HasText getEmailMsg() {
		return _emailMsg;
	}
	public void emptyEmailMsg() {
		_emailMsg.setText("") ;
	}
	public void setEmailMsgInvalid() {
		_emailMsg.setText(constants.registerErrMailInvalid()) ;
	}

	public TextBox getEmail() {
		return _emailBox ;
	}
	
	public HasText getConfirmedMailMsg() {
		return _confirmedEmailMsg ;
	}
	public void emptyConfirmedMailMsg() {
		_confirmedEmailMsg.setText("") ;
	}
	public void setConfirmedMailsAreDifferent() {
		_confirmedEmailMsg.setText(constants.registerErrDifMails()) ;
	}
	
	public TextBox getConfirmedEmail() {
		return _emailConfirmedBox ;
	}
	
	public TextBox getPseudo() {
		return _pseudoBox;
	}

	public HasText getUserNameMsg() {		
		return _pseudoMsg;
	}

	// uppercase 
	//
	protected String uppercaseFirstLetter(String sInitialString)
	{
		if (null == sInitialString)
			return null ;
		
		if (sInitialString.equals(""))
			return "" ;
		
	  return sInitialString.substring(0, 1).toUpperCase() + sInitialString.substring(1) ;	
	}
}
