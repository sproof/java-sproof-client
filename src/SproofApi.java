package at.ac.fhsalzburg.sproof;

import at.ac.fhsalzburg.sproof.model.Profile;
import at.ac.fhsalzburg.sproof.model.events.ProfileConfirm;
import at.ac.fhsalzburg.sproof.model.events.ProfileRegister;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SproofApi {

    public static final String VERSION_PATH = "/api/v1/";

    /**
     * Callback fuer HTTP Resultate / Fehler
     */
    public interface Callback {
        void onResultReady(ByteBuffer data, Exception ex);
    }

    private final Config config;
    private Socket socketIo = null;

    /**
     * erstellt ein neues SproofApi Object mit der gegebenen Config
     * @param config zu verwendende Sproof Config
     */
    public SproofApi(final Config config) {
        this.config = config;
    }

    /**
     * erstellt eine Signatur aus den gegebenen Credentials
     * @param credentials die zu signierenden Credentials
     * @return Signatur der Credentials
     * @throws NoSuchAlgorithmException
     */
    public Signature signCredentials(Credentials credentials) throws NoSuchAlgorithmException {
        return Utils.sign(credentials.getAddress(), credentials);
    }

    public SproofApi on(String event, Emitter.Listener listener) throws SproofConfigException {
        if(socketIo == null)    {
            if(config.getSocket() != null)   {
                socketIo = config.getSocket();
            }
            else if(config.getUri() != null)  {
                socketIo = IO.socket(config.getUri());
            }
            else {
                throw new SproofConfigException("no socketio information was provided in the configuration, " +
                        "please specify either a socket or a uri to use this method");
            }
        }
        socketIo.on(event, listener);
        return this;
    }

    /**
     * fuert ein GET Request zur gegebenen URL, mit den gegebenen Parametern durch.
     * Die endgueltige URL setzt sich aus der URL in der gegebenen Config und dem uebergebenen Paramter zusammen
     * e.g.
     * config.uri = "https://api.sproof.io"
     * path = "storage/status"
     * zusammengesetzte URL: "https://api.sproof.io/api/v1/user/register"
     * @param path URL Pfad
     * @param params GET Parameter
     * @param callback Callback um das Resultat zu erhalten
     * @throws SproofHttpException
     */
    public void get(String path, Map<String,String> params, Callback callback) throws SproofHttpException {

        JSONObject json = getAuthObject();

        URI uri = null;
        try {
            uri = getFinalUrl(path);
        } catch (URISyntaxException e) {
            throw new SproofHttpException(e);
        }
        StringBuilder string = new StringBuilder(uri.toString());
        string.append("?");

        if(params != null) {
            for (var entry : params.entrySet()) {
                string.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }

        string.append("auth=").append(URLEncoder.encode(json.toJSONString(), StandardCharsets.UTF_8));
        uri = URI.create(string.toString());
        System.out.println("sending get request: " + uri.toString());
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder(uri)
                .header("Content-Type", "application/json")
                .GET().build();
        sendRequest(callback, client, request);
    }

    private void sendRequest(Callback callback, HttpClient client, HttpRequest request) {
        try {
            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
            if(response.statusCode() != 200) {
                callback.onResultReady(null, new SproofHttpException("HTTP error " + response.statusCode()));
            } else {
                callback.onResultReady(ByteBuffer.wrap(response.body()), null);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            callback.onResultReady(null, e);
        }
    }

    /**
     * fuert ein POST Request zur gegebenen URL, mit den gegebenen Parametern durch.
     * Die endgueltige URL setzt sich aus der URL in der gegebenen Config und dem uebergebenen Paramter zusammen
     * e.g.
     * config.uri = "https://api.sproof.io"
     * path = "user/register"
     * zusammengesetzte URL: "https://api.sproof.io/api/v1/user/register"
     * @param path URL Pfad
     * @param data POST Daten
     * @param callback Callback um das Resultat zu erhalten
     * @throws SproofHttpException
     */
    public void post(String path, JSONObject data, Callback callback) throws SproofHttpException {

        post(path, "application/json", HttpRequest.BodyPublishers.ofString(data.toString()), callback);
    }

    /**
     * laedt die gegebene Datei zum Server hoch
     * @param path Pfad zur lokalen Datei
     * @param callback Callback um das Resultat zu erhalten
     * @throws SproofHttpException
     */
    public void uploadFile(Path path, Callback callback) throws SproofHttpException {
        try {
            JSONObject credentials = getAuthObject();
            String boundary = new BigInteger(256, new Random()).toString();
            post("storage/upload", "multipart/form-data;boundary=" +
                    boundary, HttpUtil.filePublisher(path, credentials, boundary), callback);
        } catch (IOException e) {
            throw new SproofHttpException(e);
        }
    }

    private URI getFinalUrl(String path) throws URISyntaxException {
        String relativePath = VERSION_PATH;
        if(path != null) {
            relativePath += path;
        }
        if(config.getUri() != null) {
            return new URI(config.getUri().toString() + relativePath);
        } else {
            return new URI(relativePath);
        }
    }

    public void getStatus(Callback callback) throws SproofHttpException {
        this.get("storage/status", null, callback);
    }

    public void registerPremiumUser(Profile data, Callback callback) throws SproofHttpException {
        ProfileRegister register = new ProfileRegister(data);
        JSONObject obj = new JSONObject();
        JSONArray array = new JSONArray();
        array.add(register.toJsonObject());
        obj.put("events", array);
        obj.put("credentials", getAuthObject());
        this.post("user/register", obj, callback);
    }

    /**
     * hasht die gegebenen Events
     * @param events Events die gehasht werden sollen
     * @param callback Callback um das Resultat zu erhalten
     * @throws SproofHttpException
     */
    public void getHash (List<JSONObject> events, Callback callback) throws SproofHttpException {
        JSONObject data = new JSONObject();
        JSONArray eventArray = new JSONArray();
        eventArray.addAll(events);
        data.put("data", eventArray);
        try {
            data.put("credentials", getAuthObject2());
        } catch (SproofHttpException e) {
            throw new SproofHttpException(e);
        }
        this.post("storage/hash", data, callback);
    }

    /**
     * holt eine Raw-Transaction fuer die gegebenen Events vom Server
     * @param events Events fuer die die Transaktion erstellt werden soll
     * @param callback Callback um das Resultat zu erhalten
     * @throws SproofHttpException
     */
    public void getRawTransaction (List<JSONObject> events, Callback callback) throws SproofHttpException {
        JSONObject data = new JSONObject();
        JSONArray eventArray = new JSONArray();
        events.stream().forEach(e -> eventArray.add(e));
        data.put("address", this.config.getCredentials().getAddress());
        data.put("events", eventArray);
        this.post("storage/transaction", data, callback);
    }

    /**
     * Uebermittelt die signierte Transaktion und die Events an den Server zur weiteren Verarbeitung
     * @param events Events fuer die Transaktion
     * @param txSignResult Resultat von Utils.signTx(...)
     * @param callback Callback um das Resultat zu erhalten
     * @throws SproofHttpException
     */
    public void submit (List<JSONObject> events, TxSignResult txSignResult, Callback callback) throws SproofHttpException {
        JSONObject data = new JSONObject();
        JSONArray eventArray = new JSONArray();
        eventArray.addAll(events);
        data.put("signedTx", txSignResult.getSignedTx());
        data.put("transactionHash", txSignResult.getTxHash());
        data.put("events", eventArray);

        this.post("storage/submit", data, callback);
    }

    /**
     * Uebermittelt die Events an den Server zur weiteren Verarbeitung
     * @param events Events die verarbeitet werden sollen
     * @param from Adresse des Absenders
     * @param hash Hash der Daten (via getHash(...))
     * @param signature Signatur des Hashes (Utils.sign(hash, credentials))
     * @param callback Callback um das Resultat zu erhalten
     * @throws SproofHttpException
     */
    public void submitPremium(List<JSONObject> events, String from, String hash, Signature signature,Callback callback) throws SproofHttpException {
        JSONObject data = new JSONObject();
        JSONArray eventArray = new JSONArray();
        eventArray.addAll(events);
        data.put("from", from);
        data.put("hash", hash);

        JSONObject sigJson = new JSONObject();
        sigJson.put("r", signature.getR());
        sigJson.put("s", signature.getS());
        sigJson.put("v", signature.getV());
        data.put("signature", sigJson);
        data.put("events", eventArray);
        try {
            data.put("credentials", getAuthObject());
        } catch (SproofHttpException e) {
            throw new SproofHttpException(e);
        }
        this.post("storage/premium/submit", data, callback);
    }

    /**
     * liefert das zugrundeliegende Credentials Object zurueck
     * @return aktuell verwendete Credentials
     */
    public Credentials getCredentials() {
        return config.getCredentials();
    }

    public JSONObject getAuthObject() throws SproofHttpException {
        Signature signature = null;
        try {
            signature = Utils.sign(config.getCredentials().getAddress(), config.getCredentials());
        } catch (NoSuchAlgorithmException e) {
            throw new SproofHttpException(e);
        }

        JSONObject json = new JSONObject();
        json.put("address", config.getCredentials().getAddress());
        JSONObject sig = new JSONObject();
        sig.put("r", signature.getR());
        sig.put("s", signature.getS());
        sig.put("v", signature.getV());
        json.put("signature", sig);

        return json;
    }

    public JSONObject getAuthObject2() throws SproofHttpException {
        Signature signature = null;
        try {
            signature = Utils.sign(config.getCredentials().getAddress(), config.getCredentials());
        } catch (NoSuchAlgorithmException e) {
            throw new SproofHttpException(e);
        }

        JSONObject json = new JSONObject();
        json.put("address", config.getCredentials().getAddress());
        json.put("r", signature.getR());
        json.put("s", signature.getS());
        json.put("v", signature.getV().toString());

        return json;
    }

    private void post(String path, String contentType, HttpRequest.BodyPublisher publisher, Callback callback) throws SproofHttpException {
        URI uri = null;
        try {
            uri = getFinalUrl(path);
        } catch (URISyntaxException e) {
            throw new SproofHttpException(e);
        }
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder(uri)
                .header("Content-Type", contentType)
                .POST(publisher).build();
        System.out.println("sending request to: " + uri.toString());
        System.out.println("body: " + publisher.toString());
        sendRequest(callback, client, request);
    }

}
