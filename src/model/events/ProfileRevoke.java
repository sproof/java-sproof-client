package at.ac.fhsalzburg.sproof.model.events;

import lombok.AllArgsConstructor;
import org.json.simple.JSONObject;

/**
 * Model Klasse fuer ein "PROFILE_REVOKE" Event
 */
@AllArgsConstructor
public class ProfileRevoke extends AbstractSproofEvent {

    private String reason;

    @Override
    protected JSONObject getData() {
        JSONObject json = new JSONObject();
        json.put("reason", reason);
        return json;
    }

    @Override
    public String getEventType() {
        return "PROFILE_REVOKE";
    }
}
