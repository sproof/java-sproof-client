package at.ac.fhsalzburg.sproof.model.events;

import lombok.AllArgsConstructor;
import org.json.simple.JSONObject;

/**
 * Model Klasse fuer ein "PROFILE_CONFIRM" Event
 */
@AllArgsConstructor
public class ProfileConfirm extends AbstractSproofEvent {

    private String profileId;
    private Boolean value;

    @Override
    protected JSONObject getData() {
        JSONObject json = new JSONObject();
        json.put("to", profileId);
        json.put("value", value);
        return json;
    }

    @Override
    public String getEventType() {
        return "PROFILE_CONFIRM";
    }
}
