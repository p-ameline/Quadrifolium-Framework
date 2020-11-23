package org.quadrifolium.server.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.ldv.server.Logger;

/**
 * Manage files and directories 
 * 
 **/
public class RootFilesManager 
{
	private String _sBaseDir ;
	private String _sDirSeparator ;
	
	public RootFilesManager(final String sBaseDir, final String sDirSeparator)
	{
		_sBaseDir      = sBaseDir ;
		_sDirSeparator = sDirSeparator ;
	}
	
	/**
	 * Read a Document from a the base directory
	 * 
	 * @param sFileName Short file name 
	 * 
	 * @return The Document if everything went well, <code>null</code> if nt
	 * 
	 **/
	public Document readDocumentFromBaseDir(final String sFileName)
	{
		if (null == sFileName)
			throw new NullPointerException() ;
		
		if ("".equals(sFileName))
			return null ;
		
		String sCompleteFileName = getFileCompleteName(sFileName) ; 
		
		return readDocumentFromDisk(sCompleteFileName) ;
	}
	
	/**
	 * Read a Document from a file on disk
	 * 
	 * @param sCompleteFileName File name, including directory 
	 * 
	 * @return The Document if everything went well, <code>null</code> if nt
	 * 
	 **/
	public static Document readDocumentFromDisk(final String sCompleteFileName) throws NullPointerException
	{
		if (null == sCompleteFileName)
			throw new NullPointerException() ;
		
		if ("".equals(sCompleteFileName))
			return null ;
				
		FileInputStream fi;
		try
		{
			fi = new FileInputStream(sCompleteFileName) ;
			
			return getDocumentFromInputSource(new InputSource(fi), false, sCompleteFileName) ;
		} 
		catch (FileNotFoundException e)
		{
			Logger.trace("RootFilesManager.readDocumentFromDisk: input stream exception for file " + sCompleteFileName + " ; stackTrace:" + e.getStackTrace(), -1, Logger.TraceLevel.ERROR) ;
			return null ;
		}
	}
	
	/**
	 * Convert a Document into a String 
	 * 
	 * @param doc : document to be serialized
	 * @return An XML representation as a String
	 * 
	 **/
	public boolean writeDocumentToDisk(final String sContent, final String sFileName) throws NullPointerException
	{
		if ((null == sFileName) || (null == sContent))
			throw new NullPointerException() ;
		
		if ("".equals(sContent) || "".equals(sFileName))
			return false ;
		
		// Get complete file name
		//
		String sCompleteFileName = getFileCompleteName(sFileName) ;
		
		// Write file
		//		
		return writeDocumentToDisk(sContent, sCompleteFileName) ;
	}
	
	/**
	 * Convert a Document into a String 
	 * 
	 * @param doc : document to be serialized
	 * @return An XML representation as a String
	 * 
	 **/
	public boolean writeDocumentToDisk(final String sContent, final String sFileName, final String sCompleteFileName)
	{
		if ((null == sCompleteFileName) || (null == sContent))
			throw new NullPointerException() ;
		
		String sFctName = "RootFilesManager.writeDocumentToDisk" ;
		
		if ("".equals(sContent) || "".equals(sCompleteFileName))
			return false ;
		
		// Open output file
		//
		FileOutputStream out = null ;
		try
    {
			out = new FileOutputStream(sCompleteFileName, false) ;
    } 
		catch (FileNotFoundException eOpen)
    {
			Logger.trace(sFctName + ": cannot open or create file " + sCompleteFileName + " ; stackTrace:" + eOpen.getStackTrace(), -1, Logger.TraceLevel.ERROR) ;
	    return false ;
    }
		
		// Write string to disk
		//
		boolean bSuccess = true ;
		
		byte data[] = sContent.getBytes() ;
		try
    {
	    out.write(data, 0, data.length) ;
    } 
		catch (IOException eWrite)
    {
			Logger.trace(sFctName + ": error writing file " + sCompleteFileName + " ; stackTrace:" + eWrite.getStackTrace(), -1, Logger.TraceLevel.ERROR) ;
			bSuccess = false ;
    }
		finally
		{		
			try
      {
	      out.flush() ;
      } 
			catch (IOException eFlush)
      {
				Logger.trace(sFctName + ": error flushing file " + sCompleteFileName + " ; stackTrace:" + eFlush.getStackTrace(), -1, Logger.TraceLevel.ERROR) ;
	      bSuccess = false ;
      }
			try
      {
	      out.close() ;
      } 
			catch (IOException eClose)
      {
				Logger.trace(sFctName + ": error closing file " + sCompleteFileName + " ; stackTrace:" + eClose.getStackTrace(), -1, Logger.TraceLevel.ERROR) ;
	      bSuccess = false ;
      }
		}		
		return bSuccess ;
	}
		
	/**
	* Read a Document from an InputSource
	* 
	* @param inputSource  input source content
	* @param mustValidate <code>true</code> if the xml content must be validated
	* @param sFileName    For tracing purposes
	* 
	* @return the document if everything went well, <code>null</code> if not
	* 
	**/
	public static Document getDocumentFromInputSource(InputSource inputSource, boolean mustValidate, final String sFileName) throws NullPointerException
	{
		if (null == inputSource) 
			throw new NullPointerException() ;
	
		String sFctName = "RootFilesManager.getDocumentFromInputSource" ;
	
		// Get factory instance
		//
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance() ;
	
		if (false == mustValidate)
		{
			factory.setNamespaceAware(false) ;
			factory.setValidating(false) ;
			try
			{
				factory.setFeature("http://xml.org/sax/features/namespaces", false) ;
				factory.setFeature("http://xml.org/sax/features/validation", false) ;
				factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false) ;
				factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false) ;
			} 
			catch (ParserConfigurationException e)
			{
				Logger.trace(sFctName + ": parser config exception for file \"" + sFileName + "\" ; stackTrace:" + e.getStackTrace(), -1, Logger.TraceLevel.ERROR) ;
				e.printStackTrace();
			}
		}
	
		DocumentBuilder builder ;
		try
		{
			builder = factory.newDocumentBuilder() ;
		} 
		catch (ParserConfigurationException e)
		{
			Logger.trace(sFctName + ": parser config exception when getting a document builder for file \"" + sFileName + "\" ; stackTrace:" + e.getStackTrace(), -1, Logger.TraceLevel.ERROR) ;
			return null ;
		}
  
		// Create documents
		//
		Document document = null ;
  
		try
		{
			document = builder.parse(inputSource) ;
		} 
		catch (SAXException e)
		{
			Logger.trace(sFctName + ": parser exception for file \"" + sFileName + "\" ; stackTrace:" + e.getStackTrace(), -1, Logger.TraceLevel.ERROR) ;
			return null ;
		} 
		catch (IOException e)
		{
			Logger.trace(sFctName + ": parser IO exception for file \"" + sFileName + "\" ; stackTrace:" + e.getStackTrace(), -1, Logger.TraceLevel.ERROR) ;
			return null ;
		}
      
		return document ;
	}
	
	/**
	 * Check if a file exists inside Working Directory
	 *     
	 * @return true if file exists 
	 * 
	 **/
	public boolean existFileInBaseDir(String sShortFileName)
	{
		String sCompleteFileName = getFileCompleteName(sShortFileName) ; 
		return existFile(sCompleteFileName) ;
	}
	
	/**
	 * Check if a file exists
	 *     
	 * @return true if file exists 
	 **/
	public boolean existFile(String sCompleteFileName)
	{
		File f = new File(sCompleteFileName) ;		
		return f.exists() ;
	}
	
	/**
	 * Verifies that the specified file name is valid
	 * 	  
	 * @param sFileName the file name to validate
	 * 
	 * @return true if valid, false if invalid
	 */
	public static boolean isValidFileName(String sFileName) 
	{
		if ((null == sFileName) || (sFileName.equals(""))) 
			return false ;
		
		return true ;
	}
		
	/**
	 * Get Complete name for a file, simply as base directory + file name
	 * 
	 * @param sFileName simple file name
	 * @return complete file name in base directory
	 * 
	 **/
	public String getFileCompleteName(String sFileName)
	{
		if ((null == sFileName) || sFileName.equals(""))
			return "" ;
		
		return _sBaseDir + sFileName ;
	}
	
	/**
	 * Get an xml file name from a prefix, for example "test" -> "test.xml"
	 */
	public static String getXmlFileName(String sFileNamePrefix) 
	{
		if ((null == sFileNamePrefix) || "".equals(sFileNamePrefix)) 
			return "" ;
		
		return sFileNamePrefix + ".xml" ;
	}
}
