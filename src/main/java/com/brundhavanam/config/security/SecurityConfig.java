package com.brundhavanam.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    // Custom JWT filter that validates token on every request
    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    public SecurityConfig(JwtAuthorizationFilter jwtAuthorizationFilter) {
        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            // Disable CSRF because we are building stateless REST APIs (JWT-based)
            .csrf(csrf -> csrf.disable())

            // Authorization rules: allow public endpoints, protect others
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/v1/users/otp/**",   // OTP APIs should be public
                    "/api/v1/users/**",          // user creation (temporary public)

                    "/api/v1/products/**",    // temporary public for development
                    "/api/v1/admin/**",       // temp (later secure with ADMIN role)

                    "/swagger-ui/**",         // Swagger UI public
                    "/v3/api-docs/**"         // Swagger docs public
                ).permitAll()
                .anyRequest().authenticated() // everything else requires JWT
            )

            // Attach JWT filter before Spring's default authentication filter
            .addFilterBefore(
                jwtAuthorizationFilter,
                org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    // Bean used for hashing passwords (BCrypt is secure and industry standard)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}



//package com.brundhavanam.config.security;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//
////STEP 4 – Update Security Configuration (SecurityConfig)
////
////Purpose:
////
////Allow public endpoints
////
////Protect others
////
////Attach JWT filter
//
//@Configuration
//public class SecurityConfig {
//
//    private final JwtAuthorizationFilter jwtAuthorizationFilter;
//
//    public SecurityConfig(JwtAuthorizationFilter jwtAuthorizationFilter) {
//        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
//    }
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//
//        http
//            .csrf(csrf -> csrf.disable())
//            .authorizeHttpRequests(auth -> auth
//                .requestMatchers(
//                    "/api/v1/users/otp/**",
//                    "/api/v1/users",
//                    
//                    "/api/v1/products/**",
//                    "/api/v1/admin/**",//temp.
//                    
//                    "/swagger-ui/**",
//                    "/v3/api-docs/**"
//                ).permitAll()
//                .anyRequest().authenticated()
//            )
//            .addFilterBefore(
//                jwtAuthorizationFilter,
//                org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class
//            );
//
//        return http.build();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//}
