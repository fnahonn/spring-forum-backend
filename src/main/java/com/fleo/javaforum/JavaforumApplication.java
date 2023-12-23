package com.fleo.javaforum;

import com.fleo.javaforum.config.StorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class JavaforumApplication {

	public static void main(String[] args) {
		SpringApplication.run(JavaforumApplication.class, args);
	}

}
