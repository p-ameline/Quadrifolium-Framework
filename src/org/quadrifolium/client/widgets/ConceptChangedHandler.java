package org.quadrifolium.client.widgets;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handler for {@link CodeChangedEvent} events.
 */
public interface ConceptChangedHandler extends EventHandler {
	/**
   * Called when a native click event is fired.
   * 
   * @param event the {@link CodeChangedEvent} that was fired
   */
  void onConceptChanged(ConceptChangedEvent event);
}