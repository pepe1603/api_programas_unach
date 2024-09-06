package com.unach.api_pp_sc_rp.exception;

public class ExportException extends RuntimeException{
    public ExportException(String message) {
        super(message);
    }

    public ExportException(String message, Throwable cause) {
        super(message, cause);
    }
}
