package org.quadrifolium.client.mvp;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.widget.WidgetDisplay;
import net.customware.gwt.presenter.client.widget.WidgetPresenter;

import org.quadrifolium.client.event.GoToAtelierPhpEvent;
import org.quadrifolium.client.event.PostLoginHeaderDisplayEvent;
import org.quadrifolium.client.event.PostLoginHeaderDisplayEventHandler;
import org.quadrifolium.client.event.PostLoginHeaderEvent;
import org.quadrifolium.client.event.PostLoginHeaderEventHandler;
import org.quadrifolium.client.global.QuadrifoliumSupervisor;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.inject.Inject;

public class QuadrifoliumPostLoginHeaderPresenter extends WidgetPresenter<QuadrifoliumPostLoginHeaderPresenter.Display> 
{	
	public interface Display extends WidgetDisplay 
	{	
		public FlowPanel getPanel() ;
		
		public void      setWelcomeText(String sPseudo) ;
		public void      setText(String sText) ;
		
		public HasClickHandlers getOpenAtelierPhpButton() ;
	}

	private final QuadrifoliumSupervisor _supervisor ;
	
	@Inject
	public QuadrifoliumPostLoginHeaderPresenter(final Display                display, 
						                                  final EventBus               eventBus,
						                                  final DispatchAsync          dispatcher,
						                                  final QuadrifoliumSupervisor supervisor) 
	{
		super(display, eventBus) ;
		
		_supervisor = supervisor ;
		
		display.setWelcomeText(_supervisor.getUserPseudo()) ;
		
		bind() ;
	}
	
	@Override
	protected void onBind() 
	{
		eventBus.addHandler(PostLoginHeaderEvent.TYPE, new PostLoginHeaderEventHandler(){
			public void onPostLoginHeader(final PostLoginHeaderEvent event) 
			{
				FlowPanel workSpace = event.getHeader() ;
				workSpace.add(getDisplay().asWidget()) ;
				display.setWelcomeText(_supervisor.getUserPseudo()) ;
			}
		});
		
		eventBus.addHandler(PostLoginHeaderDisplayEvent.TYPE, new PostLoginHeaderDisplayEventHandler(){
			public void onPostLoginHeaderDisplay(final PostLoginHeaderDisplayEvent event) 
			{
				String sTextToDisplay = event.getDisplayedText() ;
				
				if ("".equals(sTextToDisplay))				
					display.setWelcomeText(_supervisor.getUserPseudo()) ;
				else
					display.setText(sTextToDisplay) ;
			}
		});
		
		// Click to establish just entered concept as current concept
		//
		display.getOpenAtelierPhpButton().addClickHandler(new ClickHandler(){
			@Override
			public void onClick(final ClickEvent event)
			{
				eventBus.fireEvent(new GoToAtelierPhpEvent()) ;
			}
		});
	}

	@Override
  protected void onUnbind() {
	  // TODO Auto-generated method stub
  }

	@Override
  public void revealDisplay() {
	  // TODO Auto-generated method stub
  }

	@Override
	protected void onRevealDisplay() {
		// TODO Auto-generated method stub
	}	
}
