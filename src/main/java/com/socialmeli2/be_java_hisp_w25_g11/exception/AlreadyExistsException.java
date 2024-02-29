package com.socialmeli2.be_java_hisp_w25_g11.exception;

public class AlreadyExistsException extends RuntimeException {
    public AlreadyExistsException(String message){
        super(message);
    }
}
