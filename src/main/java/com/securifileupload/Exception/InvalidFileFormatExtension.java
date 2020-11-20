package com.securifileupload.Exception;

public class InvalidFileFormatExtension extends RuntimeException {
    public InvalidFileFormatExtension() { super();
    }
    public InvalidFileFormatExtension(String message) {
        super(message);
    }
}
