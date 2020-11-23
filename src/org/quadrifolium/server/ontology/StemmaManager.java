package org.quadrifolium.server.ontology;

import org.quadrifolium.server.model.RootFilesManager;
import org.quadrifolium.shared.ontology.QuadrifoliumNode;
import org.w3c.dom.Document;

import com.ldv.server.Logger;
import com.ldv.server.model.LdvXmlDocument;
import com.ldv.shared.graph.LdvModelNode;
import com.ldv.shared.graph.LdvModelNodeArray;
import com.ldv.shared.graph.LdvModelTree;

/**
 * Get and update stemmas
 * 
 **/
public class StemmaManager
{
	private String _sBaseDir ;
	private String _sDirSeparator ;
	
	/**
	 * Standard constructor
	 * 
	 * @param sFilesDir     Directory where stemmas xml files are located
	 * @param sDirSeparator Directories separator for server's operating system
	 * 
	 **/
	public StemmaManager(final String sFilesDir, final String sDirSeparator)
	{
		_sBaseDir      = sFilesDir ;
		_sDirSeparator = sDirSeparator ;
	}
		
	/**
	 * Open the graph by parsing all XML files at a given location
	 * 
	 * @param sConcept Concept to get the stemma for
	 * @param stemma   LdvModelTree made of nodes of type QuadrifoliumNode to fill
	 * 
	 * @return <code>true</code> if all went well, <code>false</code> if not 
	 * 
	 **/
	public boolean getStemma(final String sConcept, LdvModelTree stemma) throws NullPointerException
	{
		if (null == sConcept)
			throw new NullPointerException() ;
		
		if ("".equals(sConcept))
			return false ;
		
		RootFilesManager filesManager = new RootFilesManager(_sBaseDir, _sDirSeparator) ;
		
		return openStemma(sConcept, stemma, filesManager) ;
	}
	
	/**
	 * Open the stemma by parsing its XML file
	 *
	 * @param sConcept     Concept to get the stemma for
	 * @param stemma       LdvModelTree made of nodes of type QuadrifoliumNode to fill
	 * @param filesManager Object to access files on disk
	 * 
	 * @return <code>true</code> if all went well, <code>false</code> if not 
	 **/
	public boolean openStemma(final String sConcept, LdvModelTree stemma, RootFilesManager filesManager) throws NullPointerException
	{
		if ((null == sConcept) || (null == filesManager))
			throw new NullPointerException() ;
		
		String sFctName = "StemmaManager.openStemma" ;
		
		if ("".equals(sConcept))
			return false ;
		
		// Get file name for stemma as concept.xml
		//
		String sStemmaFileName = RootFilesManager.getXmlFileName(sConcept) ; 
		
		// Get stemma as a Document
		//
		Document stemmaAsDocument = filesManager.readDocumentFromBaseDir(sStemmaFileName) ;
		
		if (null == stemmaAsDocument)
		{
			String sErrorMsg = sFctName + ": cannot get stemma for concept " + sConcept ;
			
			if (false == filesManager.existFile(sStemmaFileName))
			{		
				sErrorMsg += " (file doesn't exist)." ;
				Logger.trace(sErrorMsg, -1, Logger.TraceLevel.WARNING) ;
			}
			else
			{
				sErrorMsg += " (even if file does exist)." ;	
				Logger.trace(sErrorMsg, -1, Logger.TraceLevel.ERROR) ;
			}
			
			return false ;
		}
		
		// Parse the Document to get a LdvModelTree, using a LdvXmlDocument
		//
		// We use LdvXmlDocument in order not to duplicate xml parsing for stemmas.
		// However LdvXmlDocument is built for opening true description stemmas and not stemmas from the ontology,
		// this is the reason why we do not let it get information from a file name.
		//
		LdvModelTree modelTree = new LdvModelTree() ;
		
		// Use constructor LdvXmlDocument(LdvXmlGraph containerGraph, String sFileName)
		//
		LdvXmlDocument ldvXmlDoc = new LdvXmlDocument(null, "") ;
		
		// Initialize the LdvXmlDocument from the DOM document 
		//
		if (false == ldvXmlDoc.initDocumentsFromMasterDocument(stemmaAsDocument))
		{
			Logger.trace(sFctName + ": cannot parse stemma for concept " + sConcept, -1, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		// Initialize the LdvModelTree from the LdvXmlDocument 
		//
		if (false == ldvXmlDoc.initializeLdvModelFromFile(modelTree))
		{
			Logger.trace(sFctName + ": cannot get the stemma for concept " + sConcept + " (looks like it is empty)", -1, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		// Now, build a tree made of QuadrifoliumNode instead of LdvModelNode so that they can receive a label
		//
		LdvModelNodeArray regularNodes = modelTree.getNodes() ;
		if (regularNodes.isEmpty())
			return false ;
		
		stemma.setTreeID(modelTree.getTreeID()) ;
		
		LdvModelNodeArray quadrifoliumNodes = stemma.getNodes() ;
		
		for (LdvModelNode node : regularNodes)
			quadrifoliumNodes.add(new QuadrifoliumNode(node)) ;
		
		return true ;
	}	
}
