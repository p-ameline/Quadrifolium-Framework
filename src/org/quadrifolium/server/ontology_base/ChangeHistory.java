package org.quadrifolium.server.ontology_base ;

/**
 * Person.java
 *
 * The ChangeHistory class represents object type "changeHistory" in database
 * 
 * Created: 20 Apr 2019
 *
 * Author: PA
 * 
 */
public class ChangeHistory
{
	private int    _iId ;
	private int    _iSessionId ;
	private String _sTable ;
	private String _sType ;
	private int    _iElementId ;
	private int    _iHistoryId ;
	
	public enum TableType  { triple, lemma, flex, undefined } ;
	public enum ChangeType { create, change, delete, undefined } ;
	
	//
	//
	public ChangeHistory() {
		reset() ;
	}
		
	/**
	 * Default constructor
	 */
	public ChangeHistory(final int iSessionId, final TableType table, final ChangeType type, final int iElementId, final int iHistoryId) 
	{
		_iSessionId = iSessionId ;
		_iElementId = iElementId ;
		_iHistoryId = iHistoryId ;
		
		setTable(table) ;
		setType(type) ;
	}
	
	/**
	 * Initialize a session from a model session
	 * @param session Model session
	 */
	public void initFromChangeHistory(final ChangeHistory other)
	{
		reset() ;
		
		if (null == other)
			return ;
		
		_iId        = other._iId ;
		
		_iSessionId = other._iSessionId ;
		_iElementId = other._iElementId ;
		_iHistoryId = other._iHistoryId ;
		
		_sTable     = other._sTable ;
		_sType      = other._sType ;
	}

	/**
	 * Set all information to void
	 */
	public void reset() 
	{
		_iId        = -1 ;
		_iSessionId = -1 ;
		_iElementId = -1 ;
		_iHistoryId = -1 ;
		_sTable     = "" ;
		_sType      = "" ;
	}
			
	/**
	 * Is this session in the database?
	 * 
	 * @return <code>true</code> if this session has a database ID, <code>false</code> if not 
	 */
	public boolean isReferenced() {
		return _iId >= 0 ; 
	}
	
	// getter and setter
	//
	public int getId() {
		return _iId ;
	}
	public void setId(final int id) {
		_iId = id ;
	}
	
	public int getSessionId() {
		return _iSessionId ;
	}
	public void setSessionId(final int id) {
		_iSessionId = id ;
	}

	public TableType getTable()
	{
		if ("L".equals(_sTable))
			return TableType.lemma ;
		if ("F".equals(_sTable))
			return TableType.flex ;
		if ("T".equals(_sTable))
			return TableType.triple ;
		
		return TableType.undefined ;
	}
	public String getTableString() {
		return _sTable ;
	}
	public void setTable(final TableType table) 
	{
		switch (table)
		{
			case lemma :
				_sTable = "L" ;
				break ;
			case flex :
				_sTable = "F" ;
				break ;
			case triple :
				_sTable = "T" ;
				break ;
			default :
				_sTable = "" ;
		}
	}
	public void setTableString(final String sTable) {
		_sTable = sTable ;
	}

	public ChangeType getType() 
	{
		if ("N".equals(_sType))
			return ChangeType.create ;
		if ("C".equals(_sType))
			return ChangeType.change ;
		if ("D".equals(_sType))
			return ChangeType.delete ;
		
		return ChangeType.undefined ;
	}
	public String getTypeString() {
		return _sType ;
	}
	public void setType(final ChangeType type) 
	{
		switch (type)
		{
			case create :
				_sType = "N" ;
				break ;
			case change :
				_sType = "C" ;
				break ;
			case delete :
				_sType = "D" ;
				break ;
			default :
				_sType = "" ;
		}
	}
	public void setTypeString(final String sType) {
		_sType = sType ;
	}
	
	public int getElementId() {
		return _iElementId ;
	}
	public void setElementId(final int id) {
		_iElementId = id ;
	}
	
	public int getHistoryId() {
		return _iHistoryId ;
	}
	public void setHistoryId(final int id) {
		_iHistoryId = id ;
	}
}
