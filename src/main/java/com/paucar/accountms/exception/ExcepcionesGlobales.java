package com.paucar.accountms.exception;

import com.paucar.accountms.util.ApiResponse;
import feign.FeignException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ExcepcionesGlobales {

    @ExceptionHandler(CuentaYaExisteException.class)
    public ResponseEntity<ApiResponse<Void>> manejarExcepcionCuentaYaExiste(CuentaYaExisteException ex) {
        return construirRespuestaError(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CuentaNoEncontradaException.class)
    public ResponseEntity<ApiResponse<Void>> manejarExcepcionCuentaNoEncontrada(CuentaNoEncontradaException ex) {
        return construirRespuestaError(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ClienteNoEncontradoException.class)
    public ResponseEntity<ApiResponse<Void>> manejarExcepcionClienteNoEncontrado(ClienteNoEncontradoException ex) {
        return construirRespuestaError(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> manejarExcepcionArgumentoInvalido(IllegalArgumentException ex) {
        return construirRespuestaError(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SaldoInsuficienteException.class)
    public ResponseEntity<ApiResponse<Void>> manejarExcepcionSaldoInsuficiente(SaldoInsuficienteException ex) {
        return construirRespuestaError(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> manejarExcepcionTipoArgumento(MethodArgumentTypeMismatchException ex) {
        String mensajeError = String.format("El valor '%s' no es válido para el parámetro '%s'. Se esperaba un valor de tipo '%s'.",
                ex.getValue(), ex.getName(), ex.getRequiredType().getSimpleName());
        return construirRespuestaError(mensajeError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> manejarExcepcionesValidacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> errores.put(error.getField(), error.getDefaultMessage()));

        ApiResponse<Map<String, String>> respuesta = ApiResponse.<Map<String, String>>builder()
                .estado(HttpStatus.BAD_REQUEST.value())
                .mensaje("Se encontraron errores de validación.")
                .datos(errores)
                .build();

        return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Void>> manejarExcepcionRecursoNoEncontrado(NoHandlerFoundException ex) {
        ApiResponse<Void> respuestaError = ApiResponse.<Void>builder()
                .estado(HttpStatus.NOT_FOUND.value())
                .mensaje("El recurso solicitado no fue encontrado. Verifique la URL.")
                .datos(null)
                .build();

        return new ResponseEntity<>(respuestaError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> manejarExcepcionEstadoIlegal(IllegalStateException ex) {
        return construirRespuestaError(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> manejarExcepcionViolacionRestriccion(ConstraintViolationException ex) {
        // Extraer los mensajes de las violaciones de restricciones y convertirlos en un Map
        Map<String, String> errores = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessage
                ));

        ApiResponse<Map<String, String>> respuesta = ApiResponse.<Map<String, String>>builder()
                .estado(HttpStatus.BAD_REQUEST.value())
                .mensaje("Se encontraron errores de validación.")
                .datos(errores)
                .build();

        return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ApiResponse<Void>> manejarExcepcionFeign(FeignException ex) {
        if (ex.status() == 404) {
            return construirRespuestaError("El cliente no existe o no fue encontrado en el servicio de clientes.", HttpStatus.NOT_FOUND);
        }
        return construirRespuestaError("Error al comunicarse con otro microservicio: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> manejarExcepcionGlobal(Exception ex) {
        return construirRespuestaError("Ocurrió un error inesperado. Por favor, intente más tarde.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ApiResponse<Void>> construirRespuestaError(String mensaje, HttpStatus estado) {
        ApiResponse<Void> respuestaError = ApiResponse.<Void>builder()
                .estado(estado.value())
                .mensaje(mensaje)
                .datos(null)
                .build();

        return new ResponseEntity<>(respuestaError, estado);
    }
}
