package org.quadrifolium.client.mvp_components;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.ButtonBase;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class QuadrifoliumComponentBaseFlowDisplay extends FlowPanel implements QuadrifoliumComponentInterface, IQuadrifoliumComponentBaseDisplayModel 
{
	// Composition to imitate multiple inheritance since some display extend FlowPanel and some others extend SplitLayoutPanel 
	//
	QuadrifoliumComponentBaseDisplayModel _baseDisplayModel = new QuadrifoliumComponentBaseDisplayModel() ;
	public QuadrifoliumComponentBaseDisplayModel getBaseDisplayModel() {
		return _baseDisplayModel ;
	}
	
	/**
	 * Initialize command panel's buttons
	 */
	protected void createCommandPanelButtons() {
		_baseDisplayModel.createCommandPanelButtons() ;
	}
	
	protected void initAddingPanelButtons() {
		_baseDisplayModel.initAddingPanelButtons() ;
	}
		
	protected void initErrorDialog() {
		_baseDisplayModel.initErrorDialog() ;
	}
	
	/**
	 * Set the text of the button that switches the view from read only mode to edit mode
	 * 
	 * @param bInEditMode if <code>true</code> then the button must display "Edit", if not it must display "Read only"
	 */
	public void setEditButton(boolean bInEditMode) {
		_baseDisplayModel.setEditButton(bInEditMode) ;
	}

	/**
	 * Open the panel dedicated to adding a new element
	 * Nothing here for the generic class, must be defined by derived components
	 */
	@Override
	public void openAddPanel() {
	}
	
	/**
	 * Close the panel dedicated to adding a new element
	 * Nothing here for the generic class, must be defined by derived components
	 */
	@Override
	public void closeAddPanel() {
	}
	
	@Override
	public HasClickHandlers getAddOkButtonKeyDown() {
		return _baseDisplayModel.getAddOkButton() ;
	}
	
	@Override
	public HasClickHandlers getAddCancelButtonKeyDown() {
		return _baseDisplayModel.getAddCancelButton() ;
	}
	
	@Override
	public HasClickHandlers getEditButtonKeyDown() {
		return _baseDisplayModel.getReadOnlyToEditButton() ;
	}

	@Override
	public HasClickHandlers getAddButtonKeyDown() {
		return _baseDisplayModel.getAddButton() ;
	}
	
	@Override
	public ArrayList<ButtonBase> getButtonsArray() {
		return _baseDisplayModel.getButtons() ;
	}
	
	@Override
	public void openErrDialogBox(final String sErrMsgId) {
		_baseDisplayModel.openErrDialogBox(sErrMsgId) ;
	}
	
	/**
	 * Set the proper text message in the error box dialog
	 * Nothing here for the generic class, must be defined by derived components
	 */
	protected void setErrorText(final String sErrMsgId) {
	}
	
	@Override
	public void closeErrDialogBox() {
		_baseDisplayModel.closeErrDialogBox() ;
	}
	
	@Override
	public HasClickHandlers getErrDialogBoxOkButton() {
		return _baseDisplayModel.getErrorDialogBoxOkButton() ;
	}
	
	@Override
	public Widget asWidget()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
