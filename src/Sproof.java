package io.sproof;

import at.ac.fhsalzburg.sproof.model.Profile;
import at.ac.fhsalzburg.sproof.model.RawTransactionResult;
import at.ac.fhsalzburg.sproof.model.TransactionBuilder;
import at.ac.fhsalzburg.sproof.model.events.*;
import io.socket.emitter.Emitter;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static at.ac.fhsalzburg.sproof.JsonUtil.verifyJsonObject;
import static at.ac.fhsalzburg.sproof.JsonUtil.verifyString;
import static at.ac.fhsalzburg.sproof.Utils.addAll;

public class Sproof {

    private List<JSONObject> events;
    private SproofApi api;

    /**
     * erstellt ein neues Sproof Object mit der gegebenen SproofApi
     * @param api zu verwendende SproofApi
     */
    public Sproof(SproofApi api) {
        events = new ArrayList<>();
        this.api = api;
    }

    /**
     * erstellt ein neues Sproof Object mit der gegebenen Konfiguration
     * @param config zu verwendende Konfigurationsdaten
     */
    public Sproof(Config config) {
        events = new ArrayList<>();
        api = new SproofApi(config);
    }

    /**
     * erstellt ein neues Sproof Object und laedt die Konfiguration aus der gegebenen Datei
     * @param pathToConfigFile Dateipfad der Konfiguration
     * @throws IOException
     * @throws ParseException
     * @throws UnreadableWalletException
     * @throws URISyntaxException
     */
    public Sproof(String pathToConfigFile) throws IOException, ParseException, UnreadableWalletException, URISyntaxException {
        this(Paths.get(pathToConfigFile));
    }

    /**
     * erstellt ein neues Sproof Object und laedt die Konfiguration aus der gegebenen Datei
     * @param pathToConfigFile Dateipfad der Konfiguration
     * @throws IOException
     * @throws ParseException
     * @throws UnreadableWalletException
     * @throws URISyntaxException
     */
    public Sproof(Path pathToConfigFile) throws IOException, ParseException, UnreadableWalletException, URISyntaxException {
        Config config = Config.fromJson(pathToConfigFile);
        this.api = new SproofApi(config);
        events = new ArrayList<>();
    }

    /**
     * fuegt das gegebene 'PROFILE_REGISTER' event zur internen Event-Liste hinzu
     * @param register ein neues 'PROFILE_REGISTER' event
     * @throws JSONException
     */
    public void registerProfile(ProfileRegister register) throws JSONException {
        addEvent(register);
    }

    /**
     * fuegt das gegebene 'PROFILE_UPDATE' event zur internen Event-Liste hinzu
     * @param update ein neues 'PROFILE_UPDATE' event
     * @throws JSONException
     */
    public void updateProfile(ProfileUpdate update) throws JSONException {
        addEvent(update);
    }

    /**
     * fuegt das gegebene 'PROFILE_CONFIRM' event zur internen Event-Liste hinzu
     * @param confirm ein neues 'PROFILE_CONFIRM' event
     * @throws JSONException
     */
    public void confirmProfile(ProfileConfirm confirm) throws JSONException {
        addEvent(confirm);
    }

    /**
     * fuegt das gegebene 'PROFILE_REVOKE' event zur internen Event-Liste hinzu
     * @param revoke ein neues 'PROFILE_REVOKE' event
     * @throws JSONException
     */
    public void revokeProfile(ProfileRevoke revoke) throws JSONException {
        addEvent(revoke);
    }

    /**
     * fuegt das gegebene 'DOCUMENT_REGISTER' event zur internen Event-Liste hinzu
     * @param register ein neues 'DOCUMENT_REGISTER' event
     * @throws JSONException
     */
    public void registerDocument(DocumentRegister register) throws JSONException {
        addEvent(register);
    }

    /**
     * fuegt das gegebene 'DOCUMENT_REVOKE' event zur internen Event-Liste hinzu
     * @param revoke ein neues 'DOCUMENT_REVOKE' event
     * @throws JSONException
     */
    public void revokeDocument(DocumentRevoke revoke) throws JSONException {
        addEvent(revoke);
    }

    /**
     * fuegt das gegebene 'DOCUMENT_RECEIVER_ADD' event zur internen Event-Liste hinzu
     * @param add ein neues 'DOCUMENT_RECEIVER_ADD' event
     * @throws JSONException
     */
    public void addDocumentReceiver(DocumentReceiverAdd add) throws JSONException {
        addEvent(add);
    }

    /**
     * fuegt das gegebene 'DOCUMENT_RECEIVER_REVOKE' event zur internen Event-Liste hinzu
     * @param revoke ein neues 'DOCUMENT_RECEIVER_REVOKE' event
     * @throws JSONException
     */
    public void revokeDocumentReceiver(DocumentReceiverRevoke revoke) throws JSONException {
        addEvent(revoke);
    }

    /**
     * fuegt das gegebene event zur internen Event-Liste hinzu
     * @param event ein neues event
     * @throws JSONException
     */
    public void addEvent(SproofEvent event){
        this.events.add(event.toJsonObject());
    }

    public void registerPremiumUser(Profile data, SproofApi.Callback callback) throws SproofHttpException {
        this.api.registerPremiumUser(data, callback);
    }

    public void getUser(SproofApi.Callback callback) throws SproofHttpException {
        this.api.get("user", null, callback);
    }

    public void getState(SproofApi.Callback callback) throws SproofHttpException {
        this.api.get("state", null , callback);
    }

    public void getEvents(Map<String,String> params, SproofApi.Callback callback) throws SproofHttpException {
        this.api.get("events", params, callback);
    }

    public void getTransactions(Map<String,String>params, SproofApi.Callback callback) throws SproofHttpException {
        this.api.get("transactions", params, callback);
    }

    public void getProfiles(Map<String,String> params, SproofApi.Callback callback) throws SproofHttpException {
        this.api.get("profiles", params, callback);
    }

    public void getReceivers(Map<String,String> params, SproofApi.Callback callback) throws SproofHttpException {
        this.api.get("receivers", params, callback);
    }

    public void getRegistrations(Map<String,String> params, SproofApi.Callback callback) throws SproofHttpException {
        this.api.get("registrations", params, callback);
    }

    public void getDocuments(Map<String,String> params, SproofApi.Callback callback) throws SproofHttpException {
        this.api.get("documents", params, callback);
    }

    public void getValidation(String id, SproofApi.Callback callback) throws SproofHttpException {
        Map<String, String> params = new HashMap<>();
        params.put("id", id);
        this.api.get("verification", params, callback);
    }

    public void on(String event, Emitter.Listener fun) throws SproofConfigException {
        this.api.on(event,fun);
    }

    /**
     * liefert das zugrundeliegende API Object zurueck
     * @return SproofApi Object
     */
    public SproofApi getApi() {
        return api;
    }

    /**
     * committet alle Events in der Liste
     * @param callback callback um das Ergebnis zu erhalten
     * @throws SproofHttpException
     */
    public void commit(final SproofApi.Callback callback) throws SproofHttpException {

        api.getRawTransaction(events, (data, ex) -> {
            if(ex != null) {
                callback.onResultReady(null, ex);
                return;
            }
            try {
                String jsonString = new String(data.array());
                System.out.println(jsonString);
                RawTransactionResult transactionResult = TransactionBuilder.fromRawJson(jsonString);
                TxSignResult result = Utils.signTx(transactionResult.getRawTransaction(), api.getCredentials());
                api.submit(events, result, callback);
            } catch (ParseException | SproofHttpException e) {
                callback.onResultReady(null, e);
            }
        });
    }

    /**
     * committet alle Events in der Liste
     * @param callback callback um das Ergebnis zu erhalten
     * @throws SproofHttpException
     */
    public void commitPremium(final SproofApi.Callback callback) throws SproofHttpException {

        this.api.getHash(events, (data, ex) -> {
            if(ex != null) {
                callback.onResultReady(null, ex);
                return;
            }
            String jsonString = new String(data.array());
            JSONParser parser = new JSONParser();
            try {
                Object resultJson = parser.parse(jsonString);
                JSONObject jsonObject = verifyJsonObject(resultJson);
                if(jsonObject == null) {
                    throw new SproofHttpException("no result received");
                }
                JSONObject result = verifyJsonObject(jsonObject.get("result"));
                if(result == null) {
                    String error = verifyString(jsonObject.get("error"));
                    if(error == null) {
                        throw new ParseException(ParseException.ERROR_UNEXPECTED_EXCEPTION);
                    }
                    throw new SproofHttpException(error);
                }

                String hashToRegister = verifyString(result.get("hashToRegister"));
                String hash = verifyString(result.get("hash"));

                Signature signature = Utils.sign(hashToRegister, api.getCredentials());

                api.submitPremium(events, api.getCredentials().getAddress(), hashToRegister, signature, callback);

            } catch (ParseException | NoSuchAlgorithmException | SproofHttpException e) {
                callback.onResultReady(null, e);
            }
        });
    }

    public List<JSONObject> getEvents() {
        return events;
    }
}
