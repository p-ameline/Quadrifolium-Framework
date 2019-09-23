package org.quadrifolium.shared.ontology ;

import com.google.gwt.user.client.rpc.IsSerializable ;

/**
 * Language.java
 *
 * The LanguageTag class represents language tags as defined by BCP 47
 * 
 * Created: 10 Oct 2010
 *
 * Author: PA
 * 
 */
public class LanguageTag implements java.lang.Comparable<LanguageTag>, IsSerializable 
{
	private int    _iId ;
	private String _sBCP47Code ;
	private String _sLabel ;
	
	public LanguageTag() {
		reset() ;
	}
	
	public LanguageTag(final int iId, final String sBCP47Code, final String sLabel) 
	{
		_iId        = iId ;
		_sBCP47Code = sBCP47Code ;
		_sLabel     = sLabel ;
	}
	
	public LanguageTag(final LanguageTag lang) 
	{
		_iId        = lang.getId() ;
		_sBCP47Code = lang.getCode() ;
		_sLabel     = lang.getLabel() ;
	}
		
	public void reset() 
	{
		_iId        = -1 ;
		_sBCP47Code = "" ;
		_sLabel     = "" ;
	}
	
	public int getId() {
		return _iId ;
	}
	public void setId(final int iId) {
		_iId = iId ;
	}
	
	public String getCode() {
		return _sBCP47Code ;
	}
	public void setCode(final String sCode) {
		_sBCP47Code = sCode ;
	}

	public String getLabel() {
		return _sLabel ;
	}
	public void setLabel(final String sLabel) {
		_sLabel = sLabel ;
	}
	
	@Override
	public int compareTo(final LanguageTag arg0) {
		if (_sBCP47Code.equals(arg0._sBCP47Code))
			return 1 ;
		return 0;
	}
}
