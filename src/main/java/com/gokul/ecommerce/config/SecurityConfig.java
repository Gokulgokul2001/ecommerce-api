package com.gokul.ecommerce.config;

import com.gokul.ecommerce.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter) {

        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                // =========================
                // CSRF + CORS
                // =========================

                .csrf(csrf -> csrf.disable())
                .cors(cors -> {})

                // =========================
                // SESSION
                // =========================

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // =========================
                // AUTHORIZATION
                // =========================

                .authorizeHttpRequests(auth -> auth

                        // =========================
                        // AUTH
                        // =========================

                        .requestMatchers(
                                "/api/auth/register",
                                "/api/auth/login"
                        ).permitAll()

                        // =========================
                        // SWAGGER
                        // =========================

                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // =========================
                        // PRODUCTS
                        // =========================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/products",
                                "/api/products/**"
                        ).hasAnyAuthority(
                                "ROLE_CUSTOMER",
                                "ROLE_ADMIN"
                        )

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/products",
                                "/api/products/**"
                        ).hasAuthority("ROLE_ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/products",
                                "/api/products/**"
                        ).hasAuthority("ROLE_ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/products",
                                "/api/products/**"
                        ).hasAuthority("ROLE_ADMIN")

                        // =========================
                        // CATEGORIES
                        // =========================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/categories",
                                "/api/categories/**"
                        ).hasAnyAuthority(
                                "ROLE_CUSTOMER",
                                "ROLE_ADMIN"
                        )

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/categories",
                                "/api/categories/**"
                        ).hasAuthority("ROLE_ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/categories",
                                "/api/categories/**"
                        ).hasAuthority("ROLE_ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/categories",
                                "/api/categories/**"
                        ).hasAuthority("ROLE_ADMIN")

                        // =========================
                        // CART
                        // =========================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/cart",
                                "/api/cart/**"
                        ).hasAnyAuthority(
                                "ROLE_CUSTOMER",
                                "ROLE_ADMIN"
                        )

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/cart",
                                "/api/cart/**"
                        ).hasAnyAuthority(
                                "ROLE_CUSTOMER",
                                "ROLE_ADMIN"
                        )

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/cart",
                                "/api/cart/**"
                        ).hasAnyAuthority(
                                "ROLE_CUSTOMER",
                                "ROLE_ADMIN"
                        )

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/cart",
                                "/api/cart/**"
                        ).hasAnyAuthority(
                                "ROLE_CUSTOMER",
                                "ROLE_ADMIN"
                        )

                        // =========================
                        // ADMIN ORDERS
                        // =========================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/orders/admin"
                        ).hasAuthority("ROLE_ADMIN")

                        // =========================
                        // ORDER STATUS
                        // =========================

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/orders/*/status"
                        ).hasAuthority("ROLE_ADMIN")

                        // =========================
                        // ADMIN
                        // =========================

                        .requestMatchers(
                                "/api/admin/**"
                        ).hasAuthority("ROLE_ADMIN")

                        // =========================
                        // EVERYTHING ELSE
                        // =========================

                        .anyRequest().authenticated()
                )

                // =========================
                // JWT FILTER
                // =========================

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    // =========================
    // CORS CONFIGURATION
    // =========================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        // React frontend URLs
        configuration.setAllowedOrigins(
                List.of(
                        "http://localhost:5173",
                        "http://localhost:5174",
                        "https://ecommerce-frontend-blush-five.vercel.app"
                )
        );

        // Allowed HTTP methods
        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "OPTIONS"
                )
        );

        // Allowed request headers
        configuration.setAllowedHeaders(
                List.of(
                        "Authorization",
                        "Content-Type"
                )
        );

        // Allow credentials
        configuration.setAllowCredentials(true);

        // Apply CORS configuration to all endpoints
        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }
}