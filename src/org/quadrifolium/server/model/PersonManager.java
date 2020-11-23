package org.quadrifolium.server.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.quadrifolium.shared.database.Person;

import com.ldv.server.DBConnector;
import com.ldv.server.Logger;

/**
 * Class in charge of the "persons" table
 * 
 * @author Philippe
 *
 */
public class PersonManager 
{
	private Person _person ;
	
	/**
	 * Constructor
	 */
	public PersonManager() {
		_person = new Person() ;
	}
		
	/**
	 * Initialize this object from the "persons" table record corresponding to an ID    
	 * 
	 * @param iId : ID of the row to be retrieved
	 * @return true if successful
	 * 
	 **/
	public boolean initFromId(int iId)
	{
		DBConnector dbConnector = new DBConnector(true, iId, DBConnector.databaseType.databaseCore) ;

		String sqlText = "SELECT * FROM persons WHERE id = ?" ;
		
		dbConnector.prepareStatememt(sqlText, Statement.NO_GENERATED_KEYS) ;
		dbConnector.setStatememtInt(1, iId) ;
		
		// Execute prepared statement 
		//
		boolean bSuccess = dbConnector.executePreparedStatement() ;
		if (false == bSuccess)
		{
			dbConnector.closeAll() ;
			return false ;
		}
		
		// Get results
		//
		ResultSet rs = dbConnector.getResultSet() ;
		if (null == rs)
		{
			dbConnector.closeAll() ;
			return false ;
		}
		
		// Fill the embedded Person with the first result content
		//
		boolean bResult = false ;
		try
		{
	    if (rs.next())
	    {
	    	fillPersonFromResult(_person, rs) ;
	  	  
	  	  bResult = true ;
	    }
		} 
		catch (SQLException e) {
			Logger.trace("PersonManager.initFromId: exception " + e.getMessage(), -1, Logger.TraceLevel.ERROR) ;
		}
		
		dbConnector.closeAll() ;
   
		return bResult ;
	}
	
	/**
	 * Initialize this object from the "persons" table record corresponding to a pseudo    
	 * 
	 * @param sPseudo Pseudo of the person to find
	 *  
	 * @return true if successful
	 **/
	public boolean initFromPseudo(final String sPseudo)
	{
		if ((null == sPseudo) || "".equals(sPseudo))
			return false ;
		
		String sqlText = "SELECT * FROM persons " +
                                    "WHERE pseudo = ?" ;

		DBConnector dbConnector = new DBConnector(false, -1, DBConnector.databaseType.databaseCore) ;

		dbConnector.prepareStatememt(sqlText, Statement.NO_GENERATED_KEYS) ;
		dbConnector.setStatememtString(1, sPseudo) ;
				
		// Execute prepared statement 
		//
		boolean bSuccess = dbConnector.executePreparedStatement() ;
		if (false == bSuccess)
		{
			dbConnector.closeAll() ;
			Logger.trace("PersonManager.initFromPseudo: prepared statement execution failed", -1, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		// Get results
		//
		ResultSet rs = dbConnector.getResultSet() ;
		if (null == rs)
		{	
			dbConnector.closeAll() ;
			return false ;
		}
		
		// Fill the embedded Person with the first result content
		//
		boolean bResult = false ;
		try
		{
	    if (rs.next())
	    {
	    	fillPersonFromResult(_person, rs) ;
	  	  
	  	  bResult = true ;
	    }
		} 
		catch (SQLException e) {
			Logger.trace("PersonManager.initFromPseudo: exception " + e.getMessage(), -1, Logger.TraceLevel.ERROR) ;
		}
		
		dbConnector.closeAll() ;
   
		return bResult ;
	}
	
	/**
	 * Initialize this object from the "persons" table record corresponding to a pseudo and a password    
	 * 
	 * @param sLogin    : login of the person to find
	 * @param sPassword : password of the person to find
	 *  
	 * @return true if successful
	 **/
	public boolean initFromPseudoAndPassword(final String sLogin, final String sPassword)
	{
		if ((null == sLogin) || "".equals(sLogin) || (null == sPassword) || "".equals(sPassword))
			return false ;
				
		if (false == initFromPseudo(sLogin))
			return false ;
		
		return sPassword.equals(_person.getPassword()) ;
	}
	
	/**
	 * Create a new person, populated with a LdV Id    
	 * 
	 * @param dbConnector : database connector
	 * 
	 * @return person's record id if successful, -1 if failed
	 * 
	 **/
	public int createNewPerson(DBConnector dbConnector)
	{
		boolean bLocallyCreatedConnector = false ;
		
		String sFunctionName = "PersonManager.createNewPerson" ;
		
		if (null == dbConnector)
		{
			dbConnector = new DBConnector(true, -1, DBConnector.databaseType.databaseCore) ;
			bLocallyCreatedConnector = true ;
		}

		// Lock table
		//
		if (false == lockTableWrite(dbConnector))
		{
			Logger.trace(sFunctionName + ": cannot lock table Persons; creation failed", -1, Logger.TraceLevel.ERROR) ;
			if (bLocallyCreatedConnector)
				dbConnector.closeAll() ;
			return -1 ;
		}
				
		// Date dateNow = new Date() ;
		// SimpleDateFormat ldvFormat = new SimpleDateFormat("yyyyMMddHHmmss") ;
		// _sDatetimeValidate = ldvFormat.format(dateNow) ;
			
		String sQuery = "INSERT INTO persons (pseudo, language, password, bio, userType) VALUES (?, ?, ?, ?, ?)" ;
		dbConnector.prepareStatememt(sQuery, Statement.RETURN_GENERATED_KEYS) ;
		if (null == dbConnector.getPreparedStatement())
		{
			Logger.trace(sFunctionName + ": cannot get Statement", -1, Logger.TraceLevel.ERROR) ;
			unlockTable(dbConnector) ;
			if (bLocallyCreatedConnector)
				dbConnector.closeAll() ;
			return -1 ;
		}
		
		dbConnector.setStatememtString(1, _person.getPseudo()) ;
		dbConnector.setStatememtString(2, _person.getLanguage()) ;
		dbConnector.setStatememtString(3, _person.getPassword()) ;
		dbConnector.setStatememtString(4, _person.getBio()) ;
		dbConnector.setStatememtString(5, getStringForUserType(_person.getUserType())) ;
		
		// Execute query 
		//
		int iNbAffectedRows = dbConnector.executeUpdatePreparedStatement(true) ;
		if (-1 == iNbAffectedRows)
		{
			Logger.trace(sFunctionName + ": failed query " + sQuery, -1, Logger.TraceLevel.ERROR) ;
			unlockTable(dbConnector) ;
			if (bLocallyCreatedConnector)
				dbConnector.closeAll() ;
			else
				dbConnector.closePreparedStatement() ;
			return -1 ;
		}
		
		ResultSet rs = dbConnector.getResultSet() ;
		int iNewPersonId = -1 ;
		try
    {
	    if (rs.next())
	    	iNewPersonId = rs.getInt(1) ;
	    else
	    	Logger.trace(sFunctionName + ": cannot get Id after query " + sQuery, -1, Logger.TraceLevel.ERROR) ;
    } 
		catch (SQLException e)
    {
			Logger.trace(sFunctionName + ": exception when iterating results " + e.getMessage(), -1, Logger.TraceLevel.ERROR) ;
    }
		dbConnector.closeResultSet() ;
		dbConnector.closePreparedStatement() ;

		unlockTable(dbConnector) ;
		
		if (bLocallyCreatedConnector)
			dbConnector.closeAll() ;
  	
		Logger.trace(sFunctionName + ": new person (" + iNewPersonId + ") created.", -1, Logger.TraceLevel.DETAIL) ;
		
   	return iNewPersonId ;
	}

	/**
	 * Fills a person from a result set entry
	 * 
	 * @param person : Person to fill
	 * @param rs     : Result set that contains information from database
	 */
	protected void fillPersonFromResult(Person person, final ResultSet rs)
	{
		try 
		{
			person.setPersonId(rs.getInt("personId")) ;
			person.setPseudo(rs.getString("pseudo")) ;
			person.setLanguage(rs.getString("language")) ;
			person.setPassword(rs.getString("password")) ;
			person.setBio(rs.getString("bio")) ;
			person.setUserType(getUserTypeForString(rs.getString("userType"))) ;
		} 
		catch (SQLException e) {
			Logger.trace("PersonManager.fillPersonFromResult: exception " + e.getMessage(), -1, Logger.TraceLevel.ERROR) ;
		}
	}
	
	/**
	 * Lock Persons table for write    
	 * 
	 * @param dbConnector : database connector
	 * @return true if successful
	 * 
	 **/
	private boolean lockTableWrite(DBConnector dbConnector)
	{
		if (null == dbConnector)
			return false ;
		
		try
		{
			dbConnector.getStatement().execute("LOCK TABLE persons WRITE") ;
		}
		catch (SQLException ex)
		{				 	
			Logger.trace("PersonManager.lockTableWrite: cannot lock table Persons " + ex.getMessage(), -1, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		return true ;
	}
	
	/**
	 * Lock Persons table for write    
	 * 
	 * @param dbConnector : database connector
	 * @return true if successful
	 * 
	 **/
	private boolean unlockTable(DBConnector dbConnector)
	{
		if (null == dbConnector)
			return false ;
		
		try
		{
			dbConnector.getStatement().execute("UNLOCK TABLES") ;
		}
		catch (SQLException ex)
		{		
			Logger.trace("PersonManager.unlockTable: exception " + ex.getMessage(), -1, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
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
	
	public void setId(int iId) {
		_person.setPersonId(iId) ;
  }
	public int getId() {
	  return _person.getPersonId() ;
  }

	public void setPseudo(final String sPseudo) {
		_person.setPseudo(sPseudo) ;
  }
	public String getPseudo() {
	  return _person.getPseudo() ;
  }

	public void setLanguage(final String sLanguage) {
		_person.setLanguage(sLanguage) ;
  }
	public String getLanguage() {
	  return _person.getLanguage() ;
  }

	public void setPassword(final String sPassword) {
		_person.setPassword(sPassword) ;
  }
	public String getPassword() {
	  return _person.getPassword() ;
  }
	
	public String getBio() {
		return _person.getBio() ;
	}
	public void setBio(final String sBio) {
		_person.setBio(sBio) ;
	}
	
	public Person.USERTYPE getUserType() {
		return _person.getUserType() ;
	}
	public void setUserType(final Person.USERTYPE iUserType) {
		_person.setUserType(iUserType) ;
	}
	public void setUserType(final String sUserType) {
		_person.setUserType(getUserTypeForString(sUserType)) ;
	}
		
	/**
	 * Get the string (to store in database) that corresponds to a user type
	 * 
	 * @param iUserType 
	 * @return  
	 */
	protected String getStringForUserType(final Person.USERTYPE iUserType)
	{
		switch (iUserType)
		{
			case visitor       : return "V" ;
			case editor        : return "E" ;
			case administrator : return "A" ;
			case unactive      : return "U" ;
			case unknown       : return "0" ;
		}
		return "0" ;
	}
	
	/**
	 * Get the user type that corresponds to a string (from the database)
	 * 
	 * @param sUserType
	 * @return The user type or "unknown" if string doesn't match with a known user type 
	 */
	protected Person.USERTYPE getUserTypeForString(final String sUserType)
	{
		if ("V".equals(sUserType))
			return Person.USERTYPE.visitor ;
		if ("E".equals(sUserType))
			return Person.USERTYPE.editor ;
		if ("A".equals(sUserType))
			return Person.USERTYPE.administrator ;
		if ("U".equals(sUserType))
			return Person.USERTYPE.unactive ;
		
		return Person.USERTYPE.unknown ;
	}
	
	public Person getPerson() { 
		return _person ;
	}
}
