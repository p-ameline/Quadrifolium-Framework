package org.quadrifolium.shared.rpc4ontology;

import net.customware.gwt.dispatch.shared.Action;

/**
 * Object used to get a list of Flex records from a text 
 */
public class GetFlexListAction implements Action<GetFlexListResult> 
{
  private int    _iUserId ;
  private String _sUserLdvId ;
  
	private String _sLanguage ;
	private String _sStartingText ;
	private int    _iBoxIndex ;

	public GetFlexListAction(final int iUserId, final String sLanguage, final String sStartingText, final int iBoxIndex) 
	{
		super() ;
		
		_iUserId       = iUserId ;
		_sUserLdvId    = "" ;
		
		_sLanguage     = sLanguage ;
		_sStartingText = sStartingText ;
		_iBoxIndex     = iBoxIndex ;
	}

	public GetFlexListAction(final String sUserLdvId, final String sLanguage, final String sStartingText, final int iBoxIndex) 
  {
    super() ;
    
    _iUserId       = -1 ;
    _sUserLdvId    = sUserLdvId ;
    
    _sLanguage     = sLanguage ;
    _sStartingText = sStartingText ;
    _iBoxIndex     = iBoxIndex ;
  }
	
	@SuppressWarnings("unused")
	public GetFlexListAction() 
	{
		super() ;
		
		_iUserId       = -1 ;
		_sUserLdvId    = "" ;
		
		_sLanguage     = "" ;
		_sStartingText = "" ;
		_iBoxIndex     = -1 ;
	}

	public String getLanguage() {
		return _sLanguage ;
	}

	public String getStartingText() {
		return _sStartingText ;
	}
	
	public int getUserId() {
		return _iUserId ;
	}
	
	public String getUserLdvId() {
    return _sUserLdvId ;
  }
	
	public int getBoxIndex() {
		return _iBoxIndex ;
	}
}
