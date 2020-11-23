package org.quadrifolium.server.ontology;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import org.quadrifolium.server.util.QuadrifoliumServerFcts;
import org.quadrifolium.shared.ontology.Flex;
import org.quadrifolium.shared.ontology.FlexWithTraits;
import org.quadrifolium.shared.ontology.Lemma;
import org.quadrifolium.shared.ontology.LemmaWithInflections;
import org.quadrifolium.shared.ontology.Triple;
import org.quadrifolium.shared.ontology.TripleWithLabel;
import org.quadrifolium.shared.rpc_util.SessionElements;

import com.ldv.server.DBConnector;
import com.ldv.server.Logger;

/** 
 * Object in charge of Read/Write operations for <code>LemmaWithInflections</code> objects 
 *   
 */
public class LemmaExtendedManager  
{	
	protected final DBConnector     _dbConnector ;
	protected final SessionElements _sessionElements ;
	
	protected       int             _iUserId ;
	
	/**
	 * Buffer used to avoid looking twice for labels in the database 
	 */
  protected       HashMap<String, String> _aLabelsForCodes ;
	
	/**
	 * Constructor (for and internal labels buffer)
	 * 
	 * @param sessionElements Can be null if only using read only functions
	 * @param dbConnector     Database connector
	 */
	public LemmaExtendedManager(final SessionElements sessionElements, final DBConnector dbConnector)
	{
		_dbConnector     = dbConnector ;
		_sessionElements = sessionElements ;
		
		_iUserId = -1 ; 
		if (null != _sessionElements)
			_iUserId = _sessionElements.getPersonId() ;
		
		_aLabelsForCodes = new HashMap<String, String>() ;
	}

	/**
	 * Constructor
	 * 
	 * @param sessionElements Can be null if only using read only functions
	 * @param dbConnector     Database connector
	 * @param aLabelsForCodes Labels buffer used to avoid looking twice for labels in the database
	 */
	public LemmaExtendedManager(final SessionElements sessionElements, final DBConnector dbConnector, HashMap<String, String> aLabelsForCodes)
	{
		_dbConnector     = dbConnector ;
		_sessionElements = sessionElements ;
		
		_iUserId = -1 ; 
		if (null != _sessionElements)
			_iUserId = _sessionElements.getPersonId() ;
		
		if (null == aLabelsForCodes)
			_aLabelsForCodes = new HashMap<String, String>() ;
		else
			_aLabelsForCodes = aLabelsForCodes ;
	}
	
	/**
	 * Fill traits and inflections for a given lemma
	 * 
	 * @param sLanguage   Language to get all synonyms for
	 * @param synonym     Object to be completed
	 * 
	 * @return
	 */
	public LemmaWithInflections fillTraitsAndInflections(final String sLanguage, final Lemma lemma) throws NullPointerException
	{
		if (null == lemma)
			throw new NullPointerException() ;
		
		LemmaWithInflections synonym = new LemmaWithInflections(lemma, null, null) ;
		
		boolean bGotTraits      = fillTraits(sLanguage, synonym) ;
		boolean bGotInflections = fillInflections(sLanguage, synonym) ;
		
		return synonym ;
	}
	
	/**
	 * Fill traits for a given lemma
	 * 
	 * @param dbconnector Database connector
	 * @param sLanguage   Language to get all synonyms for
	 * @param synonym     Object to be completed
	 * @return
	 */
	public boolean fillTraits(final String sLanguage, LemmaWithInflections synonym) throws NullPointerException
	{
		if (null == synonym)
			throw new NullPointerException() ;
		
		String sFctName = "LemmaExtendedManager.fillTraits" ;
		
		if (null == synonym)
		{
			Logger.trace(sFctName + ": bad parameter", -1, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		String sLemmaCode = synonym.getCode() ;
		
		if ((null == _dbConnector) || (null == sLemmaCode) || "".equals(sLemmaCode))
		{
			Logger.trace(sFctName + ": bad parameter", -1, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		// SQL query to get all triples a lemma is subject of
		//
		String sqlText = "SELECT * FROM triple WHERE subject = ?" ;
		
		_dbConnector.prepareStatememt(sqlText, Statement.NO_GENERATED_KEYS) ;
		_dbConnector.setStatememtString(1, sLemmaCode) ;
				
		if (false == _dbConnector.executePreparedStatement())
		{
			Logger.trace(sFctName + ": failed query " + sqlText + " and code = " + sLemmaCode, _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		int iNbRecords = 0 ;
		
		ResultSet rs = _dbConnector.getResultSet() ;
		try
		{
			// Browse resulting triples
			//
			while (rs.next())
			{
				Triple triple = new Triple() ;
				TripleManager.fillDataFromResultSet(rs, triple, _iUserId) ;
				
				synonym.addTrait(new TripleWithLabel(triple, sLanguage, "", "", "")) ;
				
				iNbRecords++ ;
			}
			
			if (0 == iNbRecords)
			{
				Logger.trace(sFctName + ": no triple found for lemma \"" + sLemmaCode + "\"", _iUserId, Logger.TraceLevel.SUBSTEP) ;
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
		
		Logger.trace(sFctName + ": found " + iNbRecords + " triples for lemma " + sLemmaCode, _iUserId, Logger.TraceLevel.SUBDETAIL) ;
		
		_dbConnector.closeResultSet() ;
		_dbConnector.closePreparedStatement() ;
		
		// Provide the traits with their labels
		//
		ArrayList<TripleWithLabel> aTraits = synonym.getTraits() ;
		for (Iterator<TripleWithLabel> it = aTraits.iterator() ; it.hasNext() ; )
		{
			TripleWithLabel trait = it.next() ;
			
			QuadrifoliumServerFcts.fillTraitWithLabels(_dbConnector, _sessionElements, sLanguage, trait, _aLabelsForCodes) ;
		}
		
		return true ;
	}
	
	/**
	 * Fill traits for a given lemma
	 * 
	 * @param sLanguage   Language to get all synonyms for
	 * @param synonym     Object to be completed
	 * 
	 * @return <code>true</code> if all went well, <code>false</code> if not
	 */
	public boolean fillInflections(final String sLanguage, LemmaWithInflections synonym) throws NullPointerException
	{
		String sFctName = "LemmaExtendedManager.fillInflections" ;
		
		if (null == synonym)
		{
			Logger.trace(sFctName + ": bad parameter", -1, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		String sLemmaCode = synonym.getCode() ;
		
		Logger.trace(sFctName + ": entering for lemma = " + sLemmaCode, _iUserId, Logger.TraceLevel.SUBSTEP) ;
		
		if ((null == _dbConnector) || (null == sLemmaCode) || "".equals(sLemmaCode))
		{
			Logger.trace(sFctName + ": bad parameter", -1, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		// Get all inflections for this lemma
		//
		FlexManager flexManager = new FlexManager(_sessionElements, _dbConnector) ;
			
		ArrayList<Flex> aFlexForLemma = new ArrayList<Flex>() ;
		if (false == flexManager.existDataForLemma(sLemmaCode, aFlexForLemma))
			return false ;
			
		if (aFlexForLemma.isEmpty())
			return true ;
		
		// Sort on code
		//
		Collections.sort(aFlexForLemma) ;
			
		// Add all inflections to the synonym
		//
		for (Iterator<Flex> it = aFlexForLemma.iterator() ; it.hasNext() ; )
		{
			Flex inflection = it.next() ;
			synonym.addInflection(new FlexWithTraits(inflection)) ;
		}
		
		Logger.trace(sFctName + ": adding traits to inflections for lemma = " + sLemmaCode, _iUserId, Logger.TraceLevel.DETAIL) ;
		
		// Add all inflections their traits
		//
		for (Iterator<FlexWithTraits> it = synonym.getInflections().iterator() ; it.hasNext() ; )
		{
			FlexWithTraits inflection = it.next() ;
			
			String sFlexCode = inflection.getCode() ;
			
			// SQL query to get all triples the inflection is subject of
			//
			String sqlText = "SELECT * FROM triple WHERE subject = ?" ;
		
			_dbConnector.prepareStatememt(sqlText, Statement.NO_GENERATED_KEYS) ;
			_dbConnector.setStatememtString(1, sFlexCode) ;
				
			if (false == _dbConnector.executePreparedStatement())
			{
				Logger.trace(sFctName + ": failed query " + sqlText + " and code = " + sFlexCode, _iUserId, Logger.TraceLevel.ERROR) ;
				return false ;
			}
		
			int iNbRecords = 0 ;
		
			ResultSet rs = _dbConnector.getResultSet() ;
			try
			{
				// Browse resulting triples
				//
				while (rs.next())
				{
					Triple triple = new Triple() ;
					TripleManager.fillDataFromResultSet(rs, triple, _iUserId) ;
				
					inflection.addTrait(new TripleWithLabel(triple, sLanguage, "", "", "")) ;
				
					iNbRecords++ ;
				}
			
				if (0 == iNbRecords)
				{
					Logger.trace(sFctName + ": no triple found for inflection \"" + sFlexCode + "\"", _iUserId, Logger.TraceLevel.SUBSTEP) ;
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
		
			Logger.trace(sFctName + ": found " + iNbRecords + " triples for inflection " + sFlexCode, _iUserId, Logger.TraceLevel.SUBDETAIL) ;
			
			_dbConnector.closeResultSet() ;
			_dbConnector.closePreparedStatement() ;
		}
		
		Logger.trace(sFctName + ": adding inflections' traits their labels for lemma = " + sLemmaCode, _iUserId, Logger.TraceLevel.DETAIL) ;
		
		// Add all inflections' traits their labels
		//
		for (Iterator<FlexWithTraits> it = synonym.getInflections().iterator() ; it.hasNext() ; )
		{
			FlexWithTraits inflection = it.next() ;
			
			Logger.trace(sFctName + ": processing traits for inflection = " + inflection.getCode(), _iUserId, Logger.TraceLevel.DETAIL) ;
			
			ArrayList<TripleWithLabel> aTraits = inflection.getTraits() ;
			if (false == aTraits.isEmpty())
			{
				for (Iterator<TripleWithLabel> itTrait = aTraits.iterator() ; itTrait.hasNext() ; )
				{
					TripleWithLabel trait = itTrait.next() ;
					QuadrifoliumServerFcts.fillTraitWithLabels(_dbConnector, _sessionElements, sLanguage, trait, _aLabelsForCodes) ;
				}
			}
		}
		
		Logger.trace(sFctName + ": leaving for lemma = " + sLemmaCode, _iUserId, Logger.TraceLevel.SUBSTEP) ;
		
		return true ;
	}
}
