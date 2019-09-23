package org.quadrifolium.client.mvp;

import org.quadrifolium.client.loc.QuadrifoliumConstants;

import com.allen_sauer.gwt.log.client.Log;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class QuadrifoliumRegisterView extends QuadrifoliumUserEditView implements QuadrifoliumRegisterPresenter.Display
{
	private final QuadrifoliumConstants constants = GWT.create(QuadrifoliumConstants.class) ;
	
	private FlowPanel       _mainPanel ;
	
	private Button          _submitButton ;
	private Button          _resetButton ;
	
	private DialogBox       _MessageDialogBox ;
	private Button          _MessageDialogBoxOkButton ;
	
	public QuadrifoliumRegisterView()
	{
		super() ;
		
		_mainPanel = new FlowPanel() ;
		_mainPanel.addStyleName("registerMainPanel") ;
				
		_submitButton = new Button(constants.registerRegister()) ;
		_submitButton.addStyleName("registerRegisterButton") ;
		_resetButton  = new Button(constants.registerCancel()) ;
		_resetButton.addStyleName("registerCancelButton") ;

		initRegisterBody() ;
		
		initDialogBox() ;
		
		initWidget(_mainPanel) ;
	}
		
	public void initRegisterBody()
	{
		Log.info("entering initRegisterBody()") ;
		
		// Language
		//
		FlowPanel languagePanel = new FlowPanel() ;
		languagePanel.setStyleName("registerPageSeparLine") ;
		languagePanel.add(_languageLabel) ;
		languagePanel.add(_languageListBox) ;
		languagePanel.add(_languageMsg) ;
		_mainPanel.add(languagePanel) ;

		// Pseudo
		//
		FlowPanel pseudoPanel = new FlowPanel() ;
		pseudoPanel.setStyleName("registerPageSeparLine") ;
		pseudoPanel.add(_pseudoLabel) ;
		pseudoPanel.add(_pseudoBox) ;
		pseudoPanel.add(_pseudoMsg) ;
		_mainPanel.add(pseudoPanel) ;

		// Password
		//
		FlowPanel passPanel = new FlowPanel() ;
		passPanel.setStyleName("registerPageSeparLine") ;
		passPanel.add(_passwdLabel) ;
		passPanel.add(_passwordBox) ;
		passPanel.add(_passwordMsg) ;
		_mainPanel.add(passPanel) ;
		
		FlowPanel passConfirmPanel = new FlowPanel() ;
		passConfirmPanel.setStyleName("registerPageLine") ;
		passConfirmPanel.add(_confirmLabel) ;
		passConfirmPanel.add(_confirmedBox) ;
		passConfirmPanel.add(_confirmedMsg) ;
		_mainPanel.add(passConfirmPanel) ;

		// e-mail
		//
		FlowPanel mailPanel = new FlowPanel() ;
		mailPanel.setStyleName("registerPageSeparLine") ;
		mailPanel.add(_emailLabel) ;
		mailPanel.add(_emailBox) ;
		mailPanel.add(_emailMsg) ;
		_mainPanel.add(mailPanel) ;
		
		FlowPanel mailConfirmPanel = new FlowPanel() ;
		mailConfirmPanel.setStyleName("registerPageLine") ;
		mailConfirmPanel.add(_emailConfirmLabel) ;
		mailConfirmPanel.add(_emailConfirmedBox) ;
		mailConfirmPanel.add(_confirmedEmailMsg) ;
		_mainPanel.add(mailConfirmPanel) ;
		
		// Buttons
		//
		FlowPanel buttonsPanel = new FlowPanel() ;
		buttonsPanel.setStyleName("registerPageSeparButtons") ;
		buttonsPanel.add(_submitButton) ;
		buttonsPanel.add(_resetButton) ;
		_mainPanel.add(buttonsPanel) ;		
	}
	
	private void initDialogBox()
	{
		_MessageDialogBox = new DialogBox() ;
		// _ErrorDialogBox.setSize("25em", "10em") ;
		_MessageDialogBox.setPopupPosition(800, 200) ;
		_MessageDialogBox.setText(constants.registerMessageSent()) ;
		_MessageDialogBox.setAnimationEnabled(true) ;
		_MessageDialogBox.setModal(true) ;
		// _ErrorDialogBox.setVisible(false) ;
    
		_MessageDialogBoxOkButton = new Button(constants.generalOk()) ;
		_MessageDialogBoxOkButton.setSize("70px", "2em") ;
		_MessageDialogBoxOkButton.getElement().setId("okbutton") ;
			
    _MessageDialogBox.add(_MessageDialogBoxOkButton) ;
	}
		
	@Override
	public Widget asWidget() {
		return this;
	}
	
	@Override
	public HasText getUserConfirmedPassword() {
		return _confirmedBox ;
	}
	
	@Override
	public HasText getUserPassword() {		
		return _passwordBox ;
	}

	@Override
	public HasClickHandlers getReset() {	
		return _resetButton ;
	}

	@Override
	public HasClickHandlers getSubmit() {
		return _submitButton ; 
	}
	
	@Override
	public Button getSubmitButton() {
		return _submitButton ; 
	}
	
	@Override
	public PasswordTextBox getPassword() {
		return _passwordBox ;
	}
	@Override
	public void setPassword(String sPass) {
		_passwordBox.setText(sPass) ;
	}
	
	@Override
	public PasswordTextBox getConfirmedPassword() {
		return _confirmedBox ;
	}
	@Override
	public void emptyPasswordMsg() {
		_passwordMsg.setText("") ;
	}
	@Override
	public void setPasswordMsgInvalid() {
		_passwordMsg.setText(constants.registerErrPassInvalid()) ;
	}

	@Override
	public HasText getPasswordMsg() {
		return _passwordMsg ;
	}

	@Override
	public HasText getConfirmedMsg() {
		return _confirmedMsg ;
	}
	public void emptyConfirmedMsg() {
		_confirmedMsg.setText("") ;
	}
	@Override
	public void setConfirmedMsgAreDifferent() {
		_confirmedMsg.setText(constants.registerErrDifPass()) ;
	}

	@Override
	public HasText getEmailMsg() {
		return _emailMsg ;
	}
	@Override
	public TextBox getEmail() {
		return _emailBox ;
	}
	@Override
	public void emptyEmailMsg() {
		_emailMsg.setText("") ;
	}
	@Override
	public void setEmailMsgInvalid() {
		_emailMsg.setText(constants.registerErrMailInvalid()) ;
	}
	
	@Override
	public TextBox getConfirmedEmail() {		
		return _emailConfirmedBox ;
	}
	@Override
	public void emptyConfirmedMailMsg() {
		_confirmedEmailMsg.setText("") ;
	}
	@Override
	public void setConfirmedMailsAreDifferent() {
		_confirmedEmailMsg.setText(constants.registerErrDifMails()) ;
	}

	@Override
	public TextBox getPseudo() {
		return _pseudoBox ;
	}
	@Override
	public void setPseudo(String sPseudo) {
		_pseudoBox.setText(sPseudo) ;
	}
	@Override
	public void emptyPseudoMsg() {
		_pseudoMsg.setText("") ;
	}
	@Override
	public void setPseudoMsgInvalid() {
		_pseudoMsg.setText(constants.registerErrPseudoInvalid()) ;
	}
	@Override
	public void setPseudoCheckError() {
		_pseudoMsg.setText(constants.registerErrPseudoCheck()) ;
	}
	@Override
	public void setPseudoMsgNotAvailable() {
		_pseudoMsg.setText(constants.registerErrPseudoNotAvailable()) ;
	}
		
	@Override
	public String getLanguage() {
		return _languageListBox.getValue(_languageListBox.getSelectedIndex()) ;
	}
	@Override
	public void addLanguage(String sLanguage) {
		_languageListBox.addItem(sLanguage) ;
	}
	
	@Override
	public DialogBox getMessageDialogBox() {
		return _MessageDialogBox ;
	}
	
	@Override
	public Button getMessageDialogBoxOkButton() {
		return _MessageDialogBoxOkButton ;
	}
}
