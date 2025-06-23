package com.mangxahoi.mangxahoi_backend.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {
    private HttpStatus status;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime timestamp;
    
    private String message;
    private String debugMessage;
    private List<String> errors;
    
    public ApiError(HttpStatus status) {
        this.status = status;
        this.timestamp = LocalDateTime.now();
        this.errors = new ArrayList<>();
    }
    
    public ApiError(HttpStatus status, String message) {
        this.status = status;
        this.timestamp = LocalDateTime.now();
        this.message = message;
        this.errors = new ArrayList<>();
    }
    
    public ApiError(HttpStatus status, String message, Throwable ex) {
        this.status = status;
        this.timestamp = LocalDateTime.now();
        this.message = message;
        this.debugMessage = ex.getLocalizedMessage();
        this.errors = new ArrayList<>();
    }
    
    public void addError(String error) {
        this.errors.add(error);
    }
} 