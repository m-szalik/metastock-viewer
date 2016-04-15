package org.jsoftware.mstock.metastock;

/**
 * Illegal input data
 */
public class IllegalDataException extends RuntimeException {
    public IllegalDataException(String message) {
        super(message);
    }

    public IllegalDataException(String message, Exception e) {
        super(message, e);
    }
}
