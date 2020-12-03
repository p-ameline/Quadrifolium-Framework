package org.quadrifolium.server;

import com.ldv.server.DbParametersModel;

/**
 * Class that hosts all the server related parameters (directories, etc)
 * 
 * @author Philippe
 *
 */
public class DbParameters extends DbParametersModel
{	
	protected static String _sFilesDir ;
	
	protected static String _sVersion ;
	
	public DbParameters(final String sBase,
			                final String sBaseOntology,
			                final String sUser,
			                final String sPass,
			                final String sIP,
			                final String sPort,
			                final String sTraceFile,
			                final String sFilesDir,
			                final String sVersion,
			                final String sDirSeparator)
	{
	  super(sBase, sBaseOntology, sUser, sPass, sIP, sPort, sTraceFile, sDirSeparator) ;
	  
		_sFilesDir     = sFilesDir ;
		_sVersion      = sVersion ;
	}	
	
	public static String getFilesDir() {
		return _sFilesDir ;
	}
	
	public static String getVersion() {
		return _sVersion ;
	}	
}
