package org.quadrifolium.server.ontology_base ;

/**
 * Lexicon.java
 *
 * The Lexicon class represents object type information in database
 * 
 * Created: 25 Dec 2011
 *
 * Author: PA
 * 
 */
public class OntologySavoir
{
	private int    _iId ;
	private String _sCode ;
	private String _sQualifie ;
	private String _sLien ;
	private String _sQualifiant ;
	private String _sDegre ;
	private String _sClass ;
	private String _sScenario ;
	
	//
	//
	public OntologySavoir() {
		reset() ;
	}
		
	/**
	 * Standard constructor
	 */
	public OntologySavoir(final String sCode, final String sQualifie, final String sLien, final String sQualifiant) 
	{
		reset() ;
		
		_sCode       = sCode ;
		_sQualifie   = sQualifie ;
		_sLien       = sLien ;
		_sQualifiant = sQualifiant ;
	}
	
	/**
	 * Copy constructor
	 */
	public OntologySavoir(final OntologySavoir model) 
	{
		reset() ;
		
		if (null == model)
			return ;
		
		_iId         = model._iId ;
		_sCode       = model._sCode ;
		_sQualifie   = model._sQualifie ;
		_sLien       = model._sLien ;
		_sQualifiant = model._sQualifiant ;
		_sDegre      = model._sDegre ;
		_sClass      = model._sClass ;
		_sScenario   = model._sScenario ;
	}
	
	public void reset() 
	{
		_iId         = -1 ;
		_sCode       = "" ;
		_sQualifie   = "" ;
		_sLien       = "" ;
		_sQualifiant = "" ;
		_sDegre      = "" ;
		_sClass      = "" ;
		_sScenario   = "" ;
	}

	/**
	 * Is this session in the database?
	 * 
	 * @return <code>true</code> if this session has a database ID, <code>false</code> if not 
	 */
	public boolean isReferenced() {
		return _iId >= 0 ; 
	}
	
	public int getId() {
  	return _iId ;
  }
	public void setId(final int iId) {
  	_iId = iId ;
  }
	
	public String getCode() {
  	return _sCode ;
  }
	public void setCode(final String sCode) {
  	_sCode = sCode ;
  }

	public String getQualifie() {
  	return _sQualifie ;
  }
	public void setQualifie(final String sQualifie) {
		_sQualifie = sQualifie ;
  }

	public String getLien() {
  	return _sLien ;
  }
	public void setLien(final String sLien) {
		_sLien = sLien ;
  }
	
	public String getQualifiant() {
  	return _sQualifiant ;
  }
	public void setQualifiant(final String sQualifiant) {
		_sQualifiant = sQualifiant ;
  }
		
	/**
	  * Determine whether two Lexicon objects are exactly similar
	  * 
	  * @return true if all data are the same, false if not
	  * @param  lexicon Other Lexicon to compare to
	  * 
	  */
	public boolean equals(final OntologySavoir other)
	{
		if (this == other) {
			return true ;
		}
		if (null == other) {
			return false ;
		}
		
		return ((_iId == other._iId) &&
				     _sCode.equals(other._sCode) && 
				     _sQualifie.equals(other._sQualifie) &&
				     _sLien.equals(other._sLien) &&
				     _sQualifiant.equals(other._sQualifiant)) ;
	}
  
	/**
	  * Determine whether an object is exactly similar to this Lexicon object
	  * 
	  * designed for ArrayList.contains(Obj) method
		* because by default, contains() uses equals(Obj) method of Obj class for comparison
	  * 
	  * @return true if all data are the same, false if not
	  * @param node LdvModelNode to compare to
	  * 
	  */
	public boolean equals(final Object o) 
	{
		if (this == o) {
			return true ;
		}
		if (null == o || getClass() != o.getClass()) {
			return false;
		}

		final OntologySavoir lexicon = (OntologySavoir) o ;

		return (this.equals(lexicon)) ;
	}
}
