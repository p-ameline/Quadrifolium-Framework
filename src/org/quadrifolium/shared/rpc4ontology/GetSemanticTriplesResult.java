package org.quadrifolium.shared.rpc4ontology;

import java.util.ArrayList;
import java.util.Iterator;

import org.quadrifolium.shared.ontology.TripleWithLabel;

import net.customware.gwt.dispatch.shared.Result;

/**
 * Object used to return a list of semantic network triples from a concept
 * 
 * @author Philippe
 */
public class GetSemanticTriplesResult implements Result
{
  private ArrayList<TripleWithLabel> _aObjectList  = new ArrayList<TripleWithLabel>() ; // Triples with concept as an object
  private ArrayList<TripleWithLabel> _aSubjectList = new ArrayList<TripleWithLabel>() ; // Triples with concept as a subject
	private String                     _sMessage ;
	
	/**
	 * No-args constructor, mandatory for serializable objects
	 */
	public GetSemanticTriplesResult()
	{
		super() ;
		
		_sMessage = "" ;
	}

	/**
	 * @param sMessage
	 * */
	public GetSemanticTriplesResult(final String sMessage)
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
	
	public ArrayList<TripleWithLabel> getObjectArray() {
		return _aObjectList ;
	}
	
	public void addTripleForConceptAsObject(final TripleWithLabel triple) {
		_aObjectList.add(new TripleWithLabel(triple)) ;
	}
	public void addListOfTriplesForConceptAsObject(final ArrayList<TripleWithLabel> aTriples)
	{
		if ((null == aTriples) || aTriples.isEmpty())
			return ;
		
		for (Iterator<TripleWithLabel> it = aTriples.iterator() ; it.hasNext() ; )
			addTripleForConceptAsObject(it.next()) ;
	}
	
	public ArrayList<TripleWithLabel> getSubjectArray() {
		return _aSubjectList ;
	}
	
	public void addTripleForConceptAsSubject(final TripleWithLabel triple) {
		_aSubjectList.add(new TripleWithLabel(triple)) ;
	}
	public void addListOfTriplesForConceptAsSubject(final ArrayList<TripleWithLabel> aTriples)
	{
		if ((null == aTriples) || aTriples.isEmpty())
			return ;
		
		for (Iterator<TripleWithLabel> it = aTriples.iterator() ; it.hasNext() ; )
			addTripleForConceptAsSubject(it.next()) ;
	}
}
