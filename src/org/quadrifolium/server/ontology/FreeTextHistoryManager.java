package org.quadrifolium.server.ontology;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.quadrifolium.server.DBConnector;
import org.quadrifolium.server.Logger;
import org.quadrifolium.server.ontology_base.FreeTextModelHistory;

/** 
 * Object in charge of Read/Write operations for the <code>historyFreeText</code> table 
 *   
 */
public class FreeTextHistoryManager  
{	
	protected final DBConnector _dbConnector ;
	protected final int         _iUserId ;
	
	/**
	 * Constructor 
	 */
	public FreeTextHistoryManager(final int iUserId, final DBConnector dbConnector)
	{
		_dbConnector = dbConnector ;
		_iUserId     = iUserId ;
	}

	/**
	  * Insert a FreeTextModel object in database, and complete this object with insertion created information
	  * 
	  * @param dataToInsert FreeTextModelHistory to be inserted
	  *
	  * @return <code>true</code> if successful, <code>false</code> if not
	  */
	public boolean insertData(final FreeTextModelHistory dataToInsert)
	{
		String sFctName = "FreeTextHistoryManager.insertData" ;
		
		if ((null == _dbConnector) || (null == dataToInsert))
		{
			Logger.trace(sFctName + ": invalid parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		String sQuery = "INSERT INTO historyFreeText (freeTextId, label, code, lang, next) VALUES (?, ?, ?, ?, ?)" ;
		
		_dbConnector.prepareStatememt(sQuery, Statement.RETURN_GENERATED_KEYS) ;
		if (null == _dbConnector.getPreparedStatement())
		{
			Logger.trace(sFctName + ": cannot get Statement", _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closeAll() ;
			return false ;
		}
		
		_dbConnector.setStatememtInt(1, dataToInsert.getFreeTextId()) ;
		
		_dbConnector.setStatememtString(2, dataToInsert.getLabel()) ;
		_dbConnector.setStatememtString(3, dataToInsert.getCode()) ;
		_dbConnector.setStatememtString(4, dataToInsert.getLanguage()) ;
		_dbConnector.setStatememtString(5, dataToInsert.getNext()) ;
		
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
		
		Logger.trace(sFctName +  ": user " + _iUserId + " successfuly recorded historyFreeText " + dataToInsert.getCode() + ":" + dataToInsert.getLabel(), _iUserId, Logger.TraceLevel.STEP) ;
		
		return true ;
	}
	
	/**
	  * Update a FreeTextModel in database
	  * 
	  * @return true if successful, false if not
	  * 
	  * @param dataToUpdate FreeTextModelHistory to be updated
	  */
	public boolean updateData(FreeTextModelHistory dataToUpdate)
	{
		if ((null == _dbConnector) || (null == dataToUpdate))
		{
			Logger.trace("FreeTextHistoryManager.updateData: bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		FreeTextModelHistory foundData = new FreeTextModelHistory() ;
		if (false == existData(dataToUpdate.getCode(), foundData))
			return false ;
		
		if (foundData.equals(dataToUpdate))
		{
			Logger.trace("FreeTextHistoryManager.updateData: Trait to update (" + dataToUpdate.getCode() + " / " + dataToUpdate.getLabel() + ") unchanged; nothing to do", _iUserId, Logger.TraceLevel.SUBSTEP) ;
			return true ;
		}
		
		return forceUpdateData(dataToUpdate) ;
	}
		
	/**
	  * Check if there is any FreeTextModel with this code in database and, if true get its content
	  * 
	  * @return True if found, else false
	  * 
	  * @param sCode     Code of Lexicon to check
	  * @param foundData FreeTextModelHistory to get existing information
	  */
	public boolean existData(final String sCode, final FreeTextModelHistory foundData)
	{
		String sFctName = "FreeTextHistoryManager.existData" ;
		
		if ((null == _dbConnector) || (null == sCode) || (null == foundData))
		{
			Logger.trace(sFctName + ": bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		String sQuery = "SELECT * FROM historyFreeText WHERE code = ?" ;
		
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
			Logger.trace(sFctName + ": no historyFreeText found for code = " + sCode, _iUserId, Logger.TraceLevel.WARNING) ;
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
	  * Update a FreeTextModelHistory in database
	  * 
	  * @return <code>true</code> if creation succeeded, <code>false</code> if not
	  * 
	  * @param  dataToUpdate FreeTextModelHistory to update
	  */
	public boolean forceUpdateData(final FreeTextModelHistory dataToUpdate)
	{
		String sFctName = "FreeTextHistoryManager.forceUpdateData" ;
		
		if ((null == _dbConnector) || (null == dataToUpdate))
		{
			Logger.trace(sFctName + ": bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		// Prepare SQL query
		//
		String sQuery = "UPDATE historyFreeText SET freeTextId = ?, label = ?, code = ?, lang = ?, next = ?" +
				                          " WHERE id = ?" ; 
		
		_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
		if (null == _dbConnector.getPreparedStatement())
		{
			Logger.trace(sFctName + ": cannot get Statement", _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
		
		_dbConnector.setStatememtInt(1, dataToUpdate.getFreeTextId()) ;
		
		_dbConnector.setStatememtString(2, dataToUpdate.getLabel()) ;
		_dbConnector.setStatememtString(3, dataToUpdate.getCode()) ;
		_dbConnector.setStatememtString(4, dataToUpdate.getLanguage()) ;
		_dbConnector.setStatememtString(5, dataToUpdate.getNext()) ;
		
		_dbConnector.setStatememtInt(6, dataToUpdate.getId()) ;
				
		// Execute query 
		//
		int iNbAffectedRows = _dbConnector.executeUpdatePreparedStatement(false) ;
		if (-1 == iNbAffectedRows)
		{
			Logger.trace(sFctName + ": failed query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}

		Logger.trace(sFctName + ": updated data for historyFreeText " + dataToUpdate.getId(), _iUserId, Logger.TraceLevel.SUBSTEP) ;
		
		_dbConnector.closePreparedStatement() ;
		
		return true ;
	}
	
	/** 
	 * Remove a historyFreeText from database   
	 * 
	 * @param  iRecordId Identifier of historyFreeText to be deleted 
	 * 
	 * @return <code>true</code> if everything OK, <code>false</code> if any problem
	 */
	public boolean deleteRecord(final int iRecordId)
	{
		String sFctName = "FreeTextHistoryManager.deleteRecord" ;
		
		if ((null == _dbConnector) || (-1 == iRecordId))
		{
			Logger.trace(sFctName + ": bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		String sQuery = "DELETE FROM historyFreeText WHERE id = ?" ;
	   
		_dbConnector.prepareStatememt(sQuery, Statement.RETURN_GENERATED_KEYS) ;
		_dbConnector.setStatememtInt(1, iRecordId) ;
		
		// Execute query 
		//
		int iNbAffectedRows = _dbConnector.executeUpdatePreparedStatement(true) ;
		
		boolean bReturn = true ;
		
		if (-1 == iNbAffectedRows)
		{
			Logger.trace(sFctName + ": failed deleting record " + iRecordId, _iUserId, Logger.TraceLevel.ERROR) ;
			bReturn = false ;
		}
		
		_dbConnector.closePreparedStatement() ;
				
		return bReturn ;
	}
	
	/**
	  * Initialize a FreeTextModelHistory from a query ResultSet 
	  * 
	  * @param rs        ResultSet of a query
	  * @param foundData FreeTextModelHistory to fill
	  * 
	  */
	public static void fillDataFromResultSet(final ResultSet rs, FreeTextModelHistory foundData, final int iUserId)
	{
		if ((null == rs) || (null == foundData))
			return ;
		
		try
		{
			foundData.setId(rs.getInt("id")) ;
			foundData.setFreeTextId(rs.getInt("freeTextId")) ;
    	foundData.setCode(rs.getString("code")) ;
    	foundData.setLabel(rs.getString("label")) ;
    	foundData.setLanguage(rs.getString("lang")) ;
    	foundData.setNext(rs.getString("next")) ;
		} 
		catch (SQLException e) {
			Logger.trace("FreeTextHistoryManager.fillDataFromResultSet: exception when processing results set: " + e.getMessage(), iUserId, Logger.TraceLevel.ERROR) ;
		}
	}		
}
