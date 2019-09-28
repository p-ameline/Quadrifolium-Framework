package org.quadrifolium.client.mvp_components;

import java.util.ArrayList;
import java.util.Iterator;

import org.quadrifolium.client.loc.QuadrifoliumConstants;
import org.quadrifolium.client.ui.QuadrifoliumResources;
import org.quadrifolium.shared.ontology.LanguageTag;
import org.quadrifolium.shared.ontology.TripleWithLabel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class QuadrifoliumDefinitionsView extends FlowPanel implements QuadrifoliumDefinitionsPresenter.Display 
{
	private final QuadrifoliumConstants constants = GWT.create(QuadrifoliumConstants.class) ;

	public enum INTERFACETYPE { undefined, readOnlyMode, editableMode, editMode } ;
	
	// Command panel controls
	//
	protected FlowPanel  _CommandPanel ;
	
	protected Button     _ReadOnlyToEditBtn ;
	protected PushButton _AddButton ;
	
	// Add definition controls
	//
	protected FlowPanel  _AddPanel ;
	
	protected ListBox    _languageSelection ;
	protected TextArea   _AddedLabel ;
	
	protected Button     _AddOkBtn ;
	protected Button     _AddCancelBtn ;
	
	private DialogBox    _ErrorDialogBox ;
	private Button       _ErrorDialogBoxOkButton ;
	
	private ArrayList<PushButton> _aButtons = new ArrayList<PushButton>() ;
	
	// Definitions display area
	//
	private   FlexTable   _definitionsTable ;
	
	public QuadrifoliumDefinitionsView() 
	{
		addStyleName("semanticsWorshopPanel") ;
		
		// Panels
		//
		initCommandPanel() ;
		initAddingPanel() ;
		initDefinitionsPanel() ;
	}
	
	/**
	 * Initialize the semantic network panel
	 */
	protected void initDefinitionsPanel()
	{
		_definitionsTable = new FlexTable() ;
		
    add(_definitionsTable) ;
	}
	
	/**
	 * Initialize the command panel
	 */
	protected void initCommandPanel()
	{
		_CommandPanel = new FlowPanel() ;
		_CommandPanel.addStyleName("definitionsCommand") ;
		
		showCaption() ;
			
		add(_CommandPanel) ;
		
		_ReadOnlyToEditBtn = new Button(constants.generalEdit()) ;
		_ReadOnlyToEditBtn.addStyleName("button white editButton") ;
		
		// _AddButton = new PushButton(new Image("iconAdd.png")) ;
		_AddButton = new PushButton(new Image(QuadrifoliumResources.INSTANCE.addIcon())) ;
		_AddButton.addStyleName("elementEditButton") ;
	}
	
	protected void initAddingPanel()
	{
		_AddPanel = new FlowPanel() ;
		_AddPanel.addStyleName("addDefinitionPanel") ;
		
		_AddPanel.setHeight("0px") ;
		
		_languageSelection = new ListBox() ;
		_languageSelection.addStyleName("addDefinitionLang") ;
		_languageSelection.setVisible(false) ;
		
		_AddedLabel        = new TextArea() ;
		_AddedLabel.addStyleName("addDefinitionText") ;
		_AddedLabel.setVisible(false) ;
		
		_AddOkBtn          = new Button(constants.generalOk()) ;
		_AddOkBtn.addStyleName("elementAddOkButton") ;
		_AddOkBtn.setVisible(false) ;
		_AddCancelBtn      = new Button(constants.generalCancel()) ;
		_AddCancelBtn.addStyleName("elementAddCancelButton") ;
		_AddCancelBtn.setVisible(false) ;
		
		_AddPanel.add(_languageSelection) ;
		_AddPanel.add(_AddedLabel) ;
		_AddPanel.add(_AddOkBtn) ;
		_AddPanel.add(_AddCancelBtn) ;
		
		add(_AddPanel) ;
		
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
	 * Update the view depending on what the user is entitled to doing
	 */
	@Override
	public void updateView(final ArrayList<TripleWithLabel> aTriples, final INTERFACETYPE iInterfaceType)
	{
		_CommandPanel.clear() ;
		showCaption() ;
		
		feedDefinitionsTable(aTriples, iInterfaceType) ;
		
		if (INTERFACETYPE.readOnlyMode == iInterfaceType)
			return ;
		
		if (INTERFACETYPE.editMode == iInterfaceType)
			_CommandPanel.add(_AddButton) ;
		
		setEditButton(INTERFACETYPE.editableMode == iInterfaceType) ;
		_CommandPanel.add(_ReadOnlyToEditBtn) ;
	}

	/**
	 * Add "Definitions" caption to the command panel 
	 */
	public void showCaption()
	{
		Label caption = new Label(constants.captionDefinitions()) ;
		caption.addStyleName("chapterCaption") ;
		
		_CommandPanel.add(caption) ;
	}
	
	/**
	 * Set the text of the button that switches the view from read only mode to edit mode
	 * 
	 * @param bInEditMode if <code>true</code> then the button must display "Edit", if not it must display "Read only"
	 */
	public void setEditButton(boolean bInEditMode)
	{
		if (bInEditMode)
			_ReadOnlyToEditBtn.setText(constants.generalEdit()) ;
		else
			_ReadOnlyToEditBtn.setText(constants.generalReadOnly()) ;
	}
	
	/**
	 * Feed and refresh the left semantic table (the one with current concept as the object of all triples)
	 */
	@Override
	public void feedDefinitionsTable(final ArrayList<TripleWithLabel> aTriples, final INTERFACETYPE iInterfaceType)
	{
		_definitionsTable.clear() ;
		
		_aButtons.clear() ;
		
		if ((null == aTriples) || aTriples.isEmpty())
			return ;

		int iRow = 0 ;
		for (Iterator<TripleWithLabel> it = aTriples.iterator() ; it.hasNext() ; iRow++)
		{
			TripleWithLabel triple = it.next() ;
			
			int iCol = 0 ;
			
			if (INTERFACETYPE.editMode == iInterfaceType)
			{
				PushButton editButton = new PushButton(new Image(QuadrifoliumResources.INSTANCE.editIcon())) ;
				editButton.addStyleName("elementEditButton") ;
				editButton.getElement().setId("edt_" + triple.getObject()) ;
				_aButtons.add(editButton) ;
				_definitionsTable.setWidget(iRow, iCol++, editButton) ;
				
				PushButton delButton = new PushButton(new Image(QuadrifoliumResources.INSTANCE.deleteIcon())) ;
				delButton.addStyleName("elementEditButton") ;
				delButton.getElement().setId("del_" + triple.getObject()) ;
				_aButtons.add(delButton) ;
				_definitionsTable.setWidget(iRow, iCol++, delButton) ;
			}
			
			_definitionsTable.setWidget(iRow, iCol++, new Label(triple.getLanguage())) ;
			_definitionsTable.setWidget(iRow, iCol++, new Label(triple.getObjectLabel())) ;
		}
	}
	
	@Override
	public void openAddPanel() 
	{
		_AddPanel.setHeight("5em") ;
		
		_languageSelection.setVisible(true) ;
		_AddedLabel.setVisible(true) ;
		_AddOkBtn.setVisible(true) ;
		_AddCancelBtn.setVisible(true) ;
	}
	
	@Override
	public void closeAddPanel() 
	{
		_languageSelection.setVisible(false) ;
		_AddedLabel.setVisible(false) ;
		_AddOkBtn.setVisible(false) ;
		_AddCancelBtn.setVisible(false) ;
		
		_AddPanel.setHeight("0em") ;
	}
	
	@Override
	public String getEditedLanguage() {
		return _languageSelection.getSelectedValue() ;
	}

	@Override
	public void setEditedLanguage(final String sLanguageTag)
	{
		if ((null == sLanguageTag) || "".equals(sLanguageTag))
		{
			_languageSelection.setSelectedIndex(-1) ;
			return ;
		}
		
		// Get index for this language tag
		//
		int iItemCount = _languageSelection.getItemCount() ;
		if (iItemCount <= 0)
			return ;
		
		int iIndexToSelect = -1 ;
		
		for (int i = 0 ; i < iItemCount ; i++)
			if (sLanguageTag.equals(_languageSelection.getValue(i)))
			{
				iIndexToSelect = i ;
				break ;
			}
		
		// Select the proper language tag or nothing if not found
		//
		_languageSelection.setSelectedIndex(iIndexToSelect) ;
	}
	
	@Override
	public String getEditedText() {
		return _AddedLabel.getText() ;
	}
	
	@Override
	public void setEditedText(final String sText)
	{
		if (null == sText)
			_AddedLabel.setText("") ;
		else
			_AddedLabel.setText(sText) ;
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
	
	public void initializeLanguagesList(final ArrayList<LanguageTag> aLanguages)
	{
		_languageSelection.clear() ;
		
		if (aLanguages.isEmpty())
			return ;
		
		for (LanguageTag language : aLanguages)
			_languageSelection.addItem(language.getLabel(), language.getCode()) ;
	}
	
	public void openErrDialogBox(final String sErrMsgId)
	{
		if      ("definitionErrEmptyLabel".equals(sErrMsgId))
			_ErrorDialogBox.setText(constants.definitionErrEmptyLabel()) ;
		else if ("definitionErrNoLanguage".equals(sErrMsgId))
			_ErrorDialogBox.setText(constants.definitionErrNoLanguage()) ;
		else if ("definitionErrLanguageExists".equals(sErrMsgId))
			_ErrorDialogBox.setText(constants.definitionErrLanguageExists()) ;
		
		_ErrorDialogBox.setVisible(true) ;
	}
	
	public void closeErrDialogBox() {
		_ErrorDialogBox.setVisible(false) ;
	}
	
	public HasClickHandlers getErrDialogBoxOkButton() {
		return _ErrorDialogBoxOkButton ;
	}

	public ArrayList<PushButton> getButtonsArray() {
		return _aButtons ;
	}
	
	public void reset() {	
	}

	public Widget asWidget() {
		return this;
	}
}
