package org.quadrifolium.client.gin;

import net.customware.gwt.dispatch.client.gin.StandardDispatchModule;

import org.quadrifolium.client.global.QuadrifoliumSupervisor;
import org.quadrifolium.client.mvp.QuadrifoliumAppPresenter;
import org.quadrifolium.client.mvp.QuadrifoliumCommandPanelPresenter;
import org.quadrifolium.client.mvp.QuadrifoliumLoginHeaderPresenter;
import org.quadrifolium.client.mvp.QuadrifoliumPostLoginHeaderPresenter;
import org.quadrifolium.client.mvp.QuadrifoliumRegisterPresenter;
import org.quadrifolium.client.mvp.QuadrifoliumWelcomePagePresenter;
import org.quadrifolium.client.mvp.QuadrifoliumWelcomeTextPresenter;
import org.quadrifolium.client.mvp_components.QuadrifoliumDefinitionsPresenter;
import org.quadrifolium.client.mvp_components.QuadrifoliumLemmasPresenter;
import org.quadrifolium.client.mvp_components.QuadrifoliumSemanticsPresenter;
import org.quadrifolium.client.widgets.FlexTextBox;

import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

/**
 * The injector is in charge of delivering objects, either by creating them or, if singletons, by passing current instances 
 * 
 * @author Philippe
 *
 */
@GinModules({ StandardDispatchModule.class, QuadrifoliumClientModule.class })
public interface QuadrifoliumGinjector extends Ginjector 
{
	QuadrifoliumSupervisor               getSupervisor() ;

	QuadrifoliumAppPresenter             getAppPresenter() ;

	QuadrifoliumLoginHeaderPresenter     getLoginPresenter() ;	
	// QuadrifoliumLoginResponsePresenter   getLoginResponsePresenter() ;
	QuadrifoliumPostLoginHeaderPresenter getPostLoginHeaderPresenter() ;
	
	QuadrifoliumCommandPanelPresenter    getCommandPresenter() ;
	
	QuadrifoliumWelcomeTextPresenter     getWelcomeTextPresenter() ;
	QuadrifoliumWelcomePagePresenter     getWelcomePagePresenter() ;
	// QuadrifoliumWorkshopPresenter        getWorkshopPresenter() ;
	
	QuadrifoliumLemmasPresenter          getLemmasPresenter() ;
	QuadrifoliumSemanticsPresenter       getSemanticsPresenter() ;
	QuadrifoliumDefinitionsPresenter     getDefinitionsPresenter() ;
	
	QuadrifoliumRegisterPresenter        getRegisterPresenter() ;
	
	FlexTextBox                          getFlexTextBox() ;
}
