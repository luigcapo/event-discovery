package com.yuewie.apievent.controller;

import com.yuewie.apievent.exception.ApiError;
import com.yuewie.apievent.exception.BadRequestException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.time.ZoneId;

@ControllerAdvice
public class RestControllerExceptionHandler {

    //souvent true dans dev et false en prod
    @Value("${api.error.include-debug-message:false}") // false par défaut
    private boolean includeDebugMessage;

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleEntityNotFound(EntityNotFoundException ex) {
        ApiError apiError = new ApiError(
                HttpStatus.NOT_FOUND,
                "Ressource non trouvée"
        );
        if (includeDebugMessage) {
            apiError.setDebugMessage(ex.getLocalizedMessage());
        }
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }


    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(BadRequestException ex) {
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Validation échouée"
        );
        if (includeDebugMessage) {
            apiError.setDebugMessage(ex.getLocalizedMessage());
        }
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    // Intercepte les erreurs de validation (ex: @Min(1) sur le @PathVariable)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex) {
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Validation échouée"
        );
        if (includeDebugMessage) {
            apiError.setDebugMessage(ex.getLocalizedMessage());
        }
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    // Intercepte les erreurs de requestBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(MethodArgumentNotValidException ex) {
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Validation échouée"
        );
        if (includeDebugMessage) {
            apiError.setDebugMessage(ex.getLocalizedMessage());
        }
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    /**
     * Pour les erreurs de parsing d'enum ou type dans le requestBody.(avant la creation validation dto)
     * Uniquement body/JSON (pas le cas pour les  query paramètres de requête).
     *
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleEnumParseError(HttpMessageNotReadableException ex) {
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                ex.getLocalizedMessage()
        );
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

}
