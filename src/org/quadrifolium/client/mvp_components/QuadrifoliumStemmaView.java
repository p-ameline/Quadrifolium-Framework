package org.quadrifolium.client.mvp_components;

import java.util.ArrayList;

import org.quadrifolium.client.cytoscape.CytoscapePanel;
import org.quadrifolium.client.mvp_components.QuadrifoliumComponentBaseDisplayModel.INTERFACETYPE;
import org.quadrifolium.shared.ontology.TripleWithLabel;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class QuadrifoliumStemmaView extends QuadrifoliumComponentBaseFlowDisplay implements QuadrifoliumStemmaPresenter.Display 
{
	// Stemma display area
	//
	protected CytoscapePanel _stemmaPanel ;
	
	public QuadrifoliumStemmaView() 
	{
		super() ;
		
		addStyleName("semanticsWorshopPanel") ;
		
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
		_stemmaPanel.addStyleName("lemmasCellTreePanel") ;
		
    add(_stemmaPanel) ;
    
    // _stemmaPanel.open() ;
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
	public void updateView(final ArrayList<TripleWithLabel> aTriples, final INTERFACETYPE iInterfaceType)
	{
		_baseDisplayModel.getCommandPanel().clear() ;
		showCaption() ;
		
		if (false == _stemmaPanel.isOpen())
			_stemmaPanel.open() ;
		
		feedStemma(iInterfaceType) ;
		
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
	public void feedStemma(final INTERFACETYPE iInterfaceType)
	{
		// _stemmaPanel.clear() ;
		
		_stemmaPanel.addNode() ;
		
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
