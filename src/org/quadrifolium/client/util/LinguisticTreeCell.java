package org.quadrifolium.client.util;

import org.quadrifolium.client.mvp_components.QuadrifoliumComponentBaseDisplayModel.INTERFACETYPE;
import org.quadrifolium.client.mvp_components.QuadrifoliumLemmasView;
import org.quadrifolium.client.ui.QuadrifoliumResources;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.view.client.ListDataProvider;

/**
 * The cell model that defines the behavior of the nodes in the linguistic tree.
 * 
 * It is mandatory to provide this superclass in order to have the tree react to nodes changes.
 * The element that reacts to a DataProvider change is the one which implements HasData<T>.
 * In CellTrees it is CellTreeNodeView.NodeCellList<C> , hence the need to provide a cell with a refresh() function.
 */
public class LinguisticTreeCell extends AbstractCell<LinguisticTreeNode> 
{
	protected ListDataProvider<LinguisticTreeNode> _dataProvider ; //for refresh
	protected INTERFACETYPE                        _iInterfaceType ;
	protected QuadrifoliumLemmasView               _parent ;
	
  public LinguisticTreeCell(ListDataProvider<LinguisticTreeNode> dataProvider)
  {
    super("click") ;
    
    _dataProvider   = dataProvider ;
    _iInterfaceType = INTERFACETYPE.undefined ;
  }
  
  public void refresh(final INTERFACETYPE iInterfaceType, final QuadrifoliumLemmasView parent)
  {
  	_iInterfaceType   = iInterfaceType ;
  	_parent           = parent ;
  	
  	_dataProvider.refresh() ; 
  }
  
  @Override
  public void onBrowserEvent(Context context, Element parent, LinguisticTreeNode value, NativeEvent event, ValueUpdater<LinguisticTreeNode> valueUpdater) 
  {
    if (null == value)
      return ;
    
    super.onBrowserEvent(context, parent, value, event, valueUpdater) ;
    
    if ("click".equals(event.getType())) {
    	this.onEnterKeyDown(context, parent, value, event, valueUpdater) ;
    }
    if ("dblclick".equals(event.getType())) {
    	this.onEnterKeyDown(context, parent, value, event, valueUpdater) ;
    }
  }

  /**
   * The HTML templates used to render the cell.
   * 
   * From http://www.gwtproject.org/doc/latest/DevGuideUiCustomCells.html
   * 
   */
  interface Templates extends SafeHtmlTemplates {
    /**
     * The template for this Cell, which includes styles and a value.
     * 
     * @param styles the styles to include in the style attribute of the div
     * @param value the safe value. Since the value type is {@link SafeHtml},
     *          it will not be escaped before including it in the template.
     *          Alternatively, you could make the value type String, in which
     *          case the value would be escaped.
     * @return a {@link SafeHtml} instance
     */
    @SafeHtmlTemplates.Template("<div style=\"{0}\">{1}</div>")
    SafeHtml cell(SafeStyles styles, SafeHtml value) ;
  }
  
  /**
   * Create a singleton instance of the templates used to render the cell.
   */
  private static Templates templates = GWT.create(Templates.class) ;
  
  @Override
  public void render(Context context, LinguisticTreeNode value, SafeHtmlBuilder sb) 
  {
  	/*
     * Always do a null check on the value. Cell widgets can pass null to cells if the underlying data contains a null,
     * or if the data arrives out of order.
     */
    if (null == value) 
      return ;
    
    sb.appendEscaped(value.getName()) ;
    
    if (INTERFACETYPE.editMode == _iInterfaceType)
		{
    	sb.appendHtmlConstant("<button type=\"button\" tabindex=\"-1\" style=\"elementEditButton\" id=\"edt_" + value.getCode() + "\">") ;
      // sb.appendHtmlConstant(QuadrifoliumResources.INSTANCE.editIcon().getSafeUri().asString()) ;
    	sb.appendHtmlConstant("<img src=\"") ;
    	// sb.appendHtmlConstant(QuadrifoliumResources.INSTANCE.editIcon().getName()) ;
    	sb.appendHtmlConstant(QuadrifoliumResources.INSTANCE.editIconAsData().getSafeUri().asString()) ;
    	sb.appendHtmlConstant("\"") ;
      sb.appendHtmlConstant("</button>") ;
    	
      sb.appendHtmlConstant("<button type=\"button\" tabindex=\"-1\" style=\"elementEditButton\" id=\"del_" + value.getCode() + "\">") ;
      // sb.appendHtmlConstant(QuadrifoliumResources.INSTANCE.deleteIcon().getSafeUri().asString()) ;
      sb.appendHtmlConstant("<img src=\"") ;
      // sb.appendHtmlConstant(QuadrifoliumResources.INSTANCE.deleteIcon().getName()) ;
      sb.appendHtmlConstant(QuadrifoliumResources.INSTANCE.deleteIconAsData().getSafeUri().asString()) ;
      sb.appendHtmlConstant("\"") ;
      sb.appendHtmlConstant("</button>") ;
		}
    
    // If the value comes from the user, we escape it to avoid XSS attacks.
    //
    // SafeHtml safeValue = SafeHtmlUtils.fromString(value.getName()) ;

    // Use the template to create the Cell's html.
    // SafeStyles styles = SafeStylesUtils.forTrustedColor(safeValue.asString());
    // SafeHtml rendered = templates.cell(styles, safeValue);
    // sb.append(rendered);
  }

  @Override
  protected void onEnterKeyDown(Context context, Element parent, LinguisticTreeNode value, NativeEvent event, ValueUpdater<LinguisticTreeNode> valueUpdater)
  {
  	if (INTERFACETYPE.editMode != _iInterfaceType)
  		return ;
  	
  	// Get target element
  	//
  	Element targetElement = event.getEventTarget().cast() ;
  	if (null == targetElement)
  		return ;
  	
  	String sID = targetElement.getId() ;
  	
  	// If an image, means that the button is the parent element
  	//
  	if ("img".equalsIgnoreCase(targetElement.getTagName()))
  	{
  		Element buttonElement = targetElement.getParentElement() ;
  		if (null == buttonElement)
  			return ;
  		
  		sID = buttonElement.getId() ;
  		
  		_parent.signalHit(sID, _parent.new hitPoint(event.getClientX(), event.getClientY())) ;
  		
  		// would turn round
  		// clickElement(buttonElement) ;  		
  	}
  	
  	// Event asEvent = Event.as(event) ;
 
  	// For mouse events, such as click, dblclick, mousedown, or mouseup, the detail property indicates how many times 
  	// the mouse has been clicked in the same location for this event. For a dblclick event the value of detail is always 2.
  	//
  	int iDetail = 0 ;
  	
  	if      ("click".equals(event.getType()))
  		iDetail = 1 ;
  	else if ("dblclick".equals(event.getType()))
  		iDetail = 2 ;
  	
  	NativeEvent clickEvent = Document.get().createClickEvent(iDetail, 
  			                                                     event.getScreenX(),
  			                                                     event.getScreenY(),
  			                                                     event.getClientX(), 
  			                                                     event.getClientY(),
  			                                                     event.getCtrlKey(), 
  			                                                     event.getAltKey(),
  			                                                     event.getShiftKey(), 
  			                                                     event.getMetaKey()) ;
  	DomEvent.fireNativeEvent(clickEvent, (HasClickHandlers) _parent) ;
  }
  
  protected native void clickElement(Element elem) /*-{
  	elem.click();
	}-*/;
}
