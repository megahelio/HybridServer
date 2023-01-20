package es.uvigo.esei.dai.hybridserver.controllers.exceptions;

public class MissedParameterException extends Exception {
    private static final long serialVersionUID = 1L;

    public MissedParameterException() {
    }

    public MissedParameterException(String message) {
        super(message);
    }

    public MissedParameterException(Throwable cause) {
        super(cause);
    }

    public MissedParameterException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissedParameterException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}