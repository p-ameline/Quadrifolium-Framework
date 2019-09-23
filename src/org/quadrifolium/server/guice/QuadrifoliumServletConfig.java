package org.quadrifolium.server.guice;

import org.quadrifolium.server.DbParameters;
import org.quadrifolium.server.Logger;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

/**
 * This class starts the quadrifolium server
 * 
 * @author Philippe
 *
 */
public class QuadrifoliumServletConfig extends GuiceServletContextListener 
{
	@SuppressWarnings("unused")
	private Logger       _loggerToInstantiate ; 
	private DbParameters _parametersToInstantiate ;
	
	@Override
	protected Injector getInjector() 
	{
		// Creating the "injector", the class that creates other classes instances 
		//
		Injector quadrifoliumInjector = Guice.createInjector(new QuadrifoliumServerModule(), new DispatchServletModule()) ;
		
		// The parameters class, a singleton, must be created first, even if not used immediately
		//
		DbParameters dbParameters = quadrifoliumInjector.getInstance(DbParameters.class) ;
		
		// Trace to give the good news
		//
		LoggerProvider loggerProvider = new LoggerProvider(_parametersToInstantiate) ;
		_loggerToInstantiate = loggerProvider.get() ;
		Logger.trace("Listener started for Quadrifolium for version " + DbParameters.getVersion(), -1, Logger.TraceLevel.STEP) ;
		
		return quadrifoliumInjector ;
	}
}