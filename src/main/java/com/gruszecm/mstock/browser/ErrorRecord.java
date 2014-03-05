package com.gruszecm.mstock.browser;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

public class ErrorRecord implements Serializable {
    public enum Type {
        FATAL, WARN
    }

    private static final long serialVersionUID = -2067925726555800200L;
    private String message;
    private Throwable exception;
    private Type type;

    private ErrorRecord(Type type, String msg) {
        this.type = type;
        this.message = msg;
    }

    public Throwable getException() {
        return exception;
    }

    public String getMessage() {
        return message;
    }

    public static ErrorRecord fatal(Throwable throwable) {
        ErrorRecord r = new ErrorRecord(Type.FATAL, throwable.toString());
        r.exception = throwable;
        return r;
    }

    public static ErrorRecord warn(String msg) {
        return new ErrorRecord(Type.WARN, msg);
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        StringWriter sw = new StringWriter();
        sw.append(type.name()).append(":  ");
        sw.append(message);
        return sw.toString();
    }

    public CharSequence getStacktraceAsString() {
        if (exception == null) {
            return "";
        } else {
            StringWriter sw = new StringWriter();
            sw.append(exception.toString()).append('\n');
            exception.printStackTrace(new PrintWriter(sw));
            return sw.toString();
        }
    }


}
