package org.quadrifolium.client.widgets4arch;

import java.util.Iterator;
import java.util.Vector;

import org.quadrifolium.shared.ontology.Triple;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.ListBox;

import com.primege.client.loc.PrimegeViewConstants;
import com.primege.client.util.FormControlOptionData;
import com.primege.client.widgets.ControlBase;
import com.primege.client.widgets.ControlModel;
import com.primege.shared.database.FormDataData;

/**
 * LisBox to select a triple's object from a list
 * 
 */
public class FixedTraitLimitedObjectsControl extends ListBox implements ControlModel
{
	protected final PrimegeViewConstants constants = GWT.create(PrimegeViewConstants.class) ;

	protected ControlBase                   _base ;
	protected Vector<FormControlOptionData> _aOptions = new Vector<FormControlOptionData>() ;
  
  /**
   * Default Constructor
   *
   */
  public FixedTraitLimitedObjectsControl(final Vector<FormControlOptionData> aOptions, final String sPath)
  {
    super() ;
    
    _base = new ControlBase(sPath) ;
    
    init(aOptions) ;
    
    setVisibleItemCount(1) ;
    setItemSelected(0, true) ;
  }
  
  /**
   * Populate the list
   */
  public void init(final Vector<FormControlOptionData> aOptions)
  {
  	if ((null == aOptions) || aOptions.isEmpty())
  		return ;
  	
  	addItem(constants.Undefined()) ;
  	
  	for (FormControlOptionData optionData : aOptions)
  	{
  		addItem(optionData.getCaption()) ;  		
  		_aOptions.add(new FormControlOptionData(optionData)) ;
  	}
  }
      
  /**
   * Return a FormDataData which value is filled with content
   */
	public FormDataData getContent()
	{
		String sSelectedObject = getSelectedObject() ;
		if ("".equals(sSelectedObject))
			return null ;
		
		FormDataData formData = new FormDataData() ;
		formData.setPath(_base.getPath() + ">" + sSelectedObject) ;
		return formData ;
	}
	
	/**
   * Return the selected option code is any, or <code>""</code> if none
   */
	public String getSelectedObject()
	{
		String sSelectedOptionLabel = getSelectedValue() ;
		
		if ("".equals(sSelectedOptionLabel) || sSelectedOptionLabel.equals(constants.Undefined()))
			return "" ;
		
		for (FormControlOptionData optionData : _aOptions)
			if (sSelectedOptionLabel.equals(optionData.getCaption()))
				return optionData.getPath() ;
		
		return "" ; 
	}
	
	/**
   * Initialize the selected option from a content 
   *
   * @param content       FormDataData used to initialize the control
   * @param sDefaultValue Default value in case there is no content 
   */
	public void setContent(final FormDataData content, final String sDefaultValue)  
	{
		String sOptionPath = "" ;
		
		if (null == content)
			sOptionPath = sDefaultValue ;
		else
			sOptionPath = content.getPath() ;
		
		//
		//
		String[] aTripleParts = sOptionPath.split(">") ;
		if (2 != aTripleParts.length)
			return ;
		
		// Find corresponding option
		//
		FormControlOptionData selectedOption = null ;
		
		for (Iterator<FormControlOptionData> it = _aOptions.iterator() ; it.hasNext() ; )
		{
			FormControlOptionData optionData = it.next() ;
			
			String sLocalPath = _base.getPath() + "/" + optionData.getPath() ;
			if (sLocalPath.equals(sOptionPath))
			{
				selectedOption = optionData ;
				break ;
			}
		}
				
		if (null == selectedOption)
			return ;
		
		String sOptionLabel = selectedOption.getCaption() ; 
		
		// Find corresponding item in the list
		//		
		int iSize = getItemCount() ;
		for (int i = 0 ; i < iSize ; i++)
			if (getItemText(i).equals(sOptionLabel))
			{
				setItemSelected(i, true) ;
				return ;
			}
	}

	/**
	 * Initialize the list of options
	 */
	protected void initOptions(final Vector<FormControlOptionData> aOptions)
	{
		_aOptions.clear() ;
		
		if ((null == aOptions) || aOptions.isEmpty())
			return ;
		
	
		for (Iterator<FormControlOptionData> it = aOptions.iterator() ; it.hasNext() ; )
			_aOptions.add(new FormControlOptionData(it.next())) ;
	}
	
	/**
	 * Get the option for a path 
	 * 
	 * @return The option if found, <code>null</code> if not
	 */
	public FormControlOptionData getOptionForPath(final String sOptionPath)
	{
		if ((null == sOptionPath) || "".equals(sOptionPath) || _aOptions.isEmpty())
			return null ;
		
		for (Iterator<FormControlOptionData> it = _aOptions.iterator() ; it.hasNext() ; )
		{
			FormControlOptionData optionData = it.next() ;
		
			String sLocalPath = _base.getPath() + "/" + optionData.getPath() ;
			if (sLocalPath.equals(sOptionPath))
				return optionData ;
		}
		
		return null ;
	}
	
	public ControlBase getControlBase() {
		return _base ;
	}
	
	public void setInitFromPrev(boolean bInitFromPrev) {
		_base.setInitFromPrev(bInitFromPrev); ;
	}
	
	public boolean getInitFromPrev() {
		return _base.getInitFromPrev() ;
	}
	
	public Vector<FormControlOptionData> getOptions() {
		return _aOptions ;
	}
}
