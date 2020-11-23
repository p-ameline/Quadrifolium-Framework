package org.quadrifolium.server.handler4ontology;

import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;

import org.quadrifolium.server.DbParameters;
import org.quadrifolium.server.ontology.StemmaManager;
import org.quadrifolium.server.util.QuadrifoliumServerFcts;
import org.quadrifolium.shared.ontology.QuadrifoliumNode;
import org.quadrifolium.shared.rpc4ontology.GetStemmaForConceptAction;
import org.quadrifolium.shared.rpc4ontology.GetStemmaForConceptResult;
import org.quadrifolium.shared.rpc_util.SessionElements;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ldv.server.DBConnector;
import com.ldv.server.Logger;
import com.ldv.server.handler.LdvActionHandler;
import com.ldv.shared.graph.LdvModelNode;

public class GetStemmaForConceptHandler extends LdvActionHandler<GetStemmaForConceptAction, GetStemmaForConceptResult>
{
	protected SessionElements _sessionElements ;
	protected int             _iUserId ;
	
	@Inject
	public GetStemmaForConceptHandler(final Provider<ServletContext> servletContext,       
                                    final Provider<HttpServletRequest> servletRequest)
	{
		super(servletContext, servletRequest) ;
		
		_sessionElements = null ;
		_iUserId         = -1 ; 
	}
	
	/**
	  * Constructor dedicated to unit tests 
	  */
	public GetStemmaForConceptHandler()
	{
		super() ;
		
		_sessionElements = null ;
		_iUserId         = -1 ;
	}

	@Override
	public GetStemmaForConceptResult execute(final GetStemmaForConceptAction action, final ExecutionContext context) throws ActionException 
  {	
		_sessionElements = action.getSessionElements() ;
 		
 		// This function is read only, hence it doesn't need a registered user
		//
		_iUserId = -1 ;
		if (null != _sessionElements)
			_iUserId = _sessionElements.getPersonId() ;
		
		try 
		{			
   		String sCode = action.getConceptCode() ;
   		String sLang = action.getLanguage() ;
   		
   		// Creates a connector to Ontology database
   		//
   		DBConnector dbconnector = new DBConnector(true, -1, DBConnector.databaseType.databaseOntology) ;
   		
   		GetStemmaForConceptResult StemmaResult = new GetStemmaForConceptResult("") ;
   		
   		if (true == getStemmaForConcept(dbconnector, sLang, sCode, StemmaResult))
   			return StemmaResult ;
   		
			return new GetStemmaForConceptResult("Error") ;
		}
		catch (Exception cause) 
		{
			Logger.trace("GetStemmaForConceptHandler.execute: ; cause: " + cause.getMessage(), _iUserId, Logger.TraceLevel.DETAIL) ;
   
			throw new ActionException(cause);
		}
  }

	/**
	 * Get the stemma for this concept 
	 * 
	 * @param dbconnector  Database connector
	 * @param sLanguage    Language to return node labels into
	 * @param sCode        Concept code to get stemma for
	 * @param stemmaResult Object to send back to client
	 * 
	 **/	
	private boolean getStemmaForConcept(DBConnector dbConnector, final String sLanguage, final String sCode, GetStemmaForConceptResult stemmaResult)
	{
		String sFctName = "GetLemmasForConceptHandler.getStemmaForConcept" ;
		
		// Get the LdvModelTree made of nodes of type QuadrifoliumNode
		//
		StemmaManager stemmaManager = new StemmaManager(DbParameters.getFilesDir(), DbParameters.getDirSeparator()) ;
		
		if (false == stemmaManager.getStemma(sCode, stemmaResult.getStemma()))
		{
			Logger.trace(sFctName + ": cannot get stemma for concept" + sCode, -1, Logger.TraceLevel.WARNING) ;
			stemmaResult.setMessage("No stemma available") ;
			return false ;
		}
		
		// Set each node with a label in the desired language
		//
		
		// Buffer used to avoid looking twice for labels in the database 
		//
	  HashMap<String, String> aLabelsForCodes = new HashMap<String, String>() ;
		
	  for (LdvModelNode node : stemmaResult.getStemma().getNodes())
	  {
	  	QuadrifoliumNode quadriNode = (QuadrifoliumNode) node ;
	  	
	  	String sLabel = QuadrifoliumServerFcts.getLabelForCode(dbConnector, _sessionElements, sLanguage, node.getLexicon(), aLabelsForCodes) ;
	  	
	  	quadriNode.setLabel(sLabel) ;
	  	quadriNode.setLanguage(sLanguage) ;
	  }
	  		
		return true ;
	}
		
	@Override
	public void rollback(final GetStemmaForConceptAction action,
        							 final GetStemmaForConceptResult result,
                       final ExecutionContext context) throws ActionException
  {
		// Nothing to do here
  }
 
	@Override
	public Class<GetStemmaForConceptAction> getActionType() {
		return GetStemmaForConceptAction.class ;
	}
}
