package io.sproof.model.events;

import org.json.simple.JSONObject;

/**
 * Basisklasse fuer Event-Klassen
 */
public abstract class AbstractSproofEvent implements SproofEvent {

    protected abstract JSONObject getData();

    @Override
    public JSONObject toJsonObject() {
        JSONObject json = new JSONObject();
        json.put("eventType", getEventType());
        json.put("data", getData());
        return json;
    }
}
