package org.quadrifolium.client.widgets;

import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Handler for {@link ConceptChangedEvent} events.
 */
public class ConceptChangedEvent extends GwtEvent<ConceptChangedHandler> 
{
	/**
   * Event type for click events. Represents the meta-data associated with this
   * event.
   */
  public static final Type<ConceptChangedHandler> TYPE = new Type<ConceptChangedHandler>() ;

  /**
   * Gets the event type associated with click events.
   * 
   * @return the handler type
   */
  public static Type<ConceptChangedHandler> getType() {
    return TYPE ;
  }

  /**
   * Protected constructor, use
   * {@link DomEvent#fireNativeEvent(com.google.gwt.dom.client.NativeEvent, com.google.gwt.event.shared.HasHandlers)}
   * to fire click events.
   */
  protected ConceptChangedEvent() {
  }

  @Override
  public final Type<ConceptChangedHandler> getAssociatedType() {
    return TYPE ;
  }

  @Override
  protected void dispatch(ConceptChangedHandler handler) {
    handler.onConceptChanged(this) ;
  }
}
