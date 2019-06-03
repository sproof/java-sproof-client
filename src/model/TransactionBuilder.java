package io.sproof.model;

import at.ac.fhsalzburg.sproof.SproofHttpException;
import org.ethereum.core.Transaction;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import static at.ac.fhsalzburg.sproof.JsonUtil.*;

public class TransactionBuilder {

    /**
     * Wandelt ein JSON Objekt in ein RawTransactionResult um
     * @param json das umzuwandelnde JSON Objekt
     * @return Java-repraesentation der Transaktion
     * @throws ParseException
     * @throws SproofHttpException
     */
    public static RawTransactionResult fromRawJson(String json) throws ParseException, SproofHttpException {
        JSONParser parser = new JSONParser();
        JSONObject resultJson = verifyJsonObject(parser.parse(json));
        JSONObject result = verifyJsonObject(resultJson.get("result"));
        if(result == null) {
            String error = verifyString(resultJson.get("error"));
            throw new SproofHttpException(error);
        }

        RawTransactionResult rTxResult = new RawTransactionResult();
        String hash = verifyString(result.get("hash"));
        rTxResult.setHash(hash);

        JSONObject rawTransaction = verifyJsonObject(result.get("rawTransaction"));
        byte[] nonce = verifyStringStripPrefix(rawTransaction.get("nonce"));
        byte[] gasPrice = verifyStringStripPrefix(rawTransaction.get("gasPrice"));
        byte[] gasLimit = verifyStringStripPrefix(rawTransaction.get("gasLimit"));
        byte[] to = verifyStringStripPrefix(rawTransaction.get("to"));
        byte[] value = verifyStringStripPrefix(rawTransaction.get("value"));
        byte[] data = verifyStringStripPrefix(rawTransaction.get("data"));
        Long chainId = verifyLong(rawTransaction.get("chainId"));

        Transaction tx = new Transaction(nonce, gasPrice, gasLimit, to, value, data, new byte[]{}, new byte[]{}, (byte) 0, chainId.intValue());
        rTxResult.setRawTransaction(tx);
        return rTxResult;
    }


}
