package io.sproof;

import org.bitcoinj.crypto.MnemonicException;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;

public class SproofApiTest {

    private SproofApi api;

    @Before
    public void setUp() throws Exception {
        String seed = "upon shiver book bulb park label orchard erupt shell acid survey solve";
        Credentials credentials = Utils.restoreCredentials(seed);
        Config config = new Config(new URI("api.sproof.io"), null, credentials);
        api = new SproofApi(config);
    }



    @Test
    public void signCredentials() throws UnreadableWalletException, NoSuchAlgorithmException, URISyntaxException, MnemonicException.MnemonicLengthException {
        Credentials cred = Utils.getCredentials();
        Signature sig = api.signCredentials(cred);
        assertTrue(Utils.verify(cred.getAddress(), sig, cred.getPublicKey()));
    }

}