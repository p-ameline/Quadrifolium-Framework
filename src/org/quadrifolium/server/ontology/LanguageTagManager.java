package org.quadrifolium.server.ontology;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.quadrifolium.server.DBConnector;
import org.quadrifolium.server.Logger;
import org.quadrifolium.shared.ontology.LanguageTag;

/** 
 * Object in charge of Read/Write operations for the <code>languageTag</code> table 
 *   
 */
public class LanguageTagManager  
{	
	protected final DBConnector _dbConnector ;
	protected final int         _iUserId ;
	
	/**
	 * Constructor 
	 */
	public LanguageTagManager(final int iUserId, final DBConnector dbConnector)
	{
		_dbConnector = dbConnector ;
		_iUserId     = iUserId ;
	}

	/**
	  * Insert a LanguageTag object in database, and complete this object with insertion created information
	  * 
	  * @param dataToInsert LanguageTag to be inserted
	  *
	  * @return <code>true</code> if successful, <code>false</code> if not
	  */
	public boolean insertData(final LanguageTag dataToInsert)
	{
		String sFctName = "LanguageTagManager.insertData" ;
		
		if ((null == _dbConnector) || (null == dataToInsert))
		{
			Logger.trace(sFctName + ": invalid parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		String sQuery = "INSERT INTO languageTag (code, label) VALUES (?, ?)" ;
		
		_dbConnector.prepareStatememt(sQuery, Statement.RETURN_GENERATED_KEYS) ;
		if (null == _dbConnector.getPreparedStatement())
		{
			Logger.trace(sFctName + ": cannot get Statement", _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closeAll() ;
			return false ;
		}
		
		_dbConnector.setStatememtString(1, dataToInsert.getCode()) ;
		_dbConnector.setStatememtString(2, dataToInsert.getLabel()) ;
		
		// Execute query 
		//
		int iNbAffectedRows = _dbConnector.executeUpdatePreparedStatement(true) ;
		if (-1 == iNbAffectedRows)
		{
			Logger.trace(sFctName + ": failed query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closeAll() ;
			return false ;
		}
		
		ResultSet rs = _dbConnector.getResultSet() ;
		try
    {
			if (rs.next())
				dataToInsert.setId(rs.getInt(1)) ;
			else
				Logger.trace(sFctName + ": cannot get row after query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
    } 
		catch (SQLException e)
    {
			Logger.trace(sFctName + ": exception when iterating results " + e.getMessage(), _iUserId, Logger.TraceLevel.ERROR) ;
    }
		
		_dbConnector.closeResultSet() ;
		_dbConnector.closePreparedStatement() ;
		
		Logger.trace(sFctName +  ": user " + _iUserId + " successfuly recorded languageTag " + dataToInsert.getCode() + ":" + dataToInsert.getLabel(), _iUserId, Logger.TraceLevel.STEP) ;
		
		return true ;
	}
	
	/**
	  * Update a LanguageTag in database
	  * 
	  * @return true if successful, false if not
	  * 
	  * @param dataToUpdate LanguageTag to be updated
	  */
	public boolean updateData(LanguageTag dataToUpdate)
	{
		if ((null == _dbConnector) || (null == dataToUpdate))
		{
			Logger.trace("LanguageTagManager.updateData: bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		LanguageTag foundData = new LanguageTag() ;
		if (false == existData(dataToUpdate.getCode(), foundData))
			return false ;
		
		if (foundData.equals(dataToUpdate))
		{
			Logger.trace("LanguageTagManager.updateData: Trait to update (" + dataToUpdate.getCode() + " / " + dataToUpdate.getLabel() + ") unchanged; nothing to do", _iUserId, Logger.TraceLevel.SUBSTEP) ;
			return true ;
		}
		
		return forceUpdateData(dataToUpdate) ;
	}
		
	/**
	  * Check if there is any LanguageTag with this code in database and, if true get its content
	  * 
	  * @return True if found, else false
	  * 
	  * @param sCode     Code of LanguageTag to look for
	  * @param foundData LanguageTag to get existing information
	  */
	public boolean existData(final String sCode, final LanguageTag foundData)
	{
		String sFctName = "LanguageTagManager.existData" ;
		
		if ((null == _dbConnector) || (null == sCode) || (null == foundData))
		{
			Logger.trace(sFctName + ": bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		String sQuery = "SELECT * FROM languageTag WHERE code = ?" ;
		
		_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
		_dbConnector.setStatememtString(1, sCode) ;
	   		
		if (false == _dbConnector.executePreparedStatement())
		{
			Logger.trace(sFctName + ": failed query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
	   		
		ResultSet rs = _dbConnector.getResultSet() ;
		if (null == rs)
		{
			Logger.trace(sFctName + ": no LanguageTag found for code = " + sCode, _iUserId, Logger.TraceLevel.WARNING) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
		
		try
		{
	    if (rs.next())
	    {
	    	fillDataFromResultSet(rs, foundData, _iUserId) ;
	    	
	    	_dbConnector.closeResultSet() ;
	    	_dbConnector.closePreparedStatement() ;
	    	
	    	return true ;	    	
	    }
		} catch (SQLException e)
		{
			Logger.trace(sFctName + ": exception when iterating results " + e.getMessage(), _iUserId, Logger.TraceLevel.ERROR) ;
		}
		
		_dbConnector.closeResultSet() ;
		_dbConnector.closePreparedStatement() ;
				
		return false ;
	}
	
	/**
	  * Update a LanguageTag in database
	  * 
	  * @return <code>true</code> if creation succeeded, <code>false</code> if not
	  * 
	  * @param  dataToUpdate LanguageTag to update
	  */
	private boolean forceUpdateData(final LanguageTag dataToUpdate)
	{
		String sFctName = "LanguageTagManager.forceUpdateData" ;
		
		if ((null == _dbConnector) || (null == dataToUpdate))
		{
			Logger.trace(sFctName + ": bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		// Prepare SQL query
		//
		String sQuery = "UPDATE languageTag SET label = ?, code = ?" +
				                          " WHERE id = ?" ; 
		
		_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
		if (null == _dbConnector.getPreparedStatement())
		{
			Logger.trace(sFctName + ": cannot get Statement", _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
		
		_dbConnector.setStatememtString(1, dataToUpdate.getLabel()) ;
		_dbConnector.setStatememtString(2, dataToUpdate.getCode()) ;
		
		_dbConnector.setStatememtInt(3, dataToUpdate.getId()) ;
				
		// Execute query 
		//
		int iNbAffectedRows = _dbConnector.executeUpdatePreparedStatement(false) ;
		if (-1 == iNbAffectedRows)
		{
			Logger.trace(sFctName + ": failed query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}

		Logger.trace(sFctName + ": updated data for LanguageTag " + dataToUpdate.getCode(), _iUserId, Logger.TraceLevel.SUBSTEP) ;
		
		_dbConnector.closePreparedStatement() ;
		
		return true ;
	}
	
	/**
	  * Initialize a LanguageTag from a query ResultSet 
	  * 
	  * @param rs        ResultSet of a query
	  * @param foundData LanguageTag to fill
	  * 
	  */
	public static void fillDataFromResultSet(final ResultSet rs, LanguageTag foundData, final int iUserId)
	{
		if ((null == rs) || (null == foundData))
			return ;
		
		try
		{
			foundData.setId(rs.getInt("id")) ;
    	foundData.setCode(rs.getString("code")) ;
    	foundData.setLabel(rs.getString("label")) ;
		} 
		catch (SQLException e) {
			Logger.trace("LemmaManager.fillDataFromResultSet: exception when processing results set: " + e.getMessage(), iUserId, Logger.TraceLevel.ERROR) ;
		}
	}		
}
