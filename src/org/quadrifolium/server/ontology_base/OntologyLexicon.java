package org.quadrifolium.server.ontology_base ;

import java.util.ArrayList;
import java.util.Iterator;

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
public class OntologyLexicon
{
	private int    _iId ;
	private String _sLabel ;
	private String _sCode ;
	private String _sGrammar ;
	private String _sFrequency ;
	private String _sLemma ;
	
	public enum LabelType   { rawLabel, selectionLabel, displayLabel} ;
	public enum Declination { nullDeclination, singular, plural } ;
	public enum Gender      { nullGender, MSGender, FSGender, NSGender, MPGender, FPGender, NPGender, ITGender } ;
	
	//
	//
	public OntologyLexicon() {
		reset() ;
	}
		
	/**
	 * Standard constructor
	 */
	public OntologyLexicon(final String sLabel, final String sCode, final String sGrammar, final String sFrequency, final String sLemma) 
	{
		reset() ;
		
		_sLabel     = sLabel ;
		_sCode      = sCode ;
		_sGrammar   = sGrammar ;
		_sFrequency = sFrequency ;
		_sLemma     = sLemma ;
	}
	
	/**
	 * Copy constructor
	 */
	public OntologyLexicon(final OntologyLexicon model) 
	{
		reset() ;
		
		if (null == model)
			return ;
		
		_iId        = model._iId ;
		_sLabel     = model._sLabel ;
		_sCode      = model._sCode ;
		_sGrammar   = model._sGrammar ;
		_sFrequency = model._sFrequency ;
		_sLemma     = model._sLemma ;
	}
	
	public void reset() 
	{
		_iId        = -1 ;
		_sLabel     = "" ;
		_sCode      = "" ;
		_sGrammar   = "" ;
		_sFrequency = "" ;
		_sLemma     = "" ;
	}

	/**
	 * Is this session in the database?
	 * 
	 * @return <code>true</code> if this session has a database ID, <code>false</code> if not 
	 */
	public boolean isReferenced() {
		return _iId >= 0 ; 
	}
	
	public String getLabelForNoon(final LabelType iType, final Declination iDeclination, final String sLanguage)
	{
		switch(iType)
		{
			case rawLabel       : return getLabel() ;
			case selectionLabel : return getSelectionLabel() ;
			case displayLabel   : return getDisplayLabelForNoon(iDeclination, sLanguage) ;
		}
		return getLabel() ;
	}

	/**
	 *  Get a label, initially in the form "Crohn [maladie|s|][de]{détails}" in the form "maladie de Crohn"
	 *  
	 *  @param  iGender Grammatical gender for inflection
	 *  @return Modified label if Ok, empty String if not
	 */
	public String getDisplayLabelForNoon(final Declination iDeclination, final String sLanguage)
	{
		if ((null == _sLabel) || _sLabel.equals(""))
			return "" ;
		
		// Flexion information is located between ||, separated by /
	  // ex : "disjoint|t/te/ts/tes|"
	  //
	  // Three cases can be met:
	  // 1) No || -> invariable word
	  // 2) A single element between || (ex test|s|) that provides first flexion
	  //    with root being the whole word example: test|s| gives "test" and "tests" 
	  // 3) Multiple elements between || (ex animal|al/aux|), first element (0)
	  //    sets the root and next ones indicate flexions (1, 2...)

		int iBracketsLevel = -1 ;

		ArrayList<String> qualifiers = new ArrayList<String>() ;
	  String currentQualifier = "" ;
	  String sStartingWord    = "" ;
	  String sEndingWord      = "" ;
	  String sOrthography     = "" ;
		
	  // Label is separated in different parts:
	  //
	  // sStartingWord = "Crohn"
	  // sEndingWord   = "{détails}"
	  // qualifiers[0] = "maladie"
	  // qualifiers[1] = "de"
	  //
	  for (int k = 0 ; k < _sLabel.length() ; k++)
	  {
	  	// Start of a qualifier
	  	//
	    if ('[' == _sLabel.charAt(k))
	    {
	    	iBracketsLevel++ ;
	    	
	    	if (false == currentQualifier.equals(""))
	      	qualifiers.add(currentQualifier) ;
	    	
	    	currentQualifier = "" ;
	    	
	      k++ ;
	      while ((k < _sLabel.length()) && (']' != _sLabel.charAt(k)))
	      {
	      	// Start of flexion information
	      	//
	      	if ('|' == _sLabel.charAt(k))
	        {
	      		sOrthography = "" ;
	          k++ ;
	          while ((k < _sLabel.length()) && ('|' != _sLabel.charAt(k)))
	          {
	          	sOrthography += _sLabel.charAt(k) ;
	            k++ ;
	          }
	          if ((k < _sLabel.length()) && ('|' == _sLabel.charAt(k)))
	          	currentQualifier = applyOrthographyForNoon(currentQualifier, sOrthography, iDeclination, sLanguage) ;
	          else
	            return "" ;
	        }
	        else
	        	currentQualifier += _sLabel.charAt(k) ;
	        k++ ;
	      }
	    }
	    else
	    {
	    	// A string starting by {| contains a preposition
	    	//
	      if      ((k < _sLabel.length() - 1) && ('{' == _sLabel.charAt(k)) && ('|' == _sLabel.charAt(k+1)))
	      {
	        while ((k < _sLabel.length()) && ('}' != _sLabel.charAt(k)))
	          k++ ;
	      }
	      else if ('|' == _sLabel.charAt(k))
	      {
	        k++ ;
	        sOrthography = "" ;
	        while ((k < _sLabel.length()) && ('|' != _sLabel.charAt(k)))
	        {
	        	sOrthography += _sLabel.charAt(k) ;
	          k++ ;
	        }
	        if ((k < _sLabel.length()) && ('|' == _sLabel.charAt(k)))
	        {
	          if (-1 == iBracketsLevel)
	          	sStartingWord = applyOrthographyForNoon(sStartingWord, sOrthography, iDeclination, sLanguage) ;
	          else
	          	sEndingWord = applyOrthographyForNoon(sEndingWord, sOrthography, iDeclination, sLanguage) ;
	        }
	        else
	          return "" ;
	      }
	      else
	      {
	        if (-1 == iBracketsLevel)
	        	sStartingWord += _sLabel.charAt(k) ;
	        else
	        	sEndingWord   += _sLabel.charAt(k) ;
	      }
	    }
	  }

	  if (false == currentQualifier.equals(""))
    	qualifiers.add(currentQualifier) ;
	  
	  // Building new label
	  //
	  char firstChar = '\0' ;
	  char lastChar  = '\0' ;

	  if (false == sStartingWord.equals(""))
	  	sStartingWord = sStartingWord.trim() ;

	  if (false == sEndingWord.equals(""))
	  	sEndingWord = sEndingWord.trim() ;
	  
	  String sResultLabel = "" ;
	  
	  if (false == qualifiers.isEmpty())
	  {
	  	Iterator<String> it = qualifiers.iterator() ;
	  	String sFirstQualifier = it.next() ;
	  	
	  	sResultLabel += sFirstQualifier ;
	  	lastChar = sFirstQualifier.charAt(sFirstQualifier.length() - 1) ;
	  	
	  	if (it.hasNext())
	  	{
	  		String sSecondQualifier = it.next() ;
	  		firstChar = sSecondQualifier.charAt(0) ;

	  		if ((' ' != firstChar) && ('-' != lastChar) && ('\'' != lastChar) && (' ' != lastChar))
	  			sResultLabel += " " ;

	  		sResultLabel += sSecondQualifier ;

	  		lastChar = sSecondQualifier.charAt(sSecondQualifier.length() - 1) ;
	  	}
	  }

	  if (false == sStartingWord.equals(""))
	  {
	  	firstChar = sStartingWord.charAt(0) ;

	    if (false == sResultLabel.equals(""))
	      if ((' ' != firstChar) && ('-' != lastChar) && ('\'' != lastChar) && (' ' != lastChar))
	      	sResultLabel += " " ;

	    sResultLabel += sStartingWord ;
	  }

	  // Omitting leading and trailing blanks
	  sResultLabel = sResultLabel.trim() ;
	  sResultLabel = removeTrailingComments(sResultLabel) ;
	  sResultLabel = removeUselessBlanks(sResultLabel) ;
	  sResultLabel = sResultLabel.trim() ;
	  
	  return sResultLabel ;
	}
	
	/**
	 *  Get a label, initially in the form "viral|l/le/aux/les|" in the form "virales"
	 *  
	 *  @param iGenderAndNummber Grammatical gender for inflection
	 *  @param sLanguage         Language the inflection is built for
	 *  
	 *  @return Modified label if Ok, empty String if not
	 */
	public String getDisplayLabelForAdjective(final Gender iGenderAndNummber, final String sLanguage)
	{
		if ((null == _sLabel) || _sLabel.equals(""))
			return "" ;
		
		// Flexion information is located between ||, separated by /
	  // ex : "disjoint|t/te/ts/tes|"
	  //
	  // Three cases can be met:
	  // 1) No || -> invariable word
	  // 2) A single element between || (ex test|s|) that provides first flexion
	  //    with root being the whole word example: test|s| gives "test" and "tests" 
	  // 3) Multiple elements between || (ex animal|al/aux|), first element (0)
	  //    sets the root and next ones indicate flexions (1, 2...)

		int iBracketsLevel = -1 ;

		ArrayList<String> qualifiers = new ArrayList<String>() ;
	  String currentQualifier = "" ;
	  String sStartingWord    = "" ;
	  String sEndingWord      = "" ;
	  String sOrthography     = "" ;
		
	  // Label is separated in different parts:
	  //
	  // sStartingWord = "Crohn"
	  // sEndingWord   = "{détails}"
	  // qualifiers[0] = "maladie"
	  // qualifiers[1] = "de"
	  //
	  for (int k = 0 ; k < _sLabel.length() ; k++)
	  {
	  	// Start of a qualifier
	  	//
	    if ('[' == _sLabel.charAt(k))
	    {
	    	iBracketsLevel++ ;
	    	
	    	if (false == currentQualifier.equals(""))
	      	qualifiers.add(currentQualifier) ;
	    	
	    	currentQualifier = "" ;
	    	
	      k++ ;
	      while ((k < _sLabel.length()) && (']' != _sLabel.charAt(k)))
	      {
	      	// Start of flexion information
	      	//
	      	if ('|' == _sLabel.charAt(k))
	        {
	      		sOrthography = "" ;
	          k++ ;
	          while ((k < _sLabel.length()) && ('|' != _sLabel.charAt(k)))
	          {
	          	sOrthography += _sLabel.charAt(k) ;
	            k++ ;
	          }
	          if ((k < _sLabel.length()) && ('|' == _sLabel.charAt(k)))
	          	currentQualifier = applyOrthographyForAdjective(currentQualifier, sOrthography, iGenderAndNummber, sLanguage) ;
	          else
	            return "" ;
	        }
	        else
	        	currentQualifier += _sLabel.charAt(k) ;
	        k++ ;
	      }
	    }
	    else
	    {
	    	// A string starting by {| contains a preposition
	    	//
	      if      ((k < _sLabel.length() - 1) && ('{' == _sLabel.charAt(k)) && ('|' == _sLabel.charAt(k+1)))
	      {
	        while ((k < _sLabel.length()) && ('}' != _sLabel.charAt(k)))
	          k++ ;
	      }
	      else if ('|' == _sLabel.charAt(k))
	      {
	        k++ ;
	        sOrthography = "" ;
	        while ((k < _sLabel.length()) && ('|' != _sLabel.charAt(k)))
	        {
	        	sOrthography += _sLabel.charAt(k) ;
	          k++ ;
	        }
	        if ((k < _sLabel.length()) && ('|' == _sLabel.charAt(k)))
	        {
	          if (-1 == iBracketsLevel)
	          	sStartingWord = applyOrthographyForAdjective(sStartingWord, sOrthography, iGenderAndNummber, sLanguage) ;
	          else
	          	sEndingWord = applyOrthographyForAdjective(sEndingWord, sOrthography, iGenderAndNummber, sLanguage) ;
	        }
	        else
	          return "" ;
	      }
	      else
	      {
	        if (-1 == iBracketsLevel)
	        	sStartingWord += _sLabel.charAt(k) ;
	        else
	        	sEndingWord   += _sLabel.charAt(k) ;
	      }
	    }
	  }

	  if (false == currentQualifier.equals(""))
    	qualifiers.add(currentQualifier) ;
	  
	  // Building new label
	  //
	  char firstChar = '\0' ;
	  char lastChar  = '\0' ;

	  if (false == sStartingWord.equals(""))
	  	sStartingWord = sStartingWord.trim() ;

	  if (false == sEndingWord.equals(""))
	  	sEndingWord = sEndingWord.trim() ;
	  
	  String sResultLabel = "" ;
	  
	  if (false == qualifiers.isEmpty())
	  {
	  	Iterator<String> it = qualifiers.iterator() ;
	  	String sFirstQualifier = it.next() ;
	  	
	  	sResultLabel += sFirstQualifier ;
	  	lastChar = sFirstQualifier.charAt(sFirstQualifier.length() - 1) ;
	  	
	  	if (it.hasNext())
	  	{
	  		String sSecondQualifier = it.next() ;
	  		firstChar = sSecondQualifier.charAt(0) ;

	  		if ((' ' != firstChar) && ('-' != lastChar) && ('\'' != lastChar) && (' ' != lastChar))
	  			sResultLabel += " " ;

	  		sResultLabel += sSecondQualifier ;

	  		lastChar = sSecondQualifier.charAt(sSecondQualifier.length() - 1) ;
	  	}
	  }

	  if (false == sStartingWord.equals(""))
	  {
	  	firstChar = sStartingWord.charAt(0) ;

	    if (false == sResultLabel.equals(""))
	      if ((' ' != firstChar) && ('-' != lastChar) && ('\'' != lastChar) && (' ' != lastChar))
	      	sResultLabel += " " ;

	    sResultLabel += sStartingWord ;
	  }

	  // Omitting leading and trailing blanks
	  sResultLabel = sResultLabel.trim() ;
	  sResultLabel = removeTrailingComments(sResultLabel) ;
	  sResultLabel = removeUselessBlanks(sResultLabel) ;
	  sResultLabel = sResultLabel.trim() ;
	  
	  return sResultLabel ;
	}
	
	/**
	*  Get a label, initially in the form "Crohn [maladie|s|][de]{détails}" in the form "Crohn (maladie){détails}"
	*  
	*  @return Modified label if Ok, empty String if not
	**/
	public String getSelectionLabel()
	{
		if ((null == _sLabel) || _sLabel.equals(""))
			return "" ;
		
		// First, remove flexing information
		//
		String sNewLabel = getLabelWithoutFlexInfo() ;
		
		int iBracketsLevel = -1 ;

		ArrayList<String> qualifiers = new ArrayList<String>() ;
	  String currentQualifier = "" ;
	  String sStartingWord    = "" ;
	  String sEndingWord      = "" ;

	  // Parsing label in the form :
	  // startingWord = "Crohn"
	  // endingWord   = "{détails}"
	  // qualifier[0] = "maladie"
	  // qualifier[1] = "de"
	  //
	  int iLabelLen = sNewLabel.length() ;
	  
	  for (int i = 0 ; i < iLabelLen ; i++)
	  {
	  	char currentChar = sNewLabel.charAt(i) ;
	    
	  	// Opening brackets: start of a qualifier
	  	//
	  	if ('[' == currentChar)
	    {
	    	iBracketsLevel++ ;
	      i++ ;
	      
	      if (false == currentQualifier.equals(""))
	      	qualifiers.add(currentQualifier) ;
	      
	      currentQualifier = "" ;

	      while ((']' != sNewLabel.charAt(i)) && (i < iLabelLen))
	      {
	      	currentQualifier += sNewLabel.charAt(i) ;
	        i++ ;
	      }
	    }
	    else
	    {
	      if (-1 == iBracketsLevel)
	      	sStartingWord += sNewLabel.charAt(i) ;
	      else
	      	sEndingWord += sNewLabel.charAt(i) ;
	    }
	  }

	  if (false == currentQualifier.equals(""))
    	qualifiers.add(currentQualifier) ;
	  
	  // Building new label
	  String sResultLabel = sStartingWord ;
	  if (false == qualifiers.isEmpty())
	  {
	  	Iterator<String> it = qualifiers.iterator() ;
	  	sResultLabel += "(" + it.next() + ")" ;
	  }
	  sResultLabel += sEndingWord ;

		// return sNewLabel ;
	  return sResultLabel ;
	}
	
	/**
	*  Flex information are between '|' - we have to remove it
	*  
	*  @return Modified label if Ok, empty String if not
	**/
	public String getLabelWithoutFlexInfo() 
	{
		if ((null == _sLabel) || _sLabel.equals(""))
			return "" ;
		
		// Flex information are between '|' - we have to remove it
		//
		String sNewLabel = "" ;
		boolean bInsidePipes = false ;
		for (int i = 0 ; i < _sLabel.length() ; i++)
		{
			char currentChar = _sLabel.charAt(i) ;
			if ('|' == currentChar)
				bInsidePipes = !bInsidePipes ;
			else if (false == bInsidePipes)
				sNewLabel += currentChar ;
		}
		
		sNewLabel = removeUselessBlanks(sNewLabel) ;
		
		return sNewLabel ;
  }
	
	/**
	*  Return a String without trailing comments -> mean it cuts at first '{'
	*  
	*  @param  sLabel String to be processed
	*  @return Modified string if Ok, empty String if not
	**/
	public static String removeTrailingComments(final String sLabel) 
	{
		if ((null == sLabel) || sLabel.equals(""))
			return "" ;
		
		int iSeparateStart = sLabel.indexOf(" {") ;
		if (-1 != iSeparateStart)
			return sLabel.substring(0, iSeparateStart) ;
		
		int iTouchingStart = sLabel.indexOf('{') ;
		if (-1 != iTouchingStart)
			return sLabel.substring(0, iTouchingStart) ;
		
		return sLabel ;		
  }
	
	/**
	*  Return a String without useless separating blanks
	*  
	*  @param  sLabel String to be processed
	*  @return Modified string if Ok, empty String if not
	**/
	public static String removeUselessBlanks(final String sLabel)
	{
		if ((null == sLabel) || sLabel.equals(""))
			return "" ;
		
		int iFirstBlank = sLabel.indexOf(' ') ;
		if (-1 == iFirstBlank)
			return sLabel ;
	
		String sReturn = sLabel ;
		
		while (-1 != iFirstBlank)
	  {
	    int iStart = iFirstBlank ;

	    int iLabelSize = sReturn.length() ;
	    if ((iStart < iLabelSize - 1) && (' ' == sReturn.charAt(iStart + 1)))
	    {
	      int iEnd = iStart + 1 ;
	      while ((iEnd < iLabelSize - 1) && (' ' == sReturn.charAt(iEnd + 1)))
	      	iEnd++ ;

	      sReturn = sReturn.substring(0, iStart) + " " + sReturn.substring(iEnd + 1, iLabelSize) ;
	    }

	    iStart++ ;
	    iFirstBlank = sReturn.indexOf(' ', iStart) ;
	  }
		
		return sReturn ;
  }
	
	/**
	 *  Return the proper inflection for a noon
	 *  
	 *  @param  sLabel String to be processed
	 *  @param  sFlexInformation Information for inflection
	 *  @param  iFlexIndex Index of inflection to be applied from sFlexInformation
	 *  
	 *  @return Modified string if Ok, empty String if not
	 */
	public String applyOrthographyForNoon(final String sLabel, final String sFlexInformation, final Declination iDeclination, final String sLanguage)
	{
		if ((null == sLabel) || sLabel.equals(""))
			return "" ;
		
		// Get index for declination
	  //
	  int iFlexIndex = indexForDeclinationInNoon(iDeclination, sLanguage) ;
	  if (-1 == iFlexIndex)
	  	return sLabel.trim() ;
	  
	  return addProperEnding(sLabel, sFlexInformation, iFlexIndex) ;
	}
	
	/**
	 *  Return the proper inflection for an adjective
	 *  
	 *  @param  sLabel            String to be processed
	 *  @param  sFlexInformation  Information for inflection
	 *  @param  iGenderAndNummber Index of inflection to be applied from sFlexInformation
	 *  @param  sLanguage         Language to get inflection for
	 *  
	 *  @return Modified string if Ok, empty String if not
	 */
	public String applyOrthographyForAdjective(final String sLabel, final String sFlexInformation, final Gender iGenderAndNummber, final String sLanguage)
	{
		if ((null == sLabel) || sLabel.equals(""))
			return "" ;
		
		// Get index for declination
	  //
	  int iFlexIndex = indexForGenderAndNumberInAdjectives(iGenderAndNummber, sLanguage) ;
	  if (-1 == iFlexIndex)
	  	return sLabel.trim() ;
	  
	  return addProperEnding(sLabel, sFlexInformation, iFlexIndex) ;
	}
	  
	/**
	 * Apply the nth inflection to a label
	 * 
	 * @param sLabel           Word to apply flection to
	 * @param sFlexInformation List of endings (separated by the '/' char) 
	 * @param iFlectionIndex   Index of the inflection in sFlexIntomation to apply to sLabel
	 * 
	 * @return Modified string if Ok, empty String if not
	 */
	protected String addProperEnding(final String sLabel, final String sFlexInformation, final int iFlectionIndex)
	{
		if ((null == sLabel) || sLabel.equals(""))
			return "" ;
		
		// Make certain undesired blanks don't get inserted between root and flexion
		//
		String sTrimmedLabel = sLabel.trim() ;
		
		// If flexion index is "basic", then nothing to do
	  //
	  if (0 == iFlectionIndex)
	    return sTrimmedLabel ;
	  
	  if ((null == sFlexInformation) || sFlexInformation.equals(""))
			return sTrimmedLabel ;

	  String[] flexEndings    = sFlexInformation.split("/") ;
	  int      iNbOfFlexElmnt = flexEndings.length ;
	  
    String sRootFlexion = flexEndings[0] ;
    
	  // If there is a single flexion data :
	  //
	  //      if iFlexIndex == 0, nothing to do
	  //      if iFlexIndex == 1, flexion is added to the word
	  if (1 == iNbOfFlexElmnt)
	  {
	  	String sResultLabel = sTrimmedLabel ;
	    if (1 == iFlectionIndex)
		  	sResultLabel += sRootFlexion ;
	    return sResultLabel ;
	  }

	  // If multiple flexion data exist :
	  //      first we find the root from element 0
	  //      then we add asked flexion

	  if (iFlectionIndex > iNbOfFlexElmnt - 1)
	  	return sTrimmedLabel ;
	  
	  String sSelectedFlexData = flexEndings[iFlectionIndex] ;
	  
	  int iRootLen = sTrimmedLabel.length() - sRootFlexion.length() ;
	  if (iRootLen <= 0)
	    return sSelectedFlexData ;

	  return sTrimmedLabel.substring(0, iRootLen) + sSelectedFlexData ;
  }
	
	/**
	*  Return the flexion index for a noon in a given language and declination 
	*  
	*  @param  iDeclination Declination (singular or plural)
	*  @param  sLanguage Language
	*  @return The index of flexion choices if Ok, -1 if not
	**/
	private int indexForDeclinationInNoon(final Declination iDeclination, final String sLanguage)
	{
		if ((null == sLanguage) || sLanguage.equals(""))
			return -1 ;
		
		if (false == this.isNoon())
			return -1 ;
		
		// For French and English languages, a noon is just singular or plural
		//
		if ((sLanguage.length() >= 2) && (sLanguage.substring(0, 2).equals("fr") ||
				                              sLanguage.substring(0, 2).equals("en")))
		{
			switch(iDeclination)
			{
				case nullDeclination : return -1 ;
				case singular        : return 0 ; 
				case plural          : return 1 ; 
			}
			return -1 ;
		}
		
		return -1 ;
	}
	
	/**
	*  Return the flexion index for an adjective in a given language, gender and number 
	*  
	*  @param  iGender Gender (+ number)
	*  @param  sLanguage Language
	*  @return The index of flexion choices if Ok, -1 if not
	**/
	private int indexForGenderAndNumberInAdjectives(final Gender iGender, final String sLanguage)
	{
		if ((null == sLanguage) || sLanguage.equals(""))
			return -1 ;
		
		if (false == this.isAdjective())
			return -1 ;
		
		if (sLanguage.length() < 2)
			return -1 ;
		
		String sLang2c = sLanguage.substring(0, 2) ;
		
		// In English, adjectives are invariable
		//
		if ("en".equals(sLang2c))
			return 0 ;
		
		// In French, there are 4 categories (MS, MP, FS and FP)
		//
		if ("fr".equals(sLang2c))
		{
			switch(iGender)
			{
				case MSGender : return 0 ; 
				case FSGender : return 1 ;
				case MPGender : return 2 ; 
				case FPGender : return 3 ;
				default       : return -1 ;
			}
		}
		
		return -1 ;
	}
	
	public int getId() {
  	return _iId ;
  }
	public void setId(final int iId) {
  	_iId = iId ;
  }
	
	public String getLabel() {
  	return _sLabel ;
  }
	public void setLabel(final String sLabel) {
  	_sLabel = sLabel ;
  }

	public String getCode() {
  	return _sCode ;
  }
	public void setCode(final String sCode) {
  	_sCode = sCode ;
  }

	public String getGrammar() {
  	return _sGrammar ;
  }
	public void setGrammar(final String sGrammar) {
		_sGrammar = sGrammar ;
  }

	public String getFrequency() {
  	return _sFrequency ;
  }
	public void setFrequency(final String sFrequency) {
		_sFrequency = sFrequency ;
  }
	
	public String getLemma() {
  	return _sLemma ;
  }
	public void setLemma(final String sLemma) {
		_sLemma = sLemma ;
  }
	
	/**
	  * Is this Lexicon entry a noon?
	  * 
	  * @return true if yes, false if not
	  */
	public boolean isNoon()
	{
		char cFirstChar = getGrammarFirstChar() ;
		return (('M' == cFirstChar) || ('F' == cFirstChar) || ('N' == cFirstChar)) ;
	}
	
	/**
	  * Is this Lexicon entry a verb?
	  * 
	  * @return true if yes, false if not
	  */
	public boolean isVerb() {
		return ('V' == getGrammarFirstChar()) ;
	}
	
	/**
	  * Is this Lexicon entry invariable?
	  * 
	  * @return true if yes, false if not
	  */
	public boolean isInvariable() {
		return ('I' == getGrammarFirstChar()) ;
	}
	
	/**
	  * Is this Lexicon entry an adjective?
	  * 
	  * @return true if yes, false if not
	  */
	public boolean isAdjective()
	{
		if (null == _sGrammar)
			return false ;
		return (_sGrammar.equals("ADJ")) ;
	}
	
	/**
	  * Is this Lexicon entry an adverb?
	  * 
	  * @return true if yes, false if not
	  */
	public boolean isAdverb()
	{
		if (null == _sGrammar)
			return false ;
		return (_sGrammar.equals("ADV")) ;
	}
	
	/**
	  * Is this Lexicon entry of male gender?
	  * 
	  * @return true if yes, false if not
	  */
	public boolean isMaleGender()
	{
		if (null == _sGrammar)
			return false ;
		return (_sGrammar.equals("MS") || _sGrammar.equals("MP")) ;
	}
	
	/**
	  * Is this Lexicon entry of female gender?
	  * 
	  * @return true if yes, false if not
	  */
	public boolean isFemaleGender()
	{
		if (null == _sGrammar)
			return false ;
		return (_sGrammar.equals("FS") || _sGrammar.equals("FP")) ;
	}
	
	/**
	  * Is this Lexicon entry of neutral gender?
	  * 
	  * @return true if yes, false if not
	  */
	public boolean isNeutralGender()
	{
		if (null == _sGrammar)
			return false ;
		return (_sGrammar.equals("NS") || _sGrammar.equals("NP")) ;
	}
	
	/**
	  * Is this Lexicon entry of singular declination?
	  * 
	  * @return true if yes, false if not
	  */
	public boolean isSingular()
	{
		if (null == _sGrammar)
			return false ;
		return (_sGrammar.equals("MS") || _sGrammar.equals("FS") || _sGrammar.equals("NS")) ;
	}
	
	/**
	  * Is this Lexicon entry of plural declination?
	  * 
	  * @return true if yes, false if not
	  */
	public boolean isPlural()
	{
		if (null == _sGrammar)
			return false ;
		return (_sGrammar.equals("MP") || _sGrammar.equals("FP") || _sGrammar.equals("NP")) ;
	}
	
	/**
	  * Get first char of the Grammar information
	  * 
	  * @return A char if Ok, <code>'\0'</code> if not
	  */
	public char getGrammarFirstChar()
	{
		if ((null == _sGrammar) || _sGrammar.equals(""))
			return '\0' ;
		
		return (_sGrammar.charAt(0)) ;
	}
	
	/**
	 * The label is in the form "Crohn [maladie|s|][de]{détails}" this function returns "détails"
	 * 
	 * @return The meaning clarification string is any, <code>""</code> if not
	 */
	public String getMeaningClarification()
	{
		if ((null == _sLabel) || "".equals(_sLabel))
			return "" ;
		
		int iStart = _sLabel.indexOf('{') ;
		if (-1 == iStart)
			return "" ;
		
		int iLabelLen = _sLabel.length() ;
		
		while (-1 != iStart)
		{
			if (iStart >= iLabelLen - 1)
				return "" ;
			
			// A "{|" sequence means grammatical information
			//
			if ('|' == _sLabel.charAt(iStart + 1))
				iStart = _sLabel.indexOf('{', iStart + 1) ;
			else
			{
				int iEnd = _sLabel.indexOf('}', iStart + 1) ;
				if (-1 == iEnd)
					return "" ;
				
				return _sLabel.substring(iStart + 1, iEnd) ;
			}
		}
		
		return "" ;	
	}
	
	/**
	  * Determine whether two Lexicon objects are exactly similar
	  * 
	  * @return true if all data are the same, false if not
	  * @param  lexicon Other Lexicon to compare to
	  * 
	  */
	public boolean equals(final OntologyLexicon lexicon)
	{
		if (this == lexicon) {
			return true ;
		}
		if (null == lexicon) {
			return false ;
		}
		
		return ((_iId == lexicon._iId) &&
				    _sLabel.equals(lexicon._sLabel) && 
				    _sCode.equals(lexicon._sCode) &&
				    _sGrammar.equals(lexicon._sGrammar) &&
				    _sFrequency.equals(lexicon._sFrequency) &&
				    _sLemma.equals(lexicon._sLemma)) ;
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

		final OntologyLexicon lexicon = (OntologyLexicon) o ;

		return (this.equals(lexicon)) ;
	}
}
