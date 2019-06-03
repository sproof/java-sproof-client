package at.ac.fhsalzburg.sproof;

public class SproofHttpException extends Exception {

    public SproofHttpException(String reason) {
        super(reason);
    }

    public SproofHttpException(Exception e) {
        super(e);
    }

}
