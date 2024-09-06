package com.unach.api_pp_sc_rp.exception;

public class UnsupportedJwtException extends RuntimeException{
    public UnsupportedJwtException(String message) {
        super(message);
    }
}
