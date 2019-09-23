package org.quadrifolium.client.mvp;

import org.quadrifolium.client.event.LoginSuccessEvent;
import org.quadrifolium.client.event.LoginSuccessEventHandler;
import org.quadrifolium.client.event.PostLoginHeaderDisplayEvent;
import org.quadrifolium.client.global.QuadrifoliumSupervisor;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.inject.Inject;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.widget.WidgetDisplay;
import net.customware.gwt.presenter.client.widget.WidgetPresenter;

public class QuadrifoliumLoginResponsePresenter extends WidgetPresenter<QuadrifoliumLoginResponsePresenter.Display>
{	
	private final DispatchAsync          _dispatcher ;
	private final QuadrifoliumSupervisor _supervisor ;
	
	public interface Display extends WidgetDisplay
	{
		FlowPanel        getWorkspace() ;
		
		void             popupWarningMessage(String sMessage) ;
		void             popupMessage(String sMessage) ;
		void             closeWarningDialog() ;
		HasClickHandlers getWarningOk() ;
		
		void             popupDeleteMessage() ;
		void             closeDeleteDialog() ;
		HasClickHandlers getDeleteOk() ;
		HasClickHandlers getDeleteCancel() ;
	}
	
	@Inject
	public QuadrifoliumLoginResponsePresenter(final Display display, 
                                            final EventBus eventBus,
                                            final DispatchAsync dispatcher,
                                            final QuadrifoliumSupervisor supervisor)
	{
		super(display, eventBus) ;
      
		_dispatcher = dispatcher ;
		_supervisor = supervisor ;
		
		bind() ;
	}
	
	@Override
	protected void onBind() 
	{		
		// Install Event Bus messages handlers
		//
		eventBus.addHandler(LoginSuccessEvent.TYPE, new LoginSuccessEventHandler() {
			public void onLoginSuccess(final LoginSuccessEvent event) 
			{
				FlowPanel workSpace = event.getWorkspace() ;
				workSpace.clear() ;
				workSpace.add(getDisplay().asWidget()) ;
				
				// eventBus.fireEvent(new HeaderButtonsEvent(false, true)) ;
				eventBus.fireEvent(new PostLoginHeaderDisplayEvent("")) ;
			}
		});
		
		/**
		 * Reacts to Ok button in warning dialog box
		 * */
		display.getWarningOk().addClickHandler(new ClickHandler(){
			public void onClick(final ClickEvent event)
			{
			  display.closeWarningDialog() ; 
			}
		});		
	}
	
	
	@Override
	protected void onUnbind() {
		// Add unbind functionality here for more complex presenters
	}

	@Override
	public void revealDisplay() {
		// nothing to do, there is more useful in UI which may be buried
		// in a tab bar, tree, etc.
	}

	@Override
	protected void onRevealDisplay() {
		// TODO Auto-generated method stub
	}
}
