package com.securifileupload.Response;

import lombok.Data;

@Data
public class ExceptionResponse {
    String message;

    public ExceptionResponse(String message){
        this.message = message;
    }
}
