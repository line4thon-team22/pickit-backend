package com.pickit.backend.config;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 프론트가 다른 주소에서 API를 호출할 수 있게 허용한다.
 * 허용 주소는 환경변수 CORS_ALLOWED_ORIGINS(쉼표로 구분)로 받고, 없으면 로컬 개발 주소를 쓴다.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private final String[] allowedOrigins;

    public CorsConfig(@Value("${pickit.cors.allowed-origins}") String allowedOrigins) {
        this.allowedOrigins = Arrays.stream(allowedOrigins.split(","))
                .map(String::strip)
                .filter(origin -> !origin.isEmpty())
                .toArray(String[]::new);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/profiles/**")
                .addResourceLocations("file:uploads/profiles/");
        registry.addResourceHandler("/images/posts/**")
                .addResourceLocations("file:uploads/posts/");
        registry.addResourceHandler("/images/chats/**")
                .addResourceLocations("file:uploads/chats/");
    }
}
