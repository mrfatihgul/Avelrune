package com.avelrune.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank
    @Size(min = 2, max = 120)
    @Schema(example = "Yeni Kullanici")
    private String fullName;

    @NotBlank
    @Email
    @Schema(example = "yeni@avelrune.com")
    private String email;

    @NotBlank
    @Size(min = 8, max = 80)
    @Schema(example = "AvelruneUser2026!")
    private String password;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
