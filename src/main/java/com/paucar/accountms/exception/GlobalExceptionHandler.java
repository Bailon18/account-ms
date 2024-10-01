package com.paucar.accountms.exception;

import com.paucar.accountms.util.ApiResponse;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccountAlreadyExistsException(AccountAlreadyExistsException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccountNotFoundException(AccountNotFoundException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomerNotFoundException(CustomerNotFoundException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ApiResponse<Void>> handleInsufficientBalanceException(InsufficientBalanceException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String errorMessage = String.format("El valor '%s' no es válido para el parámetro '%s'. Se esperaba un valor de tipo '%s'.",
                ex.getValue(), ex.getName(), ex.getRequiredType().getSimpleName());
        return buildErrorResponse(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Se encontraron errores de validación.")
                .data(errors)
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        ApiResponse<Void> errorResponse = ApiResponse.<Void>builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message("El recurso solicitado no fue encontrado. Verifique la URL.")
                .data(null)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }



    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ApiResponse<Void>> handleFeignStatusException(FeignException ex) {
        if (ex.status() == 404) {
            return buildErrorResponse("El cliente no existe o no fue encontrado en el servicio de clientes.", HttpStatus.NOT_FOUND);
        }
        return buildErrorResponse("Error al comunicarse con otro microservicio: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGlobalException(Exception ex) {
        return buildErrorResponse("Ocurrió un error inesperado. Por favor, intente más tarde.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ApiResponse<Void>> buildErrorResponse(String message, HttpStatus status) {
        ApiResponse<Void> errorResponse = ApiResponse.<Void>builder()
                .status(status.value())
                .message(message)
                .data(null)
                .build();

        return new ResponseEntity<>(errorResponse, status);
    }
}
