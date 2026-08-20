package com.major.CampuSphere;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableConfigurationProperties
public class CampuSphereApplication {

	public static void main(String[] args) {
		SpringApplication.run(CampuSphereApplication.class, args);
	}
}
