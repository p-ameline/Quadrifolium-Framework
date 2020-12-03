package org.quadrifolium.shared.rpc4ontology;

import java.util.ArrayList;

import org.quadrifolium.shared.ontology.Flex;

import net.customware.gwt.dispatch.shared.Result;

public class GetFlexListFromTextResult implements Result
{
  private ArrayList<Flex> _aFlexList = new ArrayList<Flex>() ;
	private String          _sMessage ;
	private int             _iFlexTextBoxIndex ;
	
	/**
	 * */
	public GetFlexListFromTextResult()
	{
		super() ;
		
		_sMessage = "" ;
		_iFlexTextBoxIndex = -1 ;
	}

	/**
	 * @param sMessage
	 * */
	public GetFlexListFromTextResult(final String sMessage, int iFlexTextBoxIndex)
	{
		super() ;
		
		_sMessage          = sMessage ;
		_iFlexTextBoxIndex = iFlexTextBoxIndex ;
	}
	
	public String getMessage() {
		return _sMessage ;
	}
	public void setMessage(String sMessage) {
		_sMessage = sMessage ;
	}
	
	public int getFlexTextBoxIndex() {
		return _iFlexTextBoxIndex ;
	}
	public void setFlexTextBoxIndex(int iFlexTextBoxIndex) {
		_iFlexTextBoxIndex = iFlexTextBoxIndex ;
	}

	public ArrayList<Flex> getFlexArray() {
		return _aFlexList ;
	}
	
	public void addFlex(Flex flex) {
		_aFlexList.add(new Flex(flex)) ;
	}
}
