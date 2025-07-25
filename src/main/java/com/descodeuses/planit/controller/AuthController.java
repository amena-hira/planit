package com.descodeuses.planit.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.descodeuses.planit.dto.AuthRequest;
import com.descodeuses.planit.dto.AuthResponse;
import com.descodeuses.planit.model.Utilisateur;
import com.descodeuses.planit.security.JwtUtil;
import com.descodeuses.planit.service.UserService;


@RestController
@RequestMapping("/auth")
public class AuthController {
    // 🔒 Injecte le gestionnaire d’authentification fourni par Spring Security
    // C’est lui qui va vérifier si le couple username/password est correct
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
                
        String token = jwtUtil.generateToken(request.getUsername());

        return ResponseEntity.ok(new AuthResponse(token));
    }

    @Autowired
    private UserService userService;
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Utilisateur utilisateur) {
        System.out.println("username: "+utilisateur.getUsername()+" password: "+ utilisateur.getPassword());
        try {
            Utilisateur user = userService.createUser(utilisateur);
            String token = jwtUtil.generateToken(utilisateur.getUsername());

            return ResponseEntity.ok(new AuthResponse(token));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Registration failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
                
        
    }
    
}
