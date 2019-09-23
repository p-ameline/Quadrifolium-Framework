package org.quadrifolium.shared.rpc4ontology;

import java.util.ArrayList;
import java.util.Iterator;

import org.quadrifolium.shared.ontology.TripleWithLabel;

import net.customware.gwt.dispatch.shared.Result;

/**
 * Object used to return a list of definitions triples from a concept
 * 
 * @author Philippe
 */
public class GetDefinitionsTriplesResult implements Result
{
  private ArrayList<TripleWithLabel> _aTriplesList = new ArrayList<TripleWithLabel>() ;
	private String                     _sMessage ;
	
	/**
	 * No-args constructor, mandatory for serializable objects
	 */
	public GetDefinitionsTriplesResult()
	{
		super() ;
		
		_sMessage = "" ;
	}

	/**
	 * @param sMessage
	 * */
	public GetDefinitionsTriplesResult(final String sMessage)
	{
		super() ;
		
		_sMessage = sMessage ;
	}
	
	public String getMessage() {
		return _sMessage ;
	}
	public void setMessage(String sMessage) {
		_sMessage = sMessage ;
	}
	
	public ArrayList<TripleWithLabel> getTriplesArray() {
		return _aTriplesList ;
	}
	
	public void addTriple(final TripleWithLabel triple) {
		_aTriplesList.add(new TripleWithLabel(triple)) ;
	}
	public void addListOfTriples(final ArrayList<TripleWithLabel> aTriples)
	{
		if ((null == aTriples) || aTriples.isEmpty())
			return ;
		
		for (Iterator<TripleWithLabel> it = aTriples.iterator() ; it.hasNext() ; )
			addTriple(it.next()) ;
	}
}
