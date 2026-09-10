package com.gokul.ecommerce.controller;

import com.gokul.ecommerce.dto.AuthRequest;
import com.gokul.ecommerce.dto.AuthResponse;
import com.gokul.ecommerce.dto.LoginRequest;
import com.gokul.ecommerce.exception.ResourceNotFoundException;
import com.gokul.ecommerce.service.AuthService;
import com.gokul.ecommerce.service.JwtService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void register_shouldRegisterUserSuccessfully() throws Exception {

        // Arrange
        AuthResponse response =
                new AuthResponse(
                        null,
                        "Registration successful"
                );

        when(authService.register(any(AuthRequest.class)))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "name": "Gokul",
                                            "email": "customer@example.com",
                                            "password": "Password@123"
                                        }
                                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isEmpty())
                .andExpect(jsonPath("$.message")
                        .value("Registration successful"));

        verify(authService, times(1))
                .register(any(AuthRequest.class));
    }
    @Test
    void login_shouldLoginUserSuccessfully() throws Exception {

        // Arrange
        AuthResponse response =
                new AuthResponse(
                        "test-jwt-token",
                        "Login successful"
                );

        when(authService.login(any(LoginRequest.class)))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                        "email": "customer@example.com",
                                        "password": "Password@123"
                                    }
                                    """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token")
                        .value("test-jwt-token"))
                .andExpect(jsonPath("$.message")
                        .value("Login successful"));

        verify(authService, times(1))
                .login(any(LoginRequest.class));
    }
    @Test
    void register_shouldThrowExceptionWhenEmailAlreadyRegistered() {

        // Arrange
        when(authService.register(any(AuthRequest.class)))
                .thenThrow(new RuntimeException("Email already registered"));

        AuthRequest request = new AuthRequest();
        request.setName("Gokul");
        request.setEmail("customer@example.com");
        request.setPassword("Password@123");

        // Act & Assert
        RuntimeException exception =
                org.junit.jupiter.api.Assertions.assertThrows(
                        RuntimeException.class,
                        () -> authService.register(request)
                );

        org.junit.jupiter.api.Assertions.assertEquals(
                "Email already registered",
                exception.getMessage()
        );

        verify(authService, times(1))
                .register(any(AuthRequest.class));
    }
    @Test
    void login_shouldThrowExceptionWhenUserNotFound() {

        // Arrange
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(
                        new ResourceNotFoundException(
                                "Invalid email or password"
                        )
                );

        LoginRequest request = new LoginRequest();
        request.setEmail("unknown@example.com");
        request.setPassword("Password@123");

        // Act & Assert
        ResourceNotFoundException exception =
                org.junit.jupiter.api.Assertions.assertThrows(
                        ResourceNotFoundException.class,
                        () -> authService.login(request)
                );

        org.junit.jupiter.api.Assertions.assertEquals(
                "Invalid email or password",
                exception.getMessage()
        );

        verify(authService, times(1))
                .login(any(LoginRequest.class));
    }
    @Test
    void login_shouldThrowExceptionWhenPasswordIsInvalid() {

        // Arrange
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(
                        new RuntimeException(
                                "Invalid email or password"
                        )
                );

        LoginRequest request = new LoginRequest();
        request.setEmail("customer@example.com");
        request.setPassword("WrongPassword");

        // Act & Assert
        RuntimeException exception =
                org.junit.jupiter.api.Assertions.assertThrows(
                        RuntimeException.class,
                        () -> authService.login(request)
                );

        org.junit.jupiter.api.Assertions.assertEquals(
                "Invalid email or password",
                exception.getMessage()
        );

        verify(authService, times(1))
                .login(any(LoginRequest.class));
    }
    @Test
    void register_shouldReturn400WhenRequestIsInvalid() throws Exception {

        // Act & Assert
        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                        "name": "",
                                        "email": "",
                                        "password": ""
                                    }
                                    """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Validation failed"));

        verify(authService, times(0))
                .register(any(AuthRequest.class));
    }
    @Test
    void login_shouldReturn400WhenRequestIsInvalid() throws Exception {

        // Act & Assert
        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                        "email": "",
                                        "password": ""
                                    }
                                    """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Validation failed"));

        verify(authService, times(0))
                .login(any(LoginRequest.class));
    }
    @Test
    void register_shouldReturn400WhenEmailIsMissing() throws Exception {

        // Act & Assert
        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                        "name": "Gokul",
                                        "password": "Password@123"
                                    }
                                    """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Validation failed"));

        verify(authService, times(0))
                .register(any(AuthRequest.class));
    }
    @Test
    void login_shouldReturn400WhenEmailIsMissing() throws Exception {

        // Act & Assert
        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                        "password": "Password@123"
                                    }
                                    """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Validation failed"));

        verify(authService, times(0))
                .login(any(LoginRequest.class));
    }
    @Test
    void register_shouldReturn400WhenPasswordIsMissing() throws Exception {

        // Act & Assert
        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                        "name": "Gokul",
                                        "email": "customer@example.com"
                                    }
                                    """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Validation failed"));

        verify(authService, times(0))
                .register(any(AuthRequest.class));
    }
}