package com.example.demo.model.request;

import com.opencsv.bean.CsvBindByName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).{8,50}$",
            message = "Must be 8-50 characters long and contain at least one uppercase letter and one number")
    private String password;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Size(max = 50, message = "Email must be less than 50 characters")
    private String email;
}