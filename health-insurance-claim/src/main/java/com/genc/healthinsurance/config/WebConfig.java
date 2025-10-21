// src/main/java/com/genc/healthinsurance/config/WebConfig.java

package com.genc.healthinsurance.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // This explicitly maps any request starting with /assets/
        // to the content located in classpath:/static/assets/
        registry.addResourceHandler("/assets/**")
                .addResourceLocations("classpath:/static/assets/");
        
        // Ensure default static paths are also handled
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/uploads/**")
        .addResourceLocations("file:uploads/");
    }

}