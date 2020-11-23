package org.quadrifolium.server.guice;

import com.ldv.server.DbParametersModel;
import com.ldv.server.Logger;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class LoggerProvider implements Provider<Logger>
{
	private DbParametersModel _dbparameters ;
	
	@Inject
	public LoggerProvider(DbParametersModel dbparameters)
	{
		_dbparameters = dbparameters ;
	}
	
	@Override
	public Logger get()
	{
		return new Logger() ;
	}
}
