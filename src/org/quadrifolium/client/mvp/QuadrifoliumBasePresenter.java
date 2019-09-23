package org.quadrifolium.client.mvp;

import org.quadrifolium.client.global.QuadrifoliumSupervisor;

import com.google.inject.Inject;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.widget.WidgetPresenter;

public abstract class QuadrifoliumBasePresenter<D extends WorkshopInterfaceModel> extends WidgetPresenter<D> 
{
	protected final DispatchAsync          _dispatcher ;
	protected final QuadrifoliumSupervisor _supervisor ;
	
	@Inject
	public QuadrifoliumBasePresenter(D                            display, 
							                     EventBus                     eventBus,
							                     final DispatchAsync          dispatcher,
							                     final QuadrifoliumSupervisor supervisor) 
	{
		super(display, eventBus) ;
		
		_dispatcher = dispatcher ;
		_supervisor = supervisor ;
	}
	
	@Override
	protected void onBind()
	{
	}
			
	@Override
	protected void onUnbind() {
	}

	@Override
	public void revealDisplay() {
	}
}
