package com.brundhavanam.config.security;

import com.brundhavanam.config.jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT Authorization Filter
 *
 * Runs on every request.
 * - Reads JWT token from Authorization header (Bearer token)
 * - Validates token
 * - Extracts user identity (mobile)
 * - Sets authenticated user into Spring SecurityContext
 */
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthorizationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Read Authorization header
        String header = request.getHeader("Authorization");

        // Continue only if header contains "Bearer <token>"
        if (header != null && header.startsWith("Bearer ")) {

            // Extract token string (remove "Bearer ")
            String token = header.substring(7);

            // Validate token (signature + expiry)
            if (jwtUtil.validateToken(token)) {

                // Extract user identity (mobile number) from token subject
                String mobile = jwtUtil.extractSubject(token);

                // Create Authentication object for Spring Security
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                mobile,                  // principal (logged-in user)
                                null,                    // no password required
                                Collections.emptyList()  // authorities/roles can be added later
                        );

                // Attach additional request details (IP, session, etc.)
                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Set authentication into SecurityContext (marks user as authenticated)
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // Continue request execution to next filter / controller
        filterChain.doFilter(request, response);
    }
}



////STEP 3 – JWT Authorization Filter (JwtAuthorizationFilter)
////
////Purpose:
////
////Runs on every request
////
////Reads Authorization: Bearer <token>
////
////Sets authenticated user in SecurityContext
//
//package com.brundhavanam.config.security;
//
//import com.brundhavanam.config.jwt.JwtUtil;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.Collections;
//
//@Component
//public class JwtAuthorizationFilter extends OncePerRequestFilter {
//
//    private final JwtUtil jwtUtil;
//
//    public JwtAuthorizationFilter(JwtUtil jwtUtil) {
//        this.jwtUtil = jwtUtil;
//    }
//
//    @Override
//    protected void doFilterInternal(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            FilterChain filterChain
//    ) throws ServletException, IOException {
//
//        String header = request.getHeader("Authorization");
//
//        if (header != null && header.startsWith("Bearer ")) {
//
//            String token = header.substring(7);
//
//            if (jwtUtil.validateToken(token)) {
//                String mobile = jwtUtil.extractSubject(token);
//
//                UsernamePasswordAuthenticationToken authentication =
//                        new UsernamePasswordAuthenticationToken(
//                                mobile,
//                                null,
//                                Collections.emptyList()
//                        );
//
//                authentication.setDetails(
//                        new WebAuthenticationDetailsSource().buildDetails(request)
//                );
//
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//            }
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}
