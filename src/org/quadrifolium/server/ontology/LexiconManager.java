package org.quadrifolium.server.ontology;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.quadrifolium.server.DBConnector;
import org.quadrifolium.server.Logger;
import org.quadrifolium.shared.ontology.OntologyLexicon;
import org.quadrifolium.shared.util.QuadrifoliumFcts;

/** 
 * Object in charge of Read/Write operations in the <code>lexique</code> 
 *   
 */
public class LexiconManager  
{	
	protected final DBConnector _dbConnector ;
	protected final int         _iUserId ;
	
	/**
	 * Constructor 
	 */
	public LexiconManager(final int iUserId, final DBConnector dbConnector)
	{
		_dbConnector = dbConnector ;
		_iUserId     = iUserId ;
	}

	/**
	  * Insert a Lexicon object in database
	  * 
	  * @param dataToInsert Lexicon to be inserted
	  *
	  * @return <code>true</code> if successful, <code>false</code> if not
	  */
	public boolean insertData(final OntologyLexicon dataToInsert)
	{
		String sFctName = "LexiconManager.insertData" ;
		
		if ((null == _dbConnector) || (null == dataToInsert))
		{
			Logger.trace(sFctName + ": invalid parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		boolean bInsertLemma = true ;
		if ("".equals(dataToInsert.getLemma()))
			bInsertLemma = false ;
		
		String sQuery = "INSERT INTO lexique (label, code, grammar, frequency, lemma) VALUES (?, ?, ?, ?, ?)" ;
		
		if (false == bInsertLemma)
			sQuery = "INSERT INTO lexique (label, code, grammar, frequency) VALUES (?, ?, ?, ?)" ;
		
		_dbConnector.prepareStatememt(sQuery, Statement.RETURN_GENERATED_KEYS) ;
		if (null == _dbConnector.getPreparedStatement())
		{
			Logger.trace(sFctName + ": cannot get Statement", _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closeAll() ;
			return false ;
		}
		
		_dbConnector.setStatememtString(1, dataToInsert.getLabel()) ;
		_dbConnector.setStatememtString(2, dataToInsert.getCode()) ;
		_dbConnector.setStatememtString(3, dataToInsert.getGrammar()) ;
		_dbConnector.setStatememtString(4, dataToInsert.getFrequency()) ;
		
		if (bInsertLemma)
			_dbConnector.setStatememtString(5, dataToInsert.getLemma()) ;
		
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
		
		Logger.trace(sFctName +  ": user " + _iUserId + " successfuly recorded lexicon " + dataToInsert.getCode() + ":" + dataToInsert.getLabel(), _iUserId, Logger.TraceLevel.STEP) ;
		
		return true ;
	}
	
	/**
	  * Update a Lexicon in database
	  * 
	  * @return true if successful, false if not
	  * 
	  * @param dataToUpdate Lexicon to be updated
	  */
	public boolean updateData(OntologyLexicon dataToUpdate)
	{
		if ((null == _dbConnector) || (null == dataToUpdate))
		{
			Logger.trace("LexiconManager.updateData: bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		OntologyLexicon foundData = new OntologyLexicon() ;
		if (false == existData(dataToUpdate.getCode(), foundData))
			return false ;
		
		if (foundData.equals(dataToUpdate))
		{
			Logger.trace("LexiconManager.updateData: Trait to update (" + dataToUpdate.getCode() + " / " + dataToUpdate.getLabel() + ") unchanged; nothing to do", _iUserId, Logger.TraceLevel.SUBSTEP) ;
			return true ;
		}
		
		return forceUpdateData(dataToUpdate) ;
	}
		
	/**
	  * Check if there is any Lexicon with this code in database and, if true get its content
	  * 
	  * @return True if found, else false
	  * 
	  * @param sCode     Code of Lexicon to check
	  * @param foundData Lexicon to get existing information
	  */
	public boolean existData(final String sCode, final OntologyLexicon foundData)
	{
		String sFctName = "LexiconManager.existData" ;
		
		if ((null == _dbConnector) || (null == sCode) || (null == foundData))
		{
			Logger.trace(sFctName + ": bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		String sQuery = "SELECT * FROM lexique WHERE code = ?" ;
		
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
			Logger.trace(sFctName + ": no Lexicon found for object = " + sCode, _iUserId, Logger.TraceLevel.WARNING) ;
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
	  * Check if a Quadrifolium code has already been attributed to a Lexicon concept (as a 5 chars string)  
	  * 
	  * @param sConceptCode Code of a Lexicon concept (5 chars)
	  * 
	  * @return The Quadrifolium concept code if found, <code>""</code> if not
	  * 
	  */
	public String get4foliumConcept(final String sConceptCode)
	{
		String sFctName = "LexiconManager.get4foliumConcept" ;
		
		if ((null == _dbConnector) || (null == sConceptCode))
		{
			Logger.trace(sFctName + ": bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return "" ;
		}
		
		// Get all lemmas attached to the concept
		//
		String sQuery = "SELECT * FROM lexique WHERE code LIKE ?" ;
		
		_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
		_dbConnector.setStatememtString(1, sConceptCode + "%") ;
	   		
		if (false == _dbConnector.executePreparedStatement())
		{
			Logger.trace(sFctName + ": failed query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return "" ;
		}
	   		
		ResultSet rs = _dbConnector.getResultSet() ;
		if (null == rs)
		{
			Logger.trace(sFctName + ": no Lexicon found for code starting from " + sConceptCode, _iUserId, Logger.TraceLevel.WARNING) ;
			_dbConnector.closePreparedStatement() ;
			return "" ;
		}
		
		String sResult = "" ;
		
		// Look for a lemma with a Quadrifolium lemma code attached to it
		//
		try
		{
	    while (rs.next() && "".equals(sResult))
	    {
	    	OntologyLexicon foundData = new OntologyLexicon() ;
	    	fillDataFromResultSet(rs, foundData, _iUserId) ;
	    	
	    	if (false == "".equals(foundData.getLemma()))
	    		sResult = QuadrifoliumFcts.getConceptCode(foundData.getLemma()) ;
	    }
		} catch (SQLException e)
		{
			Logger.trace(sFctName + ": exception when iterating results " + e.getMessage(), _iUserId, Logger.TraceLevel.ERROR) ;
		}
		
		_dbConnector.closeResultSet() ;
		_dbConnector.closePreparedStatement() ;
				
		return sResult ;
	}
	
	/**
	  * Update a Lexicon in database
	  * 
	  * @return <code>true</code> if creation succeeded, <code>false</code> if not
	  * 
	  * @param  dataToUpdate Lexicon to update
	  */
	private boolean forceUpdateData(final OntologyLexicon dataToUpdate)
	{
		String sFctName = "LexiconManager.forceUpdateData" ;
		
		if ((null == _dbConnector) || (null == dataToUpdate))
		{
			Logger.trace(sFctName + ": bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		// Prepare SQL query
		//
		String sQuery = "UPDATE lexique SET label = ?, grammar = ?, frequency = ?, lemma = ?" +
				                          " WHERE code = ?" ; 
		
		_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
		if (null == _dbConnector.getPreparedStatement())
		{
			Logger.trace(sFctName + ": cannot get Statement", _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
		
		_dbConnector.setStatememtString(1, dataToUpdate.getLabel()) ;
		_dbConnector.setStatememtString(2, dataToUpdate.getGrammar()) ;
		_dbConnector.setStatememtString(3, dataToUpdate.getFrequency()) ;
		
		if ("".equals(dataToUpdate.getLemma()))
			_dbConnector.setStatememtString(4, null) ;
		else
			_dbConnector.setStatememtString(4, dataToUpdate.getLemma()) ;
		
		_dbConnector.setStatememtString(5, dataToUpdate.getCode()) ;
		
		// Execute query 
		//
		int iNbAffectedRows = _dbConnector.executeUpdatePreparedStatement(false) ;
		if (-1 == iNbAffectedRows)
		{
			Logger.trace(sFctName + ": failed query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}

		Logger.trace(sFctName + ": updated data for Lexicon " + dataToUpdate.getCode(), _iUserId, Logger.TraceLevel.SUBSTEP) ;
		
		_dbConnector.closePreparedStatement() ;
		
		return true ;
	}
	
	/**
	  * Initialize a Lexicon from a query ResultSet 
	  * 
	  * @param rs        ResultSet of a query
	  * @param foundData Lexicon to fill
	  * 
	  */
	public static void fillDataFromResultSet(final ResultSet rs, OntologyLexicon foundData, final int iUserId)
	{
		if ((null == rs) || (null == foundData))
			return ;
		
		try
		{
			foundData.setId(rs.getInt("id")) ;
    	foundData.setCode(rs.getString("code")) ;
    	foundData.setLabel(rs.getString("label")) ;
    	foundData.setGrammar(rs.getString("grammar")) ;
    	foundData.setFrequency(rs.getString("frequency")) ;
    	
    	String sLemma = rs.getString("lemma") ;
    	if (null == sLemma)
    		foundData.setLemma("") ;
    	else
    		foundData.setLemma(sLemma) ;
		} 
		catch (SQLException e) {
			Logger.trace("LexiconManager.fillDataFromResultSet: exception when processing results set: " + e.getMessage(), iUserId, Logger.TraceLevel.ERROR) ;
		}
	}		
}
