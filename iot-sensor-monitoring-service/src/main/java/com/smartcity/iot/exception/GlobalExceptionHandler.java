package com.smartcity.iot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("null")
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SensorNotFoundException.class)
    public ProblemDetail handleSensorNotFound(SensorNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setType(URI.create("https://smartcity.iot/errors/sensor-not-found"));
        problem.setTitle("Capteur non trouve");
        return problem;
    }

    @ExceptionHandler(SensorAlreadyExistsException.class)
    public ProblemDetail handleSensorAlreadyExists(SensorAlreadyExistsException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setType(URI.create("https://smartcity.iot/errors/sensor-already-exists"));
        problem.setTitle("Capteur deja existant");
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errors.put(fe.getField(), fe.getDefaultMessage());
        }
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Erreur de validation des donnees");
        problem.setType(URI.create("https://smartcity.iot/errors/validation-failed"));
        problem.setTitle("Donnees invalides");
        problem.setProperty("errors", errors);
        return problem;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setType(URI.create("https://smartcity.iot/errors/bad-request"));
        problem.setTitle("Requete invalide");
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, "Une erreur interne est survenue");
        problem.setType(URI.create("https://smartcity.iot/errors/internal-error"));
        problem.setTitle("Erreur interne");
        return problem;
    }
}
