package org.quadrifolium.client.mvp_components;

import java.util.ArrayList;
import java.util.Iterator;

import org.quadrifolium.client.mvp_components.QuadrifoliumComponentBaseDisplayModel.INTERFACETYPE;
import org.quadrifolium.client.ui.QuadrifoliumResources;
import org.quadrifolium.shared.ontology.LanguageTag;
import org.quadrifolium.shared.ontology.TripleWithLabel;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class QuadrifoliumDefinitionsView extends QuadrifoliumComponentBaseFlowDisplay implements QuadrifoliumDefinitionsPresenter.Display 
{
	// Add definition controls
	//	
	protected ListBox    _languageSelection ;
	protected TextArea   _AddedLabel ;
	
	// Definitions display area
	//
	protected SimplePanel _definitionsPanel ;
	private   FlexTable   _definitionsTable ;
	
	public QuadrifoliumDefinitionsView() 
	{
		super() ;
		
		addStyleName("semanticsWorshopPanel") ;
		
		// Panels
		//
		initCommandPanel() ;
		initAddingPanel() ;
		initDefinitionsPanel() ;
		
		initErrorDialog() ;
	}
	
	/**
	 * Initialize the semantic network panel
	 */
	protected void initDefinitionsPanel()
	{
		_definitionsTable = new FlexTable() ;
		
		_definitionsPanel = new SimplePanel() ;
		_definitionsPanel.addStyleName("lemmasCellTreePanel") ;
		_definitionsPanel.add(_definitionsTable) ;
		
    add(_definitionsPanel) ;
	}
	
	/**
	 * Initialize the command panel
	 */
	protected void initCommandPanel()
	{
		// Create command pannel
		//
		_baseDisplayModel.createCommandPanel() ;
		_baseDisplayModel.getCommandPanel().addStyleName("definitionsCommand") ;
		
		showCaption() ;
			
		add(_baseDisplayModel.getCommandPanel()) ;
		
		// Create edition mode buttons
		//
		createCommandPanelButtons() ;
	}
	
	protected void initAddingPanel()
	{
		_baseDisplayModel.createAddPanel() ;
		_baseDisplayModel.getAddPanel().addStyleName("addElementPanel") ;
		
		_baseDisplayModel.getAddPanel().setHeight("0px") ;
		
		_languageSelection = new ListBox() ;
		_languageSelection.addStyleName("addDefinitionLang") ;
		_languageSelection.setVisible(false) ;
		
		_AddedLabel        = new TextArea() ;
		_AddedLabel.addStyleName("addDefinitionText") ;
		_AddedLabel.setVisible(false) ;
				
		_baseDisplayModel.getAddPanel().add(_languageSelection) ;
		_baseDisplayModel.getAddPanel().add(_AddedLabel) ;
		
		initAddingPanelButtons() ;
		
		add(_baseDisplayModel.getAddPanel()) ;
	}
	
	/**
	 * Update the view depending on what the user is entitled to doing
	 */
	@Override
	public void updateView(final ArrayList<TripleWithLabel> aTriples, final INTERFACETYPE iInterfaceType)
	{
		_baseDisplayModel.getCommandPanel().clear() ;
		showCaption() ;
		
		setDefinitionsPanelPosition() ;
		
		feedDefinitionsTable(aTriples, iInterfaceType) ;
		
		if (INTERFACETYPE.readOnlyMode == iInterfaceType)
			return ;
		
		if (INTERFACETYPE.editMode == iInterfaceType)
			_baseDisplayModel.getCommandPanel().add(_baseDisplayModel.getAddButton()) ;
		
		setEditButton(INTERFACETYPE.editableMode == iInterfaceType) ;
		_baseDisplayModel.getCommandPanel().add(_baseDisplayModel.getReadOnlyToEditButton()) ;
	}

	/**
	 * Add "Definitions" caption to the command panel 
	 */
	public void showCaption()
	{
		Label caption = new Label(_baseDisplayModel.getConstants().captionDefinitions()) ;
		caption.addStyleName("chapterCaption") ;
		
		_baseDisplayModel.getCommandPanel().add(caption) ;
	}
	
	/**
	 * Feed and refresh the left semantic table (the one with current concept as the object of all triples)
	 */
	@Override
	public void feedDefinitionsTable(final ArrayList<TripleWithLabel> aTriples, final INTERFACETYPE iInterfaceType)
	{
		_definitionsTable.clear() ;
		
		_baseDisplayModel.clearButtons() ;
		
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
				_baseDisplayModel.addButton(editButton) ;
				_definitionsTable.setWidget(iRow, iCol++, editButton) ;
				
				PushButton delButton = new PushButton(new Image(QuadrifoliumResources.INSTANCE.deleteIcon())) ;
				delButton.addStyleName("elementEditButton") ;
				delButton.getElement().setId("del_" + triple.getObject()) ;
				_baseDisplayModel.addButton(delButton) ;
				_definitionsTable.setWidget(iRow, iCol++, delButton) ;
			}
			
			_definitionsTable.setWidget(iRow, iCol++, new Label(triple.getLanguage())) ;
			_definitionsTable.setWidget(iRow, iCol++, new Label(triple.getObjectLabel())) ;
		}
	}
	
	@Override
	public void openAddPanel() 
	{
		_baseDisplayModel.getAddPanel().setHeight("5em") ;
		
		setDefinitionsPanelPosition() ;
		
		_languageSelection.setVisible(true) ;
		_AddedLabel.setVisible(true) ;
		
		_baseDisplayModel.getAddOkButton().setVisible(true) ;
		_baseDisplayModel.getAddCancelButton().setVisible(true) ;
	}
	
	@Override
	public void closeAddPanel() 
	{
		_languageSelection.setVisible(false) ;
		_AddedLabel.setVisible(false) ;
		_baseDisplayModel.getAddOkButton().setVisible(false) ;
		_baseDisplayModel.getAddCancelButton().setVisible(false) ;
		
		_baseDisplayModel.getAddPanel().setHeight("0em") ;
		
		setDefinitionsPanelPosition() ;
	}
	
	/**
	 * Set the top position of the tree view panel so that it remains under the edit panel
	 */
	protected void setDefinitionsPanelPosition() {
		_definitionsPanel.getElement().getStyle().setTop(_baseDisplayModel.getDisplayPanelPosition(), Unit.PX) ;
	}
	
	@Override
	public String getEditedLanguage() {
		return _languageSelection.getSelectedValue() ;
	}

	@Override
	public void setEditedLanguage(final String sLanguageTag) {
		_baseDisplayModel.setEditedLanguage(sLanguageTag, _languageSelection) ;
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
	
	public void initializeLanguagesList(final ArrayList<LanguageTag> aLanguages)
	{
		_languageSelection.clear() ;
		
		if (aLanguages.isEmpty())
			return ;
		
		for (LanguageTag language : aLanguages)
			_languageSelection.addItem(language.getLabel(), language.getCode()) ;
	}
	
	/**
	 * Set the proper text message in the error box dialog 
	 */
	protected void setErrorText(final String sErrMsgId)
	{
		if      ("definitionErrEmptyLabel".equals(sErrMsgId))
			_baseDisplayModel.getErrorDialogBox().setText(_baseDisplayModel.getConstants().definitionErrEmptyLabel()) ;
		else if ("definitionErrNoLanguage".equals(sErrMsgId))
			_baseDisplayModel.getErrorDialogBox().setText(_baseDisplayModel.getConstants().definitionErrNoLanguage()) ;
		else if ("definitionErrLanguageExists".equals(sErrMsgId))
			_baseDisplayModel.getErrorDialogBox().setText(_baseDisplayModel.getConstants().definitionErrLanguageExists()) ;
	}

	public void reset() {	
	}

	public Widget asWidget() {
		return this;
	}
}
