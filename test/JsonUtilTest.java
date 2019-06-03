package at.ac.fhsalzburg.sproof;

import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.math.BigInteger;
import static org.junit.Assert.*;

public class JsonUtilTest {

    private JSONObject testObject;
    private JSONObject embeddedObject;

    private String key1Value = "value1";
    private Long longKeyValue = Long.valueOf(1234L);
    private String hexWithPrefix = "0x23424324325";
    private String hex = "45737564645a";

    @Before
    public void setUp() throws Exception {
        testObject = new JSONObject();
        testObject.put("key1", key1Value);
        testObject.put("key2", longKeyValue);
        testObject.put("hexWithPrefix", hexWithPrefix);
        testObject.put("hex", hex);

        embeddedObject = new JSONObject();
        testObject.put("object", embeddedObject);
    }

    @After
    public void tearDown() throws Exception {
        testObject = null;
    }

    @Test
    public void verifyJsonObject() {
        JSONObject result = JsonUtil.verifyJsonObject(testObject.get("object"));
        assertNotNull(result);
        assertTrue(result == embeddedObject);
        result = JsonUtil.verifyJsonObject("nonexisting");
        assertNull(result);
    }

    @Test
    public void verifyString() throws SproofHttpException {
        String result = JsonUtil.verifyString(testObject.get("key1"));
        assertNotNull(result);
        assertEquals(key1Value, result);
    }

    @Test(expected = SproofHttpException.class)
    public void verifyString2() throws SproofHttpException {
        String result = JsonUtil.verifyString(testObject.get("nonexisting"));
    }

    @Test
    public void verifyLong() throws SproofHttpException {
        Long result = JsonUtil.verifyLong(testObject.get("key2"));
        assertNotNull(result);
        assertEquals(longKeyValue, result);
    }

    @Test(expected = SproofHttpException.class)
    public void verifyLong2() throws SproofHttpException {
        Long result = JsonUtil.verifyLong(testObject.get("nonexisting"));
    }

    @Test
    public void verifyStringStripPrefix() throws SproofHttpException {
        byte[] result = JsonUtil.verifyStringStripPrefix(testObject.get("hexWithPrefix"));
        assertNotNull(result);

        BigInteger expected = new BigInteger(hexWithPrefix.substring(2), 16);
        assertArrayEquals(expected.toByteArray(), result);
    }

    @Test(expected = SproofHttpException.class)
    public void verifyStringStripPrefix2() throws SproofHttpException {
        byte[] result = JsonUtil.verifyStringStripPrefix(testObject.get("nonexising"));
    }

    @Test
    public void verifyStringStripPrefix3() throws SproofHttpException {
        byte[] result = JsonUtil.verifyStringStripPrefix(testObject.get("hex"));
        assertNotNull(result);

        BigInteger expected = new BigInteger(hex, 16);
        assertArrayEquals(expected.toByteArray(), result);
    }
}