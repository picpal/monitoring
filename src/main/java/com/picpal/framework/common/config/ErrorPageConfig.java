package com.picpal.framework.common.config;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Configuration
public class ErrorPageConfig {

    @Bean
    public ErrorPageRegistrar errorPageRegistrar() {
        return new CustomErrorPageRegistrar();
    }

    private static class CustomErrorPageRegistrar implements ErrorPageRegistrar {
        @Override
        public void registerErrorPages(ErrorPageRegistry registry) {
            // 404 Not Found 페이지
            registry.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/error"));
            
            // 500 Internal Server Error 페이지
            registry.addErrorPages(new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error"));
            
            // 401 Unauthorized 페이지
            registry.addErrorPages(new ErrorPage(HttpStatus.UNAUTHORIZED, "/error"));
            
            // 403 Forbidden 페이지
            registry.addErrorPages(new ErrorPage(HttpStatus.FORBIDDEN, "/error"));
        }
    }
}