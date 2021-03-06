package org.quadrifolium.client.widgets;

import java.util.ArrayList;

import org.quadrifolium.shared.ontology.Flex;
import org.quadrifolium.shared.rpc4ontology.GetFlexListFromTextAction;
import org.quadrifolium.shared.rpc4ontology.GetFlexListFromTextResult;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;

import net.customware.gwt.dispatch.client.DispatchAsync;

/**
 * Object used to operate an array of {@link FlexTextBoxIndex} 
 * 
 */
public class FlexTextBoxManager
{
	protected ArrayList<FlexTextBoxIndex> _aFlexTextBoxBuffer ;
	protected int                         _iNextLTBBufferIndex ;
	protected int                         _iMinTextLenForSearch ;
		
  public FlexTextBoxManager()
  {
  	_aFlexTextBoxBuffer   = new ArrayList<FlexTextBoxIndex>() ;
  	_iNextLTBBufferIndex  = 0 ;
  	_iMinTextLenForSearch = 3 ;
  }
  
  /**
   * Initializes a FlexTextBox choices ListBox from entered text 
   *
   * @param  flexTextBox FlexTextBox which choices Listbox is to be updated
	 * @return <code>void</code> 
   */
	public void initFlexBoxList(final FlexTextBox flexTextBox, final int iUserId, final DispatchAsync dispatcher)
	{
		if (null == flexTextBox)
			return ;
		
		// Get text
		//
		String sText = flexTextBox.getText() ;
		
		if ("".equals(sText) || (sText.length() < _iMinTextLenForSearch))
		{
			flexTextBox.closeChoices() ;
			return ;
		}
		
		// Get index
		//
		FlexTextBoxIndex index = getFlexTextBoxIndex(flexTextBox) ;
		if (null == index)
			return ;
		
		dispatcher.execute(new GetFlexListFromTextAction(iUserId, index.getLanguage(), sText, index.getIndex()), new GetFlexBoxListCallback()) ;
	}
	
	/**
	*  Asynchronous callback function for calls to GetLexiconFromCodeHandler
	**/
	public class GetFlexBoxListCallback implements AsyncCallback<GetFlexListFromTextResult> 
	{
		public GetFlexBoxListCallback() {
			super();
		}

		@Override
		public void onFailure(final Throwable cause)
		{
			Log.error("Handle Failure:", cause) ;    				
		}

		@Override
		public void onSuccess(final GetFlexListFromTextResult result)
		{
			if (null == result)
				return ;
			
			ArrayList<Flex> entriesList = result.getFlexArray() ;
			if ((null == entriesList) || entriesList.isEmpty())
				return ;
			
			FlexTextBoxIndex flexBoxIndx = getFlexTextBox(result.getFlexTextBoxIndex()) ;
			if (null == flexBoxIndx)
				return ;
			
			flexBoxIndx.initLexiqueTextBox(entriesList) ;			
		}
	}

  /**
   * Reference a new lexiqueTextBox (by creating a new dedicated index) and returns its index number   
   * 
   * @param flexTextBox    The FlexTextBox to reference
   * @param sUserLanguage  The user language to be used with this FlexTextBox
   * 
   * @return The index number the LexiqueTextBox was referenced with
   */
  public int addFlexTextBoxToBuffer(final FlexTextBox flexTextBox, final String sUserLanguage)
	{
		int iAlreadyExistingIndex = getIndex(flexTextBox) ;
		if (-1 != iAlreadyExistingIndex)
			return iAlreadyExistingIndex ;
		
		int iNewIndex = _iNextLTBBufferIndex++ ;
		
		_aFlexTextBoxBuffer.add(new FlexTextBoxIndex(flexTextBox, iNewIndex, sUserLanguage)) ;
		
		return iNewIndex ;
	}
	
	/**
   * Find the index for a given FlexTextBox stored in the buffer  
   *
   * @param  flexTextBox LexiqueTextBox to look for in buffer
	 * @return the index for this LexiqueTextBox if found, <code>-1</code> if not 
   */
	public int getIndex(final FlexTextBox flexTextBox)
	{
		if (_aFlexTextBoxBuffer.isEmpty() || (null == flexTextBox))
			return -1 ;
		
		for (FlexTextBoxIndex index : _aFlexTextBoxBuffer)
			if (flexTextBox == index.getLexiqueTextBox())
				return index.getIndex() ;
		
		return -1 ;
	}
	
	/**
   * Find the index for a given FlexTextBox stored in the buffer  
   *
   * @param  flexTextBox FlexTextBox to look for in buffer
	 * @return the index for this FlexTextBox if found, <code>-1</code> if not 
   */
	protected FlexTextBoxIndex getFlexTextBoxIndex(final FlexTextBox flexTextBox)
	{
		if (_aFlexTextBoxBuffer.isEmpty() || (null == flexTextBox))
			return null ;
		
		for (FlexTextBoxIndex index : _aFlexTextBoxBuffer)
			if (flexTextBox == index.getLexiqueTextBox())
				return index ;
		
		return null ;
	}
	
	/**
   * Find the flexTextBoxIndex for a given index  
   *
   * @param  iIndex Index of flexTextBoxIndex to look for in buffer
	 * @return a flexTextBoxIndex if found, <code>null</code> if not 
   */
	public FlexTextBoxIndex getFlexTextBox(int iIndex)
	{
		if (_aFlexTextBoxBuffer.isEmpty())
			return (FlexTextBoxIndex) null ;
		
		for (FlexTextBoxIndex lexiqueTxtBx : _aFlexTextBoxBuffer)
			if (iIndex == lexiqueTxtBx.getIndex())
				return lexiqueTxtBx ;
		
		return (FlexTextBoxIndex) null ;
	}
	
	/**
   * Increment instance counter for a given index  
   *
   * @param  iIndex index of flexTextBoxIndex whose instance counter is to be incremented 
   */
	public void incrementFlexTextBoxIndex(int iIndex)
	{
		FlexTextBoxIndex flexTxtBxIdx = getFlexTextBox(iIndex) ;
		if (null == flexTxtBxIdx)
			return ;
		
		flexTxtBxIdx.incrementInstanceCounter() ;
	}
	
	/**
   * Decrement instance counter for a given index ; if counter becomes null then remove object  
   *
   * @param  iIndex index of flexTextBoxIndex whose instance counter is to be incremented 
   */
	public void decrementFlexTextBoxIndex(int iIndex)
	{
		FlexTextBoxIndex flexTxtBxIdx = getFlexTextBox(iIndex) ;
		if (null == flexTxtBxIdx)
			return ;
		
		flexTxtBxIdx.decrementInstanceCounter() ;
		
		if (flexTxtBxIdx.stillExists())
			return ;
			
		// Index no longer in use, remove it from buffer
		//
		for (FlexTextBoxIndex flexTxtBx : _aFlexTextBoxBuffer)
			if (iIndex == flexTxtBx.getIndex())
			{
				_aFlexTextBoxBuffer.remove(flexTxtBxIdx) ;
				return ;
			}
	}
}
