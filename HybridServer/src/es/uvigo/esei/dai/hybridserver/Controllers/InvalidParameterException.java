package es.uvigo.esei.dai.hybridserver.Controllers;

public class InvalidParameterException extends Exception {
    private static final long serialVersionUID = 1L;

    public InvalidParameterException() {
    }

    public InvalidParameterException(String message) {
        super(message);
    }

    public InvalidParameterException(Throwable cause) {
        super(cause);
    }

    public InvalidParameterException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidParameterException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}