package co.com.crediya.api.config;

import co.com.crediya.model.exception.UserCustomException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserCustomException.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(UserCustomException ex) {
        // Log the exception if needed
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
        errorDetails.put("status", HttpStatus.BAD_REQUEST.value());
        errorDetails.put("message", ex.getMessage());
        errorDetails.put("code", ex.getCode());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorDetails);
    }

    // Add more handlers as needed
}