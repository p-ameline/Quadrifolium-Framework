package org.quadrifolium.client.mvp_components;

import java.util.ArrayList;

import org.quadrifolium.client.loc.QuadrifoliumConstants;
import org.quadrifolium.client.mvp_components.QuadrifoliumComponentBaseDisplayModel.INTERFACETYPE;
import org.quadrifolium.client.ui.QuadrifoliumResources;
import org.quadrifolium.client.util.LinguisticTreeModel;
import org.quadrifolium.shared.ontology.FlexWithTraits;
import org.quadrifolium.shared.ontology.LanguageTag;
import org.quadrifolium.shared.ontology.LemmaWithInflections;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class QuadrifoliumLemmasView extends QuadrifoliumComponentBaseLayoutDisplay implements QuadrifoliumLemmasPresenter.Display, IQuadrifoliumComponentBaseDisplayModel
{
	private final QuadrifoliumConstants constants = GWT.create(QuadrifoliumConstants.class) ;
		
	// User language panel
	//
	protected FlowPanel           _UserLanguagePanel ;
	
	// Other languages panel
	//
	protected FlowPanel           _OtherLanguagesPanel ;
	protected FlowPanel           _2ndCommandPanel ;

	protected Button              _2ndReadOnlyToEditBtn ;
	protected PushButton          _2ndAddButton ;
	
	protected FlowPanel           _2ndAddPanel ;
	protected Button              _2ndAddOkBtn ;
	protected Button              _2ndAddCancelBtn ;
	
	protected ArrayList<PushButton> _a2ndButtons = new ArrayList<PushButton>() ;

	// Add lemma controls
	//
	protected TextArea   _AddedLabel ;
	protected TextBox    _AddedGrammar ;
	
	protected ListBox    _languageSelection ;
	protected TextArea   _2ndAddedLabel ;
	protected TextBox    _2ndAddedGrammar ;
	
	// Linguistic information display area
	//
	protected LinguisticTreeModel _treeModel ;
	protected CellTree            _linguisticTree ;
			
	protected LinguisticTreeModel _2ndTreeModel ;
	protected CellTree            _linguisticTree4Other ;
	
	public QuadrifoliumLemmasView() 
	{
		addStyleName("lemmasWorshopPanel") ;
		
		// Command panel
		//
		initUserLanguagePanel() ;
		initOtherLanguagesPanel() ;
		
		addNorth(_UserLanguagePanel, 250) ;
		
		initAddingPanel() ;
		initAdding2ndPanel() ;
	
		// In a SplitLayoutPanel, center panel must always be added last
		//
		add(_OtherLanguagesPanel) ;
	
		initErrorDialog() ;
	}
	
	/**
	 * Initialize user language panel
	 */
	protected void initUserLanguagePanel()
	{
		_UserLanguagePanel = new FlowPanel() ;
		
		initCommandPanel() ;
		initLinguisticPanel() ;
	}
	
	/**
	 * Initialize other languages panel
	 */
	protected void initOtherLanguagesPanel()
	{
		_OtherLanguagesPanel = new FlowPanel() ;
		
		initSecondCommandPanel() ;
		initSecondLinguisticPanel() ;
	}
	
	/**
	 * Initialize the linguistic panel
	 */
	protected void initLinguisticPanel()
	{
		ArrayList<LemmaWithInflections> aInitialList = new ArrayList<LemmaWithInflections>() ;
		LemmaWithInflections synonym = new LemmaWithInflections(1, "nom|s| commun|s|", "00000010001", "fr", null, null) ;
		synonym.addInflection(new FlexWithTraits(1, "nom commun", "0000001000101", "fr", null)) ;
		synonym.addInflection(new FlexWithTraits(2, "noms communs", "0000001000102", "fr", null)) ;
		aInitialList.add(synonym) ;
		
		_treeModel = new LinguisticTreeModel("Noma Communis", aInitialList) ;
		
		_linguisticTree = new CellTree(_treeModel, null) ;
		_linguisticTree.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED) ;
		
		_UserLanguagePanel.add(_linguisticTree) ;
	}
		
	/**
	 * Initialize the linguistic panel
	 */
	protected void initSecondLinguisticPanel()
	{
		ArrayList<LemmaWithInflections> aInitialList = new ArrayList<LemmaWithInflections>() ;
		LemmaWithInflections synonym = new LemmaWithInflections(1, "common noun|s|", "00000010002", "en", null, null) ;
		synonym.addInflection(new FlexWithTraits(1, "common noun", "0000001000201", "en", null)) ;
		synonym.addInflection(new FlexWithTraits(2, "common nouns", "0000001000202", "en", null)) ;
		aInitialList.add(synonym) ;
		LemmaWithInflections synonym1 = new LemmaWithInflections(2, "nome|e/i| comune|e/i|", "00000010003", "it", null, null) ;
		synonym1.addInflection(new FlexWithTraits(3, "nome comune", "0000001000301", "it", null)) ;
		synonym1.addInflection(new FlexWithTraits(4, "nomi comuni", "0000001000302", "it", null)) ;
		aInitialList.add(synonym1) ;
		
		_2ndTreeModel = new LinguisticTreeModel("Noma Communis", aInitialList) ;
		
		_linguisticTree4Other = new CellTree(_2ndTreeModel, null) ;
		_linguisticTree4Other.addStyleName("lemmasCellTree") ;
		_linguisticTree4Other.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED) ;
		
		_OtherLanguagesPanel.add(_linguisticTree4Other) ;
	}
	
	/**
	 * Initialize the command panel
	 */
	protected void initCommandPanel()
	{
		_baseDisplayModel.createCommandPanel() ;
		_baseDisplayModel.getCommandPanel().addStyleName("lemmasCommand") ;
		
		showCaption() ;
			
		_UserLanguagePanel.add(_baseDisplayModel.getCommandPanel()) ;
		
		createCommandPanelButtons() ;
	}
	
	/**
	 * Initialize the command panel for "other languages"
	 */
	protected void initSecondCommandPanel()
	{
		_2ndCommandPanel = new FlowPanel() ;
		_2ndCommandPanel.addStyleName("lemmasCommand") ;
		
		showSecondCaption() ;
			
		_OtherLanguagesPanel.add(_2ndCommandPanel) ;
		
		createSecondCommandPanelButtons() ;
	}
	
	/**
	 * Initialize command panel's buttons
	 */
	protected void createSecondCommandPanelButtons()
	{
		_2ndReadOnlyToEditBtn = new Button(constants.generalEdit()) ;
		_2ndReadOnlyToEditBtn.addStyleName("button white editButton") ;
		
		// _AddButton = new PushButton(new Image("iconAdd.png")) ;
		_2ndAddButton = new PushButton(new Image(QuadrifoliumResources.INSTANCE.addIcon())) ;
		_2ndAddButton.addStyleName("elementEditButton") ;
	}
	
	protected void initAddingPanel()
	{
		_baseDisplayModel.createAddPanel() ;
		_baseDisplayModel.getAddPanel().addStyleName("addDefinitionPanel") ;
		
		_baseDisplayModel.getAddPanel().setHeight("0px") ;
		
		_AddedLabel = new TextArea() ;
		_AddedLabel.addStyleName("addDefinitionText") ;
		_AddedLabel.setVisible(false) ;
		
		_AddedGrammar = new TextBox() ;
		_AddedGrammar.addStyleName("addGrammar") ;
		_AddedGrammar.setVisible(false) ;
		
		_baseDisplayModel.getAddPanel().add(_AddedLabel) ;
		_baseDisplayModel.getAddPanel().add(_AddedGrammar) ;
		
		initAddingPanelButtons() ;
		
		_UserLanguagePanel.add(_baseDisplayModel.getAddPanel()) ;
	}
	
	protected void initAdding2ndPanel()
	{
		_2ndAddPanel = new FlowPanel() ;
		_2ndAddPanel.addStyleName("addDefinitionPanel") ;
		
		_2ndAddPanel.setHeight("0px") ;
		
		_languageSelection = new ListBox() ;
		_languageSelection.addStyleName("addDefinitionLang") ;
		_languageSelection.setVisible(false) ;
		
		_2ndAddedLabel     = new TextArea() ;
		_2ndAddedLabel.addStyleName("addDefinitionText") ;
		_2ndAddedLabel.setVisible(false) ;
		
		_2ndAddedGrammar = new TextBox() ;
		_2ndAddedGrammar.addStyleName("addGrammar") ;
		_2ndAddedGrammar.setVisible(false) ;
		
		_2ndAddPanel.add(_languageSelection) ;
		_2ndAddPanel.add(_2ndAddedLabel) ;
		_2ndAddPanel.add(_2ndAddedGrammar) ;
		
		initAdding2ndPanelButtons() ;
		
		_OtherLanguagesPanel.add(_2ndAddPanel) ;
	}
	
	protected void initAdding2ndPanelButtons()
	{
		_2ndAddOkBtn     = new Button(constants.generalOk()) ;
		_2ndAddOkBtn.addStyleName("elementAddOkButton") ;
		_2ndAddOkBtn.setVisible(false) ;
		_2ndAddCancelBtn = new Button(constants.generalCancel()) ;
		_2ndAddCancelBtn.addStyleName("elementAddCancelButton") ;
		_2ndAddCancelBtn.setVisible(false) ;
		
		_2ndAddPanel.add(_2ndAddOkBtn) ;
		_2ndAddPanel.add(_2ndAddCancelBtn) ;
	}
	
	/**
	 * Update the view depending on what the user is entitled to doing
	 */
	@Override
	public void updateView(final INTERFACETYPE iInterfaceType)
	{
		_baseDisplayModel.getCommandPanel().clear() ;
		showCaption() ;
		
		// feedLinguisticTree(aSynonyms, iInterfaceType) ;
		
		_baseDisplayModel.addCommandPanelButtons(iInterfaceType) ;
	}
	
	/**
	 * Update the view depending on what the user is entitled to doing
	 */
	@Override
	public void updateViewForOthers(final INTERFACETYPE iInterfaceType)
	{
		_2ndCommandPanel.clear() ;
		showSecondCaption() ;
		
		// feedLinguisticTreeForOthers(aSynonyms, iInterfaceType) ;
		
		if (INTERFACETYPE.readOnlyMode == iInterfaceType)
			return ;
		
		if (INTERFACETYPE.editMode == iInterfaceType)
			_2ndCommandPanel.add(_2ndAddButton) ;
		
		_baseDisplayModel.setEditButton(INTERFACETYPE.editableMode == iInterfaceType, _2ndReadOnlyToEditBtn) ;
		_2ndCommandPanel.add(_2ndReadOnlyToEditBtn) ;
	}
	
	/**
	 * Add "Lemmas" caption to the command panel 
	 */
	public void showCaption()
	{
		Label caption = new Label(_baseDisplayModel.getConstants().captionLemmas()) ;
		caption.addStyleName("chapterCaption") ;
		
		_baseDisplayModel.getCommandPanel().add(caption) ;
	}
	
	/**
	 * Add "Lemmas" caption to the command panel 
	 */
	public void showSecondCaption()
	{
		Label caption = new Label(_baseDisplayModel.getConstants().captionOtherLemmas()) ;
		caption.addStyleName("chapterCaption") ;
		
		_2ndCommandPanel.add(caption) ;
	}
	
	@Override
	public void openAddPanel() 
	{
		_baseDisplayModel.getAddPanel().setHeight("5em") ;
		
		_AddedLabel.setVisible(true) ;
		
		_baseDisplayModel.getAddOkButton().setVisible(true) ;
		_baseDisplayModel.getAddCancelButton().setVisible(true) ;
	}
	
	@Override
	public void closeAddPanel() 
	{
		_AddedLabel.setVisible(false) ;
		
		_baseDisplayModel.getAddOkButton().setVisible(false) ;
		_baseDisplayModel.getAddCancelButton().setVisible(false) ;
		
		_baseDisplayModel.getAddPanel().setHeight("0em") ;
	}
	
	/**
	 * (re)initialize the tree from a set of lemmas
	 */
	@Override
	public void feedLinguisticTree(final ArrayList<LemmaWithInflections> aSynonyms, final INTERFACETYPE iInterfaceType)
	{
		_treeModel.fillData(aSynonyms, iInterfaceType) ;
		_treeModel.refresh(iInterfaceType) ;
	}

	/**
	 * (re)initialize the tree from a set of lemmas
	 */
	@Override
	public void feedLinguisticTreeForOthers(final ArrayList<LemmaWithInflections> aSynonyms, final INTERFACETYPE iInterfaceType)
	{
		_2ndTreeModel.fillData(aSynonyms, iInterfaceType) ;
		_2ndTreeModel.refresh(iInterfaceType) ;
	}
		
	/**
	 * Initialize the edit/new dialog box
	 */
	protected void initEditControls()
	{
/*
		_editDialogBox = new DialogBox() ;
		// _CodeDialogBox.setSize("240px", "160px") ;
		_editDialogBox.setPopupPosition(150, 200) ;
		_editDialogBox.setText(constants.cispCode()) ;
		_editDialogBox.setAnimationEnabled(true) ;
    
		_editDialogOkButton = new Button(constants.generalOk()) ;
		_editDialogOkButton.setSize("70px", "30px") ;
		_editDialogOkButton.getElement().setId("okbutton") ;
			
		_editDialogCancelButton = new Button(constants.generalCancel()) ;
		_editDialogCancelButton.setSize("70px", "30px") ;
		_editDialogCancelButton.getElement().setId("cancelbutton") ;
    
		_LemmaTextBox = new TextBox() ;
		_LemmaTextBox.addStyleName("lemmaLabelTextBox") ;
*/    
	}

	@Override
	public void open2ndAddPanel() 
	{
		_2ndCommandPanel.setHeight("5em") ;
		
		_languageSelection.setVisible(true) ;
		_2ndAddedLabel.setVisible(true) ;
		
		_2ndAddOkBtn.setVisible(true) ;
		_2ndAddCancelBtn.setVisible(true) ;
	}
	
	@Override
	public void close2ndAddPanel() 
	{
		_languageSelection.setVisible(false) ;
		_2ndAddedLabel.setVisible(false) ;
		
		_2ndAddOkBtn.setVisible(false) ;
		_2ndAddCancelBtn.setVisible(false) ;
		
		_2ndCommandPanel.setHeight("0em") ;
	}
	
	@Override
	public HasClickHandlers get2ndAddOkButtonKeyDown() {
		return _2ndAddOkBtn ;
	}
	
	@Override
	public HasClickHandlers get2ndAddCancelButtonKeyDown() {
		return _2ndAddCancelBtn ;
	}
	
	@Override
	public HasClickHandlers get2ndEditButtonKeyDown() {
		return _2ndReadOnlyToEditBtn ;
	}

	@Override
	public HasClickHandlers get2ndAddButtonKeyDown() {
		return _2ndAddButton ;
	}
	
	@Override
	public ArrayList<PushButton> get2ndButtonsArray() {
		return _a2ndButtons ;
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
	public String getEditedGrammar() {
		return _AddedGrammar.getText() ;
	}
	
	@Override
	public void setEditedGrammar(final String sGrammar)
	{
		if (null == sGrammar)
			_AddedGrammar.setText("") ;
		else
			_AddedGrammar.setText(sGrammar) ;
	}
	
	@Override
	public String get2ndEditedText() {
		return _2ndAddedLabel.getText() ;
	}
	
	@Override
	public void set2ndEditedText(final String sText)
	{
		if (null == sText)
			_2ndAddedLabel.setText("") ;
		else
			_2ndAddedLabel.setText(sText) ;
	}
	
	@Override
	public String get2ndEditedGrammar() {
		return _2ndAddedGrammar.getText() ;
	}
	
	@Override
	public void set2ndEditedGrammar(final String sGrammar)
	{
		if (null == sGrammar)
			_2ndAddedGrammar.setText("") ;
		else
			_2ndAddedGrammar.setText(sGrammar) ;
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
		if      ("lemmaErrEmptyLabel".equals(sErrMsgId))
			_baseDisplayModel.getErrorDialogBox().setText(_baseDisplayModel.getConstants().lemmaErrEmptyLabel()) ;
		else if ("lemmaErrNoLanguage".equals(sErrMsgId))
			_baseDisplayModel.getErrorDialogBox().setText(_baseDisplayModel.getConstants().lemmaErrNoLanguage()) ;
		else if ("lemmaErrNoGrammar".equals(sErrMsgId))
			_baseDisplayModel.getErrorDialogBox().setText(_baseDisplayModel.getConstants().lemmaErrNoGrammar()) ;
		else if ("lemmaErrAlreadyExists".equals(sErrMsgId))
			_baseDisplayModel.getErrorDialogBox().setText(_baseDisplayModel.getConstants().lemmaErrAlreadyExists()) ;
	}
	
	public void reset() {	
	}

	public Widget asWidget() {
		return this ;
	}
}
