package org.quadrifolium.client.util;

import org.quadrifolium.client.mvp_components.QuadrifoliumComponentBaseDisplayModel.INTERFACETYPE;
import org.quadrifolium.client.ui.QuadrifoliumResources;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;
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
	
  public LinguisticTreeCell(ListDataProvider<LinguisticTreeNode> dataProvider)
  {
    super("keydown", "dblclick") ;
    
    _dataProvider   = dataProvider ;
    _iInterfaceType = INTERFACETYPE.undefined ;
  }
  
  public void refresh(final INTERFACETYPE iInterfaceType)
  {
  	_iInterfaceType = iInterfaceType ;
  	
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
    	sb.appendHtmlConstant(QuadrifoliumResources.INSTANCE.editIcon().getName()) ;
      sb.appendHtmlConstant("</button>") ;
    	
      sb.appendHtmlConstant("<button type=\"button\" tabindex=\"-1\" style=\"elementEditButton\" id=\"del_" + value.getCode() + "\">") ;
      sb.appendHtmlConstant(QuadrifoliumResources.INSTANCE.deleteIcon().getSafeUri().asString()) ;
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
    Window.alert("You clicked " + event.getType() + " " + value.getName()) ;
  }
}
