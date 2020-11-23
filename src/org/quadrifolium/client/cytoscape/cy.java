package org.quadrifolium.client.cytoscape;

import jsinterop.annotations.JsType;

import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.json.client.JSONObject;

import jsinterop.annotations.JsPackage;

@JsType(isNative=true, namespace=JsPackage.GLOBAL)
public class cy 
{
	// Graph manipulation
	//
	public native void add(JSONObject eleObj) ;
	public native void add(JsArrayMixed eleObjs) ;
	
	public native void remove(JsArrayMixed eleObjs) ;
	public native void remove(final String sSelector) ;
	
	// Viewport manipulation
	//
	
	/**
	 * Pan and zooms the graph to fit all elements in the graph.
	 */
	public native void fit() ;
	
	/**
	 * Resets the zoom and pan (resets the viewport to the origin (0, 0) at zoom level 1)
	 */
	public native void reset() ;
}
