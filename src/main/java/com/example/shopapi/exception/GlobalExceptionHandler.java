package com.example.shopapi.exception;

import com.example.shopapi.model.ErrorDetail;
import com.example.shopapi.model.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.util.List;

/** Chuẩn hóa lỗi API theo schema ErrorResponse. */
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("NOT_FOUND", exception.getMessage(), List.of()));
    }

    @ExceptionHandler(UnprocessableEntityException.class)
    public ResponseEntity<ErrorResponse> handleUnprocessable(UnprocessableEntityException exception) {
        return ResponseEntity.status(422)
                .body(new ErrorResponse("UNPROCESSABLE_ENTITY", exception.getMessage(),
                        List.of(new ErrorDetail("items", exception.getMessage()))));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
        List<ErrorDetail> details = exception.getBindingResult().getFieldErrors().stream()
                .map(this::toErrorDetail)
                .toList();
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("VALIDATION_ERROR", "Request validation failed", details));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException exception) {
        List<ErrorDetail> details = exception.getConstraintViolations().stream()
                .map(violation -> new ErrorDetail(
                        violation.getPropertyPath().toString(), violation.getMessage()))
                .toList();
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("VALIDATION_ERROR", "Request validation failed", details));
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            HttpMediaTypeNotSupportedException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception exception) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("BAD_REQUEST", "Invalid request", List.of(
                        new ErrorDetail("request", "Request parameters or JSON body are invalid"))));
    }

    @ExceptionHandler({HandlerMethodValidationException.class, IllegalArgumentException.class})
    public ResponseEntity<ErrorResponse> handleInvalidValue(Exception exception) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("VALIDATION_ERROR", "Request validation failed", List.of(
                        new ErrorDetail("request", exception.getMessage()))));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception exception) {
        System.getLogger(GlobalExceptionHandler.class.getName())
                .log(System.Logger.Level.ERROR, "Unexpected API error", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred", List.of()));
    }

    private ErrorDetail toErrorDetail(FieldError error) {
        return new ErrorDetail(error.getField(), error.getDefaultMessage() == null
                ? "Invalid value" : error.getDefaultMessage());
    }
}
