package org.quadrifolium.server.ontology;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.quadrifolium.server.ontology_base.ChangeHistory;

import com.ldv.server.DBConnector;
import com.ldv.server.Logger;
import com.ldv.shared.model.LdvTime;

/** 
 * Object in charge of Read/Write operations for the <code>changeHistory</code> table 
 *   
 */
public class ChangeHistoryManager  
{	
	protected final DBConnector _dbConnector ;
	protected final int         _iUserId ;
	
	/**
	 * Constructor 
	 */
	public ChangeHistoryManager(final int iUserId, final DBConnector dbConnector)
	{
		_dbConnector = dbConnector ;
		_iUserId     = iUserId ;
	}

	/**
	  * Insert a ChangeHistory object in database
	  * 
	  * @param dataToInsert ChangeHistory to be inserted
	  *
	  * @return <code>true</code> if successful, <code>false</code> if not
	  */
	public boolean insertData(final ChangeHistory dataToInsert)
	{
		String sFctName = "ChangeHistoryManager.insertData" ;
		
		if ((null == _dbConnector) || (null == dataToInsert))
		{
			Logger.trace(sFctName + ": invalid parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		String sQuery = "INSERT INTO changeHistory (sessionId, datetime, histoTable, modifType, elementId, historyId)" + 
		                                           " VALUES (?, ?, ?, ?, ?, ?)" ;
		
		_dbConnector.prepareStatememt(sQuery, Statement.RETURN_GENERATED_KEYS) ;
		if (null == _dbConnector.getPreparedStatement())
		{
			Logger.trace(sFctName + ": cannot get Statement", _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closeAll() ;
			return false ;
		}
		
		// If no Date/Time, pick current one
		//
		String sDateTime = "" ;
		if ("".equals(dataToInsert.getDateTime()))
		{
			LdvTime time = new LdvTime(0) ;
			time.takeTime() ;
			sDateTime = time.getLocalDateTime() ;
		}
		else
			sDateTime = dataToInsert.getDateTime() ;
		
		_dbConnector.setStatememtInt(1, dataToInsert.getSessionId()) ;
		_dbConnector.setStatememtString(2, sDateTime) ;
		_dbConnector.setStatememtString(3, dataToInsert.getTableString()) ;
		_dbConnector.setStatememtString(4, dataToInsert.getTypeString()) ;
		_dbConnector.setStatememtInt(5, dataToInsert.getElementId()) ;
		_dbConnector.setStatememtInt(6, dataToInsert.getHistoryId()) ;
		
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
			if (false == rs.next())
				Logger.trace(sFctName + ": cannot get row after query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
    } 
		catch (SQLException e)
    {
			Logger.trace(sFctName + ": exception when iterating results " + e.getMessage(), _iUserId, Logger.TraceLevel.ERROR) ;
    }
		
		_dbConnector.closeResultSet() ;
		_dbConnector.closePreparedStatement() ;
		
		Logger.trace(sFctName +  ": user " + _iUserId + " successfuly recorded ChangeHistory " + dataToInsert.getId(), _iUserId, Logger.TraceLevel.STEP) ;
		
		return true ;
	}
	
	/**
	  * Update a ChangeHistory in database
	  * 
	  * @return true if successful, false if not
	  * 
	  * @param dataToUpdate ChangeHistory to be updated
	  */
	public boolean updateData(ChangeHistory dataToUpdate)
	{
		if ((null == _dbConnector) || (null == dataToUpdate))
		{
			Logger.trace("LemmaManager.updateData: bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		ChangeHistory foundData = new ChangeHistory() ;
		if (false == existData(dataToUpdate.getId(), foundData))
			return false ;
		
		if (foundData.equals(dataToUpdate))
		{
			Logger.trace("ChangeHistoryManager.updateData: ChangeHistory to update (ID " + dataToUpdate.getId() + ") unchanged; nothing to do", _iUserId, Logger.TraceLevel.SUBSTEP) ;
			return true ;
		}
		
		return forceUpdateData(dataToUpdate) ;
	}
		
	/**
	  * Check if there is any ChangeHistory with this code in database and, if true get its content
	  * 
	  * @return True if found, else false
	  * 
	  * @param iId       ID of ChangeHistory to look for
	  * @param foundData ChangeHistory to store existing information to
	  */
	public boolean existData(final int iId, final ChangeHistory foundData)
	{
		String sFctName = "ChangeHistoryManager.existData" ;
		
		if ((null == _dbConnector) || (-1 == iId) || (null == foundData))
		{
			Logger.trace(sFctName + ": bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		String sQuery = "SELECT * FROM changeHistory WHERE id = ?" ;
		
		_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
		_dbConnector.setStatememtInt(1, iId) ;
	   		
		if (false == _dbConnector.executePreparedStatement())
		{
			Logger.trace(sFctName + ": failed query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
	   		
		ResultSet rs = _dbConnector.getResultSet() ;
		if (null == rs)
		{
			Logger.trace(sFctName + ": no ChangeHistory found for ID = " + iId, _iUserId, Logger.TraceLevel.WARNING) ;
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
	  * Update a ChangeHistory in database
	  * 
	  * @return <code>true</code> if creation succeeded, <code>false</code> if not
	  * 
	  * @param  dataToUpdate ChangeHistory to update
	  */
	private boolean forceUpdateData(final ChangeHistory dataToUpdate)
	{
		String sFctName = "ChangeHistoryManager.forceUpdateData" ;
		
		if ((null == _dbConnector) || (null == dataToUpdate))
		{
			Logger.trace(sFctName + ": bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		// Prepare SQL query
		//
		String sQuery = "UPDATE changeHistory SET sessionId = ?, datetime = ?, `table` = ?, `type` = ?, elementId = ?, historyId = ?" +
				                          " WHERE id = ?" ; 
		
		_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
		if (null == _dbConnector.getPreparedStatement())
		{
			Logger.trace(sFctName + ": cannot get Statement", _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
		
		_dbConnector.setStatememtInt(1, dataToUpdate.getSessionId()) ;
		_dbConnector.setStatememtString(2, dataToUpdate.getDateTime()) ;
		_dbConnector.setStatememtString(3, dataToUpdate.getTableString()) ;
		_dbConnector.setStatememtString(4, dataToUpdate.getTypeString()) ;
		_dbConnector.setStatememtInt(5, dataToUpdate.getElementId()) ;
		_dbConnector.setStatememtInt(6, dataToUpdate.getHistoryId()) ;
		
		_dbConnector.setStatememtInt(7, dataToUpdate.getId()) ;
				
		// Execute query 
		//
		int iNbAffectedRows = _dbConnector.executeUpdatePreparedStatement(false) ;
		if (-1 == iNbAffectedRows)
		{
			Logger.trace(sFctName + ": failed query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}

		Logger.trace(sFctName + ": updated data for ChangeHistory " + dataToUpdate.getId(), _iUserId, Logger.TraceLevel.SUBSTEP) ;
		
		_dbConnector.closePreparedStatement() ;
		
		return true ;
	}
	
	/**
	  * Initialize a ChangeHistory from a query ResultSet 
	  * 
	  * @param rs        ResultSet of a query
	  * @param foundData ChangeHistory to fill
	  * 
	  */
	public static void fillDataFromResultSet(final ResultSet rs, ChangeHistory foundData, final int iUserId)
	{
		if ((null == rs) || (null == foundData))
			return ;
		
		try
		{
			foundData.setId(rs.getInt("id")) ;
			foundData.setSessionId(rs.getInt("sessionId")) ;
			foundData.setDateTime(rs.getString("datetime")) ;
    	foundData.setTableString(rs.getString("histoTable")) ;
    	foundData.setTypeString(rs.getString("modifType")) ;
    	foundData.setElementId(rs.getInt("elementId")) ;
    	foundData.setHistoryId(rs.getInt("historyId")) ;
		} 
		catch (SQLException e) {
			Logger.trace("ChangeHistoryManager.fillDataFromResultSet: exception when processing results set: " + e.getMessage(), iUserId, Logger.TraceLevel.ERROR) ;
		}
	}		
}
