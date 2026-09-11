package com.gokul.ecommerce.security;

import com.gokul.ecommerce.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader =
                request.getHeader("Authorization");

        // ---------------------------------------------------------
        // 1. Check whether Authorization header exists
        // ---------------------------------------------------------
        if (authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ")) {

            System.out.println("JWT FILTER: No Bearer token");

            filterChain.doFilter(request, response);
            return;
        }

        // ---------------------------------------------------------
        // 2. Extract JWT token
        // ---------------------------------------------------------
        String token =
                authorizationHeader.substring(7);

        // ---------------------------------------------------------
        // 3. Validate JWT
        // ---------------------------------------------------------
        if (!jwtService.validateToken(token)) {

            System.out.println(
                    "JWT FILTER: Invalid or expired token"
            );

            filterChain.doFilter(request, response);
            return;
        }

        // ---------------------------------------------------------
        // 4. Extract email and role from JWT
        // ---------------------------------------------------------
        String email =
                jwtService.extractEmail(token);

        String role =
                jwtService.extractRole(token);

        System.out.println("JWT EMAIL: " + email);
        System.out.println("JWT ROLE: " + role);

        System.out.println(
                "REQUEST: "
                        + request.getMethod()
                        + " "
                        + request.getRequestURI()
        );

        // ---------------------------------------------------------
        // 5. Create Spring Security authority
        // ---------------------------------------------------------

        /*
         * Database role:
         *
         * CUSTOMER
         * ADMIN
         *
         * Spring Security authority:
         *
         * ROLE_CUSTOMER
         * ROLE_ADMIN
         */

        String authorityName;

        if (role.startsWith("ROLE_")) {
            authorityName = role;
        } else {
            authorityName = "ROLE_" + role;
        }

        SimpleGrantedAuthority authority =
                new SimpleGrantedAuthority(authorityName);

        // ---------------------------------------------------------
        // 6. Create authenticated user
        // ---------------------------------------------------------
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        List.of(authority)
                );

        System.out.println(
                "AUTHORITIES: "
                        + authentication.getAuthorities()
        );

        // ---------------------------------------------------------
        // 7. Store authentication in SecurityContext
        // ---------------------------------------------------------
        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);

        // ---------------------------------------------------------
        // 8. Continue request
        // ---------------------------------------------------------
        filterChain.doFilter(request, response);
    }
}