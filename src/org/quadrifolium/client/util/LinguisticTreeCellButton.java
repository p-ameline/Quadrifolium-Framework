package org.quadrifolium.client.util;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Button;

/**
 * A button as appearing inside a LinguisticTreeCell
 */
public class LinguisticTreeCellButton extends Button 
{
  public LinguisticTreeCellButton(Element element)
  {
    super(element) ;
  }
  
  /**
   * Creates a Button widget that wraps an existing &lt;button&gt; element.
   * 
   * This element must already be attached to the document. If the element is
   * removed from the document, you must call
   * {@link RootPanel#detachNow(Widget)}.
   * 
   * @param element the element to be wrapped
   */
  public static LinguisticTreeCellButton wrap(com.google.gwt.dom.client.Element element)
  {
    // Assert that the element is attached.
    assert Document.get().getBody().isOrHasChild(element);

    LinguisticTreeCellButton button = new LinguisticTreeCellButton(element) ;

    // Mark it attached and remember it for cleanup.
    button.onAttach() ; 
    // RootPanel.detachOnWindowClose(button);

    return button;
  }

}
