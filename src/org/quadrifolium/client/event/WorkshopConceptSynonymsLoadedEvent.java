package org.quadrifolium.client.event;

import org.quadrifolium.client.mvp.QuadrifoliumWorkshopPresenterModel;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Message sent to other workshop components when the linguistic components has loaded the synonyms
 * 
 * @author Philippe
 *
 */
public class WorkshopConceptSynonymsLoadedEvent extends GwtEvent<WorkshopConceptSynonymsLoadedEventHandler>
{	
	public static Type<WorkshopConceptSynonymsLoadedEventHandler> TYPE = new Type<WorkshopConceptSynonymsLoadedEventHandler>() ;
	
	private QuadrifoliumWorkshopPresenterModel _targetWorkshop ;
	
	public static Type<WorkshopConceptSynonymsLoadedEventHandler> getType() 
	{
		if (null == TYPE)
			TYPE = new Type<WorkshopConceptSynonymsLoadedEventHandler>() ;
		return TYPE ;
	}
	
	public WorkshopConceptSynonymsLoadedEvent(final QuadrifoliumWorkshopPresenterModel targetWorkshop) {	
		_targetWorkshop = targetWorkshop ;
	}
	
	public QuadrifoliumWorkshopPresenterModel getTargetWorkshop(){
		return _targetWorkshop ;
	}
		
	@Override
	protected void dispatch(WorkshopConceptSynonymsLoadedEventHandler handler) {
		handler.onWorkshopConceptSynonymsLoaded(this) ;
	}

	@Override
	public Type<WorkshopConceptSynonymsLoadedEventHandler> getAssociatedType() {
		return TYPE;
	}
}
