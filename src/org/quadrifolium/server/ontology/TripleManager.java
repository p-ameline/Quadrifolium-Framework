package org.quadrifolium.server.ontology;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.quadrifolium.server.DBConnector;
import org.quadrifolium.server.Logger;
import org.quadrifolium.shared.ontology.Triple;

/** 
 * Object in charge of Read/Write operations for the <code>triple</code> table 
 *   
 */
public class TripleManager  
{	
	protected final DBConnector _dbConnector ;
	protected final int         _iUserId ;
	
	/**
	 * Constructor 
	 */
	public TripleManager(final int iUserId, final DBConnector dbConnector)
	{
		_dbConnector = dbConnector ;
		_iUserId     = iUserId ;
	}

	/**
	  * Insert a Flex object in database, and complete this object with insertion created information
	  * 
	  * @param dataToInsert Flex to be inserted
	  *
	  * @return <code>true</code> if successful, <code>false</code> if not
	  */
	public boolean insertData(Triple dataToInsert)
	{
		String sFctName = "TripleManager.insertData" ;
		
		if ((null == _dbConnector) || (null == dataToInsert))
		{
			Logger.trace(sFctName + ": invalid parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		String sQuery = "INSERT INTO triple (subject, predicate, object) VALUES (?, ?, ?)" ;
		
		_dbConnector.prepareStatememt(sQuery, Statement.RETURN_GENERATED_KEYS) ;
		if (null == _dbConnector.getPreparedStatement())
		{
			Logger.trace(sFctName + ": cannot get Statement", _iUserId, Logger.TraceLevel.ERROR) ;
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
		
		Logger.trace(sFctName +  ": user " + _iUserId + " successfuly recorded triple " + dataToInsert.getId(), _iUserId, Logger.TraceLevel.STEP) ;
		
		return true ;
	}
	
	/**
	  * Update a Triple in database
	  * 
	  * @return true if successful, false if not
	  * 
	  * @param dataToUpdate Triple to be updated
	  */
	public boolean updateData(Triple dataToUpdate)
	{
		if ((null == _dbConnector) || (null == dataToUpdate))
		{
			Logger.trace("TripleManager.updateData: bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		Triple foundData = new Triple() ;
		if (false == existData(dataToUpdate.getId(), foundData))
			return false ;
		
		if (foundData.equals(dataToUpdate))
		{
			Logger.trace("TripleManager.updateData: Triple to update (" + dataToUpdate.getId() + ") unchanged; nothing to do", _iUserId, Logger.TraceLevel.SUBSTEP) ;
			return true ;
		}
		
		return forceUpdateData(dataToUpdate) ;
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
		
		if ((null == _dbConnector) || (iId <= 0) || (null == foundData))
		{
			Logger.trace(sFctName + ": bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		String sQuery = "SELECT * FROM triple WHERE id = ?" ;
		
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
			Logger.trace(sFctName + ": no Triple found for ID " + iId, _iUserId, Logger.TraceLevel.WARNING) ;
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
		
		if ((null == _dbConnector) || (null == sSubject) || "".equals(sSubject) || (null == sPredicate) || "".equals(sPredicate) || (null == aResults))
		{
			Logger.trace(sFctName + ": bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		String sQuery = "SELECT * FROM triple WHERE subject = ? AND predicate = ?" ;
		
		_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
		_dbConnector.setStatememtString(1, sSubject) ;
		_dbConnector.setStatememtString(2, sPredicate) ;
	   		
		if (false == _dbConnector.executePreparedStatement())
		{
			Logger.trace(sFctName + ": failed query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
	   		
		ResultSet rs = _dbConnector.getResultSet() ;
		if (null == rs)
		{
			Logger.trace(sFctName + ": no Triple found for subject " + sSubject + " and predicate " + sPredicate, _iUserId, Logger.TraceLevel.WARNING) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
		
		int iCount = 0 ;
		
		try
		{
	    while (rs.next())
	    {
	    	Triple foundData = new Triple() ;
	    	fillDataFromResultSet(rs, foundData, _iUserId) ;
	    	
	    	aResults.add(foundData) ;
	    	
	    	iCount++ ;
	    }
		} catch (SQLException e)
		{
			Logger.trace(sFctName + ": exception when iterating results " + e.getMessage(), _iUserId, Logger.TraceLevel.ERROR) ;
		}
		
		if (iCount > 1)
			Logger.trace(sFctName + ": " + iCount + " Triples found for subject " + sSubject + " and predicate " + sPredicate, _iUserId, Logger.TraceLevel.WARNING) ;
		else
			Logger.trace(sFctName + ": " + iCount + " Triple found for subject " + sSubject + " and predicate " + sPredicate, _iUserId, Logger.TraceLevel.WARNING) ;
		
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
		
		if ((null == _dbConnector) || (null == sObject) || "".equals(sObject) || (null == sPredicate) || "".equals(sPredicate) || (null == aResults))
		{
			Logger.trace(sFctName + ": bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		String sQuery = "SELECT * FROM triple WHERE object = ? AND predicate = ?" ;
		
		_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
		_dbConnector.setStatememtString(1, sObject) ;
		_dbConnector.setStatememtString(2, sPredicate) ;
	   		
		if (false == _dbConnector.executePreparedStatement())
		{
			Logger.trace(sFctName + ": failed query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
	   		
		ResultSet rs = _dbConnector.getResultSet() ;
		if (null == rs)
		{
			Logger.trace(sFctName + ": no Triple found for subject " + sObject + " and predicate " + sPredicate, _iUserId, Logger.TraceLevel.WARNING) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
		
		int iCount = 0 ;
		
		try
		{
	    while (rs.next())
	    {
	    	Triple foundData = new Triple() ;
	    	fillDataFromResultSet(rs, foundData, _iUserId) ;
	    	
	    	aResults.add(foundData) ;
	    	
	    	iCount++ ;
	    }
		} catch (SQLException e)
		{
			Logger.trace(sFctName + ": exception when iterating results " + e.getMessage(), _iUserId, Logger.TraceLevel.ERROR) ;
		}
		
		if (iCount > 1)
			Logger.trace(sFctName + ": " + iCount + " Triples found for subject " + sObject + " and predicate " + sPredicate, _iUserId, Logger.TraceLevel.DETAIL) ;
		else
			Logger.trace(sFctName + ": " + iCount + " Triple found for subject " + sObject + " and predicate " + sPredicate, _iUserId, Logger.TraceLevel.DETAIL) ;
		
		_dbConnector.closeResultSet() ;
		_dbConnector.closePreparedStatement() ;
				
		return true ;
	}
	
	/**
	  * Update a Triple in database
	  * 
	  * @return <code>true</code> if creation succeeded, <code>false</code> if not
	  * 
	  * @param  dataToUpdate Triple to update
	  */
	private boolean forceUpdateData(final Triple dataToUpdate)
	{
		String sFctName = "TripleManager.forceUpdateData" ;
		
		if ((null == _dbConnector) || (null == dataToUpdate))
		{
			Logger.trace(sFctName + ": bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		// Prepare SQL query
		//
		String sQuery = "UPDATE triple SET subject = ?, predicate = ?, object = ?" +
				                          " WHERE id = ?" ; 
		
		_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
		if (null == _dbConnector.getPreparedStatement())
		{
			Logger.trace(sFctName + ": cannot get Statement", _iUserId, Logger.TraceLevel.ERROR) ;
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
			Logger.trace(sFctName + ": failed query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}

		Logger.trace(sFctName + ": updated data for Triple " + dataToUpdate.getId(), _iUserId, Logger.TraceLevel.SUBSTEP) ;
		
		_dbConnector.closePreparedStatement() ;
		
		return true ;
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
