package org.quadrifolium.client.event;

import org.quadrifolium.client.mvp.QuadrifoliumWorkshopPresenterModel;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Message sent to workshop components when the concept at work changed
 * 
 * @author Philippe
 *
 */
public class WorkshopConceptChangedEvent extends GwtEvent<WorkshopConceptChangedEventHandler>
{	
	public static Type<WorkshopConceptChangedEventHandler> TYPE = new Type<WorkshopConceptChangedEventHandler>() ;
	
	private QuadrifoliumWorkshopPresenterModel _targetWorkshop ;
	
	public static Type<WorkshopConceptChangedEventHandler> getType() 
	{
		if (null == TYPE)
			TYPE = new Type<WorkshopConceptChangedEventHandler>() ;
		return TYPE ;
	}
	
	public WorkshopConceptChangedEvent(final QuadrifoliumWorkshopPresenterModel targetWorkshop) {	
		_targetWorkshop = targetWorkshop ;
	}
	
	public QuadrifoliumWorkshopPresenterModel getTargetWorkshop(){
		return _targetWorkshop ;
	}
		
	@Override
	protected void dispatch(WorkshopConceptChangedEventHandler handler) {
		handler.onWorkshopConceptChanged(this) ;
	}

	@Override
	public Type<WorkshopConceptChangedEventHandler> getAssociatedType() {
		return TYPE;
	}
}
