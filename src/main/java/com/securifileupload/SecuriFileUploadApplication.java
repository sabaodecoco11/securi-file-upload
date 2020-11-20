package com.securifileupload;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import xyz.capybara.clamav.ClamavClient;
import xyz.capybara.clamav.Platform;

@SpringBootApplication
public class SecuriFileUploadApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecuriFileUploadApplication.class, args);

	}

}
