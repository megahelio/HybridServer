package es.uvigo.esei.dai.hybridserver.Controllers;

public class InvalidXSDException extends Exception {
    private static final long serialVersionUID = 1L;

    public InvalidXSDException() {
    }

    public InvalidXSDException(String message) {
        super(message);
    }

    public InvalidXSDException(Throwable cause) {
        super(cause);
    }

    public InvalidXSDException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidXSDException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}