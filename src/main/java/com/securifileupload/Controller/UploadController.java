package com.securifileupload.Controller;

import com.securifileupload.Exception.MaliciousContentException;
import com.securifileupload.Response.Response;
import com.securifileupload.Service.UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class UploadController {
    private final UploadService uploadService;

    @PostMapping("/upload")
    @CrossOrigin(origins = "http://localhost:8082")
    public Response uploadFile(@RequestParam MultipartFile file) throws MaliciousContentException {
        uploadService.sanitize(file);
        return new Response();
    }
}
