package org.quadrifolium.client.mvp_components;

import java.util.ArrayList;
import java.util.HashMap;

import org.quadrifolium.client.loc.QuadrifoliumConstants;
import org.quadrifolium.client.mvp_components.QuadrifoliumComponentBaseDisplayModel.INTERFACETYPE;
import org.quadrifolium.client.ui.QuadrifoliumResources;
import org.quadrifolium.client.util.GrammarListBox;
import org.quadrifolium.client.util.LinguisticTreeCellButton;
import org.quadrifolium.client.util.LinguisticTreeModel;
import org.quadrifolium.shared.ontology.FlexWithTraits;
import org.quadrifolium.shared.ontology.LanguageTag;
import org.quadrifolium.shared.ontology.LemmaWithInflections;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ButtonBase;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class QuadrifoliumLemmasView extends QuadrifoliumComponentBaseLayoutDisplay implements QuadrifoliumLemmasPresenter.Display, IQuadrifoliumComponentBaseDisplayModel, HasClickHandlers
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
	
	protected ArrayList<ButtonBase> _a2ndButtons = new ArrayList<ButtonBase>() ;
	
	// Not a good idea to store such a Map since buttons' location change when the tree is expanded or collapsed
	// protected HashMap<String, hitRect> _aButtonsHitZones = new HashMap<String, hitRect>() ;
	// We just keep the latest event
	protected String                _sHitButtonId ;
	protected hitPoint              _hitButtonPoint = new hitPoint() ;

	// Add lemma controls
	//
	protected Label          _lemmaLabel ;
	protected TextBox        _AddedLabel ;
	protected Label          _grammarLabel ;
	protected GrammarListBox _AddedGrammar ;
	
	protected Label          _languageLabel ;
	protected ListBox        _languageSelection ;
	protected Label          _2ndLemmaLabel ;
	protected TextBox        _2ndAddedLabel ;
	protected Label          _2ndGrammarLabel ;
	protected GrammarListBox _2ndAddedGrammar ;
	
	// Linguistic information display area
	//
	protected SimplePanel         _treeViewPanel ;
	protected LinguisticTreeModel _treeModel ;
	protected CellTree            _linguisticTree ;

	protected SimplePanel         _2ndTreeViewPanel ;
	protected LinguisticTreeModel _2ndTreeModel ;
	protected CellTree            _linguisticTree4Other ;

	/**
	 * Point
	 */
	public class hitPoint 
	{
		protected int _iX ;
		protected int _iY ;
		
		public hitPoint() {
			reset() ;
		}
		
		public hitPoint(final int iX, final int iY)
		{
			_iX = iX ;
			_iY = iY ;
		}
		
		public void initFrom(final hitPoint other)
		{
			reset() ;
			
			if (null == other)
				return ;
			
			_iX = other._iX ;
			_iY = other._iY ;
		}
		
		public void reset()
		{
			_iX = -1 ;
			_iY = -1 ;
		}
		
		public void setX(final int iX) {
			_iX = iX ;
		}
		public int getX() {
			return _iX ;
		}
		public void setY(final int iY) {
			_iY = iY ;
		}
		public int getY() {
			return _iY ;
		}
		
		public boolean isEmpty() {
			return ((-1 == _iX) && (-1 ==_iY)) ;
		}
		
		/**
		 * Determine whether two hitPoint are exactly similar
		 */
		public boolean equals(final hitPoint other)
		{ 
			if (null == other)
				return false ;
			
			if (this == other)
				return true ;
			
			return equals(other._iX, other._iY) ;
		}
		
		/**
		 * Determine this hitPoint is equal to a set of coordinates
		 */
		public boolean equals(final int iX, final int iY) { 
			return ((_iX == iX) && (_iY == iY)) ;
		}
		
		/**
		 * Determine whether an object is similar to this hitPoint
		 */
		public boolean equals(Object o) 
		{
			if ((null == o) || (getClass() != o.getClass()))
				return false ;
			
			if (this == o)
				return true ;

			final hitPoint other = (hitPoint) o ;

			return equals(other) ;
		}
	}

	/**
	 * Rectangle
	 */
	protected class hitRect
	{
		protected hitPoint _topLeftPoint     = new hitPoint() ;
		protected hitPoint _bottomRightPoint = new hitPoint() ;
		
		public hitRect() {
			reset() ;
		}
		
		public hitRect(final hitPoint topLeft, final hitPoint bottomRight)
		{
			_topLeftPoint.initFrom(topLeft) ;
			_bottomRightPoint.initFrom(bottomRight) ;
		}
		
		/**
		 * Is this point inside the rectangle?
		 * 
		 * @throws NullPointerException
		 */
		public boolean contains(final hitPoint point) throws NullPointerException
		{
			if (null == point)
				throw new NullPointerException() ;
			
			return contains(point.getX(), point.getY()) ;
		}
		
		/**
		 * Is this point (iX, iY) inside the rectangle?
		 * 
		 * @throws NullPointerException
		 */
		public boolean contains(final int iX, final int iY)
		{
			if (isEmpty())
				return false ;
			
			if ((iX < _topLeftPoint.getX()) || (iX > _bottomRightPoint.getX()) ||
					(iY < _topLeftPoint.getY()) || (iY > _bottomRightPoint.getY()))
				return false ;
			
			return true ;
		}
		
		/**
		 * Grow until it contains the point
		 * 
		 * @throws NullPointerException
		 */
		public void expandTo(final hitPoint point) throws NullPointerException
		{
			if (null == point)
				throw new NullPointerException() ;
			
			if (contains(point))
				return ;
			
			if (isEmpty())
			{
				_topLeftPoint.initFrom(point) ;
				_bottomRightPoint.initFrom(point) ;
			}
			
			// Growing along the X axis
			//
			if      (point.getX() < _topLeftPoint.getX())
				_topLeftPoint.setX(point.getX()) ;
			else if (point.getX() > _bottomRightPoint.getX())
				_bottomRightPoint.setX(point.getX()) ;
				
			// Growing along the Y axis
			//
			if      (point.getY() < _topLeftPoint.getY())
				_topLeftPoint.setY(point.getY()) ;
			else if (point.getY() > _bottomRightPoint.getY())
				_bottomRightPoint.setY(point.getY()) ;
		}
		
		public void initFrom(final hitRect other)
		{
			reset() ;
			
			if (null == other)
				return ;
			
			_topLeftPoint.initFrom(other._topLeftPoint) ;
			_bottomRightPoint.initFrom(other._bottomRightPoint) ;
		}
		
		public void reset()
		{
			_topLeftPoint.reset() ;
			_bottomRightPoint.reset() ;
		}
		
		public boolean isEmpty() {
			return (_topLeftPoint.isEmpty() && _bottomRightPoint.isEmpty()) ;
		}
		
		public void setTopLeftPoint(final hitPoint topLeft) {
			_topLeftPoint.initFrom(topLeft) ;
		}
		public hitPoint getTopLeftPoint() {
			return _topLeftPoint ;
		}
		public void setBottomRightPoint(final hitPoint bottomRight) {
			_bottomRightPoint.initFrom(bottomRight) ;
		}
		public hitPoint getBottomRightPoint() {
			return _bottomRightPoint ;
		}
		
		/**
		 * Determine whether two hitRect are exactly similar
		 */
		public boolean equals(final hitRect other)
		{ 
			if (null == other)
				return false ;
			
			if (this == other)
				return true ;
			
			return (_topLeftPoint.equals(other._topLeftPoint) && _bottomRightPoint.equals(other._bottomRightPoint)) ;
		}
		
		/**
		 * Determine whether an object is similar to this hitRect
		 */
		public boolean equals(Object o) 
		{
			if ((null == o) || (getClass() != o.getClass()))
				return false ;
			
			if (this == o)
				return true ;

			final hitRect other = (hitRect) o ;

			return equals(other) ;
		}
	}
	
	public QuadrifoliumLemmasView() 
	{
		addStyleName("lemmasWorshopPanel") ;
		
		// Command panel
		//
		initUserLanguagePanel() ;
		initOtherLanguagesPanel() ;
		
		addNorth(_UserLanguagePanel, 250) ;
		
		// initAddingPanel() ;
		// initAdding2ndPanel() ;
	
		// In a SplitLayoutPanel, center panel must always be added last
		//
		add(_OtherLanguagesPanel) ;
		
		// Attach the LayoutPanel to the RootLayoutPanel. The latter will listen for
    // resize events on the window to ensure that its children are informed of
    // possible size changes.
    // RootLayoutPanel rp = RootLayoutPanel.get() ;
    // rp.add(this);
	
		initErrorDialog() ;
	}
	
	/**
	 * Initialize user language panel
	 */
	protected void initUserLanguagePanel()
	{
		_UserLanguagePanel = new FlowPanel() ;
		
		initCommandPanel() ;
		initAddingPanel() ;
		initLinguisticPanel() ;
	}
	
	/**
	 * Initialize other languages panel
	 */
	protected void initOtherLanguagesPanel()
	{
		_OtherLanguagesPanel = new FlowPanel() ;
		
		initSecondCommandPanel() ;
		initAdding2ndPanel() ;
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
		
		_treeViewPanel = new SimplePanel() ;
		_treeViewPanel.addStyleName("lemmasCellTreePanel") ;
		_treeViewPanel.add(_linguisticTree) ;
		
		_UserLanguagePanel.add(_treeViewPanel) ;
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
		
		_2ndTreeViewPanel = new SimplePanel() ;
		_2ndTreeViewPanel.addStyleName("lemmasCellTreePanel") ;
		_2ndTreeViewPanel.add(_linguisticTree4Other) ;
		
		_OtherLanguagesPanel.add(_2ndTreeViewPanel) ;
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
		_baseDisplayModel.getAddPanel().addStyleName("addElementPanel") ;
		
		_baseDisplayModel.getAddPanel().setHeight("0px") ;
		
		// Lemma
		//
		_lemmaLabel = new Label(uppercaseFirstLetter(constants.generalLemma())) ;
		_lemmaLabel.addStyleName("addLabel") ;
		_lemmaLabel.setVisible(false) ;
		
		_AddedLabel = new TextBox() ;
		_AddedLabel.addStyleName("addTextBox") ;
		_AddedLabel.setVisible(false) ;
		
		// Grammar
		//
		_grammarLabel = new Label(uppercaseFirstLetter(constants.generalGrammar())) ;
		_grammarLabel.addStyleName("addLabel") ;
		_grammarLabel.setVisible(false) ;
		
		_AddedGrammar = new GrammarListBox() ;
		_AddedGrammar.addStyleName("addGrammar") ;
		_AddedGrammar.setVisible(false) ;
		
		_baseDisplayModel.getAddPanel().add(_lemmaLabel) ;
		_baseDisplayModel.getAddPanel().add(_AddedLabel) ;
		_baseDisplayModel.getAddPanel().add(_grammarLabel) ;
		_baseDisplayModel.getAddPanel().add(_AddedGrammar) ;
		
		initAddingPanelButtons() ;
		
		_UserLanguagePanel.add(_baseDisplayModel.getAddPanel()) ;
	}
	
	protected void initAdding2ndPanel()
	{
		_2ndAddPanel = new FlowPanel() ;
		_2ndAddPanel.addStyleName("addElementPanel") ;
		
		_2ndAddPanel.setHeight("0px") ;
		
		// Language
		//
		_languageLabel = new Label(uppercaseFirstLetter(constants.generalLanguage())) ;
		_languageLabel.addStyleName("addLabel") ;
		_languageLabel.setVisible(false) ;
		
		_languageSelection  = new ListBox() ;
		_languageSelection.addStyleName("addDefinitionLang") ;
		_languageSelection.setVisible(false) ;
		
		// Lemma
		//
		_2ndLemmaLabel = new Label(uppercaseFirstLetter(constants.generalLemma())) ;
		_2ndLemmaLabel.addStyleName("addLabel") ;
		_2ndLemmaLabel.setVisible(false) ;
		
		_2ndAddedLabel = new TextBox() ;
		_2ndAddedLabel.addStyleName("addTextBox") ;
		_2ndAddedLabel.setVisible(false) ;
		
		// Grammar
		//
		_2ndGrammarLabel = new Label(uppercaseFirstLetter(constants.generalGrammar())) ;
		_2ndGrammarLabel.addStyleName("addLabel") ;
		_2ndGrammarLabel.setVisible(false) ;
		
		_2ndAddedGrammar = new GrammarListBox() ;
		_2ndAddedGrammar.addStyleName("addGrammar") ;
		_2ndAddedGrammar.setVisible(false) ;
		
		// Add controls to panel
		//
		_2ndAddPanel.add(_languageLabel) ;
		_2ndAddPanel.add(_languageSelection) ;
		_2ndAddPanel.add(_2ndLemmaLabel) ;
		_2ndAddPanel.add(_2ndAddedLabel) ;
		_2ndAddPanel.add(_2ndGrammarLabel) ;
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
		// Refresh command panel
		//
		_baseDisplayModel.getCommandPanel().clear() ;
		showCaption() ;
		
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
		
		setTreeViewPanelPosition() ;
		
		_lemmaLabel.setVisible(true) ;
		_AddedLabel.setVisible(true) ;
		_grammarLabel.setVisible(true) ;
		_AddedGrammar.setVisible(true) ;
		
		_baseDisplayModel.getAddOkButton().setVisible(true) ;
		_baseDisplayModel.getAddCancelButton().setVisible(true) ;
	}
	
	@Override
	public void closeAddPanel() 
	{
		_lemmaLabel.setVisible(false) ;
		_AddedLabel.setVisible(false) ;
		_grammarLabel.setVisible(false) ;
		_AddedGrammar.setVisible(false) ;
		
		_baseDisplayModel.getAddOkButton().setVisible(false) ;
		_baseDisplayModel.getAddCancelButton().setVisible(false) ;
		
		_baseDisplayModel.getAddPanel().setHeight("0em") ;
		
		setTreeViewPanelPosition() ;
	}
	
	/**
	 * Set the top position of the tree view panel so that it remains under the edit panel
	 */
	protected void setTreeViewPanelPosition() {
		_treeViewPanel.getElement().getStyle().setTop(_baseDisplayModel.getDisplayPanelPosition(), Unit.PX) ;
	}
	
	/**
	 * (re)initialize the tree from a set of lemmas
	 */
	@Override
	public void feedLinguisticTree(final ArrayList<LemmaWithInflections> aSynonyms, final INTERFACETYPE iInterfaceType)
	{
		_treeModel.fillData(aSynonyms, iInterfaceType, this) ;
		// _treeModel.refresh(iInterfaceType) ; // Already done at the end of fillData
		
		if (INTERFACETYPE.editMode != iInterfaceType)
			detachLemmasButtons() ;
	}

	/**
	 * A button was hit for a given point, update its rectangle accordingly
	 */
	public void signalHit(final String sButtonId, final hitPoint point)
	{
		_sHitButtonId = sButtonId ;
		_hitButtonPoint.initFrom(point) ; 
/*
		hitRect rect = _aButtonsHitZones.get(sButtonId) ;
		
		if (null == rect)
			_aButtonsHitZones.put(sButtonId, new hitRect(point, point)) ;
		else
			rect.expandTo(point) ;
*/
	}
	
	/**
	 * Create button elements as Button in the array
	 */
	@Override
	public void connectLemmasButtons()
	{
		// Detach previous buttons
		//
		detachLemmasButtons() ;
		
		// Get the tree of lemmas as a DOM element
		//
		Element rootTreeElement = _linguisticTree.getElement() ;
			
		// Get all its buttons
		//
		NodeList<Element> buttons =	rootTreeElement.getElementsByTagName("button") ;
		if ((null == buttons) || (buttons.getLength() == 0))
			return ;
					
		// Get all buttons elements and add them as Button in the array
		//
		for (int i = 0 ; i < buttons.getLength() ; i++)
		{
			Element buttonElement = buttons.getItem(i) ;
			
			getBaseDisplayModel()._aButtons.add(LinguisticTreeCellButton.wrap(buttonElement)) ;
			
			// _aButtonsHitZones.put(buttonElement.getId(), new hitRect()) ;
		}
	}
	
	/**
	 * Create button elements as Button in the array
	 */
	@Override
	public void connect2ndLemmasButtons()
	{
		// Detach previous buttons
		//
		detach2ndLemmasButtons() ;
		
		// Get the tree of lemmas as a DOM element
		//
		Element rootTreeElement = _linguisticTree4Other.getElement() ;
			
		// Get all its buttons
		//
		NodeList<Element> buttons =	rootTreeElement.getElementsByTagName("button") ;
		if ((null == buttons) || (buttons.getLength() == 0))
			return ;
					
		// Get all buttons elements and add them as Button in the array
		//
		for (int i = 0 ; i < buttons.getLength() ; i++)
		{
			Element buttonElement = buttons.getItem(i) ;
			
			_a2ndButtons.add(LinguisticTreeCellButton.wrap(buttonElement)) ;
		}
	}
	
	/**
	 * Detach all buttons that were created using wrap(element)
	 */
	protected void detachLemmasButtons()
	{
		if (getBaseDisplayModel()._aButtons.isEmpty())
			return ;
		
		for (ButtonBase buttonBase : getBaseDisplayModel()._aButtons)
		{
			// _aButtonsHitZones.remove(buttonBase.getElement().getId()) ;
/*
			Button button = (Button) buttonBase ;
			
			if (null != button)
				RootPanel.detachNow(button) ;
*/
		}
		
		getBaseDisplayModel()._aButtons.clear() ;
	}
	
	/**
	 * Detach all buttons that were created using wrap(element)
	 */
	protected void detach2ndLemmasButtons()
	{
		if (_a2ndButtons.isEmpty())
			return ;
		
		for (ButtonBase buttonBase : _a2ndButtons)
		{
			// _aButtonsHitZones.remove(buttonBase.getElement().getId()) ;
			
/*
			Button button = (Button) buttonBase ;
			
			if (null != button)
				RootPanel.detachNow(button) ;
*/
		}
		
		_a2ndButtons.clear() ;
	}
	
	/**
	 * (re)initialize the tree from a set of lemmas
	 */
	@Override
	public void feedLinguisticTreeForOthers(final ArrayList<LemmaWithInflections> aSynonyms, final INTERFACETYPE iInterfaceType)
	{
		_2ndTreeModel.fillData(aSynonyms, iInterfaceType, this) ;
		// _2ndTreeModel.refresh(iInterfaceType) ; // Already done at the end of fillData
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
		_2ndAddPanel.setHeight("7em") ;
		
		set2ndTreeViewPanelPosition() ;
		
		_languageLabel.setVisible(true) ;
		_languageSelection.setVisible(true) ;
		_2ndLemmaLabel.setVisible(true) ;
		_2ndAddedLabel.setVisible(true) ;
		_2ndGrammarLabel.setVisible(true) ;
		_2ndAddedGrammar.setVisible(true) ;
		
		_2ndAddOkBtn.setVisible(true) ;
		_2ndAddCancelBtn.setVisible(true) ;
	}
	
	@Override
	public void close2ndAddPanel() 
	{
		_languageLabel.setVisible(false) ;
		_languageSelection.setVisible(false) ;
		_2ndLemmaLabel.setVisible(false) ;
		_2ndAddedLabel.setVisible(false) ;
		_2ndGrammarLabel.setVisible(false) ;
		_2ndAddedGrammar.setVisible(false) ;
		
		_2ndAddOkBtn.setVisible(false) ;
		_2ndAddCancelBtn.setVisible(false) ;
		
		_2ndAddPanel.setHeight("0em") ;
		
		set2ndTreeViewPanelPosition() ;
	}
	
	/**
	 * Set the top position of the tree view panel so that it remains under the edit panel
	 */
	protected void set2ndTreeViewPanelPosition() 
	{
		int iPixelsPosition = _2ndCommandPanel.getOffsetHeight() + _2ndAddPanel.getOffsetHeight() ;
		
		_2ndTreeViewPanel.getElement().getStyle().setTop(iPixelsPosition, Unit.PX) ;
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
	public ArrayList<ButtonBase> get2ndButtonsArray() {
		return _a2ndButtons ;
	}
	
	/**
	 * Check if a button from the 2nd buttons array was clicked
	 */
	@Override
	public ButtonBase hitTestInButtonsArray(final ClickEvent event)
	{
		if ((null == getBaseDisplayModel()._aButtons) || getBaseDisplayModel()._aButtons.isEmpty())
			return null ;
		
		int iX = event.getClientX() ;
		int iY = event.getClientY() ;
		
		for (ButtonBase button : getBaseDisplayModel()._aButtons)
			if (hiTest(button, iX, iY))
				return button ;
			
		return null ;
	}
	
	/**
	 * Check if a button from the 2nd buttons array was clicked
	 */
	@Override
	public ButtonBase hitTestIn2ndButtonsArray(final ClickEvent event)
	{
		if ((null == _a2ndButtons) || _a2ndButtons.isEmpty())
			return null ;
		
		int iX = event.getClientX() ;
		int iY = event.getClientY() ;
		
		for (ButtonBase button : _a2ndButtons)
			if (hiTest(button, iX, iY))
				return button ;
			
		return null ;
	}
	
	/**
	 * Is a point inside a button?
	 * 
	 * @param button Button to check if point 
	 * @param iX     Point's X coordinate
	 * @param iY     Point's Y coordinate
	 * 
	 * @throws NullPointerException
	 */
	protected boolean hiTest(final ButtonBase button, final int iX, final int iY) throws NullPointerException
	{
		if (null == button)
			throw new NullPointerException() ;
	
		return (_sHitButtonId.equals(button.getElement().getId()) && _hitButtonPoint.equals(iX, iY)) ;
		
/*
		hitRect rect = _aButtonsHitZones.get(button.getElement().getId()) ;
		
		if (null == rect)
			return false ;
		
		return rect.contains(iX, iY) ;
*/
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
		return _AddedGrammar.getSelectedValue() ;
	}
	
	@Override
	public void setEditedGrammar(final String sGrammar)
	{
		if (null == sGrammar)
			_AddedGrammar.setSelected("") ;
		else
			_AddedGrammar.setSelected(sGrammar) ;
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
		return _2ndAddedGrammar.getSelectedValue() ;
	}
	
	@Override
	public void set2ndEditedGrammar(final String sGrammar)
	{
		if (null == sGrammar)
			_2ndAddedGrammar.setSelected("") ;
		else
			_2ndAddedGrammar.setSelected(sGrammar) ;
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

	public Element getLemmasTreeAsDomElement() {
		return _linguisticTree.getElement() ;
	}
	
	public Element get2ndLemmasTreeAsDomElement() {
		return _linguisticTree4Other.getElement() ;
	}
	
	public void reset() {	
	}

	public Widget asWidget() {
		return this ;
	}

	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return addDomHandler(handler, ClickEvent.getType()) ;
	}
}
