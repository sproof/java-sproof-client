package io.sproof.model.events;

import org.json.simple.JSONObject;

/**
 * Basis-Interface fuer Event-Klassen
 */
public interface SproofEvent {

    String getEventType();

    JSONObject toJsonObject();

}
