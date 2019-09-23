package org.quadrifolium.client.mvp;

import org.quadrifolium.client.loc.QuadrifoliumConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasKeyDownHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class QuadrifoliumLoginHeaderView extends Composite implements QuadrifoliumLoginHeaderPresenter.Display 
{	
	private final QuadrifoliumConstants constants = GWT.create(QuadrifoliumConstants.class) ;
	
	private SimplePanel     _Header ;
	private FlexTable       _LoginTable;
	private Button          _LoginButton ;
	private TextBox         _UserName ;
	private PasswordTextBox _PassWord ;
	private DialogBox       _ErrorDialogBox ;
	private Button          _ErrorDialogBoxOkButton ;
	private Button          _ErrorDialogBoxSendIdsButton ;
	
	public QuadrifoliumLoginHeaderView() 
	{
		_Header = new SimplePanel() ;	
		_Header.addStyleName("headerlogin") ;
		
		_LoginTable = new FlexTable() ;
		_LoginTable.addStyleName("logintable") ;
		// LoginTable.setWidth("10%") ;
		
		_LoginButton = new Button(constants.loginBtn()) ;
		_UserName    = new TextBox() ;
		_UserName.setText("") ;
		_UserName.setName("session_login") ;
		_PassWord    = new PasswordTextBox() ;
		_PassWord.setText("") ;
		_PassWord.setName("session_password") ;
		
		_LoginTable.setWidget(0, 0, new Label(constants.userName())) ;
		_LoginTable.setWidget(0, 1, _UserName) ;
		_LoginTable.setWidget(0, 2, new Label(constants.passWord())) ;
		_LoginTable.setWidget(0, 3, _PassWord) ;
		_LoginTable.setWidget(0, 4, _LoginButton) ;
		
		// Dialog box
		//
		_ErrorDialogBox = new DialogBox() ;
		// _ErrorDialogBox.setSize("25em", "10em") ;
		_ErrorDialogBox.setPopupPosition(800, 200) ;
		_ErrorDialogBox.setText(constants.loginFailed()) ;
		_ErrorDialogBox.setAnimationEnabled(true) ;
		_ErrorDialogBox.setModal(true) ;
		// _ErrorDialogBox.setVisible(false) ;
    
		_ErrorDialogBoxOkButton = new Button(constants.generalOk()) ;
		_ErrorDialogBoxOkButton.setSize("70px", "2em") ;
		_ErrorDialogBoxOkButton.getElement().setId("okbutton") ;
			
		_ErrorDialogBoxSendIdsButton = new Button(constants.loginSendIds()) ;
		// _ErrorDialogBoxSendIdsButton.setSize("70px", "2em") ;
		_ErrorDialogBoxSendIdsButton.getElement().setId("senIdsButton") ;
		
		final VerticalPanel dialogVPanel = new VerticalPanel() ;
		// dialogVPanel.add(new HTML()) ;
		FlexTable detailsTable = new FlexTable() ;
		detailsTable.setWidget(0, 0, _ErrorDialogBoxOkButton) ;
		detailsTable.setWidget(0, 1, new Label(" ")) ;
		// detailsTable.setWidget(0, 2, _ErrorDialogBoxSendIdsButton) ;
    dialogVPanel.add(detailsTable) ;
    _ErrorDialogBox.add(dialogVPanel) ;
		
		_Header.add(_LoginTable) ;
		initWidget(_Header) ;
	}
	
	public DialogBox getErrDialogBox(){
		return _ErrorDialogBox ;
	}
	public Button getErrDialogBoxOkButton(){
		return _ErrorDialogBoxOkButton ;
	}
	public Button getErrDialogBoxSendIdsButton(){
		return _ErrorDialogBoxSendIdsButton ;
	}
	
	@Override
	public Widget asWidget() {
		// TODO Auto-generated method stub
		return null ;
	}
	
	@Override
	public String getPassWord() {
		return _PassWord.getValue().toString() ;
	}
	@Override
	public HasKeyDownHandlers getPassKeyDown() {
		return _PassWord ; 
	}
	
	@Override
	public String getUser() {
		// TODO Auto-generated method stub
		return _UserName.getValue().toString() ;
	}
	
	@Override
	public FlexTable getLoginTable(){
		return _LoginTable ;
	}
	
	public Button getSendLogin() {
		// TODO Auto-generated method stub
		return _LoginButton ;
	}
	
	@Override
	public void showBadVersionMessage(final String sClientVersion, final String sServerVersion)
	{
		_ErrorDialogBox.setText(constants.incorrectVersionNumber() + " (version " + sClientVersion + ")") ;
		_ErrorDialogBox.show() ;
	}
	
	@Override
	public void showWaitCursor() {
		QuadrifoliumBaseDisplay.switchToWaitCursor() ;
	}

	@Override
	public void showDefaultCursor() {
		QuadrifoliumBaseDisplay.switchToDefaultCursor() ;
	}
}
