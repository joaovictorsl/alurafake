package br.com.alura.AluraFake.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import br.com.alura.AluraFake.util.exceptions.ConflictException;
import br.com.alura.AluraFake.util.exceptions.EntityNotFoundException;
import br.com.alura.AluraFake.util.exceptions.InvalidArgumentException;
import br.com.alura.AluraFake.util.exceptions.InvalidStateException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ExceptionDTO> handleValidationExceptions(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionDTO(ex.getMessage()));
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ExceptionDTO> handleValidationExceptions(ConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ExceptionDTO(ex.getMessage()));
    }

    @ExceptionHandler(InvalidStateException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<ExceptionDTO> handleValidationExceptions(InvalidStateException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ExceptionDTO(ex.getMessage()));
    }

    @ExceptionHandler(InvalidArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ExceptionDTO> handleValidationExceptions(InvalidArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionDTO(ex.getMessage()));
    }
}