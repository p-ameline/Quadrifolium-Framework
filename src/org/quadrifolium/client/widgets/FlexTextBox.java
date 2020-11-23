package org.quadrifolium.client.widgets;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import net.customware.gwt.presenter.client.EventBus;

import static com.google.gwt.event.dom.client.KeyCodes.*;

import java.util.ArrayList;
import java.util.Iterator;

import org.quadrifolium.shared.ontology.Flex; 

/**
 * TextBox with a drop down list from Lexicon
 * 
 * Inspired from http://sites.google.com/site/gwtcomponents/auto-completiontextbox
 */
public class FlexTextBox extends TextBox implements ChangeHandler, HasConceptChangedHandlers
{   
  protected PopupPanel      _choicesPopup = new PopupPanel(true) ;
  protected ListBox         _choices      = new ListBox() ;
  
  protected Panel           _rootPanel ;
  
  protected boolean         _popupAdded   = false ;
  protected boolean         _visible      = false ;
  
  protected Flex            _selectedFlex ;
  protected ArrayList<Flex> _flexList ;
  
  private   String          _sLanguage ;
  
  private   final EventBus  _eventBus ;
  
  /**
   * Default Constructor, not to be "injected"
   */
  @Inject
  public FlexTextBox(final EventBus eventBus) 
  {
  	this("en", RootPanel.get(), eventBus) ;
  }
  
  /**
   * Default Constructor, not to be used inside a dialog box
   */
  public FlexTextBox(final String sLanguage, final EventBus eventBus) 
  {
  	this(sLanguage, RootPanel.get(), eventBus) ;
  }
  
  /**
   * Constructor with a given Panel as root panel
   */
  public FlexTextBox(final String sLanguage, final Panel rootPanel, final EventBus eventBus)
  {
    super() ;
    
    _sLanguage = sLanguage ;
    _rootPanel = rootPanel ;
    _eventBus  = eventBus ;
    
    // this.setStyleName("AutoCompleteTextBox") ;
       
    _choicesPopup.add(_choices) ;
    _choicesPopup.addStyleName("AutoCompleteChoices") ;
       
    _choices.setStyleName("list") ;
    
    _selectedFlex = null ;
    _flexList     = new ArrayList<Flex>() ;
  }

  /**
   * Is the key a "control key" for this control? (Enter, Escape, Up and Down arrows)
   */
  public boolean isControlKey(KeyUpEvent event)
  {
  	if ((event.isDownArrow()) || (event.isUpArrow()))
  		return true ;
  	
  	int iNativeKeyCode = event.getNativeKeyCode() ;
  	if ((KEY_ENTER == iNativeKeyCode) || (KEY_ESCAPE == iNativeKeyCode))
  		return true ;
  	
  	return false ;
  }
  
  /**
   * A key was released, process special keys
   * 
   * @return <code>true</code> if the list must be refreshed, <code>false</code> if not
   *  
   */
  public boolean processKeyUp(KeyUpEvent event)
  {
    // Down arrow, select the next proposal in the list (after the last one, go back to the first)
    //
    if (event.isDownArrow())
    {
    	// Get getSelectedIndex() returns 0 for first element
    	//
      int iSelectedIndex = _choices.getSelectedIndex() ;
      iSelectedIndex++ ;
      
      if (iSelectedIndex >= _choices.getItemCount())
      	iSelectedIndex = 0 ;

      _choices.setSelectedIndex(iSelectedIndex) ;
           
      return false ;
    }
    
    // Up arrow, select the former proposal in the list (before the first one, cycle to the last)
    //
    if (event.isUpArrow())
    {
      int iSelectedIndex = _choices.getSelectedIndex() ;
      iSelectedIndex-- ;
      
      if (iSelectedIndex < 0)
      	iSelectedIndex = _choices.getItemCount() ;
    
      _choices.setSelectedIndex(iSelectedIndex) ;
           
      return false ;        
    }
       
    int iNativeKeyCode = event.getNativeKeyCode() ;
    
    // Enter key, get current selection as user's choice
    //
    if (KEY_ENTER == iNativeKeyCode)
    {
      if (_visible)
        complete() ;
           
      return false ;
    }
    
    // Escape key, close the selection list
    //
    if (KEY_ESCAPE == iNativeKeyCode)
    {
    	closeChoices()  ;
      _visible = false ;
           
      return false ;
    }
    
    _selectedFlex = null ;
    
    return true ;
       
/*
    String text = this.getText();
    String[] matches = new String[]{};
    if(text.length() > 0)
    {
      // matches = items.getCompletionItems(text);
    }
       
    if (matches.length > 0)
    {
      _choices.clear();
           
      for(int i = 0; i < matches.length; i++)
      {
        _choices.addItem((String) matches[i]);
      }
           
      // if there is only one match and it is what is in the
      // text field anyways there is no need to show autocompletion
      if (matches.length == 1 && matches[0].compareTo(text) == 0)
      {
        _choicesPopup.hide();
      } 
      else 
      {
        _choices.setSelectedIndex(0);
        _choices.setVisibleItemCount(matches.length + 1);
               
        if (!_popupAdded)
        {
          RootPanel.get().add(_choicesPopup);
          _popupAdded = true;
        }
        _choicesPopup.show();
        _visible = true;
        _choicesPopup.setPopupPosition(this.getAbsoluteLeft(),
        this.getAbsoluteTop() + this.getOffsetHeight());
        //choicesPopup.setWidth(this.getOffsetWidth() + "px");
        _choices.setWidth(this.getOffsetWidth() + "px");
      }

    } else {
      _visible = false;
      _choicesPopup.hide();
    }
*/
  }

  /**
   * A mouseclick in the list of items
   */
  public void onChange(Widget arg0) {
    complete();
  }
 
  public void onClick(Widget arg0) {
    complete();
  }
   
  /**
   * Consider current selected item as the final choice, insert it's label in the textbox and close the list
   */
  public void complete()
  {
    // Consider selected Flex as user's choice and display its label in the box
    //
    if (_choices.getItemCount() > 0)
    {
    	int iSelectedIndex = _choices.getSelectedIndex() ;
    	
    	if ((iSelectedIndex >= 0) && (iSelectedIndex < _choices.getItemCount()))
    	{
    		String sCode = _choices.getValue(iSelectedIndex) ;
    		
    		Flex flex = getFlexFromCode(sCode) ;
    		if (null != flex)
    		{
    			_selectedFlex = new Flex(flex) ;
    			setText(flex.getLabel()) ;
    		}
    	}
    }
    
    // Hide the selection list
    //
    closeChoices() ;
    
    // Fire a "concept changed" event
    //
    ConceptChangedEvent event = new ConceptChangedEvent() ;
    _eventBus.fireEvent(event) ;
  }

  /**
   * Returns the selected Lexicon's code
   * 
   * @return Lexicon's code if one was selected, <code>""</code> if not 
   */
  public String getCode() 
  {
  	if (null == _selectedFlex)
  		return "" ;
  	
  	return _selectedFlex.getCode() ;
  }
  
  public Flex getSelected() {
  	return _selectedFlex ;
  }
  
	@Override
  public void onChange(ChangeEvent event) {
		complete() ;
  }
	
	public void clearList()
	{
		_choices.clear() ;
		_flexList.clear() ;
	}
	
	/**
   * Add a new flex entry in the list of proposed choices
   */
	public void addChoice(final Flex model) 
	{
		if (null == model)
			return ;
		
		Flex flex = new Flex(model) ;
		String sLabel = flex.getLabel() ;
		
		_choices.addItem(sLabel, model.getCode()) ;
		
		_flexList.add(flex) ;
	}
	
	/**
	 * Get the {@link org.quadrifolium.shared.ontology.Flex} object from the list for a given code
	 * 
	 * @param sCode Code of the {@link org.quadrifolium.shared.ontology.Flex} we are looking for
	 *  
	 * @return The {@link org.quadrifolium.shared.ontology.Flex} object, of <code>null</code> if not found
	 */
	protected Flex getFlexFromCode(final String sCode)
	{
		if (_flexList.isEmpty() || (null == sCode) || "".equals(sCode))
			return null ;
		
		for (Flex flex : _flexList)
			if (sCode.equals(flex.getCode()))
				return flex ;
		
		return null ;
	}

	/**
	 * Display the popup element that contains the list of choices
	 */
	public void showPopup()
	{
    if (false == _popupAdded)
    {
    	_rootPanel.add(_choicesPopup) ;
      _popupAdded = true ;
    }
    
    _choicesPopup.setPopupPosition(this.getAbsoluteLeft(), this.getAbsoluteTop() + this.getOffsetHeight()) ;
    // choicesPopup.setWidth(this.getOffsetWidth() + "px");
    // _choices.setWidth(this.getOffsetWidth() + "px") ;
    
    // Set popup's ZIndex as text box's ZIndex + 1
    //
    String sZorder = _rootPanel.getElement().getStyle().getZIndex() ;
    int iZorder = 0 ;
    try {
    	iZorder = Integer.parseInt(sZorder) ;
		}
		catch (NumberFormatException cause) {
			iZorder = -1 ;
		}
    _choicesPopup.getElement().getStyle().setZIndex(iZorder + 2) ;
     
    // Show list popup
    //
    _choicesPopup.show() ;
    _visible = true ;
    
    // Set size
    //
    int iNbVisibleItems = _choices.getItemCount() ;
    if (iNbVisibleItems > 10)
    	iNbVisibleItems = 10 ;
    
    _choices.setSelectedIndex(0) ;
    _choices.setVisibleItemCount(iNbVisibleItems) ;
	}
	
	public void hidePopup()
	{
		_choicesPopup.hide() ;
    _visible = false ;
	}
	
	public ListBox getListBox() {
		return _choices ;
	}
	
	public String getLanguage() {
		return _sLanguage ;
	}
	public void setLanguage(final String sLanguage) {
		_sLanguage = sLanguage ;
	}
	
	public Panel getRootPanel() {
		return _rootPanel ;
	}
	public void setRootPanel(final Panel rootPanel) {
		_rootPanel = rootPanel ;
	}
	
	/**
   * Close choices component, clearing list and hiding PopupPanel  
   *
   */
	public void closeChoices() 
	{
		_choices.clear() ;
    _choicesPopup.hide() ;
	}
	
	@Override
	public HandlerRegistration addConceptChangedHandler(ConceptChangedHandler handler) {
		return addHandler(handler, ConceptChangedEvent.getType()) ;
	}
}
