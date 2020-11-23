package org.quadrifolium.client.mvp_components;

import org.quadrifolium.client.cytoscape.CytoscapePanel;
import org.quadrifolium.client.mvp_components.QuadrifoliumComponentBaseDisplayModel.INTERFACETYPE;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import com.ldv.shared.graph.LdvModelTree;

public class QuadrifoliumStemmaView extends QuadrifoliumComponentBaseFlowDisplay implements QuadrifoliumStemmaPresenter.Display 
{
	// Stemma display area
	//
	protected CytoscapePanel _stemmaPanel ;
	
	public QuadrifoliumStemmaView() 
	{
		super() ;
		
		addStyleName("stemmaWorkshopPanel") ;
		
		// Panels
		//
		initCommandPanel() ;
		initStemmaPanel() ;
		
		initErrorDialog() ;
	}
	
	/**
	 * Initialize the semantic network panel
	 */
	protected void initStemmaPanel()
	{
		_stemmaPanel = new CytoscapePanel() ;
		_stemmaPanel.addStyleName("stemmaCytoscapePanel") ;
		
    add(_stemmaPanel) ;
    
    // _stemmaPanel.open() ;
	}
	
	/**
	 * Open the stemma viewer (cytoscape)
	 */
	@Override
	public void openStemmaViewer()
	{
		_stemmaPanel.open() ;
	}
	
	/**
	 * Initialize the command panel
	 */
	protected void initCommandPanel()
	{
		// Create command pannel
		//
		_baseDisplayModel.createCommandPanel() ;
		_baseDisplayModel.getCommandPanel().addStyleName("stemmaCommand") ;
		
		showCaption() ;
			
		add(_baseDisplayModel.getCommandPanel()) ;
		
		// Create edition mode buttons
		//
		createCommandPanelButtons() ;
	}
	
	/**
	 * Create command panel's buttons
	 * 
	 * We don't use the "baseDisplayModel" version since there is no "add" button since the stemma is a singleton 
	 */
	protected void createCommandPanelButtons() {
		_baseDisplayModel.createCommandPanelEditButton() ;
	}
	
	/**
	 * Update the view depending on what the user is entitled to doing
	 */
	@Override
	public void updateView(final LdvModelTree stemma, final INTERFACETYPE iInterfaceType)
	{
		_baseDisplayModel.getCommandPanel().clear() ;
		showCaption() ;
		
		/*
		if (false == _stemmaPanel.isOpen())
			_stemmaPanel.open() ;
		*/
		
		feedStemma(stemma, iInterfaceType) ;
		
		if (INTERFACETYPE.readOnlyMode == iInterfaceType)
			return ;
		
		setEditButton(INTERFACETYPE.editableMode == iInterfaceType) ;
		_baseDisplayModel.getCommandPanel().add(_baseDisplayModel.getReadOnlyToEditButton()) ;
	}

	/**
	 * Add "Definitions" caption to the command panel 
	 */
	public void showCaption()
	{
		Label caption = new Label(_baseDisplayModel.getConstants().captionStemma()) ;
		caption.addStyleName("chapterCaption") ;
		
		_baseDisplayModel.getCommandPanel().add(caption) ;
	}
	
	/**
	 * Feed and refresh the left semantic table (the one with current concept as the object of all triples)
	 */
	@Override
	public void feedStemma(final LdvModelTree stemma, final INTERFACETYPE iInterfaceType)
	{
		if ((false == _stemmaPanel.isOpen()) && (null != stemma))
			_stemmaPanel.open() ;
			
		if (_stemmaPanel.isOpen())
			_stemmaPanel.addNodes(stemma) ;
		
		_baseDisplayModel.clearButtons() ;
	}
	
	/**
	 * Nothing do do there since the stemma is a singleton
	 */
	@Override
	public void openAddPanel() {
	}
	
	@Override
	public void closeAddPanel() {
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
