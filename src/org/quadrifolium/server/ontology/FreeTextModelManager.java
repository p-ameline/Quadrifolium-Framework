package org.quadrifolium.server.ontology;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.quadrifolium.server.DBConnector;
import org.quadrifolium.server.Logger;
import org.quadrifolium.server.ontology_base.FreeTextModel;

/** 
 * Object in charge of Read/Write operations for the <code>freeText</code> table 
 *   
 */
public class FreeTextModelManager  
{	
	protected final DBConnector _dbConnector ;
	protected final int         _iUserId ;
	
	/**
	 * Constructor 
	 */
	public FreeTextModelManager(final int iUserId, final DBConnector dbConnector)
	{
		_dbConnector = dbConnector ;
		_iUserId     = iUserId ;
	}

	/**
	  * Insert a FreeTextModel object in database, and complete this object with insertion created information
	  * 
	  * @param dataToInsert FreeTextModel to be inserted
	  *
	  * @return <code>true</code> if successful, <code>false</code> if not
	  */
	public boolean insertData(final FreeTextModel dataToInsert)
	{
		String sFctName = "FreeTextModelManager.insertData" ;
		
		if ((null == _dbConnector) || (null == dataToInsert))
		{
			Logger.trace(sFctName + ": invalid parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		String sQuery = "INSERT INTO freeText (label, code, lang, next) VALUES (?, ?, ?, ?)" ;
		
		_dbConnector.prepareStatememt(sQuery, Statement.RETURN_GENERATED_KEYS) ;
		if (null == _dbConnector.getPreparedStatement())
		{
			Logger.trace(sFctName + ": cannot get Statement", _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closeAll() ;
			return false ;
		}
		
		_dbConnector.setStatememtString(1, dataToInsert.getLabel()) ;
		_dbConnector.setStatememtString(2, dataToInsert.getCode()) ;
		_dbConnector.setStatememtString(3, dataToInsert.getLanguage()) ;
		_dbConnector.setStatememtString(4, dataToInsert.getNext()) ;
		
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
		
		Logger.trace(sFctName +  ": user " + _iUserId + " successfuly recorded freeText " + dataToInsert.getCode() + ":" + dataToInsert.getLabel(), _iUserId, Logger.TraceLevel.STEP) ;
		
		return true ;
	}
	
	/**
	  * Update a FreeTextModel in database
	  * 
	  * @return true if successful, false if not
	  * 
	  * @param dataToUpdate FreeTextModel to be updated
	  */
	public boolean updateData(FreeTextModel dataToUpdate)
	{
		if ((null == _dbConnector) || (null == dataToUpdate))
		{
			Logger.trace("FreeTextModelManager.updateData: bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		FreeTextModel foundData = new FreeTextModel() ;
		if (false == existData(dataToUpdate.getCode(), foundData))
			return false ;
		
		if (foundData.equals(dataToUpdate))
		{
			Logger.trace("FreeTextModelManager.updateData: Trait to update (" + dataToUpdate.getCode() + " / " + dataToUpdate.getLabel() + ") unchanged; nothing to do", _iUserId, Logger.TraceLevel.SUBSTEP) ;
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
	  * @param foundData FreeTextModel to get existing information
	  */
	public boolean existData(final String sCode, final FreeTextModel foundData)
	{
		String sFctName = "FreeTextModelManager.existData" ;
		
		if ((null == _dbConnector) || (null == sCode) || (null == foundData))
		{
			Logger.trace(sFctName + ": bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		String sQuery = "SELECT * FROM freeText WHERE code = ?" ;
		
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
			Logger.trace(sFctName + ": no FreeTextModel found for code = " + sCode, _iUserId, Logger.TraceLevel.WARNING) ;
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
	  * Update a FreeTextModel in database
	  * 
	  * @return <code>true</code> if creation succeeded, <code>false</code> if not
	  * 
	  * @param  dataToUpdate FreeTextModel to update
	  */
	public boolean forceUpdateData(final FreeTextModel dataToUpdate)
	{
		String sFctName = "FreeTextModelManager.forceUpdateData" ;
		
		if ((null == _dbConnector) || (null == dataToUpdate))
		{
			Logger.trace(sFctName + ": bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		// Prepare SQL query
		//
		String sQuery = "UPDATE freeText SET label = ?, code = ?, lang = ?, next = ?" +
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
		_dbConnector.setStatememtString(3, dataToUpdate.getLanguage()) ;
		_dbConnector.setStatememtString(4, dataToUpdate.getNext()) ;
		
		_dbConnector.setStatememtInt(5, dataToUpdate.getId()) ;
				
		// Execute query 
		//
		int iNbAffectedRows = _dbConnector.executeUpdatePreparedStatement(false) ;
		if (-1 == iNbAffectedRows)
		{
			Logger.trace(sFctName + ": failed query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}

		Logger.trace(sFctName + ": updated data for FreeText " + dataToUpdate.getCode(), _iUserId, Logger.TraceLevel.SUBSTEP) ;
		
		_dbConnector.closePreparedStatement() ;
		
		return true ;
	}
	
	/** 
	 * Remove from database all ContactCodes and ContactCodeEcogens linked to a given ContactElement, then remove the ContactElement itself  
	 * 
	 * @param  dbConnector Database connector
	 * @param  iContactElementId Identifier of ContactElement to be deleted 
	 * @param  iUserId     User identifier
	 * @return true if everything ok, false if any problem
	 */
	public boolean deleteRecord(final int iRecordId)
	{
		String sFctName = "FreeTextModelManager.deleteRecord" ;
		
		if ((null == _dbConnector) || (-1 == iRecordId))
		{
			Logger.trace(sFctName + ": bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		String sQuery = "DELETE FROM freeText WHERE id = ?" ;
	   
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
	  * Initialize a FreeTextModel from a query ResultSet 
	  * 
	  * @param rs        ResultSet of a query
	  * @param foundData FreeTextModel to fill
	  * 
	  */
	public static void fillDataFromResultSet(final ResultSet rs, FreeTextModel foundData, final int iUserId)
	{
		if ((null == rs) || (null == foundData))
			return ;
		
		try
		{
			foundData.setId(rs.getInt("id")) ;
    	foundData.setCode(rs.getString("code")) ;
    	foundData.setLabel(rs.getString("label")) ;
    	foundData.setLanguage(rs.getString("lang")) ;
    	foundData.setNext(rs.getString("next")) ;
		} 
		catch (SQLException e) {
			Logger.trace("FreeTextModelManager.fillDataFromResultSet: exception when processing results set: " + e.getMessage(), iUserId, Logger.TraceLevel.ERROR) ;
		}
	}		
}
