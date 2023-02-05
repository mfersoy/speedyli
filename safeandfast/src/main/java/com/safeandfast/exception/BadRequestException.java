package com.safeandfast.exception;

//@ResponseStatus(HttpStatus.NOT_FOUND)
public class BadRequestException extends RuntimeException{
    /*
    *
    *
    */
    private static final long serialVersionUID= 1L;

    public BadRequestException(String message){
        super(message);
    }
}
