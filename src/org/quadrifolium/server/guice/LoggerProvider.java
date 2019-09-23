package org.quadrifolium.server.guice;

import org.quadrifolium.server.DbParameters;
import org.quadrifolium.server.Logger;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class LoggerProvider implements Provider<Logger>
{
	private DbParameters _dbparameters ;
	
	@Inject
	public LoggerProvider(DbParameters dbparameters)
	{
		_dbparameters = dbparameters ;
	}
	
	@Override
	public Logger get()
	{
		return new Logger(_dbparameters) ;
	}
}
