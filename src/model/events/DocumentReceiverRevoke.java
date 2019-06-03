package io.sproof.model.events;

import lombok.AllArgsConstructor;
import org.json.simple.JSONObject;

/**
 * Model Klasse fuer ein "DOCUMENT_RECEIVER_REVOKE" Event
 */
@AllArgsConstructor
public class DocumentReceiverRevoke extends AbstractSproofEvent {

    private String recieverId;
    private String reason;

    @Override
    protected JSONObject getData() {
        JSONObject json = new JSONObject();
        json.put("receiverId", recieverId);
        json.put("reason", reason);
        return json;
    }

    @Override
    public String getEventType() {
        return "DOCUMENT_RECEIVER_REVOKE";
    }
}
