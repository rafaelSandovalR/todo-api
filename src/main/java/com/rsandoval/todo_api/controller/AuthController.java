package com.rsandoval.todo_api.controller;

import com.rsandoval.todo_api.dto.LoginRequest;
import com.rsandoval.todo_api.dto.RegisterRequest;
import com.rsandoval.todo_api.model.User;
import com.rsandoval.todo_api.repository.UserRepository;
import com.rsandoval.todo_api.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService,
                          AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        // Check if username already exists
        if (userRepository.findByUsername(request.username()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already taken");
        }

        User newUser = new User();
        newUser.setUsername(request.username());
        String encryptedPassword = passwordEncoder.encode(request.password());
        newUser.setPassword(encryptedPassword);
        newUser.setRole("USER");
        userRepository.save(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        // Checks the username and hashed password against the db
        // If it fails, it throws a 403 Forbidden exception
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        // If successful, grab the UserDetails object
        UserDetails user = (UserDetails) authentication.getPrincipal();
        // Generate token
        String token = jwtService.generateToken(user);
        // Hand it to the user
        return ResponseEntity.ok(token);
    }
}
