package org.quadrifolium.client.widgets;

import java.util.ArrayList;
import java.util.Iterator;

import org.quadrifolium.shared.ontology.Flex;

/**
 * Object used to reference a {@link FlexTextBox} in order to benefit from asynchronous connection to
 * the Quadrifolium: Requests are sent with requesting TextBox's index so that answer can be used
 * to populate proper FlexTextBox  
 * 
 * Inspired from http://sites.google.com/site/gwtcomponents/auto-completiontextbox
 */
public class FlexTextBoxIndex
{
	private FlexTextBox _flexTextBox ;
	private int         _iIndex ;
	private int         _iInstanceCounter ;
		
  public FlexTextBoxIndex(FlexTextBox lexiqTxtBx, int iIndex, final String sLanguage)
  {
    _flexTextBox   = lexiqTxtBx ;
    _flexTextBox.setLanguage(sLanguage) ;
    
    _iIndex           = iIndex ;
    _iInstanceCounter = 1 ;
  }

	public FlexTextBox getLexiqueTextBox() {
		return _flexTextBox ;
	}
	public void setLexiqueTextBox(final FlexTextBox lexiqueTextBox) {
		_flexTextBox = lexiqueTextBox ;
	}

	/**
   * Initialize a LexiqueTextBox from a list of lexicon entries
   */
	public void initLexiqueTextBox(final ArrayList<Flex> entriesList)
	{
		if (null == entriesList)
			return ;
		
		_flexTextBox.clearList() ;
		
		if (entriesList.isEmpty())
		{
			_flexTextBox.hidePopup() ;
			return ;
		}
		
		for (Iterator<Flex> it = entriesList.iterator() ; it.hasNext() ; )
			_flexTextBox.addChoice(it.next()) ;
		
		_flexTextBox.showPopup() ;
	}
	
	public int getIndex() {
		return _iIndex ;
	}
	public void setIndex(int iIndex) {
		_iIndex = iIndex ;
	}
		
	public String getLanguage() {
		return _flexTextBox.getLanguage() ;
	}
	public void setLanguage(final String sLanguage) {
		_flexTextBox.setLanguage(sLanguage) ;
	}
	
	public void incrementInstanceCounter() {
		_iInstanceCounter++ ;
	}
		
	public void decrementInstanceCounter() {
		_iInstanceCounter-- ;
	}
		
	public boolean stillExists() {
		return (_iInstanceCounter > 0) ;
	}
}
