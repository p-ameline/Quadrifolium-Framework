package org.quadrifolium.client.mvp_components;

import java.util.ArrayList;

import org.quadrifolium.client.loc.QuadrifoliumConstants;
import org.quadrifolium.client.util.LinguisticTreeModel;
import org.quadrifolium.shared.ontology.FlexWithTraits;
import org.quadrifolium.shared.ontology.LemmaWithInflections;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class QuadrifoliumLemmasView extends SplitLayoutPanel implements QuadrifoliumLemmasPresenter.Display 
{
	private final QuadrifoliumConstants constants = GWT.create(QuadrifoliumConstants.class) ;
		
	// User language panel
	//
	protected FlowPanel           _UserLanguagePanel ;
	protected SimplePanel         _CommandPanel ;
	protected Button              _NewLemmaBtn ;
	
	// Other languages panel
	//
	protected FlowPanel           _OtherLanguagesPanel ;
	protected SimplePanel         _2ndCommandPanel ;
	
	// Linguistic information display area
	//
	protected LinguisticTreeModel _treeModel ;
	protected CellTree            _linguisticTree ;
			
	protected LinguisticTreeModel _2ndTreeModel ;
	protected CellTree            _linguisticTree4Other ;
	
	public QuadrifoliumLemmasView() 
	{
		addStyleName("lemmasWorshopPanel") ;
		
		// Set to null all controls involved in the edit process, hence never instantiated in read_only mode
		//
		nullifyEditControl() ;
		
		// Command panel
		//
		initUserLanguagePanel() ;
		initOtherLanguagesPanel() ;
		
		addNorth(_UserLanguagePanel, 250) ;
		add(_OtherLanguagesPanel) ;
	}

	/**
	 * Set to null all controls involved in the edit process, hence never instantiated in read_only mode
	 */
	protected void nullifyEditControl()
	{
		_NewLemmaBtn = null ;		
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
		_CommandPanel = new SimplePanel() ;
		_CommandPanel.addStyleName("lemmasCommand") ;
		
		Label caption = new Label(constants.captionLemmas()) ;
		
		// if (_bReadOnly)
		// {
			_CommandPanel.add(caption) ;
		// }
			
		_UserLanguagePanel.add(_CommandPanel) ;
	}
	
	/**
	 * Initialize the command panel for "other languages"
	 */
	protected void initSecondCommandPanel()
	{
		_2ndCommandPanel = new SimplePanel() ;
		_2ndCommandPanel.addStyleName("lemmasCommand") ;
		
		Label caption = new Label(constants.captionOtherLemmas()) ;
		
		// if (_bReadOnly)
		// {
		_2ndCommandPanel.add(caption) ;
		// }
			
		_OtherLanguagesPanel.add(_2ndCommandPanel) ;
	}
	
	/**
	 * (re)initialize the tree from a set of lemmas
	 */
	@Override
	public void feedLinguisticTree(final ArrayList<LemmaWithInflections> aSynonyms)
	{
		_treeModel.fillData(aSynonyms) ;
		_treeModel.refresh() ;
	}

	/**
	 * (re)initialize the tree from a set of lemmas
	 */
	@Override
	public void feedLinguisticTreeForOthers(final ArrayList<LemmaWithInflections> aSynonyms)
	{
		_2ndTreeModel.fillData(aSynonyms) ;
		_2ndTreeModel.refresh() ;
	}
	
	/**
	 * Initialize the command panel
	 */
	@Override
	public void initCommandPanelForEditing()
	{
		_NewLemmaBtn = new Button("+") ;
		_NewLemmaBtn.addStyleName("lemmasCommandControl") ;
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
	public HasClickHandlers getNewLemmaButton() {
		return _NewLemmaBtn ;
	}
	
	public void reset() {	
	}

	public Widget asWidget() {
		return this ;
	}
}
