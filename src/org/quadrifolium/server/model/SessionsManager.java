package org.quadrifolium.server.model;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.quadrifolium.server.DBConnector;
import org.quadrifolium.server.Logger;
import org.quadrifolium.server.database.Session;
import org.quadrifolium.shared.rpc_util.SessionElements;

/**
 * Class in charge of the "sessions" table. Quadrifolium keeps a sessions history.
 * 
 * @author Philippe
 *
 */
public class SessionsManager 
{
	private Session     _session ;
	private DBConnector _dbConnector ;
	
	public SessionsManager()
	{
		_session     = new Session() ;
		_dbConnector = new DBConnector(true, -1, DBConnector.databaseType.databaseCore) ;
	}
		
	public SessionsManager(DBConnector dbConnector)
	{
		_session     = new Session() ;
		_dbConnector = dbConnector ;
	}
	
	/**
	 * Create a new session for a given user    
	 * 
	 * @param sPersonId : user's identifier
	 * @return <code>true</code> if the new session was properly created, <code>false</code> if not
	 * 
	 **/
	public boolean createNewSession(final int iPersonId)
	{
		if (iPersonId < 0)
			return false ;
		
		if (null == _dbConnector)
			return false ;

		// First, close existing sessions (if any)
		//
		closePreviousSessions(iPersonId) ;
		
		_session.reset() ;
		
		_session.setPersonId(iPersonId) ;
		
		initSessionTokenAndDate() ;
		
		// Finally, store session in database
		//
		int iSessionId = createNewSession() ;
		if (-1 == iSessionId)
			return false ;
		
		return true ;
	}
	
	/**
	 * Close the session for a given userId and token    
	 * 
	 * @param sPersonId User's identifier
	 * @param sToken    Token
	 * 
	 **/
	public void closeSession(final int iPersonId, final String sToken)
	{
		if ((iPersonId < 0) || (null ==sToken) || sToken.equals(""))
			return ;
		
		if (false == checkConnectorAndPersonId(iPersonId, "closeSession"))
			return ;
		
		// Get formated current date
		//
		_session.setDateTimeClose(getFormatedCurrentDateTime()) ;
		_session.setToken("") ;
		
		updateSession() ;		
	}
	
	/**
	 * Delete all sessions records for a given PersonId    
	 * 
	 * @param sPersonId Person's Ldv identifier
	 * 
	 **/
	private void closePreviousSessions(final int iPersonId)
	{
		if (false == checkConnectorAndPersonId(iPersonId, "closePreviousSessions"))
			return ;
		
		String sqlText = "DELETE FROM sessions WHERE personid = ?" ;
		
		_dbConnector.prepareStatememt(sqlText, Statement.NO_GENERATED_KEYS) ;
		_dbConnector.setStatememtInt(1, iPersonId) ;
		
		// Execute prepared statement 
		//
		_dbConnector.executeUpdatePreparedStatement(false) ;
		
		_dbConnector.closePreparedStatement() ;
	}
		
	/**
	 * Check if database connector and PersonId are valid    
	 * 
	 * @param PersonId      Person's Ldv identifier
	 * @param sFonctionName Name of calling function - for logging purposes
	 * 
	 * @return <code>true</code> if all is fine, <code>false</code> elsewhere
	 * 
	 **/
	private boolean checkConnectorAndPersonId(final int iPersonId, final String sFonctionName)
	{
		if ((null == _dbConnector) || (iPersonId < 0))
		{
			if ((null == _dbConnector) && (iPersonId < 0))
			{
				Logger.trace("SessionsManager." + sFonctionName + ": null connector and empty PersonId.", -1, Logger.TraceLevel.ERROR) ;
				return false ;
			}
			
			if (null == _dbConnector)
				Logger.trace("SessionsManager." + sFonctionName + ": null connector for PersonId=" + iPersonId, -1, Logger.TraceLevel.ERROR) ;
			if (iPersonId < 0)
				Logger.trace("LdvSessionsManager." + sFonctionName + ": empty PersonId.", -1, Logger.TraceLevel.ERROR) ;
			
			return false ;
		}
		return true ;
	}
	
	/**
	 * Create a new session, populated with a Person Id    
	 * 
	 * @return person's record id if successful, -1 if failed
	 * 
	 **/
	public int createNewSession()
	{
		if (null == _dbConnector)
		{
			Logger.trace("SessionsManager.createNewSession: null connector; creation failed", -1, Logger.TraceLevel.ERROR) ;
			return -1 ;
		}
		
		String sQuery = "INSERT INTO sessions (personid, token, datetimeOpen) VALUES (?, ?, ?)" ;
		_dbConnector.prepareStatememt(sQuery, Statement.RETURN_GENERATED_KEYS) ;
		if (null == _dbConnector.getPreparedStatement())
		{
			Logger.trace("SessionsManager.createNewSession: cannot get Statement", -1, Logger.TraceLevel.ERROR) ;
			return -1 ;
		}
		
		_dbConnector.setStatememtInt(1, _session.getPersonId()) ;
		_dbConnector.setStatememtString(2, _session.getToken()) ;
		_dbConnector.setStatememtString(3, _session.getDateTimeOpen()) ;
		
		// Execute query 
		//
		int iNbAffectedRows = _dbConnector.executeUpdatePreparedStatement(true) ;
		if (-1 == iNbAffectedRows)
		{
			Logger.trace("SessionsManager.createNewSession: failed query " + sQuery, -1, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return -1 ;
		}
		
		ResultSet rs = _dbConnector.getResultSet() ;
		int iNewSessionId = -1 ;
		try
    {
	    if (rs.next())
	    	iNewSessionId = rs.getInt(1) ;
	    else
	    	Logger.trace("SessionsManager.createNewSession: cannot get Id after query " + sQuery, -1, Logger.TraceLevel.ERROR) ;
    } 
		catch (SQLException e)
    {
			Logger.trace("SessionsManager.createNewSession: exception when iterating results " + e.getMessage(), -1, Logger.TraceLevel.ERROR) ;
    }
		_dbConnector.closeResultSet() ;
		_dbConnector.closePreparedStatement() ;

		Logger.trace("SessionsManager.createNewSession: new session (" + iNewSessionId + ") created for person " + _session.getPersonId(), -1, Logger.TraceLevel.DETAIL) ;
		
   	return iNewSessionId ;
	}

	/**
	 * Sets the token and the date and time of opening    
	 * 
	 **/
	private void initSessionTokenAndDate()
	{
		// Get formated current date
		//
		_session.setDateTimeOpen(getFormatedCurrentDateTime()) ;
		
		// Create token
		//
		String sTokenSeed = _session.getPersonId() + _session.getDateTimeOpen() ;
		
		try
		{
			MessageDigest md = MessageDigest.getInstance("MD5") ;
			final byte[] md5Digest = md.digest(sTokenSeed.getBytes()) ;
	    final BigInteger md5Number = new BigInteger(1, md5Digest) ;
	    final String md5String = md5Number.toString(16) ;

	    _session.setToken(md5String)  ;
		} 
		catch (NoSuchAlgorithmException e) {
			Logger.trace("LdvSessionsManager.initSessionTokenAndDate: no MD5 algorithm found. " + e.getMessage(), -1, Logger.TraceLevel.ERROR) ;
		}    
	}
	
	/**
	 * Check if a session exists for this token and this Person's Id, and, if yes, initialize the Session object from it    
	 * 
	 * @param sLdvId  : Person's identifier
	 * @param sToken  : Token
	 * 
	 * @return <code>true</code> if the session exists, <code>false</code> if not
	 * 
	 **/
	public boolean isValidToken(final int iPersonId, final String sToken)
	{
		if (iPersonId < 0)
			return false ;
		
		if (null == _dbConnector)
			return false ;
		
		String sQuery = "SELECT * FROM sessions WHERE personid = ? AND token = ?" ;
		_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
		_dbConnector.setStatememtInt(1, iPersonId) ;
		_dbConnector.setStatememtString(2, sToken) ;
		
		// Execute prepared statement 
		//
		boolean bSuccess = _dbConnector.executePreparedStatement() ;
		if (false == bSuccess)
		{
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
		
		ResultSet rs = _dbConnector.getResultSet() ;
		if (null == rs)
		{
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
		
		boolean bResult = false ;
		try
		{
	    if (rs.next())
	    {
	    	fillDataFromResultSet(rs, iPersonId) ;
	  	  bResult = true ;
	    }
		} 
		catch(SQLException ex)
		{
			Logger.trace("SessionsManager.isValidToken: exception enumerating result ", -1, Logger.TraceLevel.ERROR) ;
			Logger.trace("SQLException: " + ex.getMessage(), -1, Logger.TraceLevel.ERROR) ;
			Logger.trace("SQLState: " + ex.getSQLState(), -1, Logger.TraceLevel.ERROR) ;
			Logger.trace("VendorError: " +ex.getErrorCode(), -1, Logger.TraceLevel.ERROR) ;
		}
		
		_dbConnector.closeResultSet() ;
		_dbConnector.closePreparedStatement() ;

		return bResult ;
	}

	/**
	 * Check if a session exists for this token and this Person's Id    
	 * 
	 * @param sLdvId  : Person's identifier
	 * @param sToken  : Token
	 * 
	 * @return <code>true</code> if the session exists, <code>false</code> if not
	 * 
	 **/
	public boolean checkTokenAndHeartbeat(final int iPersonId, final String sToken)
	{
		if (iPersonId < 0)
			return false ;
		
		// Check if the session
		//
		if (false == isValidToken(iPersonId, sToken))
			return false ;
		
		String sCurrentDateTime = getFormatedCurrentDateTime() ;
		_session.setDateTimeLastBeat(sCurrentDateTime) ;
		
		updateSession() ;
		
		return true ;
	}
	
	/**
	 * Check if a session exists for a given session element (person ID + token)    
	 * 
	 * @param sessionElements : SessionElements object to check
	 * 
	 * @return <code>true</code> if the session exists, <code>false</code> if not
	 * 
	 **/
	public boolean checkTokenAndHeartbeat(final SessionElements sessionElements)
	{
		if (null == sessionElements)
			return false ;
		
		return checkTokenAndHeartbeat(sessionElements.getPersonId(), sessionElements.getToken()) ;
	}
	
	/**
	  * Update the Session in database
	  * 
	  * @return <code>true</code> if update succeeded, <code>false</code> if not
	  * 
	  * @param  dataToUpdate Lexicon to update
	  */
	private boolean updateSession()
	{
		String sFctName = "SessionsManager.updateSession" ;
		
		if (null == _dbConnector)
		{
			Logger.trace(sFctName + ": bad connector", _session.getPersonId(), Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		if (false == _session.isReferenced())
		{
			Logger.trace(sFctName + ": invalid session (no session ID)", _session.getPersonId(), Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		// Prepare SQL query
		//
		String sQuery = "UPDATE sessions SET personid = ?, token = ?, datetimeOpen = ?, datetimeClose = ?, datetimeLastBeat = ?" +
				                          " WHERE sessionId = ?" ; 
		
		_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
		if (null == _dbConnector.getPreparedStatement())
		{
			Logger.trace(sFctName + ": cannot get Statement", _session.getPersonId(), Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}
		
		_dbConnector.setStatememtInt(1, _session.getPersonId()) ;
		_dbConnector.setStatememtString(2, _session.getToken()) ;
		_dbConnector.setStatememtString(3, _session.getDateTimeOpen()) ;
		_dbConnector.setStatememtString(4, _session.getDateTimeClose()) ;
		_dbConnector.setStatememtString(5, _session.getDateTimeLastBeat()) ;
				
		_dbConnector.setStatememtInt(6, _session.getSessionId()) ;
		
		// Execute query 
		//
		int iNbAffectedRows = _dbConnector.executeUpdatePreparedStatement(false) ;
		if (-1 == iNbAffectedRows)
		{
			Logger.trace(sFctName + ": failed query " + sQuery, _session.getPersonId(), Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}

		Logger.trace(sFctName + ": updated data for Session " + _session.getSessionId(), _session.getPersonId(), Logger.TraceLevel.SUBSTEP) ;
		
		_dbConnector.closePreparedStatement() ;
		
		return true ;
	}
	
	/**
	  * Set the date to "now" for a date whose field name is in sDateFieldName 
	  */
/*
	private boolean setDateToNow(String sDateFieldName)
	{
		if ((null == sDateFieldName) || (sDateFieldName.equals("")) || (-1 == _iId))
			return false ;
		
		// Get formated current date
		//
		Date dateNow = new Date() ;
		SimpleDateFormat ldvFormat = new SimpleDateFormat("yyyyMMddHHmmss") ;
		String sFormatedNow = ldvFormat.format(dateNow) ;
				
		DBConnector dbConnector = new DBConnector(true, _iId) ;
		if (null == dbConnector)
			return false ;
		
		// Prepare sql query
		//
		String sqlText = "UPDATE clients SET " + sDateFieldName + " = ? WHERE id = ?" ;
		
		dbConnector.prepareStatememt(sqlText, Statement.NO_GENERATED_KEYS) ;
		dbConnector.setStatememtString(1, sFormatedNow) ;
		dbConnector.setStatememtInt(1, _iId) ;
				
		// Execute query and get the Id that was automatically set 
		//
		int iNbAffectedRows = dbConnector.executeUpdatePreparedStatement(false) ;
		if (-1 == iNbAffectedRows)
			return false ;

		dbConnector.closeAll() ;
		
		return true ;
	}
*/

	/**
	  * Initialize the Session from a query ResultSet 
	  * 
	  * @param rs Query resultSet
	  * 
	  */
	public void fillDataFromResultSet(final ResultSet rs, final int iUserId)
	{
		if (null == rs)
			return ;
		
		try
		{
			_session.setSessionId(rs.getInt("sessionId")) ;
			_session.setPersonId(rs.getInt("personid")) ;
			_session.setToken(rs.getString("token")) ;
			_session.setDateTimeOpen(rs.getString("datetimeOpen")) ;
			_session.setDateTimeClose(rs.getString("datetimeClose")) ;
			_session.setDateTimeLastBeat(rs.getString("datetimeLastBeat")) ;
		} 
		catch (SQLException e) {
			Logger.trace("SessionsManager.fillDataFromResultSet: exception when processing results set: " + e.getMessage(), iUserId, Logger.TraceLevel.ERROR) ;
		}
	}
	
	public String getToken() {
	  return _session.getToken() ;
  }
	
	public int getPersonId() {
	  return _session.getPersonId() ;
  }
	
	/**
	 * Get current date and time at the "yyyyMMddHHmmss" format
	 */
	protected String getFormatedCurrentDateTime()
	{
		Date dateNow = new Date() ;
		SimpleDateFormat sessionFormat = new SimpleDateFormat("yyyyMMddHHmmss") ;
		return sessionFormat.format(dateNow) ;
	}
}
