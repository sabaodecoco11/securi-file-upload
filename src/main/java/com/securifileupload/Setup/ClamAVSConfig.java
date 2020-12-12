package com.securifileupload.Setup;

import org.apache.tika.utils.SystemUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.capybara.clamav.ClamavClient;
import xyz.capybara.clamav.Platform;

@Configuration
public class ClamAVSConfig {
    @Value("${clamav.hostname}") String hostname;
    @Value("${clamav.port}") int port;

    @Bean
    public ClamavClient clamavClient(){
        return new ClamavClient(hostname, port, (SystemUtils.IS_OS_WINDOWS ? Platform.WINDOWS : Platform.UNIX));
    }
}
