package org.quadrifolium.server.ontology;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.quadrifolium.server.DBConnector;
import org.quadrifolium.server.Logger;
import org.quadrifolium.server.util.QuadrifoliumServerFcts;
import org.quadrifolium.shared.ontology.Flex;
import org.quadrifolium.shared.util.QuadrifoliumFcts;

/** 
 * Object in charge of Read/Write operations for the <code>flex</code> table 
 *   
 */
public class FlexManager  
{	
	protected final DBConnector _dbConnector ;
	protected final int         _iUserId ;
	
	/**
	 * Constructor 
	 */
	public FlexManager(final int iUserId, final DBConnector dbConnector)
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
	public boolean insertData(Flex dataToInsert)
	{
		String sFctName = "FlexManager.insertData" ;
		
		if ((null == _dbConnector) || (null == dataToInsert))
		{
			Logger.trace(sFctName + ": invalid parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		String sQuery = "INSERT INTO flex (label, code, lang) VALUES (?, ?, ?)" ;
		
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
		
		Logger.trace(sFctName +  ": user " + _iUserId + " successfuly recorded flex " + dataToInsert.getCode() + ":" + dataToInsert.getLabel(), _iUserId, Logger.TraceLevel.STEP) ;
		
		return true ;
	}
	
	/**
	  * Update a Flex in database
	  * 
	  * @return true if successful, false if not
	  * 
	  * @param dataToUpdate Flex to be updated
	  */
	public boolean updateData(Flex dataToUpdate)
	{
		if ((null == _dbConnector) || (null == dataToUpdate))
		{
			Logger.trace("FlexManager.updateData: bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		Flex foundData = new Flex() ;
		if (false == existData(dataToUpdate.getCode(), foundData))
			return false ;
		
		if (foundData.equals(dataToUpdate))
		{
			Logger.trace("LemmaManager.updateData: Trait to update (" + dataToUpdate.getCode() + " / " + dataToUpdate.getLabel() + ") unchanged; nothing to do", _iUserId, Logger.TraceLevel.SUBSTEP) ;
			return true ;
		}
		
		return forceUpdateData(dataToUpdate) ;
	}
		
	/**
	  * Check if there is any Flex with this code in database and, if true get its content
	  * 
	  * @return True if found, else false
	  * 
	  * @param sCode     Code of Lexicon to check
	  * @param foundData Flex to get existing information
	  */
	public boolean existData(final String sCode, final Flex foundData)
	{
		String sFctName = "FlexManager.existData" ;
		
		if ((null == _dbConnector) || (null == sCode) || (null == foundData))
		{
			Logger.trace(sFctName + ": bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		String sQuery = "SELECT * FROM flex WHERE code = ?" ;
		
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
			Logger.trace(sFctName + ": no Lemma found for code = " + sCode, _iUserId, Logger.TraceLevel.WARNING) ;
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
	  * Get all inflections for this lemma
	  * 
	  * @return <code>true</code> if everything went well, <code>false</code> if not
	  * 
	  * @param sLemmaCode Code of lemma to look for corresponding inflections
	  * @param foundData  aResults list of inflections to fill (not cleared before adding data) 
	  */
	public boolean existDataForLemma(final String sLemmaCode, ArrayList<Flex> aResults)
	{
		String sFctName = "FlexManager.existDataForLemma" ;
		
		if ((null == _dbConnector) || (null == sLemmaCode) || "".equals(sLemmaCode) || (null == aResults))
		{
			Logger.trace(sFctName + ": bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		String sQuery = "SELECT * FROM flex WHERE code LIKE ?" ;
		
		_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
		_dbConnector.setStatememtString(1, sLemmaCode + "%") ;
	   		
		if (false == _dbConnector.executePreparedStatement())
		{
			Logger.trace(sFctName + ": failed query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
	   		
		ResultSet rs = _dbConnector.getResultSet() ;
		if (null == rs)
		{
			Logger.trace(sFctName + ": no Flex found for lemma = " + sLemmaCode, _iUserId, Logger.TraceLevel.WARNING) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
		
		int iCount = 0 ;
		
		try
		{
	    while (rs.next())
	    {
	    	Flex foundData = new Flex() ;
	    	fillDataFromResultSet(rs, foundData, _iUserId) ;
	    	
	    	aResults.add(foundData) ;
	    	
	    	iCount++ ;
	    }
		} catch (SQLException e)
		{
			Logger.trace(sFctName + ": exception when iterating results for lemma = " + sLemmaCode, _iUserId, Logger.TraceLevel.ERROR) ;
		}
		
		if (iCount > 1)
			Logger.trace(sFctName + ": " + iCount + " inflections found for lemma " + sLemmaCode, _iUserId, Logger.TraceLevel.DETAIL) ;
		else
			Logger.trace(sFctName + ": " + iCount + " inflection found for lemma " + sLemmaCode, _iUserId, Logger.TraceLevel.DETAIL) ;
		
		_dbConnector.closeResultSet() ;
		_dbConnector.closePreparedStatement() ;
				
		return true ;
	}
	
	/**
	  * Update a Flex in database
	  * 
	  * @return <code>true</code> if creation succeeded, <code>false</code> if not
	  * 
	  * @param  dataToUpdate Flex to update
	  */
	private boolean forceUpdateData(final Flex dataToUpdate)
	{
		String sFctName = "FlexManager.forceUpdateData" ;
		
		if ((null == _dbConnector) || (null == dataToUpdate))
		{
			Logger.trace(sFctName + ": bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		// Prepare SQL query
		//
		String sQuery = "UPDATE flex SET label = ?, code = ?, lang = ?" +
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

		Logger.trace(sFctName + ": updated data for Flex " + dataToUpdate.getCode(), _iUserId, Logger.TraceLevel.SUBSTEP) ;
		
		_dbConnector.closePreparedStatement() ;
		
		return true ;
	}
	
	/**
	 * Get the next available code for a given lemma
	 * 
	 * @param sQLemmaCode Code of the lemma this flex is a flexed form for, can be <code>""</code>
	 * 
	 * @return Next available flex code
	 */
	public String getNextFlexCode(final String sQLemmaCode)
	{
		String sFctName = "FlexManager.getNextFlexCode" ;
		
		if ((null == sQLemmaCode) || "".equals(sQLemmaCode))
		{
			Logger.trace(sFctName + ": empty parameter.", _iUserId, Logger.TraceLevel.ERROR) ;
			return "" ;
		}
		
		String sQuery = "SELECT MAX(code) AS MAX_CODE FROM flex WHERE code LIKE ?" ;
		
		_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
		if (null == _dbConnector.getPreparedStatement())
		{
			Logger.trace(sFctName + ": cannot get Statement", _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return "" ;
		}
		
		_dbConnector.setStatememtString(1, sQLemmaCode + "%") ;
		
		if (false == _dbConnector.executePreparedStatement())
		{
			Logger.trace(sFctName + ": failed query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return "" ;
		}
	   		
		ResultSet rs = _dbConnector.getResultSet() ;
		if (null == rs)
		{
			Logger.trace(sFctName + ": no max code found in table flex for lemma = " + sQLemmaCode, _iUserId, Logger.TraceLevel.WARNING) ;
			_dbConnector.closePreparedStatement() ;
			return "" ;
		}
		
		String sNextFlexCode = "" ;
		
		try
		{
	    if (rs.next())
	    {
	    	String sMax = rs.getString("MAX_CODE") ;
	    	
	    	if (null != sMax)		// when table is empty, it returns a null value
	    	{
	    		if (sQLemmaCode.equals(QuadrifoliumFcts.getFullLemmaCode(sMax)))
	    			sNextFlexCode = QuadrifoliumServerFcts.getNextFlexCodeForLemma(sMax) ;
	    		else
	    			sNextFlexCode = QuadrifoliumServerFcts.getFirstFlexCodeForLemma(sQLemmaCode) ;
	    	}
	    	// Empty database (?)
	    	//
		  	else
		  		sNextFlexCode = QuadrifoliumServerFcts.getFirstFlexCodeForLemma(sQLemmaCode) ;
	    }
		} catch (SQLException e)
		{
			Logger.trace(sFctName + ": exception when iterating results " + e.getMessage(), _iUserId, Logger.TraceLevel.ERROR) ;
		}
		
		_dbConnector.closeResultSet() ;
		_dbConnector.closePreparedStatement() ;
		
		return sNextFlexCode ;
	}
	
	/**
	  * Initialize a Flex from a query ResultSet 
	  * 
	  * @param rs        ResultSet of a query
	  * @param foundData Flex to fill
	  * 
	  */
	public static void fillDataFromResultSet(final ResultSet rs, Flex foundData, final int iUserId)
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
			Logger.trace("FlexManager.fillDataFromResultSet: exception when processing results set: " + e.getMessage(), iUserId, Logger.TraceLevel.ERROR) ;
		}
	}		
}