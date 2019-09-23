package org.quadrifolium.server;

/**
 * Class that hosts all the server related parameters (directories, etc)
 * 
 * @author Philippe
 *
 */
public class DbParameters 
{	
	// Server
	//
	protected static String _sUser ;
	protected static String _sPass ;
	protected static String _sIP ;
	protected static String _sPort ;
	protected static String _sTrace ;
	
	//Database
	//
	protected static String _sBase ;
	protected static String _sBaseOntology ;
	
	protected static String _sFilesDir ;
	protected static String _sDirSeparator ;
	
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
		_sBase         = sBase ;
		_sBaseOntology = sBaseOntology ;
		
		_sUser         = sUser ;
		_sPass         = sPass ;
		_sIP           = sIP ;
		_sPort         = sPort ;
		_sTrace        = sTraceFile ;
		
		_sFilesDir     = sFilesDir ;
		_sVersion      = sVersion ;
		_sDirSeparator = sDirSeparator ;
	}	
	
	public static String getBase() {
		return _sBase ;
	}
	
	public static String getBaseOntology() {
		return _sBaseOntology ;
	}
	
	public static String getUser() {
		return _sUser ;
	}
	
	public static String getPass() {
		return _sPass ;
	}
	
	public static String getIP() {
		return _sIP ;
	}
	
	public static String getPort() {
		return _sPort ;
	}
	
	public static String getTrace() {
		return _sTrace ;
	}
	
	public static String getFilesDir() {
		return _sFilesDir ;
	}
	
	public static String getVersion() {
		return _sVersion ;
	}
	
	public static String getDirSeparator() {
		return _sDirSeparator ;
	}
}
