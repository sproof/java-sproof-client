package at.ac.fhsalzburg.sproof;

import io.socket.client.Socket;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Containerklasse fuer eine Sproof Konfiguration
 */
public class Config {

    /**
     * laedt eine Sproof Konfiguration aus der angegebenen Datei
     * @param file eine .json Datei mit einer Sproof Config
     * @return die, aus der Datei geladene Sproof Config
     * @throws URISyntaxException
     * @throws UnreadableWalletException
     * @throws ParseException
     * @throws IOException
     */
    public static Config fromJson(Path file) throws URISyntaxException, UnreadableWalletException, ParseException, IOException {
        String configContent = Files.readString(file);
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(configContent);
        if(!(obj instanceof JSONObject)) {
            throw new IOException(String.format("no valid config in file %s", file.toString()));
        }
        JSONObject jsonObject = (JSONObject) obj;
        obj = jsonObject.get("uri");
        if(!(obj instanceof String)) {
            throw new IOException(String.format("no valid uri in config-file %s", file.toString()));
        }
        String uriString = (String) obj;

        obj = jsonObject.get("credentials");
        if(!(obj instanceof JSONObject)) {
            throw new IOException(String.format("no valid credentials in config-file %s", file.toString()));
        }
        JSONObject credentials = (JSONObject) obj;

        obj = credentials.get("sproofCode");
        if(!(obj instanceof String)) {
            throw new IOException(String.format("no valid sproofCode in credentials - %s", file.toString()));
        }
        String sproofCode = (String) obj;

        URI uri = new URI(uriString);
        Credentials creds = Utils.restoreCredentials(sproofCode);
        return new Config(uri, null, creds);
    }

    private URI uri = null;
    private Socket socket = null;
    private Credentials credentials;

    /**
     * erstellt eine neue Config mit den gegebenen Parametern
     * @param uri URI des Sproof Servers
     * @param socket SocketIO Socket (oder null, wenn SocketIO nicht verwendet wird)
     * @param credentials Zugangsdaten fuer den Sproof Server
     */
    public Config(URI uri, Socket socket, Credentials credentials) {
        this.uri = uri;
        this.socket = socket;
        this.credentials = credentials;
    }

    public URI getUri() {
        return uri;
    }

    public Socket getSocket() {
        return socket;
    }

    public Credentials getCredentials() {
        return credentials;
    }

}
