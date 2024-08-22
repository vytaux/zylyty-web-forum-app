package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.model.request.RegisterRequest;
import com.example.demo.repository.UserRepository;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.RFC4180Parser;
import com.opencsv.RFC4180ParserBuilder;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CsvImportService {

    @Value("${admin.api.key}")
    private String adminApiKey;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Validator validator;
    private final EncryptionService encryptionService;

    private final PlatformTransactionManager transactionManager;
    private final EntityManager entityManager;

    private static final Logger logger = LoggerFactory.getLogger(CsvImportService.class);

    public ResponseEntity<Void> importUsersViaCsv(String token, String csvData) {
        if (!token.equals(adminApiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Set<String> usernames = new HashSet<>();
        Set<String> emails = new HashSet<>();
        List<User> usersToPersist = new ArrayList<>();

        final RFC4180Parser rfc4180Parser = new RFC4180ParserBuilder().build();
        try (final CSVReader csvReader = new CSVReaderBuilder(new StringReader(csvData))
                .withCSVParser(rfc4180Parser)
                .withSkipLines(1)
                .build()
        ) {
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                System.out.println(String.join(" | ", line));

                RegisterRequest request = new RegisterRequest(line[0], line[1], line[2]);

                Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
                if (!violations.isEmpty()) {
                    throw new Exception("Validation failed");
                }

                if (usernames.contains(request.getUsername()) || emails.contains(request.getEmail())) {
                    throw new Exception("Invalid data");
                }

                String encryptedEmail = encryptionService.encrypt(request.getEmail());

                if (userRepository.existsByEmailOrUsername(encryptedEmail, request.getUsername())) {
                    throw new Exception("Username or email already exists");
                }

                User user = new User();
                user.setUsername(request.getUsername());
                user.setPassword(passwordEncoder.encode(request.getPassword()));
                user.setEmail(encryptedEmail);

                usersToPersist.add(user);
                usernames.add(request.getUsername());
                emails.add(request.getEmail());
            }
        } catch (Exception e) {
            logger.error("Error processing CSV data: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (usersToPersist.isEmpty()) {
            return ResponseEntity.ok().build();
        }

        TransactionStatus transactionStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            int batchSize = 20; // Adjust batch size as needed
            for (int i = 0; i < usersToPersist.size(); i += batchSize) {
                int end = Math.min(i + batchSize, usersToPersist.size());
                List<User> batch = usersToPersist.subList(i, end);
                userRepository.saveAll(batch);
                // Explicitly flush and clear after each batch
                entityManager.flush();
                entityManager.clear();
            }
            transactionManager.commit(transactionStatus);
        } catch (Exception e) {
            transactionManager.rollback(transactionStatus);
            logger.error("Error persisting users: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}