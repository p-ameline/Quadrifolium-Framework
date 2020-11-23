package org.quadrifolium.server.ontology;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.quadrifolium.server.ontology_base.HistoryLemma;
import org.quadrifolium.shared.rpc_util.SessionElements;

import com.ldv.server.DBConnector;
import com.ldv.server.Logger;

/** 
 * Object in charge of Read/Write operations for the <code>historyLemma</code> table 
 *   
 */
public class LemmaHistoryManager  
{	
	protected final DBConnector     _dbConnector ;
	protected final SessionElements _sessionElements ;
	
	/**
	 * Constructor
	 * 
	 * @param sessionElements Can be null if only using read only functions
	 */
	public LemmaHistoryManager(final SessionElements sessionElements, final DBConnector dbConnector)
	{
		_dbConnector     = dbConnector ;
		_sessionElements = sessionElements ;
	}

	/**
	 * Insert a HistoryLemma object in database, and complete this object with insertion created information<br>
	 * <br>
	 * This function needs a registered user.
	 * 
	 * @param dataToInsert HistoryLemma to be inserted
	 *
	 * @return <code>true</code> if successful, <code>false</code> if not
	 */
	public boolean insertData(final HistoryLemma dataToInsert)
	{
		String sFctName = "LemmaHistoryManager.insertData" ;
		
		// This function needs a registered user
		//
		if (null == _sessionElements)
		{
			Logger.trace(sFctName + ": no session elements.", -1, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		if ((null == _dbConnector) || (null == dataToInsert))
		{
			Logger.trace(sFctName + ": invalid parameter", _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		String sQuery = "INSERT INTO historyLemma (lemmaId, label, code, lang) VALUES (?, ?, ?, ?)" ;
		
		_dbConnector.prepareStatememt(sQuery, Statement.RETURN_GENERATED_KEYS) ;
		if (null == _dbConnector.getPreparedStatement())
		{
			Logger.trace(sFctName + ": cannot get Statement", _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
			_dbConnector.closeAll() ;
			return false ;
		}
		
		_dbConnector.setStatememtInt(1, dataToInsert.getLemmaId()) ;
		_dbConnector.setStatememtString(2, dataToInsert.getLabel()) ;
		_dbConnector.setStatememtString(3, dataToInsert.getCode()) ;
		_dbConnector.setStatememtString(4, dataToInsert.getLanguage()) ;
		
		// Execute query 
		//
		int iNbAffectedRows = _dbConnector.executeUpdatePreparedStatement(true) ;
		if (-1 == iNbAffectedRows)
		{
			Logger.trace(sFctName + ": failed query " + sQuery, _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
			_dbConnector.closeAll() ;
			return false ;
		}
		
		ResultSet rs = _dbConnector.getResultSet() ;
		try
    {
			if (rs.next())
				dataToInsert.setId(rs.getInt(1)) ;
			else
				Logger.trace(sFctName + ": cannot get row after query " + sQuery, _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
    } 
		catch (SQLException e)
    {
			Logger.trace(sFctName + ": exception when iterating results " + e.getMessage(), _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
    }
		
		_dbConnector.closeResultSet() ;
		_dbConnector.closePreparedStatement() ;
		
		Logger.trace(sFctName +  ": user " + _sessionElements.getPersonId() + " successfuly recorded lexicon " + dataToInsert.getCode() + ":" + dataToInsert.getLabel(), _sessionElements.getPersonId(), Logger.TraceLevel.STEP) ;
		
		return true ;
	}
	
	/**
	 * Update a HistoryLemma in database<br>
	 * <br>
	 * This function needs a registered user.
	 * 
	 * @return true if successful, false if not
	 * 
	 * @param dataToUpdate HistoryLemma to be updated
	 */
	public boolean updateData(HistoryLemma dataToUpdate)
	{
		String sFctName = "LemmaHistoryManager.updateData" ;
		
		// This function needs a registered user
		//
		if (null == _sessionElements)
		{
			Logger.trace(sFctName + ": no session elements.", -1, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		if ((null == _dbConnector) || (null == dataToUpdate))
		{
			Logger.trace(sFctName + ": bad parameter", _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		HistoryLemma foundData = new HistoryLemma() ;
		if (false == existData(dataToUpdate.getCode(), foundData))
			return false ;
		
		if (foundData.equals(dataToUpdate))
		{
			Logger.trace(sFctName + ": Trait to update (" + dataToUpdate.getCode() + " / " + dataToUpdate.getLabel() + ") unchanged; nothing to do", _sessionElements.getPersonId(), Logger.TraceLevel.SUBSTEP) ;
			return true ;
		}
		
		return forceUpdateData(dataToUpdate) ;
	}
		
	/**
	 * Check if there is any HistoryLemma with this code in database and, if true get its content<br>
	 * <br>
	 * This function is read only, hence it doesn't need a registered user.
	 * 
	 * @return True if found, else false
	 * 
	 * @param sCode     Code of HistoryLemma to check
	 * @param foundData HistoryLemma to get existing information
	 */
	public boolean existData(final String sCode, final HistoryLemma foundData)
	{
		String sFctName = "LemmaHistoryManager.existData" ;
		
		// This function is read only, hence it doesn't need a registered user
		//
		int iUserId = -1 ;
		if (null != _sessionElements)
			iUserId = _sessionElements.getPersonId() ;
		
		if ((null == _dbConnector) || (null == sCode) || (null == foundData))
		{
			Logger.trace(sFctName + ": bad parameter", iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		String sQuery = "SELECT * FROM historyLemma WHERE code = ?" ;
		
		_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
		_dbConnector.setStatememtString(1, sCode) ;
	   		
		if (false == _dbConnector.executePreparedStatement())
		{
			Logger.trace(sFctName + ": failed query " + sQuery, iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
	   		
		ResultSet rs = _dbConnector.getResultSet() ;
		if (null == rs)
		{
			Logger.trace(sFctName + ": no HistoryLemma found for code = " + sCode, iUserId, Logger.TraceLevel.WARNING) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
		
		try
		{
	    if (rs.next())
	    {
	    	fillDataFromResultSet(rs, foundData, iUserId) ;
	    	
	    	_dbConnector.closeResultSet() ;
	    	_dbConnector.closePreparedStatement() ;
	    	
	    	return true ;	    	
	    }
		} catch (SQLException e)
		{
			Logger.trace(sFctName + ": exception when iterating results " + e.getMessage(), iUserId, Logger.TraceLevel.ERROR) ;
		}
		
		_dbConnector.closeResultSet() ;
		_dbConnector.closePreparedStatement() ;
				
		return false ;
	}
		
	/**
	 * Update a HistoryLemma in database<br>
	 * <br>
	 * This function needs a registered user.
	 * 
	 * @return <code>true</code> if creation succeeded, <code>false</code> if not
	 * 
	 * @param  dataToUpdate HistoryLemma to update
	 */
	private boolean forceUpdateData(final HistoryLemma dataToUpdate)
	{
		String sFctName = "LemmaHistoryManager.forceUpdateData" ;
		
		// This function needs a registered user
		//
		if (null == _sessionElements)
		{
			Logger.trace(sFctName + ": no session elements.", -1, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		if ((null == _dbConnector) || (null == dataToUpdate))
		{
			Logger.trace(sFctName + ": bad parameter", _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		// Prepare SQL query
		//
		String sQuery = "UPDATE historyLemma SET lemmaId = ?, label = ?, code = ?, lang = ?" +
				                          " WHERE id = ?" ; 
		
		_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
		if (null == _dbConnector.getPreparedStatement())
		{
			Logger.trace(sFctName + ": cannot get Statement", _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
		
		_dbConnector.setStatememtInt(1, dataToUpdate.getLemmaId()) ;
		_dbConnector.setStatememtString(2, dataToUpdate.getLabel()) ;
		_dbConnector.setStatememtString(3, dataToUpdate.getCode()) ;
		_dbConnector.setStatememtString(4, dataToUpdate.getLanguage()) ;
		
		_dbConnector.setStatememtInt(5, dataToUpdate.getId()) ;
				
		// Execute query 
		//
		int iNbAffectedRows = _dbConnector.executeUpdatePreparedStatement(false) ;
		if (-1 == iNbAffectedRows)
		{
			Logger.trace(sFctName + ": failed query " + sQuery, _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}

		Logger.trace(sFctName + ": updated data for HistoryLemma " + dataToUpdate.getCode(), _sessionElements.getPersonId(), Logger.TraceLevel.SUBSTEP) ;
		
		_dbConnector.closePreparedStatement() ;
		
		return true ;
	}
	
	/**
	  * Initialize a HistoryLemma from a query ResultSet 
	  * 
	  * @param rs        ResultSet of a query
	  * @param foundData HistoryLemma to fill
	  * 
	  */
	public static void fillDataFromResultSet(final ResultSet rs, HistoryLemma foundData, final int iUserId)
	{
		if ((null == rs) || (null == foundData))
			return ;
		
		try
		{
			foundData.setId(rs.getInt("id")) ;
			foundData.setLemmaId(rs.getInt("lemmaId")) ;
    	foundData.setCode(rs.getString("code")) ;
    	foundData.setLabel(rs.getString("label")) ;
    	foundData.setLanguage(rs.getString("lang")) ;
		} 
		catch (SQLException e) {
			Logger.trace("LemmaHistoryManager.fillDataFromResultSet: exception when processing results set: " + e.getMessage(), iUserId, Logger.TraceLevel.ERROR) ;
		}
	}		
}
