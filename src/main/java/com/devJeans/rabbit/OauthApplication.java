package com.devJeans.rabbit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@SpringBootApplication
public class OauthApplication {

    public static void main(String[] args) {
        SpringApplication.run(OauthApplication.class, args);
    }

}
