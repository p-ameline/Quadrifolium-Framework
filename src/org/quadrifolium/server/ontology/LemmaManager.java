package org.quadrifolium.server.ontology;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.quadrifolium.server.DBConnector;
import org.quadrifolium.server.Logger;
import org.quadrifolium.server.ontology_base.ChangeHistory;
import org.quadrifolium.server.ontology_base.HistoryLemma;
import org.quadrifolium.server.ontology_base.ChangeHistory.ChangeType;
import org.quadrifolium.server.ontology_base.ChangeHistory.TableType;
import org.quadrifolium.server.util.QuadrifoliumServerFcts;
import org.quadrifolium.shared.ontology.Lemma;
import org.quadrifolium.shared.rpc_util.SessionElements;

/** 
 * Object in charge of Read/Write operations for the <code>lemma</code> table 
 *   
 */
public class LemmaManager  
{	
	protected final DBConnector     _dbConnector ;
	protected final SessionElements _sessionElements ;
	
	/**
	 * Constructor
	 * 
	 * @param sessionElements Can be null if only using read only functions
	 */
	public LemmaManager(final SessionElements sessionElements, final DBConnector dbConnector)
	{
		_dbConnector     = dbConnector ;
		_sessionElements = sessionElements ;
	}

	/**
	 * Insert a Lemma object in database, and complete this object with insertion created information<br>
	 * <br>
	 * This function needs a registered user.
	 * 
	 * @param dataToInsert Lemma to be inserted
	 *
	 * @return <code>true</code> if successful, <code>false</code> if not
	 */
	public boolean insertData(final Lemma dataToInsert)
	{
		String sFctName = "LemmaManager.insertData" ;
		
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
		
		String sQuery = "INSERT INTO lemma (label, code, lang) VALUES (?, ?, ?)" ;
		
		_dbConnector.prepareStatememt(sQuery, Statement.RETURN_GENERATED_KEYS) ;
		if (null == _dbConnector.getPreparedStatement())
		{
			Logger.trace(sFctName + ": cannot get Statement", _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
			_dbConnector.closeAll() ;
			return false ;
		}
		
		_dbConnector.setStatememtString(1, dataToInsert.getLabel()) ;
		_dbConnector.setStatememtString(2, dataToInsert.getCode()) ;
		_dbConnector.setStatememtString(3, dataToInsert.getLanguage()) ;
		
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
		
		return historize(dataToInsert, ChangeType.create) ;
	}
	
	/**
	 * Historize a change
	 * 
	 * @param lemma      Lemma to historize (new one for new and update, old one for delete)
	 * @param changeType Modification type (add, update, delete)
	 */
	protected boolean historize(final Lemma lemma, ChangeHistory.ChangeType changeType)
	{
		// Create an historization object and save it
		//
		HistoryLemma lemmaHistory = new HistoryLemma(lemma) ;
		
		LemmaHistoryManager historyManager = new LemmaHistoryManager(_sessionElements, _dbConnector) ;
		if (false == historyManager.insertData(lemmaHistory))
			return false ;
		
		// Create a Change history record
		//
		ChangeHistory changeHistory = new ChangeHistory(_sessionElements.getSessionId(), "", TableType.lemma, changeType, lemma.getId(), lemmaHistory.getId()) ;
		
		ChangeHistoryManager changeManager = new ChangeHistoryManager(_sessionElements.getPersonId(), _dbConnector) ;
		return changeManager.insertData(changeHistory) ;
	}
	
	/**
	 * Update a Lemma in database<br>
	 * <br>
	 * This function needs a registered user.
	 * 
	 * @return true if successful, false if not
	 * 
	 * @param dataToUpdate Lemma to be updated
	 */
	public boolean updateData(Lemma dataToUpdate)
	{
		String sFctName = "LemmaManager.updateData" ;
		
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
		
		Lemma foundData = new Lemma() ;
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
	 * Check if there is any Lemma with this code in database and, if true get its content<br>
	 * <br>
	 * This function is read only, hence it doesn't need a registered user.
	 * 
	 * @return True if found, else false
	 * 
	 * @param sCode     Code of Lemma to check
	 * @param foundData Lemma to get existing information
	 */
	public boolean existData(final String sCode, final Lemma foundData)
	{
		String sFctName = "LemmaManager.existData" ;
		
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
		
		String sQuery = "SELECT * FROM lemma WHERE code = ?" ;
		
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
			Logger.trace(sFctName + ": no Lemma found for code = " + sCode, iUserId, Logger.TraceLevel.WARNING) ;
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
	 * Get all Lemmas for this concept (and this language if specified)<br>
	 * <br>
	 * This function is read only, hence it doesn't need a registered user.
	 * 	  * 
	 * @return <code>true</code> if everything went well, <code>false</code> if not
	 * 
	 * @param sConceptCode Code of Concept to look for corresponding lemmas
	 * @param sLanguage    Language if a constraint, <code>null</code> or <code>""</code> if all lemmas are to be returned
	 * @param aResults     List of lemmas to fill (not cleared before adding data) 
	 */
	public boolean existDataForConcept(final String sConceptCode, final String sLanguage, ArrayList<Lemma> aResults)
	{
		String sFctName = "LemmaManager.existDataForConcept" ;
		
		// This function is read only, hence it doesn't need a registered user
		//
		int iUserId = -1 ;
		if (null != _sessionElements)
			iUserId = _sessionElements.getPersonId() ;
		
		if ((null == _dbConnector) || (null == sConceptCode) || "".equals(sConceptCode) || (null == aResults))
		{
			Logger.trace(sFctName + ": bad parameter", iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		boolean bCheckLanguage = true ;
		if ((null == sLanguage) || "".equals(sLanguage))
			bCheckLanguage = false ;
		
		String sQuery = "SELECT * FROM lemma WHERE code LIKE ?" ;
		
		if (bCheckLanguage)
			sQuery += " AND lang = ?" ;
		
		_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
		_dbConnector.setStatememtString(1, sConceptCode + "%") ;
	   		
		if (bCheckLanguage)
			_dbConnector.setStatememtString(2, sLanguage) ;
		
		if (false == _dbConnector.executePreparedStatement())
		{
			Logger.trace(sFctName + ": failed query " + sQuery, iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
	   		
		ResultSet rs = _dbConnector.getResultSet() ;
		if (null == rs)
		{
			if (bCheckLanguage)
				Logger.trace(sFctName + ": no Lemma found for concept = " + sConceptCode + " and language = " + sLanguage , iUserId, Logger.TraceLevel.WARNING) ;
			else
				Logger.trace(sFctName + ": no Lemma found for concept = " + sConceptCode + " whatever the language.", iUserId, Logger.TraceLevel.WARNING) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
		
		int iCount = 0 ;
		
		try
		{
	    while (rs.next())
	    {
	    	Lemma foundData = new Lemma() ;
	    	fillDataFromResultSet(rs, foundData, iUserId) ;
	    	
	    	aResults.add(foundData) ;
	    	
	    	iCount++ ;
	    }
		} catch (SQLException e)
		{
			if (bCheckLanguage)
				Logger.trace(sFctName + ": exception when iterating results for concept = " + sConceptCode + " and language = " + sLanguage , iUserId, Logger.TraceLevel.ERROR) ;
			else
				Logger.trace(sFctName + ": exception when iterating results for concept = " + sConceptCode + " whatever the language.", iUserId, Logger.TraceLevel.ERROR) ;
		}
		
		if (bCheckLanguage)
			Logger.trace(sFctName + ": " + iCount + " Lemma(s) found for concept " + sConceptCode + " and language " + sLanguage, iUserId, Logger.TraceLevel.DETAIL) ;
		else
			Logger.trace(sFctName + ": " + iCount + " Lemma(s) found for concept " + sConceptCode + " whatever the language.", iUserId, Logger.TraceLevel.DETAIL) ;
		
		_dbConnector.closeResultSet() ;
		_dbConnector.closePreparedStatement() ;
				
		return true ;
	}
	
	/**
	 * Update a Lemma in database<br>
	 * <br>
	 * This function needs a registered user.
	 * 
	 * @return <code>true</code> if creation succeeded, <code>false</code> if not
	 * 
	 * @param  dataToUpdate Lemma to update
	 */
	private boolean forceUpdateData(final Lemma dataToUpdate)
	{
		String sFctName = "LemmaManager.forceUpdateData" ;
		
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
		String sQuery = "UPDATE lemma SET label = ?, code = ?, lang = ?" +
				                          " WHERE id = ?" ; 
		
		_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
		if (null == _dbConnector.getPreparedStatement())
		{
			Logger.trace(sFctName + ": cannot get Statement", _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
		
		_dbConnector.setStatememtString(1, dataToUpdate.getLabel()) ;
		_dbConnector.setStatememtString(2, dataToUpdate.getCode()) ;
		_dbConnector.setStatememtString(3, dataToUpdate.getLanguage()) ;
		
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

		Logger.trace(sFctName + ": updated data for Lemma " + dataToUpdate.getCode(), _sessionElements.getPersonId(), Logger.TraceLevel.SUBSTEP) ;
		
		_dbConnector.closePreparedStatement() ;
		
		return historize(dataToUpdate, ChangeType.change) ;
	}

	/**
	 * Get the next available code for a given concept<br>
	 * <br>
	 * This function needs a registered user.
	 * 
	 * @param sQConceptCode Code of the concept this lemma is a word for, can be <code>""</code>
	 * 
	 * @return Next available lemma code
	 */
	public String getNextLemmaCode(final String sQConceptCode)
	{
		String sFctName = "LemmaManager.getNextLemmaCode" ;
		
		// This function needs a registered user
		//
		if (null == _sessionElements)
		{
			Logger.trace(sFctName + ": no session elements.", -1, Logger.TraceLevel.ERROR) ;
			return "" ;
		}
		
		String sQuery = "SELECT MAX(code) AS MAX_CODE FROM lemma" ;
		
		// Note that the concept remains virtual, hence if a non void concept code is present, it means that the lemma table
		// already contains at least a row with a lemma attached to this concept 
		//
		boolean bForConcept = false ;
		if ((null != sQConceptCode) && (false == "".equals(sQConceptCode)))
		{
			bForConcept = true ;
			sQuery += " WHERE code LIKE ?" ;
		}
		
		_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
		if (null == _dbConnector.getPreparedStatement())
		{
			Logger.trace(sFctName + ": cannot get Statement", _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return "" ;
		}
		
		if (bForConcept)
			_dbConnector.setStatememtString(1, sQConceptCode + "%") ;
		
		if (false == _dbConnector.executePreparedStatement())
		{
			Logger.trace(sFctName + ": failed query " + sQuery, _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return "" ;
		}
	   		
		ResultSet rs = _dbConnector.getResultSet() ;
		if (null == rs)
		{
			// Abnormal since a lemma for this concept has to exist in this case
			if (bForConcept)
				Logger.trace(sFctName + ": no max code found in table lemma for concept = " + sQConceptCode, _sessionElements.getPersonId(), Logger.TraceLevel.WARNING) ;
			else
				Logger.trace(sFctName + ": cannot find max code in table lemma", _sessionElements.getPersonId(), Logger.TraceLevel.WARNING) ;
			_dbConnector.closePreparedStatement() ;
			return "" ;
		}
				
		String sPreviousLemmaCode = "" ;
		
		try
		{
	    if (rs.next())
	    {
	    	String sMax = rs.getString("MAX_CODE") ;
	    	
	    	if (null != sMax)		// when table is empty, it returns a null value
	    		sPreviousLemmaCode = sMax ;
	    }
		} catch (SQLException e)
		{
			Logger.trace(sFctName + ": exception when iterating results " + e.getMessage(), _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
		}
		
		_dbConnector.closeResultSet() ;
		_dbConnector.closePreparedStatement() ;
		
		// Should only occur when table is empty
		//
		if ("".equals(sPreviousLemmaCode))
			return QuadrifoliumServerFcts.getFirstLemmaCodeForConcept(QuadrifoliumServerFcts.getFirstConceptCode()) ;
		
		// Returns next available lemma code for an existing concept
		//
		if (bForConcept)
			return QuadrifoliumServerFcts.getNextLemmaCodeForExistingConcept(sPreviousLemmaCode) ;
		
		// Returns first lemma code for a new concept
		//
		return QuadrifoliumServerFcts.getNextLemmaCodeForNewConcept(sPreviousLemmaCode) ;
	}
	
	/**
	  * Initialize a Lemma from a query ResultSet 
	  * 
	  * @param rs        ResultSet of a query
	  * @param foundData Lemma to fill
	  * 
	  */
	public static void fillDataFromResultSet(final ResultSet rs, Lemma foundData, final int iUserId)
	{
		if ((null == rs) || (null == foundData))
			return ;
		
		try
		{
			foundData.setId(rs.getInt("id")) ;
    	foundData.setCode(rs.getString("code")) ;
    	foundData.setLabel(rs.getString("label")) ;
    	foundData.setLanguage(rs.getString("lang")) ;
		} 
		catch (SQLException e) {
			Logger.trace("LemmaManager.fillDataFromResultSet: exception when processing results set: " + e.getMessage(), iUserId, Logger.TraceLevel.ERROR) ;
		}
	}		
}
