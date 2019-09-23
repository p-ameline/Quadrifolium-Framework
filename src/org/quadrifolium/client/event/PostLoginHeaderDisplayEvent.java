package org.quadrifolium.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class PostLoginHeaderDisplayEvent extends GwtEvent<PostLoginHeaderDisplayEventHandler> 
{	
	public static Type<PostLoginHeaderDisplayEventHandler> TYPE = new Type<PostLoginHeaderDisplayEventHandler>();
	
	private String _sDisplayedText ;
	
	public static Type<PostLoginHeaderDisplayEventHandler> getType() 
	{
		if (null == TYPE)
			TYPE = new Type<PostLoginHeaderDisplayEventHandler>();
		return TYPE;
	}
	
	public PostLoginHeaderDisplayEvent(final String sDisplayedText) {
		_sDisplayedText = sDisplayedText ;
	}
	
	public String getDisplayedText() {
		return _sDisplayedText ;
	}
		
	@Override
	protected void dispatch(PostLoginHeaderDisplayEventHandler handler) {
		handler.onPostLoginHeaderDisplay(this) ;
	}

	@Override
	public Type<PostLoginHeaderDisplayEventHandler> getAssociatedType() {
		// TODO Auto-generated method stub
		return TYPE;
	}
}
