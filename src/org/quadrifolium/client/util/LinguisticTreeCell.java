package org.quadrifolium.client.util;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Window;
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
	ListDataProvider<LinguisticTreeNode> _dataProvider ; //for refresh
	
  public LinguisticTreeCell(ListDataProvider<LinguisticTreeNode> dataProvider)
  {
    super("keydown", "dblclick") ;
    
    _dataProvider = dataProvider ;
  }
  
  public void refresh() {
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

  @Override
  public void render(Context context, LinguisticTreeNode value, SafeHtmlBuilder sb) 
  {
    if (null == value) 
      return ;
 
    sb.appendEscaped(value.getName()) ;
    //add HERE for better formating
  }


  @Override
  protected void onEnterKeyDown(Context context, Element parent, LinguisticTreeNode value, NativeEvent event, ValueUpdater<LinguisticTreeNode> valueUpdater)
  {
    Window.alert("You clicked " + event.getType() + " " + value.getName()) ;
  }
}
