package org.quadrifolium.shared.rpc4ontology;

import net.customware.gwt.dispatch.shared.Action;

/**
 * Object used to get a list of Flex records from a text 
 */
public class GetLanguageTagsAction implements Action<GetLanguageTagsResult> 
{
  private int _iUserId ;

	public GetLanguageTagsAction(final int iUserId) 
	{
		super() ;
		
		_iUserId = iUserId ;
	}

	@SuppressWarnings("unused")
	public GetLanguageTagsAction() 
	{
		super() ;
		
		_iUserId = -1 ;
	}

	public int getUserId() {
		return _iUserId ;
	}
}
