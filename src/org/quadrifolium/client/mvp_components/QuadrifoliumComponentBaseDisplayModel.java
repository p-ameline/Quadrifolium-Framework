package org.quadrifolium.client.mvp_components;

import java.util.ArrayList;

import org.quadrifolium.client.loc.QuadrifoliumConstants;
import org.quadrifolium.client.ui.QuadrifoliumResources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ButtonBase;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

public class QuadrifoliumComponentBaseDisplayModel implements QuadrifoliumComponentInterface 
{
	protected final QuadrifoliumConstants constants = GWT.create(QuadrifoliumConstants.class) ;

	public enum INTERFACETYPE { undefined, readOnlyMode, editableMode, editMode } ;
	
	//Command panel controls
	//
	protected FlowPanel  _CommandPanel ;
	
	protected Button     _ReadOnlyToEditBtn ;
	protected PushButton _AddButton ;
	
	protected FlowPanel  _AddPanel ;
	protected Button     _AddOkBtn ;
	protected Button     _AddCancelBtn ;
	
	protected DialogBox  _ErrorDialogBox ;
	protected Button     _ErrorDialogBoxOkButton ;
	
	protected ArrayList<ButtonBase> _aButtons = new ArrayList<ButtonBase>() ;
	
	public QuadrifoliumComponentBaseDisplayModel()
	{
		_CommandPanel      = null ;
		
		_ReadOnlyToEditBtn = null ;
		_AddButton         = null ;
		
		_AddPanel          = null ;
		_AddOkBtn          = null ;
		_AddCancelBtn      = null ;
		
		_ErrorDialogBox         = null ;
		_ErrorDialogBoxOkButton = null ;
	}
	
	/**
	 * Create command panel's buttons
	 */
	protected void createCommandPanelButtons()
	{
		createCommandPanelEditButton() ;
		createCommandPanelAddButton() ;
	}
	
	/**
	 * Create command panel's buttons
	 */
	public void createCommandPanelEditButton()
	{
		_ReadOnlyToEditBtn = new Button(constants.generalEdit()) ;
		_ReadOnlyToEditBtn.addStyleName("button white editButton") ;
	}
	
	/**
	 * Create command panel's buttons
	 */
	protected void createCommandPanelAddButton()
	{
		_AddButton = new PushButton(new Image(QuadrifoliumResources.INSTANCE.addIcon())) ;
		_AddButton.addStyleName("elementEditButton") ;
	}
	
	/**
	 * Add command panel's buttons to the command panel according to edit mode status
	 */
	protected void addCommandPanelButtons(final INTERFACETYPE iInterfaceType)
	{
		if (INTERFACETYPE.readOnlyMode == iInterfaceType)
			return ;

		// Add the "add element" button only if in edit mode
		//
		if (INTERFACETYPE.editMode == iInterfaceType)
			_CommandPanel.add(_AddButton) ;
		
		// Set the "mode switching" button with the proper caption and add it 
		//
		setEditButton(INTERFACETYPE.editableMode == iInterfaceType) ;
		_CommandPanel.add(_ReadOnlyToEditBtn) ;
	}
	
	protected void initAddingPanelButtons()
	{
		_AddOkBtn          = new Button(constants.generalOk()) ;
		_AddOkBtn.addStyleName("elementAddOkButton") ;
		_AddOkBtn.setVisible(false) ;
		_AddCancelBtn      = new Button(constants.generalCancel()) ;
		_AddCancelBtn.addStyleName("elementAddCancelButton") ;
		_AddCancelBtn.setVisible(false) ;
		
		_AddPanel.add(_AddOkBtn) ;
		_AddPanel.add(_AddCancelBtn) ;
	}
		
	protected void initErrorDialog()
	{
		// Error dialog box
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
	}
	
	/**
	 * Set the text of the button that switches the view from read only mode to edit mode
	 * 
	 * @param bInEditMode if <code>true</code> then the button must display "Edit", if not it must display "Read only"
	 */
	public void setEditButton(boolean bInEditMode) {
		setEditButton(bInEditMode, _ReadOnlyToEditBtn) ;
	}
	
	/**
	 * Set the text of the button that switches the view from read only mode to edit mode
	 * 
	 * @param bInEditMode If <code>true</code> then the button must display "Edit", if not it must display "Read only"
	 * @param button      Button to change caption to
	 */
	public void setEditButton(boolean bInEditMode, Button button) throws NullPointerException
	{
		if (null == button)
			throw new NullPointerException() ;
		
		if (bInEditMode)
			button.setText(constants.generalEdit()) ;
		else
			button.setText(constants.generalReadOnly()) ;
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
	
	/**
	 * Set the top position of the tree view panel so that it remains under the edit panel
	 */
	protected int getDisplayPanelPosition() {
		return _CommandPanel.getOffsetHeight() + _AddPanel.getOffsetHeight() ;
	}
	
	@Override
	public HasClickHandlers getAddOkButtonKeyDown() {
		return _AddOkBtn ;
	}
	
	@Override
	public HasClickHandlers getAddCancelButtonKeyDown() {
		return _AddCancelBtn ;
	}
	
	@Override
	public HasClickHandlers getEditButtonKeyDown() {
		return _ReadOnlyToEditBtn ;
	}

	@Override
	public HasClickHandlers getAddButtonKeyDown() {
		return _AddButton ;
	}
	
	@Override
	public ArrayList<ButtonBase> getButtonsArray() {
		return _aButtons ;
	}
	
	@Override
	public void openErrDialogBox(final String sErrMsgId)
	{
		setErrorText(sErrMsgId) ;
		
		_ErrorDialogBox.setVisible(true) ;
	}
	
	/**
	 * Set the proper text message in the error box dialog
	 * Nothing here for the generic class, must be defined by derived components
	 */
	protected void setErrorText(final String sErrMsgId) {
	}
	
	@Override
	public void closeErrDialogBox() {
		_ErrorDialogBox.setVisible(false) ;
	}
	
	@Override
	public HasClickHandlers getErrDialogBoxOkButton() {
		return _ErrorDialogBoxOkButton ;
	}
	
	// Getters
	//
	public FlowPanel getCommandPanel() {
		return _CommandPanel ;
	}
	public void createCommandPanel() {
		_CommandPanel = new FlowPanel() ;
	}
	
	public Button getReadOnlyToEditButton() {
		return _ReadOnlyToEditBtn ;
	}
	
	public PushButton getAddButton() {
		return _AddButton ;
	}
	
	public FlowPanel getAddPanel() {
		return _AddPanel ;
	}
	public void createAddPanel() {
		_AddPanel = new FlowPanel() ;
		_AddPanel.addStyleName("addElementPanel") ;
	}
	
	public Button getAddOkButton() {
		return _AddOkBtn ;
	}
	
	public Button getAddCancelButton() {
		return _AddCancelBtn ;
	}
	
	public DialogBox getErrorDialogBox() {
		return _ErrorDialogBox ;
	}
	
	public Button getErrorDialogBoxOkButton() {
		return _ErrorDialogBoxOkButton ;
	}
	
	public ArrayList<ButtonBase> getButtons() {
		return _aButtons ;
	}
	public void clearButtons() {
		_aButtons.clear() ;
	}
	public void addButton(PushButton button) {
		_aButtons.add(button) ;
	}

	public QuadrifoliumConstants getConstants() {
		return constants ;
	}
	
	/**
	 * Select the item from the list box that fits a given language tag
	 */
	public void setEditedLanguage(final String sLanguageTag, ListBox languageListBox) throws NullPointerException
	{
		if (null == languageListBox)
			throw new NullPointerException() ;
		
		if ((null == sLanguageTag) || "".equals(sLanguageTag))
		{
			languageListBox.setSelectedIndex(-1) ;
			return ;
		}
		
		// Get index for this language tag
		//
		int iItemCount = languageListBox.getItemCount() ;
		if (iItemCount <= 0)
			return ;
		
		int iIndexToSelect = -1 ;
		
		for (int i = 0 ; i < iItemCount ; i++)
			if (sLanguageTag.equals(languageListBox.getValue(i)))
			{
				iIndexToSelect = i ;
				break ;
			}
		
		// Select the proper language tag or nothing if not found
		//
		languageListBox.setSelectedIndex(iIndexToSelect) ;
	}
	
	@Override
	public Widget asWidget()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
