package org.powerimo.secret.server.exceptions;

public class InvalidConfigPropertyException extends Exception {

    public InvalidConfigPropertyException(String message) {
        super(message);
    }

    public InvalidConfigPropertyException(String message, Throwable cause) {
        super(message, cause);
    }
}
