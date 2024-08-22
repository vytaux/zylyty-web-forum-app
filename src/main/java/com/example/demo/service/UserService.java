package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.model.request.LoginRequest;
import com.example.demo.model.request.RegisterRequest;
import com.example.demo.model.response.LoginResponse;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtService jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final EncryptionService encryptionService;

    public ResponseEntity<String> registerUser(RegisterRequest request) {
        String encryptedEmail = encryptionService.encrypt(request.getEmail());

        if (userRepository.existsByEmailOrUsername(encryptedEmail, request.getUsername())) {
            return new ResponseEntity<>("User already exists", HttpStatus.I_AM_A_TEAPOT);
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(encryptedEmail);

        userRepository.save(user);

        return new ResponseEntity<>("Registration successful", HttpStatus.CREATED);
    }

    public ResponseEntity<LoginResponse> login(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername()).orElse(null);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String decryptedEmail = encryptionService.decrypt(user.getEmail());
        if (decryptedEmail == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        LoginResponse response = new LoginResponse();
        response.setUsername(user.getUsername());
        response.setEmail(decryptedEmail);

        String token = jwtUtil.generateToken(user.getUsername());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, "session=" + token + "; HttpOnly; Path=/");

        return ResponseEntity.ok().headers(headers).body(response);
    }
}