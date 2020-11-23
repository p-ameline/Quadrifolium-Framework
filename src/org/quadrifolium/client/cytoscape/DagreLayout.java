package org.quadrifolium.client.cytoscape;

import jsinterop.annotations.JsType;

import jsinterop.annotations.JsPackage;

@JsType(isNative=true, namespace=JsPackage.GLOBAL)
public class DagreLayout 
{
	// Graph manipulation
	//
	public native void        register(cy cytoscape) ;
	public native DagreLayout run() ;
}
