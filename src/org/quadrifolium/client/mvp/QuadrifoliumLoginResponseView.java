package org.quadrifolium.client.mvp;

import org.quadrifolium.client.global.QuadrifoliumSupervisor;
import org.quadrifolium.client.loc.QuadrifoliumConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class QuadrifoliumLoginResponseView extends Composite implements QuadrifoliumLoginResponsePresenter.Display
{
	private final QuadrifoliumConstants constants = GWT.create(QuadrifoliumConstants.class) ;
	
	private FlowPanel _workspace ;
	
	private DialogBox      _WarnindDialogBox ;
	private Label          _WarnindDialogBoxLabel ;
	private Button         _WarningDialogBoxOkButton ;
	
	private DialogBox      _DeleteDialogBox ;
	private Label          _DeleteDialogBoxLabel ;
	private Button         _DeleteDialogBoxOkButton ;
	private Button         _DeleteDialogBoxCancelButton ;
	
	protected final QuadrifoliumSupervisor _supervisor ;
	
	@Inject
	public QuadrifoliumLoginResponseView(final QuadrifoliumSupervisor supervisor) 
	{
		super();
		
		_supervisor = supervisor ;
		
		initWorkspace() ;
	}
					 
	public void initWorkspace() 
	{					
		_workspace = new FlowPanel() ;
		_workspace.addStyleName("mapworkspace") ;
				
		initWarningDialogBox() ;
		
		initWidget(_workspace) ;
	}
	
	/** 
	 * initWarningDialogBox - Initialize warning dialog box
	 * 
	 * @param    nothing
	 * @return   nothing  
	 */
	private void initWarningDialogBox()
	{
		_WarnindDialogBox = new DialogBox() ;
		_WarnindDialogBox.setPopupPosition(100, 200) ;
		_WarnindDialogBox.setText(constants.warning()) ;
		_WarnindDialogBox.setAnimationEnabled(true) ;
		
		_WarnindDialogBoxLabel = new Label("") ;
		_WarnindDialogBoxLabel.addStyleName("warningDialogLabel") ;
    
		_WarningDialogBoxOkButton = new Button(constants.generalOk()) ;
		_WarningDialogBoxOkButton.setSize("70px", "30px") ;
		_WarningDialogBoxOkButton.getElement().setId("okbutton") ;
		
		FlowPanel warningPannel = new FlowPanel() ;
		warningPannel.add(_WarnindDialogBoxLabel) ;
		warningPannel.add(_WarningDialogBoxOkButton) ;
		
		_WarnindDialogBox.add(warningPannel) ;
	}
	
	/** 
	 * popupWarningMessage - Display warning dialog box
	 * 
	 * @param    nothing
	 * @return   nothing  
	 */
	@Override
	public void popupWarningMessage(String sMessage)
	{
/*
		if      (sMessage.equals("ERROR_MUST_SELECT_ENCOUNTER"))
			_WarnindDialogBoxLabel.setText(constants.warningAlreadyExist()) ;
		else if (sMessage.equals("ERROR_MUST_ENTER_EVERY_INFORMATION"))
			_WarnindDialogBoxLabel.setText(constants.mandatoryEnterAll()) ;
*/		
		_WarnindDialogBox.show() ;
	}
	
	@Override
	public void popupMessage(String sMessage)
	{
		_WarnindDialogBoxLabel.setText(sMessage) ;		
		_WarnindDialogBox.show() ;
	}
	
	@Override
	public void closeWarningDialog() {
		_WarnindDialogBox.hide() ;
	}
	
	@Override
	public void popupDeleteMessage() {
		_DeleteDialogBox.show() ;
	}
	
	@Override
	public void closeDeleteDialog() {
		_DeleteDialogBox.hide() ;
	}
	
	@Override
	public HasClickHandlers getDeleteOk() {
		return _DeleteDialogBoxOkButton ;
	}
	
	@Override
	public HasClickHandlers getDeleteCancel() {
		return _DeleteDialogBoxCancelButton ;
	}
	
	public void reset() {
	}

	public Widget asWidget() {
		return this ;
	}
		
	public FlowPanel getWorkspace() {
		return _workspace ;
	}
		
	@Override
	public HasClickHandlers getWarningOk()
	{
		return _WarningDialogBoxOkButton ;
	}
}
