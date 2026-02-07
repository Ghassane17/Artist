
package com.artapp.backend.auth.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private String username;
    private String artistRole;
}