package org.quadrifolium.client.gin;

import net.customware.gwt.presenter.client.DefaultEventBus;
import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.gin.AbstractPresenterModule;

import org.quadrifolium.client.CachingDispatchAsync;
import org.quadrifolium.client.global.QuadrifoliumSupervisor;
import org.quadrifolium.client.mvp.QuadrifoliumAppPresenter;
import org.quadrifolium.client.mvp.QuadrifoliumCommandPanelPresenter;
import org.quadrifolium.client.mvp.QuadrifoliumCommandPanelView;
import org.quadrifolium.client.mvp.QuadrifoliumLoginHeaderPresenter;
import org.quadrifolium.client.mvp.QuadrifoliumLoginHeaderView;
import org.quadrifolium.client.mvp.QuadrifoliumLoginResponsePresenter;
import org.quadrifolium.client.mvp.QuadrifoliumLoginResponseView;
import org.quadrifolium.client.mvp.QuadrifoliumMainPresenter;
import org.quadrifolium.client.mvp.QuadrifoliumMainView;
import org.quadrifolium.client.mvp.QuadrifoliumPostLoginHeaderPresenter;
import org.quadrifolium.client.mvp.QuadrifoliumPostLoginHeaderView;
import org.quadrifolium.client.mvp.QuadrifoliumRegisterPresenter;
import org.quadrifolium.client.mvp.QuadrifoliumRegisterView;
import org.quadrifolium.client.mvp.QuadrifoliumWelcomePagePresenter;
import org.quadrifolium.client.mvp.QuadrifoliumWelcomePageView;
import org.quadrifolium.client.mvp.QuadrifoliumWelcomeTextPresenter;
import org.quadrifolium.client.mvp.QuadrifoliumWelcomeTextView;
import org.quadrifolium.client.mvp.QuadrifoliumWorkshopPresenter;
import org.quadrifolium.client.mvp.QuadrifoliumWorkshopView;
import org.quadrifolium.client.mvp_components.QuadrifoliumDefinitionsPresenter;
import org.quadrifolium.client.mvp_components.QuadrifoliumDefinitionsView;
import org.quadrifolium.client.mvp_components.QuadrifoliumLemmasPresenter;
import org.quadrifolium.client.mvp_components.QuadrifoliumLemmasView;
import org.quadrifolium.client.mvp_components.QuadrifoliumSemanticsPresenter;
import org.quadrifolium.client.mvp_components.QuadrifoliumSemanticsView;
import org.quadrifolium.client.widgets.FlexTextBox;

import com.google.inject.Singleton;

public class QuadrifoliumClientModule extends AbstractPresenterModule 
{
	@Override
	protected void configure() 
	{		
		bind(EventBus.class).to(DefaultEventBus.class).in(Singleton.class) ;
		
		bindPresenter(QuadrifoliumMainPresenter.class,            QuadrifoliumMainPresenter.Display.class,            QuadrifoliumMainView.class) ;		
		bindPresenter(QuadrifoliumWelcomeTextPresenter.class,     QuadrifoliumWelcomeTextPresenter.Display.class,     QuadrifoliumWelcomeTextView.class) ;
		bindPresenter(QuadrifoliumWelcomePagePresenter.class,     QuadrifoliumWelcomePagePresenter.Display.class,     QuadrifoliumWelcomePageView.class) ;
		bindPresenter(QuadrifoliumLoginHeaderPresenter.class,     QuadrifoliumLoginHeaderPresenter.Display.class,     QuadrifoliumLoginHeaderView.class) ;
		bindPresenter(QuadrifoliumCommandPanelPresenter.class,    QuadrifoliumCommandPanelPresenter.Display.class,    QuadrifoliumCommandPanelView.class) ;
		bindPresenter(QuadrifoliumPostLoginHeaderPresenter.class, QuadrifoliumPostLoginHeaderPresenter.Display.class, QuadrifoliumPostLoginHeaderView.class) ;
		bindPresenter(QuadrifoliumLoginResponsePresenter.class,   QuadrifoliumLoginResponsePresenter.Display.class,   QuadrifoliumLoginResponseView.class) ;
		bindPresenter(QuadrifoliumRegisterPresenter.class,        QuadrifoliumRegisterPresenter.Display.class,        QuadrifoliumRegisterView.class) ;
		
		bindPresenter(QuadrifoliumWorkshopPresenter.class,        QuadrifoliumWorkshopPresenter.Display.class,        QuadrifoliumWorkshopView.class) ;
		bindPresenter(QuadrifoliumLemmasPresenter.class,          QuadrifoliumLemmasPresenter.Display.class,          QuadrifoliumLemmasView.class) ;
		bindPresenter(QuadrifoliumSemanticsPresenter.class,       QuadrifoliumSemanticsPresenter.Display.class,       QuadrifoliumSemanticsView.class) ;
		bindPresenter(QuadrifoliumDefinitionsPresenter.class,     QuadrifoliumDefinitionsPresenter.Display.class,     QuadrifoliumDefinitionsView.class) ;
		
		bind(QuadrifoliumAppPresenter.class).in(Singleton.class) ;
		bind(QuadrifoliumSupervisor.class).in(Singleton.class) ;
		bind(FlexTextBox.class) ;
		bind(CachingDispatchAsync.class) ;		
	}
}
