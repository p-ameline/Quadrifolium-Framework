package org.quadrifolium.client.mvp;

import net.customware.gwt.dispatch.client.DispatchAsync;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;

public class QuadrifoliumAppPresenter 
{
	private HasWidgets                _container ;
	private QuadrifoliumMainPresenter _mainPresenter ;
	
	@Inject
	public QuadrifoliumAppPresenter(final DispatchAsync dispatcher, final QuadrifoliumMainPresenter mainPresenter) 
	{
		_mainPresenter = mainPresenter ;
	}
	
	private void showMain() 
	{
		// The MainPresenter's natural behavior is to display the welcome text page 
		//
		_container.clear() ;
		_container.add(_mainPresenter.getDisplay().asWidget()) ;
	}
		
	public void go(final HasWidgets container, String sConcept, String sLocale) 
	{
		_container = container ;	
		
		if ((null != sConcept) && (false == "".equals(sConcept)))
			_mainPresenter.setTargetConcept(sConcept) ;
				
		if ((null != sLocale) && (false == "".equals(sLocale)))
			_mainPresenter.setTargetLanguage(sLocale) ;
		
		showMain() ;		
	}
}
