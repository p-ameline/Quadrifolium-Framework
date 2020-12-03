package org.quadrifolium.server.handler4ontology;

import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;

import org.quadrifolium.server.ontology.FlexManager;
import org.quadrifolium.server.ontology.LemmaManager;
import org.quadrifolium.server.ontology.TripleManager;
import org.quadrifolium.shared.ontology.Flex;
import org.quadrifolium.shared.ontology.FlexWithTraits;
import org.quadrifolium.shared.ontology.Lemma;
import org.quadrifolium.shared.ontology.Triple;
import org.quadrifolium.shared.ontology.TripleWithLabel;
import org.quadrifolium.shared.rpc4ontology.GetFlexListFromCodesAction;
import org.quadrifolium.shared.rpc4ontology.GetFlexListFromCodesResult;
import org.quadrifolium.shared.rpc4ontology.ResultElement;
import org.quadrifolium.shared.rpc4ontology.SearchAttribute;
import org.quadrifolium.shared.rpc4ontology.SearchElement;
import org.quadrifolium.shared.util.QuadrifoliumFcts;

import com.google.inject.Inject;
import com.google.inject.Provider;

import com.ldv.server.DBConnector;
import com.ldv.server.Logger;
import com.ldv.server.handler.LdvActionHandler;

public class GetFlexListFromCodesHandler extends LdvActionHandler<GetFlexListFromCodesAction, GetFlexListFromCodesResult>
{
	protected int _iUserId ;
	
	@Inject
	public GetFlexListFromCodesHandler(final Provider<ServletContext> servletContext,       
                                     final Provider<HttpServletRequest> servletRequest)
	{
		super(servletContext, servletRequest) ;
		
		_iUserId = -1 ; 
	}
	
	/**
	  * Constructor dedicated to unit tests 
	  */
	public GetFlexListFromCodesHandler()
	{
		super() ;
		
		_iUserId = -1 ; 
	}

	@Override
	public GetFlexListFromCodesResult execute(final GetFlexListFromCodesAction action, final ExecutionContext context) throws ActionException 
  {	
		try 
		{
		  _iUserId = action.getUserId() ;
   		
   		ArrayList<SearchElement> _aElementsToFind = action.getElements() ;
   		if ((null == _aElementsToFind) || _aElementsToFind.isEmpty())
   		  return new GetFlexListFromCodesResult("Void request", null) ;
   		
   		// Creates a connector to Ontology database
   		//
   		DBConnector dbConnector = new DBConnector(true, -1, DBConnector.databaseType.databaseOntology) ;
   		
   		GetFlexListFromCodesResult flexListResult = new GetFlexListFromCodesResult("", null) ;
   		
   		// We will need a TripleManager
   		//
      TripleManager tripleManager = new TripleManager(null, dbConnector) ;
   		
   		for (SearchElement searchElement : _aElementsToFind)
   		  getFlexFromCode(searchElement, dbConnector, tripleManager, flexListResult) ;
   		
			return flexListResult ;
		}
		catch (Exception cause) 
		{
			Logger.trace("GetFlexListFromCodesHandler.execute: ; cause: " + cause.getMessage(), _iUserId, Logger.TraceLevel.DETAIL) ;
			throw new ActionException(cause);
		}
  }

	/**
	 * Look for an entry with a given code in the "flex" table 
	 * 
	 * @param dbconnector Database connector
	 * @param sCode       Code to be looked for
	 * @param lexicon     Record content
	 * 
	 **/	
	private boolean getFlexFromCode(final SearchElement searchElement, DBConnector dbConnector, TripleManager tripleManager, GetFlexListFromCodesResult flexListResult)
	{
		String sFctName = "GetFlexListFromCodesHandler.getFlexFromCode" ;
		
		if ((null == dbConnector) || (null == searchElement) || (null == flexListResult))
		{
			Logger.trace(sFctName + ": bad parameter", -1, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		String sCode = searchElement.getCode() ;
		
		// Easy case, we have got a Flex code
		//
		if (QuadrifoliumFcts.isFlexCode(sCode))
		  return getFlexFromExactCode(searchElement, dbConnector, tripleManager, flexListResult, null) ;
		
		// A little harder, we have got a lemma code, we have to get the Flex code from attributes passed in searchElement
		//
		if (QuadrifoliumFcts.isLemmaCode(sCode))
		  return getFlexFromLemmaCode(searchElement, dbConnector, tripleManager, flexListResult, null) ;
		
		// A little harder again, we have got a concept code, we have to get the preferred lemma for the specified language
		//
		if (QuadrifoliumFcts.isConceptCode(sCode))
		  return getFlexFromConceptCode(searchElement, dbConnector, tripleManager, flexListResult) ;
		
		return false ;
	}

	/**
	 * Get a flex element and all its traits (from its code)
	 * 
	 * @param searchElement  Search structure
	 * @param dbConnector    Database connector
	 * @param tripleManager  Traits manager
	 * @param flexListResult Result structure to add information to
	 * 
	 * @return <code>true</code> if all went well, <code>false</code> if not
	 */
	private boolean getFlexFromExactCode(final SearchElement searchElement, DBConnector dbConnector, TripleManager tripleManager, GetFlexListFromCodesResult flexListResult, final String sSpecificFlexCode)
	{
	  String sFctName = "GetFlexListFromCodesHandler.getFlexFromExactCode" ;

	  if ((null == dbConnector) || (null == searchElement) || (null == flexListResult))
	  {
	    Logger.trace(sFctName + ": bad parameter", -1, Logger.TraceLevel.ERROR) ;
	    return false ;
	  }
		
	  // Get the code of the flex element to look traits for
	  //
	  String sFlexCode = searchElement.getCode() ;
	  if ((null != sSpecificFlexCode) && (false == "".equals(sSpecificFlexCode)))
	    sFlexCode = sSpecificFlexCode ;
	  
	  if ((null == sFlexCode) || "".equals(sFlexCode))
    {
      Logger.trace(sFctName + ": void code", -1, Logger.TraceLevel.ERROR) ;
      return false ;
    }
	  
	  // Get corresponding Flex object
	  //
	  FlexManager flexManager = new FlexManager(null, dbConnector) ;
	  
	  Flex flex = new Flex() ;
	  if (false == flexManager.existData(sFlexCode, flex))
	  {
	    Logger.trace(sFctName + ": nothing found for code \"" + sFlexCode + "\" in flex", _iUserId, Logger.TraceLevel.SUBSTEP) ;
	    return false ;
	  }
	  
	  
	  // Create a FlexWithTraits object from the Flex
	  //
	  FlexWithTraits fullLexicoFlex = new FlexWithTraits(flex) ;
	  
	  // Fill it with all traits it is the subject of
	  //
		addTraits(fullLexicoFlex, tripleManager) ;
	  
		// Add it to the result structure
		//
		flexListResult.addResult(new ResultElement(fullLexicoFlex, searchElement.getCallbackIndex())) ;
		
		return true ;
	}
	
	/**
   * Get a flex element and all its traits (from a lemma code)
   * 
   * @param searchElement  Search structure
   * @param dbConnector    Database connector
   * @param tripleManager  Traits manager
   * @param flexListResult Result structure to add information to
   * 
   * @return <code>true</code> if all went well, <code>false</code> if not
   */
	private boolean getFlexFromLemmaCode(final SearchElement searchElement, DBConnector dbConnector, TripleManager tripleManager, GetFlexListFromCodesResult flexListResult, final String sSpecificLemmaCode)
	{
	  String sFctName = "GetFlexListFromCodesHandler.getFlexFromLemmaCode" ;

	  if ((null == dbConnector) || (null == searchElement) || (null == flexListResult))
	  {
	    Logger.trace(sFctName + ": bad parameter", -1, Logger.TraceLevel.ERROR) ;
	    return false ;
	  }
	  
	  // Get lemma's part of speech
	  //
	  String sLemmaCode = searchElement.getCode() ;
	  if ((null != sSpecificLemmaCode) && (false == "".equals(sSpecificLemmaCode)))
	    sLemmaCode = sSpecificLemmaCode ;
	  
	  String sLemmaPoS = getPartOfSpeechForLemma(sLemmaCode, tripleManager) ;
	  
	  // Not found ???
	  //
    if ("".equals(sLemmaPoS))
    {
    }
    
    ArrayList<SearchAttribute> aAttributes = new ArrayList<SearchAttribute>() ;
    aAttributes.addAll(searchElement.getAttributes()) ;
    
	  // Add preferred attributes for this language and lemma's part of speech
    //
    QuadrifoliumFcts.getAttributesForPreferredFlex(searchElement.getLanguage(), sLemmaPoS, aAttributes) ;
    
    // Get the flex that fits all attributes
    //
    ArrayList<String> aFlexCodes = new ArrayList<String>() ;
    tripleManager.getTriplesFromElementAndAttributes(sLemmaCode, aAttributes, aFlexCodes) ;
    
    if (aFlexCodes.isEmpty())
      return true ;
    
    // Process the first flex code from the list
    //
    for (String sResultCode : aFlexCodes)
      if (QuadrifoliumFcts.isFlexCode(sResultCode))
        return getFlexFromExactCode(searchElement, dbConnector, tripleManager, flexListResult, sResultCode) ;
    
	  return true ;
	}
	
	/**
   * Get a flex element and all its traits (from a concept code)
   * 
   * @param searchElement  Search structure
   * @param dbConnector    Database connector
   * @param tripleManager  Traits manager
   * @param flexListResult Result structure to add information to
   * 
   * @return <code>true</code> if all went well, <code>false</code> if not
   */
  private boolean getFlexFromConceptCode(final SearchElement searchElement, DBConnector dbConnector, TripleManager tripleManager, GetFlexListFromCodesResult flexListResult)
  {
    String sFctName = "GetFlexListFromCodesHandler.getFlexFromConceptCode" ;

    if ((null == dbConnector) || (null == searchElement) || (null == flexListResult))
    {
      Logger.trace(sFctName + ": bad parameter", -1, Logger.TraceLevel.ERROR) ;
      return false ;
    }
    
    // Get preferred lemma for the concept
    //
    String sConceptCode = searchElement.getCode() ;
    
    LemmaManager lemmaManager = new LemmaManager(null, dbConnector) ;
    
    ArrayList<Lemma> aLemmasForConcept = new ArrayList<Lemma>() ;
    if ((false == lemmaManager.existDataForConcept(sConceptCode, searchElement.getLanguage(), aLemmasForConcept)) || aLemmasForConcept.isEmpty())
      return true ;
    
    // Is there a mandatory Part of Speech in the search element?
    //
    String sRequiredPoS = "" ;
    if (false == searchElement.getAttributes().isEmpty())
    {
      for (SearchAttribute attribute : searchElement.getAttributes())
        if (QuadrifoliumFcts.getConceptCodeForPartOfSpeech().equals(attribute.getPredicate()))
          sRequiredPoS = attribute.getObject() ;
    }
    
    // First pass: process the lemma which is both preferred and with proper Part of Speech
    //
    for (Lemma lemma : aLemmasForConcept)
    {
      String sLemmaCode = lemma.getCode() ;
      if (isPreferredLemma(sLemmaCode, tripleManager))
      {
        if ("".equals(sRequiredPoS) || sRequiredPoS.equals(getPartOfSpeechForLemma(sLemmaCode, tripleManager)))
          return getFlexFromLemmaCode(searchElement, dbConnector, tripleManager, flexListResult, sLemmaCode) ;
      }
    }
    
    // Second pass: process the first lemma with proper Part of Speech
    //
    if (false == "".equals(sRequiredPoS))
    {
      for (Lemma lemma : aLemmasForConcept)
      {
        String sLemmaCode = lemma.getCode() ;
        if (sRequiredPoS.equals(getPartOfSpeechForLemma(sLemmaCode, tripleManager)))
          return getFlexFromLemmaCode(searchElement, dbConnector, tripleManager, flexListResult, sLemmaCode) ;
      }
    }
    
    return getFlexFromLemmaCode(searchElement, dbConnector, tripleManager, flexListResult, aLemmasForConcept.get(0).getCode()) ;
  }
	
  /**
   * Get the Part of Speech code for a lemma
   * 
   * @param sLemmaCode    Code of the lemma to get PoS for
   * @param tripleManager Object in charge of triples in the database
   * 
   * @return The code for the Part of Speech category if found, <code>""</code> if not
   */
  protected String getPartOfSpeechForLemma(final String sLemmaCode, TripleManager tripleManager)
  {
    if ((null == sLemmaCode) || "".equals(sLemmaCode) || (null == tripleManager))
      return "" ;
    
    ArrayList<Triple> aTriples = new ArrayList<Triple>() ;
    if ((false == tripleManager.getObjects(sLemmaCode, QuadrifoliumFcts.getConceptCodeForPartOfSpeech(), "", aTriples)) || aTriples.isEmpty())
      return "" ;
    
    return aTriples.get(0).getObject() ;
  }
  
  /**
   * Is this lemma a preferred one for this concept?
   * 
   * @param sLemmaCode    Code of the lemma to get the preferred status for
   * @param tripleManager Object in charge of triples in the database
   * 
   * @return <code>true</code> if a preferred triple is found, <code>""</code> if not
   */
  protected boolean isPreferredLemma(final String sLemmaCode, TripleManager tripleManager)
  {
    if ((null == sLemmaCode) || "".equals(sLemmaCode) || (null == tripleManager))
      return false ;
    
    ArrayList<Triple> aTriples = new ArrayList<Triple>() ;
    if ((false == tripleManager.getSubjects(sLemmaCode, QuadrifoliumFcts.getConceptCodeForPreferredTerm(), "", aTriples)) || aTriples.isEmpty())
      return false ;
    
    return true ;
  }
  
	/**
	 * Fill a {@link FlexWithTraits} with traits it is the subject of
	 * 
	 * @param fullLexicoFlex {@link FlexWithTraits} to fill
	 * @param tripleManager  {@link TripleManager} to query database for traits
	 */
	protected void addTraits(FlexWithTraits fullLexicoFlex, TripleManager tripleManager)
	{
	  String sFctName = "GetFlexListFromCodesHandler.addTraits" ;
	  
	  if ((null == tripleManager) || (null == fullLexicoFlex))
	  {
	    Logger.trace(sFctName + ": bad parameter", -1, Logger.TraceLevel.ERROR) ;
	    return ;
	  }
	  
	  String sFlexCode = fullLexicoFlex.getCode() ;
	  
	  if ((null == sFlexCode) || "".equals(sFlexCode))
	  {
	    Logger.trace(sFctName + ": void Flex", -1, Logger.TraceLevel.ERROR) ;
	    return ;
	  }
	  
	  // Get all traits with Flex or its lemma as a subject
    //
    ArrayList<Triple> triples = new ArrayList<Triple>() ;
    if (tripleManager.getObjects(sFlexCode, "*", "%", triples) && (false == triples.isEmpty()))
      for (Triple triple : triples)
        fullLexicoFlex.addTrait(new TripleWithLabel(triple, fullLexicoFlex.getLanguage(), null, null, null)) ;
    
	}
	
	@Override
	public void rollback(final GetFlexListFromCodesAction action,
        							 final GetFlexListFromCodesResult result,
                       final ExecutionContext context) throws ActionException
  {
		// Nothing to do here
  }
 
	@Override
	public Class<GetFlexListFromCodesAction> getActionType()
	{
		return GetFlexListFromCodesAction.class ;
	}
}
