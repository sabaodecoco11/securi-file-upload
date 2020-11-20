package com.securifileupload.Setup;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.capybara.clamav.ClamavClient;
import xyz.capybara.clamav.Platform;

@Configuration
public class ClamAVSConfig {
    @Value("${clamav.hostname}") String hostname;
    @Value("${clamav.port}") int port;
    @Value("${clamav.platform}") Platform platform;

    @Bean
    public ClamavClient clamavClient(){
        return new ClamavClient(hostname, port, platform);
    }
}
