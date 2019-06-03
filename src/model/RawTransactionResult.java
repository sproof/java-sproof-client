package at.ac.fhsalzburg.sproof.model;

import lombok.Data;
import org.ethereum.core.Transaction;

/**
 * Container-Klasse fuer die Daten eines getRawTransaction() Requests
 */
@Data
public class RawTransactionResult {

    private String hash;
    private Transaction RawTransaction;

}
