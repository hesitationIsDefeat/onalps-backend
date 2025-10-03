package dev.onat.onalps.exceptions;

import dev.onat.onalps.dto.response.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleEntityNotFoundException(EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createErrorBody(HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(EntityNotEnabledException.class)
    public ResponseEntity<ErrorResponseDto> handleEntityNotEnabledException(EntityNotEnabledException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(createErrorBody(HttpStatus.FORBIDDEN));
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleEntityAlreadyExistsException(EntityAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body(createErrorBody(HttpStatus.ALREADY_REPORTED));
    }

    @ExceptionHandler(InvalidUUIDStringException.class)
    public ResponseEntity<Object> handleInvalidUUIDStringException(InvalidUUIDStringException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorBody(HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleException(Exception e) {
        System.out.println(e.toString());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorBody(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    private ErrorResponseDto createErrorBody(HttpStatus status) {
        return new ErrorResponseDto(status.value(), LocalDateTime.now());
    }

    // TODO: write a function to create a ResponseEntity
}
