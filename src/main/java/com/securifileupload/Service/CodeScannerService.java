package com.securifileupload.Service;

import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import xyz.capybara.clamav.ClamavClient;
import xyz.capybara.clamav.commands.scan.result.ScanResult;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;


@Service
@RequiredArgsConstructor
public class CodeScannerService {
    private final ClamavClient clamavClient;


    @Value("${files.valid-formats}")
    private String[] allowFileFormats;


    boolean hasMaliciousContent(InputStream inputStream){
        ScanResult scaningResult = clamavClient.scan(inputStream);

        return (scaningResult instanceof ScanResult.VirusFound);
    }

    boolean isContentTypeInvalid(InputStream inputStream, String filename) throws IOException {
        Tika tika = new Tika();

        String fileFormat = filename.substring(filename.lastIndexOf('.'));

        String filenameMimeType = tika.detect(filename);
        String inputStreamMimeType = tika.detect(inputStream);

        return (!Arrays.asList(allowFileFormats).contains(fileFormat) || !filenameMimeType.equals(inputStreamMimeType));
    }
}
