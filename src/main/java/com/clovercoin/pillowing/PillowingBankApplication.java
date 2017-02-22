package com.clovercoin.pillowing;

import lombok.extern.java.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
@Log
public class PillowingBankApplication {

	public static void main(String[] args) {
		SpringApplication.run(PillowingBankApplication.class, args);
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		log.info("Configuring CORS");
		return new WebMvcConfigurerAdapter() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**");
			}
		};
	}
}
