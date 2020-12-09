package org.quadrifolium.shared.rpc4ontology;

import java.util.ArrayList;

import net.customware.gwt.dispatch.shared.Action;

/**
 * Object used to get a list of Flex records from a list of codes 
 */
public class GetFlexListFromCodesAction implements Action<GetFlexListFromCodesResult> 
{
  protected int                      _iUserId ;
  protected String                   _sUserLdvId ;
  
	protected ArrayList<SearchElement> _aElements = new ArrayList<SearchElement>() ;

	/**
	 * Plain vanilla constructor where user identifier is expressed as an <code>int</code>
	 */
	public GetFlexListFromCodesAction(final int iUserId, final ArrayList<SearchElement> aElements) 
	{
		super() ;
		
		reset() ;
		
		_iUserId = iUserId ;
		
		if (null != aElements)
		  _aElements.addAll(aElements) ;
	}
	
	/**
   * Plain vanilla constructor where user identifier is expressed as a <code>string</code>
   */
	public GetFlexListFromCodesAction(final String sUserLdvId, final ArrayList<SearchElement> aElements) 
  {
    super() ;
    
    reset() ;
    
    _sUserLdvId = sUserLdvId ;

    if (null != aElements)
      _aElements.addAll(aElements) ;
  }
	
	/**
   * Void constructor for serialization purposes
   */
	@SuppressWarnings("unused")
  public GetFlexListFromCodesAction() 
  {
    super() ;
    
    reset() ;
  }
	
	protected void reset()
	{
	  _iUserId       = -1 ;
    _sUserLdvId    = "" ;
    
    _aElements.clear() ;
	}
	
	public int getUserId() {
		return _iUserId ;
	}
	
	public String getUserLdvId() {
    return _sUserLdvId ;
  }
	
	public ArrayList<SearchElement> getElements() {
	  return _aElements ;
	}
}
