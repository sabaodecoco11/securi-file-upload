package com.securifileupload.Exception;

public class MaliciousContentException extends RuntimeException {
    public MaliciousContentException(String message){
        super(message);
    }
    public MaliciousContentException(){ super(); }
}
