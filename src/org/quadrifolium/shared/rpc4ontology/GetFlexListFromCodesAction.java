package org.quadrifolium.shared.rpc4ontology;

import java.util.ArrayList;

import net.customware.gwt.dispatch.shared.Action;

/**
 * Object used to get a list of Flex records from a text 
 */
public class GetFlexListFromCodesAction implements Action<GetFlexListFromCodesResult> 
{
  protected int                      _iUserId ;
  
	protected ArrayList<SearchElement> _aElements = new ArrayList<SearchElement>() ;

	public GetFlexListFromCodesAction(final int iUserId, final ArrayList<SearchElement> aElements) 
	{
		super() ;
		
		_iUserId = iUserId ;
		
		if (null == aElements)
		  _aElements.addAll(aElements) ;
	}
	
	public int getUserId() {
		return _iUserId ;
	}
	
	public ArrayList<SearchElement> getElements() {
	  return _aElements ;
	}
}
