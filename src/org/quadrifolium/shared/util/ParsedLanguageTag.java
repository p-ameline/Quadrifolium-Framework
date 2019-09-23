package org.quadrifolium.shared.util ;

import com.google.gwt.user.client.rpc.IsSerializable ;

/**
 *  Language tag as defined by BCP 47 (https://tools.ietf.org/html/bcp47)
 * 
 * Author: PA
 * 
 */
public class ParsedLanguageTag implements IsSerializable 
{
	protected String _sBCP47Code ;
	
	protected String _sLanguage ;
	protected String _sExtLang ;
	protected String _sScript ;
	protected String _sRegion ;
	protected String _sVariant ;
	protected String _sExtension ;
	protected String _sPrivateUse ;
	
	public ParsedLanguageTag() {
		reset() ;
	}
	
	public ParsedLanguageTag(final String sBCP47Code) 
	{
		_sBCP47Code = sBCP47Code ;
		
		resetVars() ;
		
		parse() ;
	}
	
	/**
	 * Copy constructor
	 */
	public ParsedLanguageTag(final ParsedLanguageTag other) 
	{
		copyFromModel(other) ;
	}
	
	/**
	 * Set all information to <code>""</code>
	 */
	public void reset()
	{
		_sBCP47Code  = "" ;
		
		resetVars() ;
	}
	
	/**
	 * Set all information, but the BCP 47 code, to <code>""</code>
	 */
	public void resetVars()
	{
		_sLanguage   = "" ;
		_sExtLang    = "" ;
		_sScript     = "" ;
		_sRegion     = "" ;
		_sVariant    = "" ;
		_sExtension  = "" ;
		_sPrivateUse = "" ;
	}
	
	/**
	 * Initialize from another object of the same type
	 * @param other
	 */
	public void copyFromModel(final ParsedLanguageTag other) 
	{
		reset() ;
		
		if (null == other)
			return ;
		
		_sBCP47Code  = other._sBCP47Code ;
		
		_sLanguage   = other._sLanguage ;
		_sExtLang    = other._sExtLang ;
		_sScript     = other._sScript ;
		_sRegion     = other._sRegion ;
		_sVariant    = other._sVariant ;
		_sExtension  = other._sExtension ;
		_sPrivateUse = other._sPrivateUse ;
	}
	
	/**
	 * Parse the BCP 47 code to fill other variables
	 */
	protected void parse()
	{
		resetVars() ;
		
		if ((null == _sBCP47Code) || "".equals(_sBCP47Code))
			return ;
		
		// BCP 47 code is in the form language-extlang-script-region-variant-extension-privateuse (separated by hyphens ('-'))
		// See https://www.w3.org/International/articles/language-tags/ (later referred as (the article)
		//
		String[] aSubtags = _sBCP47Code.split("-") ;
		
		// All language tags must begin with a primary language subtag
		//
		
		if (1 == aSubtags.length)
		{
			_sLanguage = _sBCP47Code ;
			return ;
		}
		
		int iSubTagIndex = 0 ;  // current cursor in aSubtags 
		int iSubTagPosit = 0 ;  // current cursor in the theoretical list (language = 0, extlang = 1,-script = 2, etc) 
		
		_sLanguage = aSubtags[iSubTagIndex++] ;
	
		// TODO To be continued
		//
	}
	
	/**
	 * Is this language tag compatible with another one, means can it be proposed to a user whose language is "other"?
	 */
	public boolean isCompatibleWith(final ParsedLanguageTag other)
	{
		if (null == other)
			return false ;
		
		if ("".equals(_sLanguage))
			return false ;
		
		// Not the same language, not compatible 
		//
		if (false == _sLanguage.equalsIgnoreCase(other._sLanguage))
			return false ;
		
		// If user's country is specified, check if it it the same one, or none
		// (for example French is OK for someone who speaks Canadian French)
		//
		if (false == "".equals(_sRegion))
		{
			if ((false == "".equals(other._sRegion)) && (false == _sRegion.equalsIgnoreCase(other._sRegion)))
				return false ;
		}
		
		return true ;
	}
	
	public String getCode() {
		return _sBCP47Code ;
	}
	public void setCode(final String sCode) {
		_sBCP47Code = sCode ;
	}

	public String getExtLang() {
		return _sExtLang ;
	}
	public void setExtLang(final String sExtLang) {
		_sExtLang = sExtLang ;
	}
	
	public String getLanguage() {
		return _sLanguage ;
	}
	public void setLanguage(final String sLanguage) {
		_sLanguage = sLanguage ;
	}
	
	public String getScript() {
		return _sScript ;
	}
	public void setScript(final String sScript) {
		_sScript = sScript ;
	}
	
	public String getRegion() {
		return _sRegion ;
	}
	public void setRegion(final String sRegion) {
		_sRegion = sRegion ;
	}
	
	public String getVariant() {
		return _sVariant ;
	}
	public void setVariant(final String sVariant) {
		_sVariant = sVariant ;
	}
	
	public String getExtension() {
		return _sExtension ;
	}
	public void setExtension(final String sExtension) {
		_sExtension = sExtension ;
	}
	
	public String getPrivateUse() {
		return _sPrivateUse ;
	}
	public void setPrivateUse(final String sPrivateUse) {
		_sPrivateUse = sPrivateUse ;
	}
	
	/**
	 * Get tags count
	 */
	public int getInformationCount()
	{
		int iCount = 0 ;

		if (false == "".equals(_sLanguage))
			iCount++ ;
		if (false == "".equals(_sExtLang))
			iCount++ ;
		if (false == "".equals(_sScript))
			iCount++ ;
		if (false == "".equals(_sRegion))
			iCount++ ;
		if (false == "".equals(_sVariant))
			iCount++ ;
		if (false == "".equals(_sExtension))
			iCount++ ;
		if (false == "".equals(_sPrivateUse))
			iCount++ ;
		
		return iCount ;
	}
}
