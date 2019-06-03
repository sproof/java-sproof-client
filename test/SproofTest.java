package io.sproof;

import at.ac.fhsalzburg.sproof.model.Document;
import at.ac.fhsalzburg.sproof.model.Profile;
import at.ac.fhsalzburg.sproof.model.events.*;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SproofTest {

    static void assertJsonContains(JSONObject obj, String key, Object value) {

        if(key.contains(".")) {
            String firstKey;
            int indexOfDot = key.indexOf('.');
            firstKey = key.substring(0, indexOfDot);
            key = key.substring(indexOfDot + 1);
            Object parent = obj.get(firstKey);
            assertNotNull(parent);
            assertTrue(parent instanceof JSONObject);
            assertJsonContains((JSONObject)parent, key, value);
        } else {
            Object v = obj.get(key);
            assertEquals(value, v);
        }
    }

    private Sproof sproof;
    @Mock
    private SproofApi mockedApi;

    @Before
    public void setUp() throws Exception {
        sproof = new Sproof(mockedApi);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void registerProfile() {
        Profile profile = new Profile();
        profile.setName("testy mctestson");
        profile.setProfileText("testtext");
        profile.setImage("imagebytes");
        profile.setWebsite("testwebsite.com");
        ProfileRegister register = new ProfileRegister(profile);
        sproof.registerProfile(register);
        List<JSONObject> events = sproof.getEvents();
        assertEquals(1, events.size());

        JSONObject event = events.get(0);
        assertJsonContains(event, "eventType", "PROFILE_REGISTER");
        assertJsonContains(event, "data.name", "testy mctestson");
        assertJsonContains(event, "data.profileText", "testtext");
        assertJsonContains(event, "data.image", "imagebytes");
        assertJsonContains(event, "data.website", "testwebsite.com");
    }

    @Test
    public void updateProfile() {
        Profile profile = new Profile();
        profile.setName("testy testson");
        ProfileUpdate update = new ProfileUpdate(profile);
        sproof.updateProfile(update);
        List<JSONObject> events = sproof.getEvents();
        assertEquals(1, events.size());

        JSONObject event = events.get(0);
        assertJsonContains(event, "eventType", "PROFILE_UPDATE");
        assertJsonContains(event, "data.name", "testy testson");
    }

    @Test
    public void revokeProfile() {
        ProfileRevoke revoke = new ProfileRevoke("testing");
        sproof.revokeProfile(revoke);
        List<JSONObject> events = sproof.getEvents();
        assertEquals(1, events.size());

        JSONObject event = events.get(0);
        assertJsonContains(event, "eventType", "PROFILE_REVOKE");
        assertJsonContains(event, "data.reason", "testing");
    }

    @Test
    public void registerDocument() {
        Document document = new Document();
        document.setValidFrom(0L);
        document.setValidUntil(2L);
        document.setName("some document");
        document.setDocumentHash("deadbeef");
        DocumentRegister register = new DocumentRegister(document);

        sproof.registerDocument(register);
        List<JSONObject> events = sproof.getEvents();
        assertEquals(1, events.size());

        JSONObject event = events.get(0);
        assertJsonContains(event, "eventType", "DOCUMENT_REGISTER");
        assertJsonContains(event, "data.validFrom", 0L);
        assertJsonContains(event, "data.validUntil", 2L);
        assertJsonContains(event, "data.name", "some document");
        assertJsonContains(event, "data.documentHash", "deadbeef");
    }

    @Test
    public void revokeDocument() {
        DocumentRevoke revoke = new DocumentRevoke("badeaffe", "somereason");
        sproof.revokeDocument(revoke);
        List<JSONObject> events = sproof.getEvents();
        assertEquals(1, events.size());

        JSONObject event = events.get(0);
        assertJsonContains(event, "eventType", "DOCUMENT_REVOKE");
        assertJsonContains(event, "data.documentHash", "badeaffe");
        assertJsonContains(event, "data.reason", "somereason");
    }

    @Test
    public void addDocumentReceiver() {
        DocumentReceiverAdd receiverAdd = new DocumentReceiverAdd("receiverid", "decafbad");
        sproof.addDocumentReceiver(receiverAdd);
        List<JSONObject> events = sproof.getEvents();
        assertEquals(1, events.size());

        JSONObject event = events.get(0);
        assertJsonContains(event, "eventType", "DOCUMENT_RECEIVER_ADD");
        assertJsonContains(event, "data.receiverId", "receiverid");
        assertJsonContains(event, "data.documentHash", "decafbad");
    }

    @Test
    public void revokeDocumentReceiver() {
        DocumentReceiverRevoke receiverRevoke = new DocumentReceiverRevoke("receiverid", "foobar");
        sproof.revokeDocumentReceiver(receiverRevoke);
        List<JSONObject> events = sproof.getEvents();
        assertEquals(1, events.size());

        JSONObject event = events.get(0);
        assertJsonContains(event, "eventType", "DOCUMENT_RECEIVER_REVOKE");
        assertJsonContains(event, "data.receiverId", "receiverid");
        assertJsonContains(event, "data.reason", "foobar");
    }

    @Test
    public void addEvent() {
        DocumentReceiverRevoke receiverRevoke = new DocumentReceiverRevoke("receiverid", "foobar");
        DocumentReceiverAdd receiverAdd = new DocumentReceiverAdd("receiverid", "decafbad");
        sproof.addEvent(receiverAdd);
        sproof.addEvent(receiverRevoke);
        assertEquals(2, sproof.getEvents().size());
    }

    @Test
    public void registerPremiumUser() throws SproofHttpException {
        doNothing().when(mockedApi).registerPremiumUser(any(), any());
        sproof.registerPremiumUser(null, null);
        verify(mockedApi, times(1)).registerPremiumUser(any(), any());
    }

    @Test
    public void getUser() throws SproofHttpException {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        doNothing().when(mockedApi).get(captor.capture(), any(), any());
        sproof.getUser(null);
        verify(mockedApi, times(1)).get(any(), any(), any());
        assertEquals("user", captor.getValue());
    }

    @Test
    public void getState() throws SproofHttpException {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        doNothing().when(mockedApi).get(captor.capture(), any(), any());
        sproof.getState(null);
        verify(mockedApi, times(1)).get(any(), any(), any());
        assertEquals("state", captor.getValue());
    }

    @Test
    public void getEvents() throws SproofHttpException {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        doNothing().when(mockedApi).get(captor.capture(), any(), any());
        sproof.getEvents(null, null);
        verify(mockedApi, times(1)).get(any(), any(), any());
        assertEquals("events", captor.getValue());
    }

    @Test
    public void getTransactions() throws SproofHttpException {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        doNothing().when(mockedApi).get(captor.capture(), any(), any());
        sproof.getTransactions(null, null);
        verify(mockedApi, times(1)).get(any(), any(), any());
        assertEquals("transactions", captor.getValue());
    }

    @Test
    public void getProfiles() throws SproofHttpException {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        doNothing().when(mockedApi).get(captor.capture(), any(), any());
        sproof.getProfiles(null, null);
        verify(mockedApi, times(1)).get(any(), any(), any());
        assertEquals("profiles", captor.getValue());
    }

    @Test
    public void getReceivers() throws SproofHttpException {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        doNothing().when(mockedApi).get(captor.capture(), any(), any());
        sproof.getReceivers(null, null);
        verify(mockedApi, times(1)).get(any(), any(), any());
        assertEquals("receivers", captor.getValue());
    }

    @Test
    public void getRegistrations() throws SproofHttpException {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        doNothing().when(mockedApi).get(captor.capture(), any(), any());
        sproof.getRegistrations(null, null);
        verify(mockedApi, times(1)).get(any(), any(), any());
        assertEquals("registrations", captor.getValue());
    }

    @Test
    public void getDocuments() throws SproofHttpException {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        doNothing().when(mockedApi).get(captor.capture(), any(), any());
        sproof.getDocuments(null, null);
        verify(mockedApi, times(1)).get(any(), any(), any());
        assertEquals("documents", captor.getValue());
    }

    @Test
    public void getValidation() throws SproofHttpException {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        doNothing().when(mockedApi).get(captor.capture(), any(), any());
        sproof.getValidation(null, null);
        verify(mockedApi, times(1)).get(any(), any(), any());
        assertEquals("verification", captor.getValue());
    }

}