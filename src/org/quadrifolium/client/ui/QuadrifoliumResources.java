package org.quadrifolium.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.resources.client.DataResource;

/**
 * An object that implements this interface may have validation components.
 *
 * @author lowec
 *
 */
public interface QuadrifoliumResources extends ClientBundle 
{
	public static final QuadrifoliumResources INSTANCE =  GWT.create(QuadrifoliumResources.class) ;

	@NotStrict
  @Source("Quadrifolium.css")
  public CssResource css();
	
	@Source("logo_Quadrifolium.gif")
	public ImageResource welcomeImg() ;
	
	@Source("iconEdit.png")
	public ImageResource editIcon() ;
	
	@Source("iconEdit.png")
	public DataResource editIconAsData() ;
	
	@Source("iconDel.png")
	public ImageResource deleteIcon() ;
	
	@Source("iconDel.png")
	public DataResource deleteIconAsData() ;

	@Source("iconPlus.png")
	public ImageResource addIcon() ;
	
	@Source("cytoscape.js")
	public TextResource cytoscapeJs() ;
	
	@Source("dagre.js")
	public TextResource dagreJs() ;
	
	@Source("cytoscape-dagre.js")
	public TextResource cytoscapeDagreJs() ;
	
/*
  @Source("config.xml")
  public TextResource initialConfiguration();

  @Source("manual.pdf")
  public DataResource ownersManual();
*/
}
