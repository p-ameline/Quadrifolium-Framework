package org.quadrifolium.client.cytoscape;

import jsinterop.annotations.JsType;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayMixed;

import jsinterop.annotations.JsPackage;

@JsType(isNative=true, namespace=JsPackage.GLOBAL)
public class cy 
{
	// Graph manipulation
	//
	public native void add(JavaScriptObject eleObj) ;
	public native void add(JsArrayMixed eleObjs) ;
}
