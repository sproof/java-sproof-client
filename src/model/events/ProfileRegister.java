package at.ac.fhsalzburg.sproof.model.events;

import at.ac.fhsalzburg.sproof.model.Profile;
import lombok.AllArgsConstructor;
import org.json.simple.JSONObject;

/**
 * Model Klasse fuer ein "PROFILE_REGISTER" Event
 */
@AllArgsConstructor
public class ProfileRegister extends AbstractSproofEvent {

    private Profile profile;

    @Override
    protected JSONObject getData() {
        return profile.toJsonObject();
    }

    @Override
    public String getEventType() {
        return "PROFILE_REGISTER";
    }
}
