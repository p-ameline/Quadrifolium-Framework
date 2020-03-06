package org.quadrifolium.client.cytoscape;

import org.quadrifolium.client.ui.QuadrifoliumResources;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Panel with a Cytoscape graph inside
 * 
 * @author Philippe
 *
 */
public class CytoscapePanel extends SimplePanel 
{
	protected cy _cytoscapeGraph ;
	
	protected static boolean _bIsInjected = false ;
	protected        boolean _bOpened     = false ;
	
	public CytoscapePanel() 
	{
		super() ;
		
		inject() ;
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
			elements: [
				{ data: { id: 'a' } },
				{ data: { id: 'b' } },
				{
					data: {
						id: 'ab',
						source: 'a',
						target: 'b'
					}
				}]
			});
			
		return cy ;
	}-*/;
	
	public void addNode()
	{
/*
		JsArrayString arr = JavaScriptObject.createArray(4).cast() ;
		arr.set(0, "{ data: { id: 'c' } }") ;
		arr.set(1, "{ data: { id: 'd' } }") ;
		arr.set(2, "{ data: { id: 'ac', source: 'a', target: 'c'} }") ;
		arr.set(3, "{ data: { id: 'bd', source: 'b', target: 'd'} }") ;
*/
		JsArrayString arr = JavaScriptObject.createArray(4).cast() ;
		arr.set(0, "{ group: 'nodes', { data: { id: 'c' } } }") ;
		arr.set(1, "{ group: 'nodes', { data: { id: 'd' } } }") ;
		arr.set(2, "{ group: 'edges', { data: { id: 'ac', source: 'a', target: 'c'} } }") ;
		arr.set(3, "{ group: 'edges', { data: { id: 'bd', source: 'b', target: 'd'} } }") ;
		
/*	
		JsArrayString arr = JavaScriptObject.createArray(1).cast() ;
		arr.set(0, "{ elements: [ { data: { id: 'c' } }, { data: { id: 'd' } }, { data: { id: 'ac', source: 'a', target: 'c'} }, { data: { id: 'bd', source: 'b', target: 'd'} } ] }") ;
*/		
		_cytoscapeGraph.add(arr) ;
		
/*
		_cytoscapeGraph.add("{" + 
				"			elements: [" + 
				"				{ data: { id: 'c' } }," + 
				"				{ data: { id: 'd' } }," + 
				"				{" + 
				"					data: {" + 
				"						id: 'ac'," + 
				"						source: 'a'," + 
				"						target: 'c'" + 
				"					}" + 
				"				}," +
				"				{" + 
				"					data: {" + 
				"						id: 'bd'," + 
				"						source: 'b'," + 
				"						target: 'd'" + 
				"					}" + 
				"				}]" +
				"			}") ;
*/
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
		
		_bIsInjected = true ;
	}
	
	public boolean isOpen() {
		return _bOpened ;
	}
	
	public void reset() {	
	}

	public Widget asWidget() {
		return this;
	}
}
