package at.ac.fhsalzburg.sproof.model.events;

import at.ac.fhsalzburg.sproof.model.Document;
import lombok.AllArgsConstructor;
import org.json.simple.JSONObject;

/**
 * Model Klasse fuer ein "DOCUMENT_REGISTER" Event
 */
@AllArgsConstructor
public class DocumentRegister extends AbstractSproofEvent {

    private Document document;

    @Override
    protected JSONObject getData() {
        return document.toJsonObject();
    }

    @Override
    public String getEventType() {
        return "DOCUMENT_REGISTER";
    }
}
