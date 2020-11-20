package com.securifileupload.Exception.Handler;

import com.securifileupload.Exception.InvalidFileFormatExtension;
import com.securifileupload.Exception.MaliciousContentException;
import com.securifileupload.Response.ExceptionResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionHandler extends ResponseEntityExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(
            value = {MaliciousContentException.class})
    ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex,
                new ExceptionResponse("Falha ao enviar arquivo..."), new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(
            value = {InvalidFileFormatExtension.class})
    ResponseEntity<Object> handleConflicts(InvalidFileFormatExtension ex, WebRequest request) {
        return handleExceptionInternal(ex, new ExceptionResponse("Arquivo com formato inválido..."),
                new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

}
