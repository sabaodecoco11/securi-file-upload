package com.securifileupload.Service;

import com.securifileupload.Exception.InvalidFileFormatExtension;
import com.securifileupload.Exception.MaliciousContentException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UploadService {
    private final CodeScannerService codeScannerService;

    public void sanitize(@NotEmpty MultipartFile multipartFile) throws MaliciousContentException, InvalidFileFormatExtension{
        try {
            if(codeScannerService.isContentTypeInvalid(multipartFile.getInputStream(),
                    Objects.requireNonNull(multipartFile.getOriginalFilename())))
                throw new InvalidFileFormatExtension();

            if(codeScannerService.hasMaliciousContent(multipartFile.getInputStream()))
                throw new MaliciousContentException();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
