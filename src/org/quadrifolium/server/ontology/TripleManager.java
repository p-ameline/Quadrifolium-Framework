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
import org.quadrifolium.shared.rpc4ontology.SearchAttribute;
import org.quadrifolium.shared.rpc_util.SessionElements;
import org.quadrifolium.shared.util.QuadrifoliumFcts;

import com.ldv.server.DBConnector;
import com.ldv.server.Logger;
import com.ldv.shared.util.UsefulConcepts;

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
	  * @param sPredicate Predicate<br>pass <code>null</code> or <code>""</code> or <code>"*"</code> to get all triples for the element
    * @param sRange     Pass <code>"%"</code> to get all triples for the corresponding lemma<br>pass <code>"%%"</code> to get all triples for the corresponding concept
	  * @param aResults   Array of {@link Triple} to fill (not cleared before adding data)
	  */
	public boolean getObjects(final String sSubject, final String sPredicate, final String sRange, ArrayList<Triple> aResults) {
	  return getTriplesFromElementAndPredicate("subject", sSubject, sPredicate, sRange, aResults) ;
	}
	
	/**
	  * Fill an array with Triple(s) with a given object and a given predicate
	  * 
	  * @return <code>true</code> if everything went well, <code>false</code> if not
	  * 
	  * @param sObject    Object to look for
	  * @param sPredicate Predicate<br>pass <code>null</code> or <code>""</code> or <code>"*"</code> to get all triples for the element
    * @param sRange     Pass <code>"%"</code> to get all triples for the corresponding lemma<br>pass <code>"%%"</code> to get all triples for the corresponding concept
	  * @param aResults   Array of {@link Triple} to fill (not cleared before adding data)
	  */
	public boolean getSubjects(final String sObject, final String sPredicate, final String sRange, ArrayList<Triple> aResults) {
	  return getTriplesFromElementAndPredicate("object", sObject, sPredicate, sRange, aResults) ;
	}
	
	/**
   * Get all triples for a given predicate, with the concept as an object or a subject
   * 
   * @param sPosition  Either "object" or "subject"
   * @param sCode      Code of the concepts to be looked for (either as object or subject depending on position)
   * @param sPredicate Predicate<br>pass <code>null</code> or <code>""</code> or <code>"*"</code> to get all triples for the element
   * @param sRange     Pass <code>"%"</code> to get all triples for the corresponding lemma<br>pass <code>"%%"</code> to get all triples for the corresponding concept
   * @param aResults   Array of {@link Triple} to fill (not cleared before adding data)
   * 
   * @return <code>true</code> if all went well and <code>false</code> if not 
   */
  protected boolean getTriplesFromElementAndPredicate(final String sPosition, final String sCode, final String sPredicate, final String sRange, ArrayList<Triple> aResults)
  {
    String sFctName = "TripleManager.getTriplesFromElementAndPredicate" ;
    
    // This function is read only, hence it doesn't need a registered user
    //
    int iUserId = -1 ;
    if (null != _sessionElements)
      iUserId = _sessionElements.getPersonId() ;
    
    if ((null == sCode) || "".equals(sCode) || (null == aResults))
    {
      Logger.trace(sFctName + ": bad parameter", iUserId, Logger.TraceLevel.ERROR) ;
      return false ;
    }
    
    if ((null == sPosition) || ((false == "object".equals(sPosition)) && (false == "subject".equals(sPosition))))
    {
      Logger.trace(sFctName + ": wrong position parameter", iUserId, Logger.TraceLevel.ERROR) ;
      return false ;
    }
    
    // Exact search or wider scope?
    //
    int iExtendedLevel = 0 ;
    
    // Search all traits at lemma level
    //
    if ("%".equals(sRange) && QuadrifoliumFcts.isFlexCode(sCode))
      iExtendedLevel = 1 ;
    if ("%%".equals(sRange))
    {
      if      (QuadrifoliumFcts.isFlexCode(sCode))
        iExtendedLevel = 2 ;
      else if (QuadrifoliumFcts.isLemmaCode(sCode))
        iExtendedLevel = 1 ;
    }
    
    //
    //
    boolean bExistPredicate = true ;
    if ((null == sPredicate) || "".equals(sPredicate) || "*".equals(sPredicate))
      bExistPredicate = false ;
    
    // SQL query to get all triples for an element (and a predicate)
    //
    String sqlText = "SELECT * FROM triple WHERE " ;
    if (iExtendedLevel > 0)
      sqlText = "(" ;
    sqlText += sPosition + " = ?" ;
    
    for (int i = 0 ; i < iExtendedLevel ; i++)
      sqlText += " OR " + sPosition + " = ?" ;
    
    if (iExtendedLevel > 0)
      sqlText = ")" ;
    
    if (bExistPredicate)
      sqlText += " AND predicate = ?" ;
    
    _dbConnector.prepareStatememt(sqlText, Statement.NO_GENERATED_KEYS) ;
    _dbConnector.setStatememtString(1, sCode) ;
    
    int iIndex = 2 ;
    
    if (iExtendedLevel > 0)
    {
      if (QuadrifoliumFcts.isFlexCode(sCode))
      {
        _dbConnector.setStatememtString(iIndex++, QuadrifoliumFcts.getFullLemmaCode(sCode)) ;
        if (iExtendedLevel > 1)
          _dbConnector.setStatememtString(iIndex++, QuadrifoliumFcts.getConceptCode(sCode)) ;
      }
      else if (QuadrifoliumFcts.isLemmaCode(sCode))
        _dbConnector.setStatememtString(iIndex++, QuadrifoliumFcts.getConceptCode(sCode)) ;
    }
    
    if (bExistPredicate)
      _dbConnector.setStatememtString(iIndex, sPredicate) ;
    
    if (false == _dbConnector.executePreparedStatement())
    {
      Logger.trace(sFctName + ": failed query " + sqlText + " for " + sPosition + " = " + sCode + " and predicate = " + sPredicate, iUserId, Logger.TraceLevel.ERROR) ;
      return false ;
    }
    
    int iNbRecords = 0 ;
    
    ResultSet rs = _dbConnector.getResultSet() ;
    try
    {
      // Browse results
      //
      while (rs.next())
      {
        Triple triple = new Triple() ;
        fillDataFromResultSet(rs, triple, iUserId) ;
      
        // TODO remove when the DRC will have been treated properly
        //
        if ((false == UsefulConcepts.CLASSIFICATION_DRC_RC.equals(triple.getSubject())) && (false == UsefulConcepts.CLASSIFICATION_DRC_RC.equals(triple.getObject())))
          aResults.add(triple) ;
        
        iNbRecords++ ;
      }
      
      if (0 == iNbRecords)
      {
        Logger.trace(sFctName + ": no triple found for object \"" + sCode + "\" and predicate \"" + sPredicate + "\".", iUserId, Logger.TraceLevel.SUBSTEP) ;
        _dbConnector.closePreparedStatement() ;
        return true ;
      }
    }
    catch(SQLException ex)
    {
      Logger.trace(sFctName + ": DBConnector.dbSelectPreparedStatement: executeQuery failed for preparedStatement " + sqlText, -1, Logger.TraceLevel.ERROR) ;
      Logger.trace(sFctName + ": SQLException: " + ex.getMessage(), -1, Logger.TraceLevel.ERROR) ;
      Logger.trace(sFctName + ": SQLState: " + ex.getSQLState(), -1, Logger.TraceLevel.ERROR) ;
      Logger.trace(sFctName + ": VendorError: " +ex.getErrorCode(), -1, Logger.TraceLevel.ERROR) ;        
    }
    
    Logger.trace(sFctName + ": found " + iNbRecords + " triples for concept " + sCode + " and predicate " + sPredicate, iUserId, Logger.TraceLevel.SUBDETAIL) ;
    
    _dbConnector.closeResultSet() ;
    _dbConnector.closePreparedStatement() ;
    
    return true ;
  }

  /**
   * Get the flex codes for all flex from a lemma with proper attributes
   * 
   * @param sLemmaCode  Code of the lemma to find flex of
   * @param aAttributes Attributes the required flex must fulfill
   * @param aResults    Codes of found flex
   * 
   * @return @return <code>true</code> if all went well and <code>false</code> if not
   */
  public boolean getTriplesFromElementAndAttributes(final String sLemmaCode, final ArrayList<SearchAttribute> aAttributes, ArrayList<String> aResults)
  {
    String sFctName = "TripleManager.getTriplesFromElementAndAttributes" ;
    
    // This function is read only, hence it doesn't need a registered user
    //
    int iUserId = -1 ;
    if (null != _sessionElements)
      iUserId = _sessionElements.getPersonId() ;
    
    if ((null == sLemmaCode) || "".equals(sLemmaCode) || (null == aResults) || (null == aAttributes) || aAttributes.isEmpty())
    {
      Logger.trace(sFctName + ": bad parameter", iUserId, Logger.TraceLevel.ERROR) ;
      return false ;
    }
    
    // Create query
    //
    String sQuery = "SELECT TRIPLE0.subject FROM triple TRIPLE0" ;
    for (int i = 1 ; i < aAttributes.size() ; i++)
      sQuery += ", triple TRIPLE" + i ;
    
    sQuery += " WHERE TRIPLE0.subject LIKE ?" ;
    
    for (int iAttributeIndex = 0 ; iAttributeIndex < aAttributes.size() ; iAttributeIndex++)
    {
      String sAttributeIndex = " TRIPLE" + iAttributeIndex + "." ;
      
      // Connection to TRIPLE0 as a lemma or a flex
      //
      sQuery += " AND LEFT(TRIPLE0.subject, " + QuadrifoliumFcts.LEMMA_CODE_LEN + ") = LEFT(" + sAttributeIndex + "subject, " + QuadrifoliumFcts.LEMMA_CODE_LEN + ") AND " ;
      
      // Specific attributes
      //
      sQuery += sAttributeIndex + "predicate = ? AND " + sAttributeIndex + "object = ?" ;
    }
    
    _dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
    
    int iTraitIndex = 1 ;
    
    _dbConnector.setStatememtString(iTraitIndex++, sLemmaCode + "%") ;
    
    for (SearchAttribute attribute : aAttributes)
    {
      _dbConnector.setStatememtString(iTraitIndex++, attribute.getPredicate()) ;
      _dbConnector.setStatememtString(iTraitIndex++, attribute.getObject()) ;
    }
    
    if (false == _dbConnector.executePreparedStatement())
    {
      Logger.trace(sFctName + ": failed query " + sQuery + "for lemma " + sLemmaCode, iUserId, Logger.TraceLevel.ERROR) ;
      return false ;
    }
    
    int iNbRecords = 0 ;
    
    ResultSet rs = _dbConnector.getResultSet() ;
    try
    {
      // Browse results
      //
      while (rs.next())
      {
        aResults.add(rs.getString("subject")) ;
        
        iNbRecords++ ;
      }
      
      if (0 == iNbRecords)
      {
        Logger.trace(sFctName + ": no triple found for lemma \"" + sLemmaCode, iUserId, Logger.TraceLevel.SUBSTEP) ;
        _dbConnector.closePreparedStatement() ;
        return true ;
      }
    }
    catch(SQLException ex)
    {
      Logger.trace(sFctName + ": DBConnector.dbSelectPreparedStatement: executeQuery failed for preparedStatement " + sQuery, -1, Logger.TraceLevel.ERROR) ;
      Logger.trace(sFctName + ": SQLException: " + ex.getMessage(), -1, Logger.TraceLevel.ERROR) ;
      Logger.trace(sFctName + ": SQLState: " + ex.getSQLState(), -1, Logger.TraceLevel.ERROR) ;
      Logger.trace(sFctName + ": VendorError: " +ex.getErrorCode(), -1, Logger.TraceLevel.ERROR) ;        
    }
    
    Logger.trace(sFctName + ": found " + iNbRecords + " flex for Lemma " + sLemmaCode, iUserId, Logger.TraceLevel.SUBDETAIL) ;
    
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
