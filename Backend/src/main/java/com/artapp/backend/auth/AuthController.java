// src/main/java/com/artapp/backend/auth/AuthController.java
package com.artapp.backend.auth;
import com.artapp.backend.auth.dto.LoginRequest;
import com.artapp.backend.auth.dto.RegisterRequest;
import com.artapp.backend.user.User;
import com.artapp.backend.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import com.artapp.backend.config.JwtUtil;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        // Validate email uniqueness
        if (userRepository.existsByEmail(request.getEmail())) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Email already registered");
            return ResponseEntity.badRequest().body(response);
        }
        
        // Create user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setUsername(request.getUsername());
        user.setArtistRole(request.getArtistRole());
        
        userRepository.save(user);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("userId", user.getId().toString());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/test")
    public String test() {
        return "Auth endpoint working";
    }

    // Add to AuthController.java
@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
    
    if (userOpt.isEmpty()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Invalid credentials"));
    }
    
    User user = userOpt.get();
    
    if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Invalid credentials"));
    }
    
    String token = jwtUtil.generateToken(user.getEmail());
    
    Map<String, Object> response = new HashMap<>();
    response.put("token", token);
    response.put("user", Map.of(
        "id", user.getId(),
        "email", user.getEmail(),
        "username", user.getUsername(),
        "artistRole", user.getArtistRole()
    ));
    
    return ResponseEntity.ok(response);
}
}