package com.unach.api_pp_sc_rp.exception;

public class MalformedJwtException extends RuntimeException{
    public MalformedJwtException(String message) {
        super(message);
    }
}
