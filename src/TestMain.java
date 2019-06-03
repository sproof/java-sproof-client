package io.sproof;

import at.ac.fhsalzburg.sproof.model.Document;
import at.ac.fhsalzburg.sproof.model.Profile;
import at.ac.fhsalzburg.sproof.model.events.*;
import org.bitcoinj.crypto.MnemonicException;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

public class TestMain {

    private static final String CONFIG_PATH = "C:\\Users\\tkreu\\Documents\\FH\\BA2v2\\Software\\Original\\config.json";
    private static final String TESTDATA_PATH = "C:\\Users\\tkreu\\Documents\\FH\\BA2v2\\Software\\Original\\eula.1028.txt";

    private static final SproofApi.Callback printCallback = (data, ex) -> {

        if(data != null) {
            System.out.println(new String(data.array()));
        } else if(ex != null) {
            ex.printStackTrace();
        }
    };

    public static void main(String[] args) throws URISyntaxException, MnemonicException.MnemonicLengthException, UnreadableWalletException, IOException, ParseException, SproofHttpException {

//        getUser();
        getState();
//        createPremiumProfile();
//        updateProfile();
//        uploadFile();
//        getEvents();
//        revokeProfile();
//        registerPremiumUser();
//        Credentials neu = Utils.getCredentials();
//        System.out.println(neu);
    }

    private static void revokeProfile() throws URISyntaxException, UnreadableWalletException, ParseException, IOException, SproofHttpException {
        Sproof sproof = new Sproof(CONFIG_PATH);
        ProfileRevoke profile = new ProfileRevoke("test2");
        sproof.addEvent(profile);
        sproof.commitPremium(printCallback);
    }

    private static void getState() throws URISyntaxException, UnreadableWalletException, ParseException, IOException, SproofHttpException {
        Sproof sproof = new Sproof(CONFIG_PATH);
        sproof.getState(printCallback);
    }

    private static void registerPremiumUser() throws URISyntaxException, UnreadableWalletException, ParseException, IOException, SproofHttpException {
        Sproof sproof = new Sproof(CONFIG_PATH);
        ProfileConfirm profile = new ProfileConfirm("0xed1182752ce034f9fd120f95887e5fc2d2bc76a8", true);
        //profile.setName("MaxMustermann");
        sproof.addEvent(profile);
        //profile.setProfileText("commitPremium test");
        sproof.commitPremium(printCallback);
        //sproof.registerPremiumUser(profile, printCallback);
    }

    private static void getEvents() throws URISyntaxException, UnreadableWalletException, ParseException, IOException, SproofHttpException {
        Sproof sproof = new Sproof(CONFIG_PATH);
        sproof.getEvents(null, printCallback);
    }

    private static void uploadFile() throws URISyntaxException, UnreadableWalletException, ParseException, IOException, SproofHttpException {
        Sproof sproof = new Sproof(CONFIG_PATH);
        sproof.getApi().uploadFile(Paths.get(TESTDATA_PATH), (data, ex) -> {
            if(data != null) {
                JSONParser parser = new JSONParser();
                try {
                    Object result = parser.parse(new String(data.array(), StandardCharsets.UTF_8));
                    JSONObject json = JsonUtil.verifyJsonObject(result);
                    if(json != null) {
                        JSONObject res = JsonUtil.verifyJsonObject(json.get("result"));
                        if(res != null) {
                            String hashToRegister = JsonUtil.verifyString(res.get("hashToRegister"));

                            Document doc = new Document();
                            doc.setDocumentHash(hashToRegister);
                            doc.setName("test3");
                            doc.setValidFrom(System.currentTimeMillis() / 1000);
                            doc.setValidUntil(4080000000L);

                            sproof.registerDocument(new DocumentRegister(doc));
                            sproof.commitPremium(printCallback);
                        }
                    }
                } catch (ParseException | SproofHttpException e) {
                    e.printStackTrace();
                }
            } else if(ex != null) {
                ex.printStackTrace();
            }

        });
    }

    private static void getUser() throws URISyntaxException, UnreadableWalletException, ParseException, IOException, SproofHttpException {
        Sproof sproof = new Sproof(CONFIG_PATH);

        sproof.getUser(printCallback);
    }

    private static void createPremiumProfile() throws URISyntaxException, UnreadableWalletException, ParseException, IOException, SproofHttpException {
        Sproof sproof = new Sproof(CONFIG_PATH);

        Profile profile = new Profile();
        profile.setName("Max Mustermann");
//        profile.setProfileText("test");

        sproof.registerProfile(new ProfileRegister(profile));

        sproof.commitPremium(printCallback);

    }

    private static void updateProfile() throws URISyntaxException, UnreadableWalletException, ParseException, IOException, SproofHttpException {
        Sproof sproof = new Sproof(CONFIG_PATH);

        Profile profile = new Profile();
        //profile.setName("Mustermann Max");
        profile.setProfileText("commit classic test");
        ProfileUpdate update = new ProfileUpdate(profile);

        sproof.updateProfile(update);
        sproof.commit(printCallback);
    }

}
