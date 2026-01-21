package com.brundhavanam.config.cloudinary;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Cloudinary configuration.
 * Creates a Cloudinary bean which can be injected anywhere.
 */
@Configuration
public class CloudinaryConfig {

    @Value("${cloudinary.cloud_name}")
    private String cloudName;

    @Value("${cloudinary.api_key}")
    private String apiKey;

    @Value("${cloudinary.api_secret}")
    private String apiSecret;

    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> values = new HashMap<>();
        values.put("cloud_name", cloudName);
        values.put("api_key", apiKey);
        values.put("api_secret", apiSecret);
        return new Cloudinary(values);
    }
}
