package org.quadrifolium.server.ontology;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.quadrifolium.server.ontology_base.ChangeHistory;
import org.quadrifolium.server.ontology_base.HistoryTriple;
import org.quadrifolium.server.ontology_base.ChangeHistory.ChangeType;
import org.quadrifolium.server.ontology_base.ChangeHistory.TableType;
import org.quadrifolium.shared.ontology.Triple;
import org.quadrifolium.shared.rpc_util.SessionElements;

import com.ldv.server.DBConnector;
import com.ldv.server.Logger;

/** 
 * Object in charge of Read/Write operations for the <code>triple</code> table 
 *   
 */
public class TripleManager  
{	
	protected final DBConnector     _dbConnector ;
	protected final SessionElements _sessionElements ;
	
	/**
	 * Constructor
	 * 
	 * @param sessionElements Can be null if only using read only functions
	 */
	public TripleManager(final SessionElements sessionElements, final DBConnector dbConnector)
	{
		_dbConnector     = dbConnector ;
		_sessionElements = sessionElements ;
	}

	/**
	 * Insert a Triple object in database, and complete this object with insertion created information<br>
	 * <br>
	 * This function needs a registered user.
	 * 
	 * @param dataToInsert Triple to be inserted
	 *
	 * @return <code>true</code> if successful, <code>false</code> if not
	 */
	public boolean insertData(Triple dataToInsert)
	{
		String sFctName = "TripleManager.insertData" ;
		
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
		
		String sQuery = "INSERT INTO triple (subject, predicate, object) VALUES (?, ?, ?)" ;
		
		_dbConnector.prepareStatememt(sQuery, Statement.RETURN_GENERATED_KEYS) ;
		if (null == _dbConnector.getPreparedStatement())
		{
			Logger.trace(sFctName + ": cannot get Statement", _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
			_dbConnector.closeAll() ;
			return false ;
		}
		
		_dbConnector.setStatememtString(1, dataToInsert.getSubject()) ;
		_dbConnector.setStatememtString(2, dataToInsert.getPredicate()) ;
		_dbConnector.setStatememtString(3, dataToInsert.getObject()) ;
		
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
		
		Logger.trace(sFctName +  ": user " + _sessionElements.getPersonId() + " successfuly recorded triple " + dataToInsert.getId(), _sessionElements.getPersonId(), Logger.TraceLevel.STEP) ;
		
		return historize(dataToInsert, ChangeType.create) ;
	}
	
	/**
	 * Update a Triple in database<br>
	 * <br>
	 * This function needs a registered user.
	 * 
	 * @return true if successful, false if not
	 * 
	 * @param dataToUpdate Triple to be updated
	 */
	public boolean updateData(Triple dataToUpdate)
	{
		String sFctName = "TripleManager.updateData" ;
		
		// This function needs a registered user
		//
		if (null == _sessionElements)
		{
			Logger.trace(sFctName + ": no session elements.", -1, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		if ((null == _dbConnector) || (null == dataToUpdate))
		{
			Logger.trace("TripleManager.updateData: bad parameter", _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		Triple foundData = new Triple() ;
		if (false == existData(dataToUpdate.getId(), foundData))
			return false ;
		
		if (foundData.equals(dataToUpdate))
		{
			Logger.trace("TripleManager.updateData: Triple to update (" + dataToUpdate.getId() + ") unchanged; nothing to do", _sessionElements.getPersonId(), Logger.TraceLevel.SUBSTEP) ;
			return true ;
		}
		
		return forceUpdateData(dataToUpdate) ;
	}
		
	/**
	 * Historize a change
	 * 
	 * @param triple     Triple to historize (new one for new and update, old one for delete)
	 * @param changeType Modification type (add, update, delete)
	 */
	protected boolean historize(final Triple triple, ChangeHistory.ChangeType changeType)
	{
		// Create an historization object and save it
		//
		HistoryTriple tripleHistory = new HistoryTriple(triple) ;
		
		TripleHistoryManager historyManager = new TripleHistoryManager(_sessionElements, _dbConnector) ;
		if (false == historyManager.insertData(tripleHistory))
			return false ;
		
		// Create a Change history record
		//
		ChangeHistory changeHistory = new ChangeHistory(_sessionElements.getSessionId(), "", TableType.triple, changeType, triple.getId(), tripleHistory.getId()) ;
		
		ChangeHistoryManager changeManager = new ChangeHistoryManager(_sessionElements.getPersonId(), _dbConnector) ;
		return changeManager.insertData(changeHistory) ;
	}
	
	/**
	  * Check if there is any Triple with this ID in database and, if true get its content
	  * 
	  * @return True if found, else false
	  * 
	  * @param iId       Identifier of Triple to look for
	  * @param foundData Triple to store existing information to 
	  */
	public boolean existData(final int iId, Triple foundData)
	{
		String sFctName = "TripleManager.existData" ;
		
		// This function is read only, hence it doesn't need a registered user
		//
		int iUserId = -1 ;
		if (null != _sessionElements)
			iUserId = _sessionElements.getPersonId() ;
		
		if ((null == _dbConnector) || (iId <= 0) || (null == foundData))
		{
			Logger.trace(sFctName + ": bad parameter.", iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		String sQuery = "SELECT * FROM triple WHERE id = ?" ;
		
		_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
		_dbConnector.setStatememtInt(1, iId) ;
	   		
		if (false == _dbConnector.executePreparedStatement())
		{
			Logger.trace(sFctName + ": failed query " + sQuery, iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
	   		
		ResultSet rs = _dbConnector.getResultSet() ;
		if (null == rs)
		{
			Logger.trace(sFctName + ": no Triple found for ID " + iId, iUserId, Logger.TraceLevel.WARNING) ;
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
	  * Fill an array with Triple(s) with a given subject and a given predicate
	  * 
	  * @return <code>true</code> if everything went well, <code>false</code> if not
	  * 
	  * @param sSubject   Subject to look for
	  * @param sPredicate Predicate to look for 
	  * @param aResults   Array to fill (not cleared before adding data)
	  */
	public boolean getObjects(final String sSubject, final String sPredicate, ArrayList<Triple> aResults)
	{
		String sFctName = "TripleManager.getObjects" ;
		
		// This function is read only, hence it doesn't need a registered user
		//
		int iUserId = -1 ;
		if (null != _sessionElements)
			iUserId = _sessionElements.getPersonId() ;
		
		if ((null == _dbConnector) || (null == sSubject) || "".equals(sSubject) || (null == sPredicate) || "".equals(sPredicate) || (null == aResults))
		{
			Logger.trace(sFctName + ": bad parameter.", iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		String sQuery = "SELECT * FROM triple WHERE subject = ? AND predicate = ?" ;
		
		_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
		_dbConnector.setStatememtString(1, sSubject) ;
		_dbConnector.setStatememtString(2, sPredicate) ;
	   		
		if (false == _dbConnector.executePreparedStatement())
		{
			Logger.trace(sFctName + ": failed query " + sQuery, iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
	   		
		ResultSet rs = _dbConnector.getResultSet() ;
		if (null == rs)
		{
			Logger.trace(sFctName + ": no Triple found for subject " + sSubject + " and predicate " + sPredicate, iUserId, Logger.TraceLevel.WARNING) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
		
		int iCount = 0 ;
		
		try
		{
	    while (rs.next())
	    {
	    	Triple foundData = new Triple() ;
	    	fillDataFromResultSet(rs, foundData, iUserId) ;
	    	
	    	aResults.add(foundData) ;
	    	
	    	iCount++ ;
	    }
		} catch (SQLException e)
		{
			Logger.trace(sFctName + ": exception when iterating results " + e.getMessage(), iUserId, Logger.TraceLevel.ERROR) ;
		}
		
		if (iCount > 1)
			Logger.trace(sFctName + ": " + iCount + " Triples found for subject " + sSubject + " and predicate " + sPredicate, iUserId, Logger.TraceLevel.WARNING) ;
		else
			Logger.trace(sFctName + ": " + iCount + " Triple found for subject " + sSubject + " and predicate " + sPredicate, iUserId, Logger.TraceLevel.WARNING) ;
		
		_dbConnector.closeResultSet() ;
		_dbConnector.closePreparedStatement() ;
				
		return true ;
	}
	
	/**
	  * Fill an array with Triple(s) with a given object and a given predicate
	  * 
	  * @return <code>true</code> if everything went well, <code>false</code> if not
	  * 
	  * @param sObject    Object to look for
	  * @param sPredicate Predicate to look for 
	  * @param aResults   Array to fill (not cleared before adding data)
	  */
	public boolean getSubjects(final String sObject, final String sPredicate, ArrayList<Triple> aResults)
	{
		String sFctName = "TripleManager.getObjects" ;
		
		// This function is read only, hence it doesn't need a registered user
		//
		int iUserId = -1 ;
		if (null != _sessionElements)
			iUserId = _sessionElements.getPersonId() ;
		
		if ((null == _dbConnector) || (null == sObject) || "".equals(sObject) || (null == sPredicate) || "".equals(sPredicate) || (null == aResults))
		{
			Logger.trace(sFctName + ": bad parameter", iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		String sQuery = "SELECT * FROM triple WHERE object = ? AND predicate = ?" ;
		
		_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
		_dbConnector.setStatememtString(1, sObject) ;
		_dbConnector.setStatememtString(2, sPredicate) ;
	   		
		if (false == _dbConnector.executePreparedStatement())
		{
			Logger.trace(sFctName + ": failed query " + sQuery, iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
	   		
		ResultSet rs = _dbConnector.getResultSet() ;
		if (null == rs)
		{
			Logger.trace(sFctName + ": no Triple found for subject " + sObject + " and predicate " + sPredicate, iUserId, Logger.TraceLevel.WARNING) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
		
		int iCount = 0 ;
		
		try
		{
	    while (rs.next())
	    {
	    	Triple foundData = new Triple() ;
	    	fillDataFromResultSet(rs, foundData, iUserId) ;
	    	
	    	aResults.add(foundData) ;
	    	
	    	iCount++ ;
	    }
		} catch (SQLException e)
		{
			Logger.trace(sFctName + ": exception when iterating results " + e.getMessage(), iUserId, Logger.TraceLevel.ERROR) ;
		}
		
		if (iCount > 1)
			Logger.trace(sFctName + ": " + iCount + " Triples found for subject " + sObject + " and predicate " + sPredicate, iUserId, Logger.TraceLevel.DETAIL) ;
		else
			Logger.trace(sFctName + ": " + iCount + " Triple found for subject " + sObject + " and predicate " + sPredicate, iUserId, Logger.TraceLevel.DETAIL) ;
		
		_dbConnector.closeResultSet() ;
		_dbConnector.closePreparedStatement() ;
				
		return true ;
	}
	
	/**
	 * Update a Triple in database<br>
	 * <br>
	 * This function needs a registered user.
	 * 
	 * @return <code>true</code> if creation succeeded, <code>false</code> if not
	 * 
	 * @param  dataToUpdate Triple to update
	 */
	private boolean forceUpdateData(final Triple dataToUpdate)
	{
		String sFctName = "TripleManager.forceUpdateData" ;
		
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
		String sQuery = "UPDATE triple SET subject = ?, predicate = ?, object = ?" +
				                          " WHERE id = ?" ; 
		
		_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
		if (null == _dbConnector.getPreparedStatement())
		{
			Logger.trace(sFctName + ": cannot get Statement", _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
		
		_dbConnector.setStatememtString(1, dataToUpdate.getSubject()) ;
		_dbConnector.setStatememtString(2, dataToUpdate.getPredicate()) ;
		_dbConnector.setStatememtString(3, dataToUpdate.getObject()) ;
		
		_dbConnector.setStatememtInt(4, dataToUpdate.getId()) ;
				
		// Execute query 
		//
		int iNbAffectedRows = _dbConnector.executeUpdatePreparedStatement(false) ;
		if (-1 == iNbAffectedRows)
		{
			Logger.trace(sFctName + ": failed query " + sQuery, _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}

		Logger.trace(sFctName + ": updated data for Triple " + dataToUpdate.getId(), _sessionElements.getPersonId(), Logger.TraceLevel.SUBSTEP) ;
		
		_dbConnector.closePreparedStatement() ;
		
		return historize(dataToUpdate, ChangeType.change) ;
	}
	
	/** 
	 * Remove a Triple from database
	 * <br>
	 * This function needs a registered user.
	 * 
	 * @param  dataToDelete Triple to delete 
	 * 
	 * @return <code>true</code> if everything OK, <code>false</code> if any problem
	 */
	public boolean deleteRecord(final Triple dataToDelete)
	{
		String sFctName = "TripleManager.deleteRecord" ;
		
		// This function needs a registered user
		//
		if (null == _sessionElements)
		{
			Logger.trace(sFctName + ": no session elements.", -1, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		if ((null == _dbConnector) || (null == dataToDelete) || (dataToDelete.getId() < 0))
		{
			Logger.trace(sFctName + ": bad parameter", _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		String sQuery = "DELETE FROM triple WHERE id = ?" ;
	   
		_dbConnector.prepareStatememt(sQuery, Statement.RETURN_GENERATED_KEYS) ;
		_dbConnector.setStatememtInt(1, dataToDelete.getId()) ;
		
		// Execute query 
		//
		int iNbAffectedRows = _dbConnector.executeUpdatePreparedStatement(true) ;
		
		if (-1 == iNbAffectedRows)
		{
			Logger.trace(sFctName + ": failed deleting record " + dataToDelete.getId(), _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
		
		_dbConnector.closePreparedStatement() ;
				
		return historize(dataToDelete, ChangeType.delete) ;
	}
	
	/**
	  * Initialize a Triple from a query ResultSet 
	  * 
	  * @param rs        ResultSet of a query
	  * @param foundData Triple to fill
	  * 
	  */
	public static void fillDataFromResultSet(final ResultSet rs, Triple foundData, final int iUserId)
	{
		if ((null == rs) || (null == foundData))
			return ;
		
		try
		{
			foundData.setId(rs.getInt("id")) ;
    	foundData.setSubject(rs.getString("subject")) ;
    	foundData.setPredicate(rs.getString("predicate")) ;
    	foundData.setObject(rs.getString("object")) ;
		} 
		catch (SQLException e) {
			Logger.trace("TripleManager.fillDataFromResultSet: exception when processing results set: " + e.getMessage(), iUserId, Logger.TraceLevel.ERROR) ;
		}
	}		
}
