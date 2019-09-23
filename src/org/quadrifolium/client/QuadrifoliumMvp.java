package org.quadrifolium.client;

import org.quadrifolium.client.gin.QuadrifoliumGinjector;
import org.quadrifolium.client.global.QuadrifoliumSupervisor;
import org.quadrifolium.client.mvp.QuadrifoliumAppPresenter;
import org.quadrifolium.client.ui.QuadrifoliumResources;

import com.allen_sauer.gwt.log.client.Log;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

public class QuadrifoliumMvp implements EntryPoint 
{
	private final QuadrifoliumGinjector injector = GWT.create(QuadrifoliumGinjector.class);
	
	public void onModuleLoad() 
	{
		Log.info("onModuleLoad") ;
		
		String sLocale  = Window.Location.getParameter("locale") ;
	  String sConcept = Window.Location.getParameter("concept") ;
	  if (null != sConcept)
	  	Log.info("concept parameter detected") ;
		
		QuadrifoliumResources.INSTANCE.css().ensureInjected() ;
		
		final QuadrifoliumSupervisor supervisor = injector.getSupervisor() ;
		
		supervisor.setInjector(injector) ;
		final QuadrifoliumAppPresenter appPresenter = injector.getAppPresenter() ;
		
		appPresenter.go(RootPanel.get(), sConcept, sLocale) ;
	}
}
