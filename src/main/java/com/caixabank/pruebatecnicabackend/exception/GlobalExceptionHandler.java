package com.caixabank.pruebatecnicabackend.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice

public class GlobalExceptionHandler {
    /**
     * Maneja una ApiException y devuelve un ResponseEntity con un cuerpo
     * de tipo ProblemDetail que contiene el mensaje de error y el estado HTTP.
     *
     * @param ex la ApiException a manejar
     * @param request la HttpServletRequest de la petición
     * @return el ResponseEntity con el cuerpo ProblemDetail
     */

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiError> handleApi(ApiException ex, HttpServletRequest request) {

        ApiError body = new ApiError(
                Instant.now(),
                ex.getStatus().value(),
                ex.getStatus().getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI(),
                ex.getCode()
        );

        return ResponseEntity.status(ex.getStatus()).body(body);
    }

    /**
     * Maneja una MethodArgumentNotValidException y devuelve un ResponseEntity con un cuerpo
     * de tipo ProblemDetail que contiene el mensaje de error y el estado HTTP.

     * El cuerpo ProblemDetail contiene la siguiente información:
     *   - title: "Bad Request"
     *   - detail: "Validación inválida"
     *   - instance: URI de la petición
     *   - fields: Mapa con los campos inválidos y sus mensajes de error
     *
     * @param ex la MethodArgumentNotValidException a manejar
     * @param request la HttpServletRequest de la petición
     * @return el ResponseEntity con el cuerpo ProblemDetail
     */

    /**
     * Maneja una MethodArgumentNotValidException y devuelve un ResponseEntity con un cuer
     * de tipo ProblemDetail que contiene el mensaje de error y el estado HTTP.
     *
     * El cuer ProblemDetail contiene la siguiente informaci n:
     *   - title: "Bad Request"
     *   - detail: "Validaci n inv lida"
     *   - instance: URI de la petici n
     *   - fields: Mapa con los campos inv lidos y sus mensajes de error
     *
     * @param ex la MethodArgumentNotValidException a manejar
     * @param request la HttpServletRequest de la petici n
     * @return el ResponseEntity con el cuer ProblemDetail
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {

        Map<String, String> fields = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        f -> String.valueOf(f.getDefaultMessage()),
                        (a, b) -> a + "; " + b
                ));

        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Bad Request");
        pd.setDetail("Validación inválida");
        pd.setInstance(URI.create(request.getRequestURI()));
        pd.setProperty("fields", fields);

        return ResponseEntity.badRequest().body(pd);
    }

    /**
     * Maneja una HttpMessageNotReadableException y devuelve un ResponseEntity con un cuer
     * de tipo ApiError que contiene el mensaje de error y el estado HTTP.
     *
     * El cuer ApiError contiene la siguiente informaci n:
     *   - timestamp: Fecha y hora actual
     *   - status: C digo HTTP de la respuesta
     *   - title: T tulo de la respuesta
     *   - detail: Descripci n del error
     *   - instance: URI de la petici n
     *   - code: C digo interno del error
     *
     * @param ex la HttpMessageNotReadableException a manejar
     * @param request la HttpServletRequest de la petici n
     * @return el ResponseEntity con el cuer ApiError
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {

        String detail = "JSON inválido o mal formado";

        // Caso típico: enum inválido ("ACEPTADO")
        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException ife) {
            if (ife.getTargetType() != null && ife.getTargetType().isEnum()) {
                String field = (ife.getPath() != null && !ife.getPath().isEmpty())
                        ? ife.getPath().get(0).getFieldName()
                        : "campo desconocido";

                Object value = ife.getValue();

                Object[] allowed = ife.getTargetType().getEnumConstants();
                detail = "Valor inválido para '" + field + "': '" + value + "'. Valores permitidos: " +
                        java.util.Arrays.toString(allowed);
            } else {
                detail = ife.getOriginalMessage();
            }
        }

        ApiError body = new ApiError(
                java.time.Instant.now(),
                org.springframework.http.HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                detail,
                request.getRequestURI(),
                "INVALID_JSON"
        );

        return ResponseEntity.badRequest().body(body);
    }
}
