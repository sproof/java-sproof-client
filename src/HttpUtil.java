package at.ac.fhsalzburg.sproof;

import org.json.simple.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;

import static java.net.http.HttpRequest.BodyPublisher;
import static java.net.http.HttpRequest.BodyPublishers;

public class HttpUtil {

    /**
     * erstellt einen BodyPublisher fuer den "multipart/form-data" kodierten Upload einer Datei
     * @param file Datei die hochgeladen werden soll
     * @param credentials Zugangsdaten fuer den Server
     * @param boundary multipart/form-data boundary String (e.g. --boundary)
     * @return BodyPublisher der den Inhalt der Datei und die Zugangsdaten an den Server uebertraegt
     * @throws IOException
     */
    public static BodyPublisher filePublisher(Path file,
                                              JSONObject credentials,
                                              String boundary) throws IOException {

        var byteArrays = new ArrayList<byte[]>();
        byte[] separator = ("--" + boundary + "\r\nContent-Disposition: form-data; name=")
                .getBytes(StandardCharsets.UTF_8);


        String fileString = Base64.getEncoder().encodeToString(Files.newInputStream(file).readAllBytes());
        String credentialsString = Base64.getEncoder().encodeToString(credentials.toJSONString().getBytes(StandardCharsets.UTF_8));

        byteArrays.add(separator);
        byteArrays.add(String.format("\"file\"; filename=\"%s\"\r\n", file.getFileName()).getBytes(StandardCharsets.UTF_8));
        byteArrays.add("Content-Type: text/plain\r\n".getBytes(StandardCharsets.UTF_8));
        byteArrays.add("Content-Transfer-Encoding: base64\r\n\r\n".getBytes(StandardCharsets.UTF_8));
        byteArrays.add(fileString.getBytes(StandardCharsets.UTF_8));

        byteArrays.add("\r\n".getBytes(StandardCharsets.UTF_8));
        byteArrays.add(separator);
        byteArrays.add("\"credentials\"\r\n".getBytes(StandardCharsets.UTF_8));
        byteArrays.add("Content-Type: text/plain\r\n".getBytes(StandardCharsets.UTF_8));
        byteArrays.add("Content-Transfer-Encoding: base64\r\n\r\n".getBytes(StandardCharsets.UTF_8));
        byteArrays.add(credentialsString.getBytes(StandardCharsets.UTF_8));

        byteArrays.add("\r\n--".getBytes(StandardCharsets.UTF_8));
        byteArrays.add(boundary.getBytes(StandardCharsets.UTF_8));
        byteArrays.add("--".getBytes(StandardCharsets.UTF_8));

        return BodyPublishers.ofByteArrays(byteArrays);
    }
}
