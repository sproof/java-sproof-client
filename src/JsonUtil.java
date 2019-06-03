package io.sproof;

import org.json.simple.JSONObject;

import java.math.BigInteger;

public class JsonUtil {

    /**
     * ueberprueft ob es sich bei dem gegebenen Object um ein JSONObject handelt und liefert es gecastet zurueck
     * @param obj das zu ueberpruefende Object
     * @return zu JSONObject gecastetes Object, oder null, wenn es sich nicht um ein JSONObject handelt
     */
    public static JSONObject verifyJsonObject(Object obj) {
        if(!(obj instanceof JSONObject)) {
            return null;
        }
        return (JSONObject) obj;
    }

    /**
     * ueberprueft ob es sich bei dem gegebenen Object um einen String handelt und liefert ihn gecastet zurueck
     * @param obj das zu ueberpruefende Object
     * @return zu String gecastetes Object
     * @throws SproofHttpException wenn es sich nicht um einen String handelt
     */
    public static String verifyString(Object obj) throws SproofHttpException {
        if(!(obj instanceof String)) {
            throw new SproofHttpException(obj + " is not a valid String value");
        }
        return (String) obj;
    }

    /**
     * ueberprueft ob es sich bei dem gegebenen Object um ein Long Objekt handelt und liefert es gecastet zurueck
     * @param obj das zu ueberpruefende Object
     * @return zu Long gecastetes Object
     * @throws SproofHttpException wenn es sich nicht um ein Long Objekt handelt
     */
    public static Long verifyLong(Object obj) throws SproofHttpException {
        if(!(obj instanceof Long)) {
            throw new SproofHttpException(obj + " is not a valid Long value");
        }
        return (Long) obj;
    }

    /**
     * ueberprueft ob es sich bei dem gegebenen Object um einen String handelt und liefert ihn gecastet und ohne '0x' prefix zurueck
     * @param obj das zu ueberpruefende Object
     * @return zu String gecastetes Object ohne '0x' prefix
     * @throws SproofHttpException wenn es sich nicht um einen String handelt
     */
    public static byte[] verifyStringStripPrefix(Object obj) throws SproofHttpException {
        if(!(obj instanceof String)) {
            throw new SproofHttpException(obj + " is not a valid String value");
        }
        String str = (String) obj;
        if(str.startsWith("0x")) {
            return new BigInteger(str.substring(2), 16).toByteArray();
        }
        return new BigInteger(str, 16).toByteArray();
    }
}
