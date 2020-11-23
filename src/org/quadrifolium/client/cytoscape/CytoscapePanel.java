package org.quadrifolium.client.cytoscape;

import java.util.Arrays;

import org.quadrifolium.client.ui.QuadrifoliumResources;
import org.quadrifolium.shared.ontology.QuadrifoliumNode;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.dom.client.Element;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.ldv.shared.graph.LdvModelNode;
import com.ldv.shared.graph.LdvModelNodeArray;
import com.ldv.shared.graph.LdvModelTree;

/**
 * Panel with a Cytoscape graph inside
 * 
 * @author Philippe
 *
 */
public class CytoscapePanel extends SimplePanel 
{
	protected cy             _cytoscapeGraph ;
	protected DagreLayout    _dagreLayout ;

	protected static boolean _bIsInjected = false ;
	protected        boolean _bOpened ;
	
	//Prepare nodes and edges structure
	//		
	protected JSONArray      _aNodes = new JSONArray() ;
	protected JSONArray      _aEdges = new JSONArray() ;
   
	protected int            _iNodesIndex = 0 ;
	protected int            _iEdgesIndex = 0 ;
	
	protected        boolean _bJustTest ;
	
	public CytoscapePanel() 
	{
		super() ;
		
		_bOpened     = false ;
		
		inject() ;
		
		_iNodesIndex = 0 ;
		_iEdgesIndex = 0 ;
		
		_bJustTest   = false ;
	}
	
	/**
	 * Create the Cytoscape graph inside the panel
	 */
	public void open()
	{
		if (_bOpened || (false == isVisible()))
			return ;
		
		_cytoscapeGraph = createGraph(getElement()) ;
		
		_bOpened = true ;
	}
	
	/**
	 * Bootstrap the javascript factory to create a graph and get a cy object as a handle to it
	 * 
	 * @param thisElement DOM element the graph must be deployed into.<br> 
	 *        Note, from Cytoscape documentation that "Cytoscape.js uses the dimensions of your HTML DOM element container for layouts and rendering at initialisation."
	 *        
	 * @return The graph object
	 */
	public native cy createGraph(final Element thisElement) /*-{
		
		// Bootstrap the javascript factory to get a cy object as a handle to the graph
		//
		var cy = cytoscape({
			container: thisElement, // document.getElementById('cy'),
			
			boxSelectionEnabled: false,
			autounselectify: true,
  		
  		// initial viewport state:
			zoom: 1,
			pan: { x: 0, y: 0 },
  		
			layout: {
				name: 'dagre',
				fit: true
			},
			
			style: [
				{
					selector: 'node',
					style: {
						'background-color': '#fff',
						'shape': 'rectangle',
						'width': 'label',
						'padding': '2px',
						'height': 'label',
						'border-width': '2px',
						'border-color': '#11479e',
						'content': 'data(label)',
						// 'content': 'data(id)',
        		'text-opacity': '0.5',
        		'text-valign': 'center',
        		'text-halign': 'center',
        		// 'text-outline-color': '#555',
						// 'text-outline-width': '2px',
						'color': '#000',
						'overlay-padding': '6px',
						'z-index': '10'
					}
				},
				{
					selector: 'edge',
					style: {
						'width': 4,
						'target-arrow-shape': 'triangle',
						'line-color': '#9dbaea',
						'target-arrow-color': '#9dbaea',
						'curve-style': 'bezier'
					}
				}
			]
			// elements: [
			//	{ data: { id: 'a' } },
			//	{ data: { id: 'b' } },
			//	{
			//		data: {
			//			id: 'ab',
			//			source: 'a',
			//			target: 'b'
			//		}
			//	}]
			});
		
		// Set Dagre layout
		//		
		var defaults = {
			name: 'dagre',
			
			// dagre algo options, uses default value on undefined
			nodeSep: undefined, // the separation between adjacent nodes in the same rank
  		edgeSep: undefined, // the separation between adjacent edges in the same rank
  		rankSep: undefined, // the separation between each rank in the layout
  		rankDir: undefined, // 'TB' for top to bottom flow, 'LR' for left to right,
  		ranker: undefined, // Type of algorithm to assign a rank to each node in the input graph. Possible values: 'network-simplex', 'tight-tree' or 'longest-path'
  		minLen: function( edge ){ return 1; }, // number of ranks to keep between the source and target of the edge
  		edgeWeight: function( edge ){ return 1; }, // higher weight edges are generally made shorter and straighter than lower weight edges

  		// general layout options
  		fit: true, // whether to fit to viewport
  		padding: 30, // fit padding
  		spacingFactor: undefined, // Applies a multiplicative factor (>0) to expand or compress the overall area that the nodes take up
  		nodeDimensionsIncludeLabels: false, // whether labels should be included in determining the space used by a node
  		animate: false, // whether to transition the node positions
  		animateFilter: function( node, i ){ return true; }, // whether to animate specific nodes when animation is on; non-animated nodes immediately go to their final positions
  		animationDuration: 500, // duration of animation in ms if enabled
  		animationEasing: undefined, // easing of animation if enabled
  		boundingBox: undefined, // constrain layout bounds; { x1, y1, x2, y2 } or { x1, y1, w, h }
  		transform: function( node, pos ){ return pos; }, // a function that applies a transform to the final node position
  		ready: function(){}, // on layoutready
  		stop: function(){} // on layoutstop
		} ;
					
		// Use the Dagre layout
		//
		var layout = cy.layout(defaults);

		layout.run() ;
					
		return cy ;
	}-*/;

	/**
	 * Populate the graph with stemma's content
	 */
	public void addNodes(final LdvModelTree stemma)
	{
		if (_bJustTest)
		{
			addNodesTest() ;
			return ;
		}
		
		clear(_cytoscapeGraph) ;
		
		if ((null == stemma) || stemma.isEmpty())
			return ;
		
		// Prepare nodes and edges structure
		//		
		resetNodesAndEdges() ;
    
    // Start from root node
    //
		QuadrifoliumNode rootNode = (QuadrifoliumNode) stemma.getRootNode() ;
    addNode(rootNode) ;
    
    // Recursively treat its sons
    //
    addNextLevel(stemma, rootNode) ;
    
    JSONArray allTogether = new JSONArray() ;
    
    int i = 0 ;
    for ( ; i < _iNodesIndex ; i++)
    	allTogether.set(i, _aNodes.get(i)) ;
    for (int j = 0 ; j < _iEdgesIndex ; i++, j++)
    	allTogether.set(i, _aEdges.get(j)) ;
    
    String finalString = allTogether.toString() ;
    
    _cytoscapeGraph.add(JsonUtils.safeEval(finalString)) ;
    
    runLayout(_cytoscapeGraph) ;
    
    // _cytoscapeGraph.reset() ;
    // _cytoscapeGraph.fit() ;    
	}

	/**
	 * Populate the graph with stemma's content
	 */
	public void addNodesTest()
	{
		clear(_cytoscapeGraph) ;
		
		JSONObject pointA = new JSONObject() ;
		pointA.put("id", new JSONString("a")) ;
		// pointA.put("size", new JSONString("[150, 50]")) ;
		JSONObject pointAdata = new JSONObject() ;
		pointAdata.put("data", pointA) ;
		
		JSONObject pointB = new JSONObject() ;
		pointB.put("id", new JSONString("b")) ;
		// pointB.put("size", new JSONString("[150, 50]")) ;
		JSONObject pointBdata = new JSONObject() ;
		pointBdata.put("data", pointB) ;
		
		JSONObject pointC = new JSONObject() ;
		pointC.put("id", new JSONString("c")) ;
		// pointC.put("size", new JSONString("[150, 50]")) ;
		JSONObject pointCdata = new JSONObject() ;
		// pointCdata.put("group", new JSONString("nodes")) ;
		pointCdata.put("data", pointC) ;

		JSONObject pointD = new JSONObject() ;
		pointD.put("id", new JSONString("d")) ;
		// pointD.put("size", new JSONString("[150, 50]")) ;
		JSONObject pointDdata = new JSONObject() ;
		// pointDdata.put("group", new JSONString("nodes")) ;
		pointDdata.put("data", pointD) ;
		
    JSONObject edgeAB = new JSONObject() ;
    edgeAB.put("id", new JSONString("ab")) ;
    edgeAB.put("source", new JSONString("a")) ;
    edgeAB.put("target", new JSONString("b")) ;
    edgeAB.put("shape", new JSONString("polyline")) ;
    JSONObject edgeABdata = new JSONObject() ;
    // edgeACdata.put("group", new JSONString("edges")) ;
    edgeABdata.put("data", edgeAB) ;

    JSONObject edgeAC = new JSONObject() ;
    edgeAC.put("id", new JSONString("ac")) ;
    edgeAC.put("source", new JSONString("a")) ;
    edgeAC.put("target", new JSONString("c")) ;
    edgeAC.put("shape", new JSONString("polyline")) ;
    JSONObject edgeACdata = new JSONObject() ;
    // edgeACdata.put("group", new JSONString("edges")) ;
    edgeACdata.put("data", edgeAC) ;
    
    JSONObject edgeAD = new JSONObject() ;
    edgeAD.put("id", new JSONString("ad")) ;
    edgeAD.put("source", new JSONString("a")) ;
    edgeAD.put("target", new JSONString("d")) ;
    edgeAD.put("shape", new JSONString("polyline")) ;
    JSONObject edgeADdata = new JSONObject() ;
    // edgeBDdata.put("group", new JSONString("edges")) ;
    edgeADdata.put("data", edgeAD) ;
  
    JSONArray allTogether = new JSONArray() ;
    allTogether.set(0, pointAdata) ;
    allTogether.set(1, pointBdata) ;
    allTogether.set(2, pointCdata) ;
    allTogether.set(3, pointDdata) ;
    allTogether.set(4, edgeABdata) ;
    allTogether.set(5, edgeACdata) ;
    allTogether.set(6, edgeADdata) ;
    String finalString = allTogether.toString() ;
        
    _cytoscapeGraph.add(JsonUtils.safeEval(finalString)) ;
    
    runLayout(_cytoscapeGraph) ;
    
    // _cytoscapeGraph.reset() ;
    // _cytoscapeGraph.fit() ;
	}
	
	/**
	 * Connect all sons of a reference node and their sons recursively up to the leaves
	 * 
	 * @throws NullPointerException
	 */
	protected void addNextLevel(final LdvModelTree stemma, final QuadrifoliumNode referenceNode) throws NullPointerException
	{
		if ((null == stemma) || stemma.isEmpty() || (null == referenceNode))
			throw new NullPointerException() ;
		
		QuadrifoliumNode sonNode = (QuadrifoliumNode) stemma.findFirstSon(referenceNode) ;
		
		while (null != sonNode)
		{
			// Add current son and connect it to the reference node
			//
			addNode(sonNode) ;
			addEdge(referenceNode, sonNode) ;
			
			// Recursively connect son's sons 
			//
			addNextLevel(stemma, sonNode) ;
			
			// Get next son
			//
			sonNode = (QuadrifoliumNode) stemma.findFirstBrother(sonNode) ;
		}
	}
	
	public native void runLayout(cy cytoscapeGraph) /*-{
		if (cytoscapeGraph.elements())		
			cytoscapeGraph.elements().layout({ name: 'dagre' }).run() ;
	}-*/;
	
	protected native void clear(cy cytoscapeGraph) /*-{
		if (cytoscapeGraph.elements())		
			cytoscapeGraph.elements().remove() ;
	}-*/;
	
	/**
	 * Add a new node in the graph from a QuadrifoliumNode 
	 * 
	 * @throws NullPointerException
	 */
	protected void addNode(final QuadrifoliumNode node) throws NullPointerException
	{
		if (null == node)
			throw new NullPointerException() ;
		
		JSONObject JsonObject = getJsonObject((QuadrifoliumNode) node) ;
		if (null == JsonObject)
			return ;
    
    _aNodes.set(_iNodesIndex++, JsonObject) ;
	}
	
	protected void addEdge(final QuadrifoliumNode fromNode, final QuadrifoliumNode toNode) throws NullPointerException
	{
		if ((null == fromNode) || (null == toNode))
			throw new NullPointerException() ;
		
		JSONObject edge = new JSONObject() ;
    edge.put("id",     new JSONString(fromNode.getNodeID() + "-" + toNode.getNodeID())) ;
    edge.put("source", new JSONString(fromNode.getNodeID())) ;
    edge.put("target", new JSONString(toNode.getNodeID())) ;
    edge.put("shape",  new JSONString("polyline")) ;
    
    JSONObject edgedata = new JSONObject() ;
    edgedata.put("data", edge) ;
    
    _aEdges.set(_iEdgesIndex++, edgedata) ;
	}
	
	/**
	 * Get a JSON object for a node
	 * 
	 * @param node
	 * @return
	 * 
	 * @throws NullPointerException
	 */
	protected JSONObject getJsonObject(final QuadrifoliumNode node) throws NullPointerException 
	{
		if (null == node)
			throw new NullPointerException() ;
		
		JSONObject point = new JSONObject() ;
		point.put("id",    new JSONString(node.getNodeID())) ;
		point.put("label", new JSONString(node.getLabel())) ;
		
		JSONObject pointdata = new JSONObject() ;
		pointdata.put("data", point) ;
		
		return pointdata ;
	}
	
	/**
	 * Dynamically create the cytoscape script and attach it to the DOM
	 */
	public static void inject()
	{
		if (_bIsInjected)
			return ;
		
		// Inject the cytoscape javascript
		//
		ScriptInjector.fromString(QuadrifoliumResources.INSTANCE.cytoscapeJs().getText()).inject() ;
		
		// Inject the dagre javascript
		//
		ScriptInjector.fromString(QuadrifoliumResources.INSTANCE.dagreJs().getText()).inject() ;
		
		// Inject the cytoscape-dagre javascript
		//
		ScriptInjector.fromString(QuadrifoliumResources.INSTANCE.cytoscapeDagreJs().getText()).inject() ;
		
		_bIsInjected = true ;
	}
	
	public boolean isOpen() {
		return _bOpened ;
	}
	
	/**
	 * Reset Nodes and Edges structures
	 */
	public void resetNodesAndEdges()
	{	
		for (int i = 0 ; i < _aNodes.size() ; i++)
			_aNodes.set(0, null) ;
	
		for (int i = 0 ; i < _aEdges.size() ; i++)
			_aEdges.set(0, null) ;
   
		_iNodesIndex = 0 ;
		_iEdgesIndex = 0 ;
	}
	
	public void reset() {	
	}

	public Widget asWidget() {
		return this;
	}
}
