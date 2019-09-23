package org.quadrifolium.client.mvp;

import java.util.ArrayList;

import org.quadrifolium.client.global.QuadrifoliumSupervisor;
import org.quadrifolium.client.loc.QuadrifoliumConstants;
import org.quadrifolium.client.widgets.FlexTextBox;
import org.quadrifolium.shared.database.Language;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class QuadrifoliumCommandPanelView extends FlowPanel implements QuadrifoliumCommandPanelPresenter.Display 
{
	private final QuadrifoliumConstants constants = GWT.create(QuadrifoliumConstants.class) ;
	
	// Concept search area
	//
	private   final FlexTextBox _researchTxtBox ;
	private   final Button      _ChangeTermBtn ;
	
	protected       SimplePanel _titlePanel ;
	
	protected final ListBox     _languagesListBox ;
	
	@Inject
	public QuadrifoliumCommandPanelView(QuadrifoliumSupervisor supervisor) 
	{
		// Style
		//
		addStyleName("researchPanel") ;
			
		_researchTxtBox = supervisor.getInjector().getFlexTextBox() ;
		_researchTxtBox.setRootPanel(this) ;
		_researchTxtBox.addStyleName("researchBox") ;
			
		_ChangeTermBtn = new Button(constants.SelectThisTerm()) ;
		_ChangeTermBtn.addStyleName("researchButton") ;
			
		_titlePanel = new SimplePanel() ;
		_titlePanel.addStyleName("selectedTerm") ;
		
		_languagesListBox = new ListBox() ;
		_languagesListBox.addStyleName("researchLanguageList") ;
		
		add(_researchTxtBox) ;
		add(_ChangeTermBtn) ;
		add(_titlePanel) ;
		add(_languagesListBox) ;
	}
	
	/**
   * Populate the languages list
   */
	@Override
  public void initLanguages(final ArrayList<Language> aLanguages)
  {
  	_languagesListBox.clear() ;
  	
  	if ((null == aLanguages) || aLanguages.isEmpty())
  		return ;
  	
  	for (Language language : aLanguages)
  		_languagesListBox.addItem(language.getLabel(), language.getIsoCode()) ;  		
  }

	/**
	 * Ask the interface to display in a given language 
	 */
	@Override
	public void selectLanguage(final String sLanguageCode)
	{
		if ((null == sLanguageCode) || "".equals(sLanguageCode))
			return ;
		
		// Update language selection list box
		//
		setListBoxLanguage(sLanguageCode) ;
		
		// Update research text box
		//
		_researchTxtBox.setLanguage(sLanguageCode) ;
	}
	
	/**
	 * Show the selected concept title in the "caption and search" bar
	 */
	@Override
	public void displayTitle(final String sTitle)
	{
		_titlePanel.clear() ;
		
		if ((null == sTitle) || "".equals(sTitle))
			return ;
		
		Label titleLabel = new Label(sTitle) ;
		titleLabel.setStyleName("selectedTermLabel") ;
		
		_titlePanel.add(titleLabel) ;
	}
	
	/**
	 * Ask the language list to set a language (from its Iso code) as the selected one 
	 */
	protected void setListBoxLanguage(final String sLanguageCode)
	{
		if ((null == sLanguageCode) || "".equals(sLanguageCode))
			return ;
		
		int iItemsCount  = _languagesListBox.getItemCount() ;
		int iLangCodeLen = sLanguageCode.length() ;  
		
		int iClosest     = -1 ;
		int iClosestLen  = -1 ;
		
		for (int i = 0 ; i < iItemsCount ; i++)
		{
			String sLang = _languagesListBox.getValue(i) ;
			
			// Is there an exact match?
			//
			if (sLanguageCode.equals(sLang))
			{
				_languagesListBox.setSelectedIndex(i) ;
				return ;
			}
			
			// Is it a sub-code ?
			//
			int iLangLen = sLang.length() ;
			if ((iLangLen < iLangCodeLen) && (iLangLen > iClosestLen) && sLang.equals(sLanguageCode.substring(0, iLangLen)))
			{
				// Sub-codes are only considered if they are before a separation char ('-')
				//
				if (sLanguageCode.charAt(iLangLen) == '-')
				{
					iClosest    = i ;
					iClosestLen = iLangLen ;
				}
			}
		}
		
		// If a closest exist, select it
		//
		if (iClosest > -1)
			_languagesListBox.setSelectedIndex(iClosest) ;
	}
	
	/**
	 * Get languages list box index for a given language label
	 * 
	 * @return The index (0 based) if found, <code>-1</code> if not.
	 */
	protected int getIndexForLanguage(final String sLanguageLabel)
	{
		//Find corresponding item in the list
		//		
		int iSize = _languagesListBox.getItemCount() ;
		for (int i = 0 ; i < iSize ; i++)
			if (_languagesListBox.getItemText(i).equals(sLanguageLabel))
				return i ;
		
		return -1 ;
	}
	
	@Override
	public String getSelectedLanguageCode() {
		return _languagesListBox.getSelectedValue() ;
	}
		
	@Override
	public FlexTextBox getTermChangeTextBox() {
		return _researchTxtBox ;
	}
	
	@Override
	public HasClickHandlers getTermChangeButton() {
		return _ChangeTermBtn ;
	}
	
	@Override
	public HasChangeHandlers getLanguageChangeHandler() {
		return _languagesListBox ;
	}
	
	public void reset() {	
	}

	public Widget asWidget() {
		return this;
	}
}
