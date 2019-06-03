package io.sproof.model.events;

import at.ac.fhsalzburg.sproof.model.Profile;
import lombok.AllArgsConstructor;
import org.json.simple.JSONObject;

/**
 * Model Klasse fuer ein "PROFILE_UPDATE" Event
 */
@AllArgsConstructor
public class ProfileUpdate extends AbstractSproofEvent {

    public static String EVENT_TYPE = "PROFILE_UPDATE";

    private Profile profile;

    @Override
    public String getEventType() {
        return EVENT_TYPE;
    }

    @Override
    protected JSONObject getData() {
        return profile.toJsonObject();
    }
}
