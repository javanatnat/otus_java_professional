package ru.otus.antibruteforce.api.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.otus.antibruteforce.service.IllegalIpv4Exception;

@ControllerAdvice
public class ExceptionHandlerAdvice {
    private static final Logger LOG = LoggerFactory.getLogger(ExceptionHandlerAdvice.class);

    @ExceptionHandler(IllegalIpv4Exception.class)
    public ResponseEntity<Object> handleIpv4Exception(IllegalIpv4Exception exc) {
        LOG.error("handleIpv4Exception: " + exc.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("argument \"ip\" was incorrect");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException exc) {
        LOG.error("handleIllegalArgumentException: " + exc.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
