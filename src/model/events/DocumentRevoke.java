package at.ac.fhsalzburg.sproof.model.events;

import lombok.AllArgsConstructor;
import org.json.simple.JSONObject;

/**
 * Model Klasse fuer ein "DOCUMENT_REVOKE" Event
 */
@AllArgsConstructor
public class DocumentRevoke extends AbstractSproofEvent {

    private String documentHash;
    private String reason;

    @Override
    protected JSONObject getData() {
        JSONObject json = new JSONObject();
        json.put("documentHash", documentHash);
        json.put("reason", reason);
        return json;
    }

    @Override
    public String getEventType() {
        return "DOCUMENT_REVOKE";
    }
}
