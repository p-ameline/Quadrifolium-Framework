package org.quadrifolium.client.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.quadrifolium.shared.ontology.FlexWithTraits;
import org.quadrifolium.shared.ontology.LemmaWithInflections;
import org.quadrifolium.shared.ontology.TripleWithLabel;

import com.google.gwt.view.client.AbstractDataProvider;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;

/**
 * The tree model that defines the nodes in the linguistic tree.
 */
public class LinguisticTreeModel implements TreeViewModel 
{
	private String                     _sConceptName ;
	// private List<LemmaWithInflections> _aSynonyms ;

	// Data provider that contains the list of nodes.
	//
	// A concrete subclass of AbstractDataProvider that is backed by an in-memory list.
	// 
	// ListDataProvider: 
	// Modifications (inserts, removes, sets, etc.) to the list returned by getList() will be reflected in the model.
	// However, mutations to the items contained within the list will NOT be reflected in the model.
	// You must call List.set(int, Object) to update the item within the list and push the change to the display,
	// or call refresh() to push all rows to the displays.
	// List.set(int, Object) performs better because it allows the data provider to push only those rows which have changed, 
	// and usually allows the display to re-render only a subset of the rows. 
	//
  // private final AbstractDataProvider<LinguisticTreeNode> _dataProvider = new ListDataProvider<LinguisticTreeNode>() ;

  private Map<LinguisticTreeNode, ListDataProvider<LinguisticTreeNode>> _mapDataProviders = null ;
  private ListDataProvider<LinguisticTreeNode>                          _rootDataProvider = null ;
  
  /**
   * This selection model is shared across all leaf nodes. A selection model
   * can also be shared across all nodes in the tree, or each set of child
   * nodes can have its own instance. This gives you flexibility to determine
   * how nodes are selected.
   */
	private final SingleSelectionModel<LinguisticTreeNode> selectionModel = new SingleSelectionModel<LinguisticTreeNode>() ;
	
	private LinguisticTreeNode _root ;

	public LinguisticTreeModel(final String sConceptName, final List<LemmaWithInflections> synonyms) 
	{
		_mapDataProviders = new HashMap<LinguisticTreeNode, ListDataProvider<LinguisticTreeNode>>() ;
		
		_sConceptName = sConceptName ;
		_root = new LinguisticTreeNode(sConceptName, null) ; 
		
		_rootDataProvider = new ListDataProvider<LinguisticTreeNode>(new ArrayList<LinguisticTreeNode>()) ;
		// _rootDataProvider.getList().add(_root) ;
    _mapDataProviders.put(_root, _rootDataProvider) ;
		
		initializeData(synonyms) ;
	}
	
	/**
   * Get the {@link TreeViewModel.NodeInfo} that will provide the ProvidesKey, Cell, and HasData instances
   * to retrieve and display the children of the specified value.
   * 
   * This method is called when a node gets opened (means asked to display its children)
   * It is not called again when an already open node gets changed
   * 
   */
	@Override
  public <T> NodeInfo<?> getNodeInfo(T value)
  {
/*
    if (null == value)
    {
      // LEVEL 0.
      // We passed null as the root value. Return the synonyms.

      // Create a cell to display a synonym.
      Cell<LemmaWithInflections> cell = new AbstractCell<LemmaWithInflections>()
      {
        @Override
        public void render(Context context, LemmaWithInflections value, SafeHtmlBuilder sb) {
          if (null != value) {
            sb.appendEscaped(value.getName());
          }
        }
      };

      // Return a node info that pairs the data provider and the cell.
      return new DefaultNodeInfo<LemmaWithInflections>(_dataProvider, cell) ;
    } 
    else if (value instanceof LemmaWithInflections) 
    {
      // LEVEL 1.
      // We want the children of the synonym. Return the inflections.
      ListDataProvider<FlexWithTraits> dataProvider = new ListDataProvider<FlexWithTraits>(((LemmaWithInflections) value).getInflections()) ;
      Cell<FlexWithTraits> cell = new AbstractCell<FlexWithTraits>()
      {
        @Override
        public void render(Context context, FlexWithTraits value, SafeHtmlBuilder sb) {
          if (null != value) {
            sb.appendEscaped(value.getName()) ;
          }
        }
      };
      return new DefaultNodeInfo<FlexWithTraits>(dataProvider, cell) ;
    } 
    else if (value instanceof FlexWithTraits) 
    {
      // LEVEL 2 - LEAF.
      // We want the children of the synonyms. Return the traits.
      ListDataProvider<TripleWithLabel> dataProvider = new ListDataProvider<TripleWithLabel>(((FlexWithTraits) value).getTraits()) ;
      Cell<TripleWithLabel> cell = new AbstractCell<TripleWithLabel>()
      {
        @Override
        public void render(Context context, TripleWithLabel value, SafeHtmlBuilder sb) {
          if (null != value) {
            sb.appendEscaped(value.getName()) ;
          }
        }
      };
      return new DefaultNodeInfo<TripleWithLabel>(dataProvider, cell) ;
    }
*/
  	LinguisticTreeNode treeNode = null ;
  	
  	if (null == value)  // root is not set 
  		treeNode = _root ;
  	else 
  		treeNode = (LinguisticTreeNode) value ; 
  	
  	ListDataProvider<LinguisticTreeNode> cellDataProvider = _mapDataProviders.get(treeNode) ;
  	if (null == cellDataProvider)
  	{
  		cellDataProvider = new ListDataProvider<LinguisticTreeNode>(treeNode.getList()) ;
  		_mapDataProviders.put(treeNode, cellDataProvider) ;
  	}
  	
  	LinguisticTreeCell treeCell = new LinguisticTreeCell((ListDataProvider<LinguisticTreeNode>) cellDataProvider) ;
  	
  	if (null != treeNode) 
  		treeNode.setCell(treeCell) ;
  	
  	return new DefaultNodeInfo<LinguisticTreeNode>(cellDataProvider, treeCell, selectionModel, null) ; 
  }

  /**
   * Check if the specified value represents a leaf node. Leaf nodes cannot be opened.
   */
	@Override
  public boolean isLeaf(Object value) 
  {
    // The leaf nodes are the traits.
    if (value instanceof TripleWithLabel)
      return true ;
    
    return false ;
  }
  
  /**
   * Fills the list of nodes from a list of synonyms 
   */
  public void initializeData(final List<LemmaWithInflections> synonyms) 
  {
  	ArrayList<LinguisticTreeNode> dataList = new ArrayList<LinguisticTreeNode>() ;
  	
  	// dataList.add(_root) ;
  	
  	if (null == synonyms)
  	{
  		// ((ListDataProvider<LinguisticTreeNode>)_dataProvider).setList(dataList) ;
  		((ListDataProvider<LinguisticTreeNode>)_rootDataProvider).setList(dataList) ;
  		return ;
  	}

  	fillDataList(dataList, synonyms) ;
  	
  	// Set the list as data provider's information list
  	//
  	// ((ListDataProvider<LinguisticTreeNode>)_dataProvider).setList(dataList) ;
  	((ListDataProvider<LinguisticTreeNode>)_rootDataProvider).setList(dataList) ;
  }
 
  /**
   * Fills the list of nodes from a list of synonyms 
   */
  public void fillData(final List<LemmaWithInflections> synonyms) 
  {
  	// List<LinguisticTreeNode> dataList = ((ListDataProvider<LinguisticTreeNode>)_dataProvider).getList() ;
  	List<LinguisticTreeNode> dataList = ((ListDataProvider<LinguisticTreeNode>)_rootDataProvider).getList() ;
  	
  	// Clear all root node's children
  	//
  	// _root.getList().clear() ;
  	while (false == _root.getList().isEmpty())
  	{
  		Iterator<LinguisticTreeNode> it = _root.getList().iterator() ;
  		remove(it.next()) ;
  	}
  	
  	// Clear root node's children list 
  	//
  	dataList.clear() ;
  	// dataList.add(_root) ;
  	
  	fillDataList(dataList, synonyms) ;
  	
  	_root.refresh() ;
  }
  
  /**
   * Add all information to data provider's list
   * 
   * @param dataList Data provider's list
   * @param synonyms Lemmas to feed the list with
   */
  protected void fillDataList(List<LinguisticTreeNode> dataList, final List<LemmaWithInflections> synonyms)
  {
  	if ((null == synonyms) || (null == dataList) || synonyms.isEmpty())
  		return ;
  	
  	// Add all lemmas as children of the root node
  	//
  	for (Iterator<LemmaWithInflections> it = synonyms.iterator() ; it.hasNext() ; )
  	{
  		LemmaWithInflections lemma = it.next() ;
  		
  		LinguisticTreeNode lemmaNode = new LinguisticTreeNode(lemma, _root) ;
  		dataList.add(lemmaNode) ;
  		
  		// Add traits as children of current lemma
  		//
  		if (false == lemma.getTraits().isEmpty())
  		{
  			for (Iterator<TripleWithLabel> itTrait = lemma.getTraits().iterator() ; itTrait.hasNext() ; )
  			{
  				LinguisticTreeNode traitNode = new LinguisticTreeNode(itTrait.next(), lemmaNode) ;
  				// dataList.add(traitNode) ;
  			}
  		}
  		
  		// Add inflections as children of current lemma
  		//
  		if (false == lemma.getInflections().isEmpty())
  		{
  			for (Iterator<FlexWithTraits> itInflection = lemma.getInflections().iterator() ; itInflection.hasNext() ; )
  			{
  				FlexWithTraits flex = itInflection.next() ;
  				LinguisticTreeNode flexNode = new LinguisticTreeNode(flex, lemmaNode) ;
  				// dataList.add(flexNode) ;
  				
  				if (false == lemma.getTraits().isEmpty())
  	  		{
  	  			for (Iterator<TripleWithLabel> itTrait = flex.getTraits().iterator() ; itTrait.hasNext() ; )
  	  			{
  	  				LinguisticTreeNode traitNode = new LinguisticTreeNode(itTrait.next(), flexNode) ; 
  	  				// dataList.add(traitNode) ;
  	  			}
  	  		}
  			}
  		}
  		
  		// Create the data provider for this node and reference it
  		//
  		ListDataProvider<LinguisticTreeNode> cellDataProvider = new ListDataProvider<LinguisticTreeNode>(lemmaNode.getList()) ;
  		_mapDataProviders.put(lemmaNode, cellDataProvider) ;
  	}  	
  }
    
  public void remove(LinguisticTreeNode nodeToRemove) 
  {
  	ListDataProvider<LinguisticTreeNode> dataprovider = _mapDataProviders.get(nodeToRemove) ;
  	if (null != dataprovider)
  	{
  		dataprovider.getList().remove(nodeToRemove) ;
//  mapDataProviders.remove(nodeToRemove) ;
  		dataprovider.refresh() ;
  		dataprovider.flush() ;
  	}

    if (nodeToRemove.getParent() != null)
    {
    	ListDataProvider<LinguisticTreeNode> dataproviderParent = _mapDataProviders.get(nodeToRemove.getParent()) ;
    	nodeToRemove.getParent().getList().remove(nodeToRemove) ;
    	dataproviderParent.refresh() ;
    	dataproviderParent.flush() ;
    }
    else
    {
    	_rootDataProvider.refresh() ;
    	_rootDataProvider.flush();
    }       
  } 
  
  public String getsConceptName() {
  	return _sConceptName ;
  }
  public void setsConceptName(final String sConceptName) 
  {
  	_sConceptName = sConceptName ;
  	_root.setContent(sConceptName) ;
  }
  
  public void refresh() {
  	_root.refresh() ;
  }
}
