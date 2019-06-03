package io.sproof.model.events;

import lombok.AllArgsConstructor;
import org.json.simple.JSONObject;

/**
 * Model Klasse fuer ein "DOCUMENT_RECEIVER_ADD" Event
 */
@AllArgsConstructor
public class DocumentReceiverAdd extends AbstractSproofEvent {

    private String receiverId;
    private String documentHash;

    @Override
    protected JSONObject getData() {
        JSONObject json = new JSONObject();
        json.put("documentHash", documentHash);
        json.put("receiverId", receiverId);
        return json;
    }

    @Override
    public String getEventType() {
        return "DOCUMENT_RECEIVER_ADD";
    }
}
