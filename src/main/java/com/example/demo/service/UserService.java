package com.example.demo.service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.exception.AuthenticationException;
import com.example.demo.exception.UserAlreadyExistsException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public ResponseEntity<String> registerUser(User user) {
        if (userRepository.existsByEmailOrUsername(user.getEmail(), user.getUsername())) {
            throw new UserAlreadyExistsException("User already registered with the provided email or username");
        }

        userRepository.save(user);

        return new ResponseEntity<>("Registration successful", HttpStatus.CREATED);
    }

    public ResponseEntity<Map<String, String>> login(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new AuthenticationException("Invalid username or password"));

        if (!user.getPassword().equals(loginRequest.getPassword())) {
            throw new AuthenticationException("Invalid username or password");
        }

        Map<String, String> response = new HashMap<>();
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());

        String token = JwtUtil.generateToken(user.getUsername());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, "session=" + token + "; HttpOnly; Path=/");

        return ResponseEntity.ok().headers(headers).body(response);
    }
}