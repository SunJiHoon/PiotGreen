package com.piotgreen.piotgreen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class PiotgreenApplication {

	public static void main(String[] args) {
		SpringApplication.run(PiotgreenApplication.class, args);
	}
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedOrigins("http://localhost:8080", "http://192.168.59.194/",
								"http://main.putiez.com")
						.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTION")
						.allowCredentials(true).allowedHeaders("*");//;;

				////.allowedHeaders("*");;
			}
		};
	}

}
