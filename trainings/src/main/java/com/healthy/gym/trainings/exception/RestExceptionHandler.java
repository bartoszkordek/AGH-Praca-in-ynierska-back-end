package com.healthy.gym.trainings.exception;

import com.healthy.gym.trainings.component.Translator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String TIMESTAMP = "timestamp";
    private static final String STATUS = "status";
    private static final String ERROR = "error";
    private static final String MESSAGE = "message";
    private static final String ERRORS = "errors";

    private final Translator translator;

    @Autowired
    public RestExceptionHandler(Translator translator) {
        this.translator = translator;
    }

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<Object> handleAccessDeniedException() {
        String reason = translator.toLocale("exception.access.denied");

        Map<String, Object> body = new HashMap<>();
        body.put(TIMESTAMP, LocalDateTime.now());
        body.put(STATUS, HttpStatus.FORBIDDEN.value());
        body.put(ERROR, HttpStatus.FORBIDDEN.getReasonPhrase());
        body.put(MESSAGE, reason);

        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({ResponseBindException.class})
    public ResponseEntity<Object> handleBindException(ResponseBindException exception) {
        String reason = exception.getReason();
        HttpStatus httpStatus = exception.getHttpStatus();
        BindException bindException = exception.getException();

        Map<String, Object> body = new HashMap<>();
        body.put(TIMESTAMP, LocalDateTime.now());
        body.put(STATUS, httpStatus.value());
        body.put(ERROR, httpStatus.getReasonPhrase());
        body.put(MESSAGE, reason);
        body.put(ERRORS, getBindExceptionErrorMessages(bindException));

        return ResponseEntity
                .status(httpStatus)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_ENCODING, StandardCharsets.UTF_8.name())
                .body(body);
    }

    private Map<String, String> getBindExceptionErrorMessages(BindException exception) {
        return exception.getFieldErrors()
                .stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException exception) {
        Set<ConstraintViolation<?>> exceptions = exception.getConstraintViolations();

        String reason = translator.toLocale("exception.constraint.violation");
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        Map<String, String> errors = exceptions
                .stream()
                .collect(
                        Collectors.toMap(
                                constraintViolation -> constraintViolation.getPropertyPath()
                                        .toString().split("\\.")[1],
                                ConstraintViolation::getMessage
                        )
                );

        Map<String, Object> body = new HashMap<>();
        body.put(TIMESTAMP, LocalDateTime.now());
        body.put(STATUS, httpStatus.value());
        body.put(ERROR, httpStatus.getReasonPhrase());
        body.put(MESSAGE, reason);
        body.put(ERRORS, errors);

        return ResponseEntity
                .status(httpStatus)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_ENCODING, StandardCharsets.UTF_8.name())
                .body(body);
    }
}
