package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class CsvImportService {

    @Value("${admin.api.key}")
    private String adminApiKey;

    private final UserRepository userRepository;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Logger logger = LoggerFactory.getLogger(CsvImportService.class);

    public ResponseEntity<Void> importUsersViaCsv(String token, String csvData) {
        if (!token.equals(adminApiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (csvData == null || csvData.isEmpty()) {
            return ResponseEntity.ok().build();
        }

        Set<String> usernames = new HashSet<>();
        Set<String> emails = new HashSet<>();

        String line;
        try (BufferedReader reader = new BufferedReader(new StringReader(csvData))) {
            while ((line = reader.readLine()) != null) {
                try {
                    String[] fields = line.split(",");
                    if (fields.length != 3) {
                        continue; // Skip invalid lines
                    }

                    String username = fields[0].trim();
                    String password = fields[1].trim();
                    String email = fields[2].trim();

                    if (usernames.contains(username) || emails.contains(email) || !EMAIL_PATTERN.matcher(email).matches()) {
                        continue; // Skip invalid or duplicate entries
                    }

                    User user = new User();
                    user.setUsername(username);
                    user.setPassword(password);
                    user.setEmail(email);

                    userRepository.save(user);

                    usernames.add(username);
                    emails.add(email);
                } catch (Exception e) {
                    logger.error("Error processing line: {}", line);
                }
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (usernames.isEmpty()) {
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}