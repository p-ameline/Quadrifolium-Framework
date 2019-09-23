package org.quadrifolium.client.widgets;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * A widget that implements this interface provides registration for
 * {@link ConceptChangedHandler} instances.
 */
public interface HasConceptChangedHandlers extends HasHandlers 
{
	/**
   * Adds a {@link ConceptChangedEvent} handler.
   * 
   * @param handler the concept changed handler
   * @return {@link HandlerRegistration} used to remove this handler
   */
  HandlerRegistration addConceptChangedHandler(ConceptChangedHandler handler) ;  
}