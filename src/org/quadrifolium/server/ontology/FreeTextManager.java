package org.quadrifolium.server.ontology;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.quadrifolium.server.DBConnector;
import org.quadrifolium.server.Logger;
import org.quadrifolium.server.ontology_base.ChangeHistory;
import org.quadrifolium.server.ontology_base.FreeTextModel;
import org.quadrifolium.server.ontology_base.FreeTextModelHistory;
import org.quadrifolium.server.ontology_base.ChangeHistory.ChangeType;
import org.quadrifolium.server.ontology_base.ChangeHistory.TableType;
import org.quadrifolium.server.util.QuadrifoliumServerFcts;
import org.quadrifolium.shared.ontology.FreeText;
import org.quadrifolium.shared.rpc_util.SessionElements;
import org.quadrifolium.shared.util.QuadrifoliumFcts;

/** 
 * Object in charge of managing free texts 
 *   
 */
public class FreeTextManager  
{	
	public static final int TEXT_SIZE = 255 ;
	
	protected final DBConnector     _dbConnector ;
	protected final SessionElements _sessionElements ;
	
	/**
	 * Constructor
	 * 
	 * @param sessionElements Can be null if only using read only functions 
	 */
	public FreeTextManager(final SessionElements sessionElements, final DBConnector dbConnector)
	{
		_dbConnector     = dbConnector ;
		_sessionElements = sessionElements ;
	}

	/**
	 * Get the label of a free text in database from its code<br>
	 * <br>
	 * This function is read only, hence it doesn't need a registered user.
	 * 
	 * @param sFreeTextHeaderCode Code to get the free text attached to
	 * @param sLanguage           Mandatory language, usually <code>""</code>
	 * 
	 * @return The text if anything went well, <code>null</code> if something went wrong or if the language doesn't fit the mandatory language
	 */
	public String getFreeTextLabel(final String sFreeTextHeaderCode, final String sMandatoryLanguage)
	{
		String sFctName = "FreeTextManager.getFreeTextLabel" ;
		
		// This function is read only, hence it doesn't need a registered user
		//
		int iUserId = -1 ;
		if (null != _sessionElements)
			iUserId = _sessionElements.getPersonId() ;
		
		if ((null == _dbConnector) || (null == sFreeTextHeaderCode) || "".equals(sFreeTextHeaderCode))
		{
			Logger.trace(sFctName + ": invalid parameter", iUserId, Logger.TraceLevel.ERROR) ;
			return null ;
		}
		
		if (false == QuadrifoliumFcts.isFreeTextHeaderCode(sFreeTextHeaderCode))
		{
			Logger.trace(sFctName + ": invalid free text header code (\"" + sFreeTextHeaderCode + "\").", iUserId, Logger.TraceLevel.ERROR) ;
			return null ;
		}
		
		FreeTextModelManager manager = new FreeTextModelManager(iUserId, _dbConnector) ;
		
		FreeTextModel firstSlice = new FreeTextModel() ; 
		if (false == manager.existData(sFreeTextHeaderCode, firstSlice))
		{
			Logger.trace(sFctName + ": not free text found for code \"" + sFreeTextHeaderCode + "\".", iUserId, Logger.TraceLevel.ERROR) ;
			return null ;
		}
		
		// If the language doesn't fit with the mandatory language, then leave now
		//
		if ((null != sMandatoryLanguage) && (false == "".equals(sMandatoryLanguage)) && (false == sMandatoryLanguage.equals(firstSlice.getLanguage())))
			return null ;
		
		// If there is a single slice, then we are done
		//
		if (false == firstSlice.hasNext())
			return firstSlice.getLabel() ;
		
		String sResult = firstSlice.getLabel() ; 
		
		// Getting all slices
		//
		while (firstSlice.hasNext())
		{
			String sSliceId = firstSlice.getNext() ;
			
			if (false == manager.existData(sSliceId, firstSlice))
			{
				Logger.trace(sFctName + ": slice " + sSliceId + " not found for free text \"" + sFreeTextHeaderCode + "\".", iUserId, Logger.TraceLevel.ERROR) ;
				return null ;
			}
			
			sResult += firstSlice.getLabel() ;
		}
		
		return sResult ;
	}
	
	/**
	 * Get a free text in database from its code<br>
	 * <br>
	 * This function is read only, hence it doesn't need a registered user.
	 * 
	 * @param sFreeTextHeaderCode Code to get the free text attached to
	 * 
	 * @return The text object if anything went well, <code>null</code> if something went wrong
	 */
	public FreeText getFreeText(final String sFreeTextHeaderCode)
	{
		String sFctName = "FreeTextManager.getFreeText" ;
		
		// This function is read only, hence it doesn't need a registered user
		//
		int iUserId = -1 ;
		if (null != _sessionElements)
			iUserId = _sessionElements.getPersonId() ;
		
		if ((null == _dbConnector) || (null == sFreeTextHeaderCode) || "".equals(sFreeTextHeaderCode))
		{
			Logger.trace(sFctName + ": invalid parameter", iUserId, Logger.TraceLevel.ERROR) ;
			return null ;
		}
		
		if (false == QuadrifoliumFcts.isFreeTextHeaderCode(sFreeTextHeaderCode))
		{
			Logger.trace(sFctName + ": invalid free text header code (\"" + sFreeTextHeaderCode + "\").", iUserId, Logger.TraceLevel.ERROR) ;
			return null ;
		}
		
		FreeTextModelManager manager = new FreeTextModelManager(iUserId, _dbConnector) ;
		
		FreeTextModel firstSlice = new FreeTextModel() ; 
		if (false == manager.existData(sFreeTextHeaderCode, firstSlice))
		{
			Logger.trace(sFctName + ": not free text found for code \"" + sFreeTextHeaderCode + "\".", iUserId, Logger.TraceLevel.ERROR) ;
			return null ;
		}
		
		FreeText freeText = new FreeText(firstSlice.getId(), "", firstSlice.getCode(), firstSlice.getLanguage()) ;
		
		// If there is a single slice, then we are done
		//
		if (false == firstSlice.hasNext())
		{
			freeText.setLabel(firstSlice.getLabel()) ;
			return freeText ;
		}
		
		String sResult = firstSlice.getLabel() ; 
		
		// Getting all slices
		//
		while (firstSlice.hasNext())
		{
			String sSliceId = firstSlice.getNext() ;
			
			if (false == manager.existData(sSliceId, firstSlice))
			{
				Logger.trace(sFctName + ": slice " + sSliceId + " not found for free text \"" + sFreeTextHeaderCode + "\".", iUserId, Logger.TraceLevel.ERROR) ;
				return null ;
			}
			
			sResult += firstSlice.getLabel() ;
		}
		
		freeText.setLabel(sResult) ;
		return freeText ;
	}
	
	/**
	 * Insert a free text in database (as a chained list of free texts records)<br>
	 * <br>
	 * This function needs a registered user.
	 * 
	 * @param sConceptCode Concept this free text is related to
	 * @param sFreeText    Free text to record in database
	 * @param sLanguage    Language of this text
	 *
	 * @return The code for this free text if everything went well or <code>""</code> if not 
	 */
	public String insertData(final String sConceptCode, final String sFreeText, final String sLanguage)
	{
		String sFctName = "FreeTextManager.insertData" ;
		
		// This function needs a registered user
		//
		if (null == _sessionElements)
		{
			Logger.trace(sFctName + ": no session elements.", -1, Logger.TraceLevel.ERROR) ;
			return "" ;
		}
		
		if ((null == _dbConnector) || (null == sConceptCode) || (null == sFreeText) || "".equals(sConceptCode) || "".equals(sFreeText))
		{
			Logger.trace(sFctName + ": invalid parameter.", _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
			return "" ;
		}
		
		// Get next available header code
		//
		String sHeaderCode = getNextFreeTextHeaderCode(sConceptCode) ;
		
		if ("".equals(sHeaderCode))
			return "" ;
		
		// Get slices
		//
		String[] aSlices = getSlicedFreeText(sFreeText) ;
		
		if ((null == aSlices) || (aSlices.length < 1))
			return "" ;
		
		int iSlicesCount = aSlices.length ;
		
		FreeTextModelManager manager = new FreeTextModelManager(_sessionElements.getPersonId(), _dbConnector) ;
		
		FreeTextModel freeText = new FreeTextModel() ;
		freeText.setLabel(aSlices[0]) ;
		freeText.setCode(sHeaderCode) ;
		freeText.setLanguage(sLanguage) ;
		
		// If the text was not sliced, then record it and leave
		//
		if (1 == iSlicesCount)
		{
			insertHistorizedData(freeText, manager) ;
			Logger.trace(sFctName +  ": user " + _sessionElements.getPersonId() + " successfuly recorded free text \"" + sFreeText + "\" for concept " + sConceptCode + " as text ID " + sHeaderCode, _sessionElements.getPersonId(), Logger.TraceLevel.STEP) ;
			return sHeaderCode ;
		}
		
		// In case of multiple slices, we have to manage the chained list of slices
		//
		
		// Get next available header code and record the heading slice
		//
		String sNextCode = getNextFreeTextCode(sConceptCode) ;
		freeText.setNext(sNextCode) ;
		
		insertHistorizedData(freeText, manager) ;
		
		// Record all other slices
		//
		for (int i = 1 ; i < iSlicesCount ; i++)
		{
			FreeTextModel slice = new FreeTextModel() ;
			slice.setLabel(aSlices[i]) ;
			slice.setCode(sNextCode) ;
			slice.setLanguage(sLanguage) ;
			
			// The last slice has no "next" pointer
			//
			if (i < iSlicesCount - 1)
			{
				sNextCode = getNextFreeTextCode(sConceptCode) ;
				slice.setNext(sNextCode) ;
			}
			
			insertHistorizedData(slice, manager) ;
		}
		
		Logger.trace(sFctName +  ": user " + _sessionElements.getPersonId() + " successfuly recorded free text \"" + sFreeText + "\" for concept " + sConceptCode + " as text ID " + sHeaderCode + " (as " + iSlicesCount + " slices).", _sessionElements.getPersonId(), Logger.TraceLevel.STEP) ;
		
		return sHeaderCode ;
	}

	/**
	 * Insert historized information
	 * 
	 * @param slice   New free text slice
	 * @param manager Free text slices manager
	 */
	protected boolean insertHistorizedData(FreeTextModel slice, FreeTextModelManager manager)
	{
		if ((null == slice) || "".equals(slice.getLabel()) || (null == manager))
			return false ;
		
		if (false == manager.insertData(slice))
			return false ;
		
		return historize(slice, ChangeType.create) ;
	}
	
	/**
	 * Historize the change
	 * 
	 * @param slice      Free text slice to historize (new one for new and update, old one for delete)
	 * @param changeType Modification type (add, update, delete)
	 */
	protected boolean historize(final FreeTextModel slice, ChangeHistory.ChangeType changeType)
	{
		// Create an historization object and save it
		//
		FreeTextModelHistory sliceHistory = new FreeTextModelHistory(slice) ;
		
		FreeTextHistoryManager historyManager = new FreeTextHistoryManager(_sessionElements.getPersonId(), _dbConnector) ;
		if (false == historyManager.insertData(sliceHistory))
			return false ;
		
		// Create a Change history record
		//
		ChangeHistory changeHistory = new ChangeHistory(_sessionElements.getSessionId(), "", TableType.freeText, changeType, slice.getId(), sliceHistory.getId()) ;
		
		ChangeHistoryManager changeManager = new ChangeHistoryManager(_sessionElements.getPersonId(), _dbConnector) ;
		return changeManager.insertData(changeHistory) ;
	}
	
	/**
	 * Update a free text in database<br>
	 * <br>
	 * This function needs a registered user.
	 * 
	 * @return true if successful, false if not
	 * 
	 * @param sFreeTextHeaderCode The code of the first free text from the chained list
	 * @param sFreeText           Text to put in replacement of existing one (<code>""</code> or <code>null</code> means erase)
	 * @param sLanguageTag        Language tag to put in replacement of existing one (<code>""</code> or <code>null</code> means keep existing one)
	 */
	public boolean updateData(final String sFreeTextHeaderCode, final String sFreeText, final String sLanguageTag)
	{
		String sFctName = "FreeTextManager.updateData" ;
		
		// This function needs a registered user
		//
		if (null == _sessionElements)
		{
			Logger.trace(sFctName + ": no session elements.", -1, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		if ((null == _dbConnector) || (null == sFreeTextHeaderCode) || "".equals(sFreeTextHeaderCode))
		{
			Logger.trace(sFctName + ": invalid parameter", _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		if (false == QuadrifoliumFcts.isFreeTextHeaderCode(sFreeTextHeaderCode))
		{
			Logger.trace(sFctName + ": invalid free text header code (\"" + sFreeTextHeaderCode + "\").", _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		// Empty text, same meaning as "erase free text"
		//
		if ((null == sFreeText) || "".equals(sFreeText))
			return eraseData(sFreeTextHeaderCode) ;
		
		FreeTextModelManager manager = new FreeTextModelManager(_sessionElements.getPersonId(), _dbConnector) ;
		
		FreeTextModel slice = new FreeTextModel() ; 
		if (false == manager.existData(sFreeTextHeaderCode, slice))
		{
			Logger.trace(sFctName + ": No free text found for header code \"" + sFreeTextHeaderCode + "\".", _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		// In case new slices must be added
		//
		String sLanguage    = slice.getLanguage() ;
		String sConceptCode = QuadrifoliumFcts.getConceptCode(slice.getCode()) ;
		
		String sLanguageToSet = sLanguage ;
		if ((null != sLanguageTag) && (false == "".equals(sLanguageTag)))
			sLanguageToSet = sLanguageTag ;
		
		// Get slices for new text
		//
		String[] aSlices = getSlicedFreeText(sFreeText) ;
			
		if ((null == aSlices) || (aSlices.length < 1))
			return false ;
		
		String sNextSliceToRecord = "" ;
		
		int iSliceIndex = 0 ;
		
		// Update slices
		//
		boolean bKeepOn = true ;
		while (bKeepOn)
		{
			String sNextRecordedSlice = slice.getNext() ;
			
			// If the slice need to be updated
			//
			if (false == slice.getLabel().equals(aSlices[iSliceIndex]))
			{
				slice.setLabel(aSlices[iSliceIndex]) ;
				slice.setLanguage(sLanguageToSet) ;
				
				// If last slice of the new text, don't forget to set the next pointer to void 
				//
				if (iSliceIndex == aSlices.length - 1)
					slice.setNext("") ;
				//
				// Not last slice of the new text, but last slice of the previous one, need to extend the chained list
				//
				else if ("".equals(sNextRecordedSlice))
				{
					sNextSliceToRecord = getNextFreeTextCode(sConceptCode) ;
					slice.setNext(sNextSliceToRecord) ;
				}
					
				// Update record
				//
				if (false == updateHistorizedData(slice, manager))
					return false ;
			}
			
			// In case we just have to update the language tag
			//
			else if (false == sLanguageToSet.equals(slice.getLanguage()))
			{
				slice.setLanguage(sLanguageToSet) ;
				
				// Update record
				//
				if (false == updateHistorizedData(slice, manager))
					return false ;
			}
			
			iSliceIndex++ ;
			
			// If we reached the last slice of the new text, we have to delete trailing recorded slices
			//
			if (iSliceIndex == aSlices.length)
			{
				bKeepOn = false ;
				
				// If the last new slice fitted in the last record of the previous one, we are done
				//
				if ("".equals(sNextRecordedSlice))
					return true ;
				
				// Delete all recorded trailing slices
				//
				while (false == "".equals(sNextRecordedSlice))
				{
					if (false == manager.existData(sNextRecordedSlice, slice))
					{
						Logger.trace(sFctName + ": No free text found for code \"" + sFreeTextHeaderCode + "\".", _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
						return false ;
					}
					
					sNextRecordedSlice = slice.getNext() ;
					
					if (false == HistorizeDataDeletion(slice, manager))
					{
						Logger.trace(sFctName + ": Cannot delete free text for code \"" + sFreeTextHeaderCode + "\".", _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
						return false ;
					}
				}
				
				return true ;
			}
			
			// If there, it means that there are still new slices to manage
			//
			
			// If no records left, leave the "update" loop
			//
			if ("".equals(sNextRecordedSlice))
				bKeepOn = false ;
			else
			{
				if (false == manager.existData(sNextRecordedSlice, slice))
				{
					Logger.trace(sFctName + ": No free text found for code \"" + sFreeTextHeaderCode + "\".", _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
					return false ;
				}
			}
		}
		
		// If there, it means that the chained list must be expanded to accommodate new slices 
		//
		for ( ; iSliceIndex < aSlices.length ; iSliceIndex++)
		{
			FreeTextModel sliceToAdd = new FreeTextModel() ;
			
			sliceToAdd.setCode(sNextSliceToRecord) ;
			sliceToAdd.setLabel(aSlices[iSliceIndex]) ;
			sliceToAdd.setLanguage(sLanguageToSet) ;
			
			// The last slice has no "next" pointer
			//
			if (iSliceIndex < aSlices.length - 1)
			{
				sNextSliceToRecord = getNextFreeTextCode(sConceptCode) ;
				sliceToAdd.setNext(sNextSliceToRecord) ;
			}
					
			if (false == insertHistorizedData(sliceToAdd, manager))
				return false ;
		}
		
		return true ;
	}
		
	/**
	 * Update historized information
	 * 
	 * @param slice   New free text slice
	 * @param manager Free text slices manager
	 */
	protected boolean updateHistorizedData(FreeTextModel slice, FreeTextModelManager manager)
	{
		if ((null == slice) || "".equals(slice.getLabel()) || (null == manager))
			return false ;
		
		if (false == manager.forceUpdateData(slice))
			return false ;
		
		return historize(slice, ChangeType.change) ;
	}
	
	/**
	 * Erase a free text in database<br>
	 * <br>
	 * This function needs a registered user.
	 * 
	 * @return true if successful, false if not
	 * 
	 * @param sFreeTextHeaderCode Code of free text to erase
	 */
	public boolean eraseData(final String sFreeTextHeaderCode)
	{
		String sFctName = "FreeTextManager.eraseData" ;
		
		// This function needs a registered user
		//
		if (null == _sessionElements)
		{
			Logger.trace(sFctName + ": no session elements.", -1, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		if ((null == _dbConnector) || (null == sFreeTextHeaderCode) || "".equals(sFreeTextHeaderCode))
		{
			Logger.trace(sFctName + ": invalid parameter.", _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		if (false == QuadrifoliumFcts.isFreeTextHeaderCode(sFreeTextHeaderCode))
		{
			Logger.trace(sFctName + ": invalid free text header code (\"" + sFreeTextHeaderCode + "\").", _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		// Try getting the first slice
		//
		FreeTextModelManager manager = new FreeTextModelManager(_sessionElements.getPersonId(), _dbConnector) ;
		
		FreeTextModel slice = new FreeTextModel() ; 
		if (false == manager.existData(sFreeTextHeaderCode, slice))
		{
			Logger.trace(sFctName + ": No free text found for header code \"" + sFreeTextHeaderCode + "\".", _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		// If the only one, just delete record and we are done
		//
		boolean bDeleted = HistorizeDataDeletion(slice, manager) ;
		if (false == slice.hasNext())
			return bDeleted ; 
		
		while (slice.hasNext())
		{
			if (false == manager.existData(slice.getNext(), slice))
			{
				Logger.trace(sFctName + ": No free text found for code \"" + slice.getNext() + "\".", _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
				return false ;
			}
			
			if (false == HistorizeDataDeletion(slice, manager))
				return false ;			
		}
		
		return true ;
	}
	
	/**
	 * Historize information deletion
	 * 
	 * @param slice   New free text slice
	 * @param manager Free text slices manager
	 */
	protected boolean HistorizeDataDeletion(FreeTextModel slice, FreeTextModelManager manager)
	{
		if ((null == slice) || "".equals(slice.getLabel()) || (null == manager))
			return false ;
		
		if (false == manager.deleteRecord(slice.getId()))
			return false ;
		
		return historize(slice, ChangeType.delete) ;
	}
	
	/**
	 * Cut the free text in fragments of less than 255 chars
	 * 
	 * @param sFreeText The text to be sliced
	 * 
	 * @return An array of as many strings as needed if everything went well, <code>null</code> if not
	 */
	protected String[] getSlicedFreeText(final String sFreeText)
	{
		if ((null == sFreeText) || "".equals(sFreeText))
			return null ;
		
		// If no slicing necessary, just return the text
		//
		if (false == isSlicingNecessary(sFreeText))
		{
			String[] aResult = new String[1] ;
			aResult[0] = sFreeText ;
			return aResult ;
		}
		
		int iTextLen = sFreeText.length() ;
		
		// Determine slices count
		//
		double dSlices = iTextLen / TEXT_SIZE ;
		double dSlicesCount = Math.floor(dSlices) ;
		
		int iSlicesCount = (int) dSlicesCount + 1 ; 
		
		String[] aResults = new String[iSlicesCount] ;
		
		// Slicing
		//
		int iStart = 0 ;
		
		int i = 0 ;
		for ( ; i < iSlicesCount - 1; i++)
		{
			aResults[i] = sFreeText.substring(iStart, TEXT_SIZE + iStart) ;
			iStart += TEXT_SIZE ;
		}
		
		aResults[i] = sFreeText.substring(iStart, iTextLen) ;
		
		return aResults ;
	}
	
	/**
	 * Should this text be sliced in order to get stored in database?
	 */
	protected boolean isSlicingNecessary(final String sFreeText)
	{
		if ((null == sFreeText) || "".equals(sFreeText))
			return false ;
		
		return (sFreeText.length() > TEXT_SIZE) ;
	}
	
	/**
	 * Get the next available code for a given concept<br>
	 * <br>
	 * This function needs a registered user.
	 * 
	 * @param sConceptCode Code of the concept this free text is attached to
	 * 
	 * @return Next available free text code, or <code>""</code> if something went wrong
	 */
	public String getNextFreeTextHeaderCode(final String sConceptCode)
	{
		String sFctName = "FreeTextManager.getNextFreeTextHeaderCode" ;

		// This function needs a registered user
		//
		if (null == _sessionElements)
		{
			Logger.trace(sFctName + ": no session elements.", -1, Logger.TraceLevel.ERROR) ;
			return "" ;
		}
		
		if ((null == _dbConnector) || (null == sConceptCode) || "".equals(sConceptCode))
		{
			Logger.trace(sFctName + ": bad parameter", _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
			return "" ;
		}
		
		String sQuery = "SELECT MAX(code) AS MAX_CODE FROM freeText WHERE code LIKE ?" ;
		
		_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
		if (null == _dbConnector.getPreparedStatement())
		{
			Logger.trace(sFctName + ": cannot get Statement", _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return "" ;
		}
		
		// Free texts "head of chain" codes are in the form "concept" + '+' + "specific code"
		//
		_dbConnector.setStatememtString(1, sConceptCode + "+" + "%") ;
		
		if (false == _dbConnector.executePreparedStatement())
		{
			Logger.trace(sFctName + ": failed query " + sQuery, _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return "" ;
		}
	   		
		ResultSet rs = _dbConnector.getResultSet() ;
		if (null == rs)
		{
			Logger.trace(sFctName + ": no max code found in table freeText for concept = " + sConceptCode, _sessionElements.getPersonId(), Logger.TraceLevel.WARNING) ;
			_dbConnector.closePreparedStatement() ;
			return "" ;
		}
				
		String sPreviousFreeTextCode = "" ;
		
		try
		{
	    if (rs.next())
	    {
	    	String sMax = rs.getString("MAX_CODE") ;
	    	
	    	if (null != sMax)		// when table is empty, it returns a null value
	    		sPreviousFreeTextCode = sMax ;
	    }
		} catch (SQLException e)
		{
			Logger.trace(sFctName + ": exception when iterating results " + e.getMessage(), _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
		}
		
		_dbConnector.closeResultSet() ;
		_dbConnector.closePreparedStatement() ;
		
		// Should occur when table is empty or if the concept has no registered free text already
		//
		if ("".equals(sPreviousFreeTextCode))
			return QuadrifoliumServerFcts.getFirstFreeTextHeaderCodeForConcept(sConceptCode) ;
		
		// Returns next available free text header code when free texts already exist for this concept
		//
		return QuadrifoliumServerFcts.getNextFreeTextHeaderCodeForConcept(sPreviousFreeTextCode) ;		
	}
	
	/**
	 * Get the next available code for a given concept<br>
	 * <br>
	 * This function needs a registered user.
	 * 
	 * @param sConceptCode Code of the concept this free text is attached to
	 * 
	 * @return Next available free text code, or <code>""</code> if something went wrong
	 */
	public String getNextFreeTextCode(final String sConceptCode)
	{
		String sFctName = "FreeTextManager.getNextFreeTextCode" ;

		// This function needs a registered user
		//
		if (null == _sessionElements)
		{
			Logger.trace(sFctName + ": no session elements.", -1, Logger.TraceLevel.ERROR) ;
			return "" ;
		}
		
		if ((null == _dbConnector) || (null == sConceptCode) || "".equals(sConceptCode))
		{
			Logger.trace(sFctName + ": bad parameter", _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
			return "" ;
		}
		
		// Get free texts for this concept, but not free text "headers" (in the form concept + "+" + code)
		//
		String sQuery = "SELECT MAX(code) AS MAX_CODE FROM freeText WHERE code LIKE ? AND code NOT LIKE ?" ;
		
		_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
		if (null == _dbConnector.getPreparedStatement())
		{
			Logger.trace(sFctName + ": cannot get Statement", _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return "" ;
		}
		
		_dbConnector.setStatememtString(1, sConceptCode + "%") ;
		_dbConnector.setStatememtString(2, sConceptCode + "+" + "%") ;
		
		if (false == _dbConnector.executePreparedStatement())
		{
			Logger.trace(sFctName + ": failed query " + sQuery, _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return "" ;
		}
	   		
		ResultSet rs = _dbConnector.getResultSet() ;
		if (null == rs)
		{
			Logger.trace(sFctName + ": no max code found in table freeText for concept = " + sConceptCode, _sessionElements.getPersonId(), Logger.TraceLevel.WARNING) ;
			_dbConnector.closePreparedStatement() ;
			return "" ;
		}
				
		String sPreviousFreeTextCode = "" ;
		
		try
		{
	    if (rs.next())
	    {
	    	String sMax = rs.getString("MAX_CODE") ;
	    	
	    	if (null != sMax)		// when table is empty, it returns a null value
	    		sPreviousFreeTextCode = sMax ;
	    }
		} catch (SQLException e)
		{
			Logger.trace(sFctName + ": exception when iterating results " + e.getMessage(), _sessionElements.getPersonId(), Logger.TraceLevel.ERROR) ;
		}
		
		_dbConnector.closeResultSet() ;
		_dbConnector.closePreparedStatement() ;
		
		// Should occur when table is empty or if the concept has no registered free text already
		//
		if ("".equals(sPreviousFreeTextCode))
			return QuadrifoliumServerFcts.getFirstFreeTextCodeForConcept(sConceptCode) ;
		
		// Returns next available free text header code when free texts already exist for this concept
		//
		return QuadrifoliumServerFcts.getNextFreeTextCodeForConcept(sPreviousFreeTextCode) ;		
	}	
}
