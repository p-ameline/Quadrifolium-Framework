package org.quadrifolium.server.ontology;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.quadrifolium.server.ontology_base.OntologySavoir;

import com.ldv.server.DBConnector;
import com.ldv.server.Logger;

/** 
 * Object in charge of Read/Write operations in the <code>lexique</code> 
 *   
 */
public class SavoirManager  
{	
	protected final DBConnector _dbConnector ;
	protected final int         _iUserId ;
	
	/**
	 * Constructor 
	 */
	public SavoirManager(final int iUserId, final DBConnector dbConnector)
	{
		_dbConnector = dbConnector ;
		_iUserId     = iUserId ;
	}
	
	/**
	  * Initialize a Savoir from a query ResultSet 
	  * 
	  * @param rs        ResultSet of a query
	  * @param foundData Savoir to fill
	  * 
	  */
	public static void fillDataFromResultSet(final ResultSet rs, OntologySavoir foundData, final int iUserId)
	{
		if ((null == rs) || (null == foundData))
			return ;
		
		try
		{
			foundData.setId(rs.getInt("id")) ;
    	foundData.setCode(rs.getString("code")) ;
    	foundData.setQualifie(rs.getString("qualifie")) ;
    	foundData.setLien(rs.getString("lien")) ;
    	foundData.setQualifiant(rs.getString("qualifiant")) ;
		} 
		catch (SQLException e) {
			Logger.trace("SavoirManager.fillDataFromResultSet: exception when processing results set: " + e.getMessage(), iUserId, Logger.TraceLevel.ERROR) ;
		}
	}		
}
