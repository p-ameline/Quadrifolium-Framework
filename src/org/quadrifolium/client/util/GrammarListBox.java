package org.quadrifolium.client.util;

import org.quadrifolium.client.loc.QuadrifoliumConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.ListBox;

/**
 * ListBox to select grammatical types 
 */
public class GrammarListBox extends ListBox
{
	protected final QuadrifoliumConstants constants = GWT.create(QuadrifoliumConstants.class) ;

  /**
   * Default Constructor
   *
   */
  public GrammarListBox()
  {
    super() ;
    
    init() ;
    
    // Display as a drop down list
    //
    setVisibleItemCount(1) ;
    
    // Set to undefined
    //
    setItemSelected(0, true) ;
  }
  
  /**
   * Select a an item from its grammatical code 
   */
  public void setSelected(final String sValue)
  {
  	if ((null == sValue) || "".equals(sValue))
  	{
  		setSelectedIndex(0) ;
  		return ;
  	}
  	
  	// Find corresponding item in the list
 		//
  	int iIndex = getIndexForValue(sValue) ;
  	
  	if (-1 == iIndex)
  	{
  		setSelectedIndex(0) ;
  		return ;
  	}
  	
 		setItemSelected(iIndex, true) ;
  }
  
  /**
   * Find corresponding item in the list
   * 
   * @return <code>-1</code> if not found
   */
  protected int getIndexForValue(final String sValue) throws NullPointerException
  {
  	if (null == sValue)
  		throw new NullPointerException() ;
  	
  	int iSize = getItemCount() ;
 		for (int i = 0 ; i < iSize ; i++)
 			if (getValue(i).equals(sValue))
 				return i ;
 		
 		return -1 ;
  }
  
  /**
   * Populate the list
   */
  public void init()
  {
  	addItem(constants.Undefined()) ;
  
  	addItem(constants.grammarAdjective(),    "ADJ") ;
  	addItem(constants.grammarNounMascSing(), "MS") ;
  	addItem(constants.grammarNounMascPlur(), "MP") ;
  	addItem(constants.grammarNounFemSing(),  "FS") ;
  	addItem(constants.grammarNounFemPlur(),  "FP") ;
  	addItem(constants.grammarNounNeutSing(), "NS") ;
  	addItem(constants.grammarNounNeutPlur(), "NP") ;
  }     
}
