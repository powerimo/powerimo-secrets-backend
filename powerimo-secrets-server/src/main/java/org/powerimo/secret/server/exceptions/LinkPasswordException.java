package org.powerimo.secret.server.exceptions;

public class LinkPasswordException extends RuntimeException {
    public LinkPasswordException() {
        super();
    }

    public LinkPasswordException(String message) {
        super(message);
    }

    public LinkPasswordException(String message, Throwable cause) {
        super(message, cause);
    }

    public LinkPasswordException(Throwable cause) {
        super(cause);
    }
}
