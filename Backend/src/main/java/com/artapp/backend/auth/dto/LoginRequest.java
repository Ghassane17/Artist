// src/main/java/com/artapp/backend/auth/dto/LoginRequest.java
package com.artapp.backend.auth.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
