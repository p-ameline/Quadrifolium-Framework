package org.quadrifolium.shared.rpc4ontology;

import java.util.ArrayList;

import org.quadrifolium.shared.ontology.Flex;

import net.customware.gwt.dispatch.shared.Result;

public class GetFlexListResult implements Result
{
  private ArrayList<Flex> _aFlexList = new ArrayList<Flex>() ;
	private String          _sMessage ;
	private int             _iFlexTextBoxIndex ;
	
	/**
	 * */
	public GetFlexListResult()
	{
		super() ;
		
		_sMessage = "" ;
		_iFlexTextBoxIndex = -1 ;
	}

	/**
	 * @param sMessage
	 * */
	public GetFlexListResult(final String sMessage, int iFlexTextBoxIndex)
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
