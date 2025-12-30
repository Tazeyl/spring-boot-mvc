package sokolov.spring.springmvc.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionValidation {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionValidation.class);
    private static final String GOT_EXCEPTION = "Got exception";

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorMessageResponse> handleNoSuchElementException(NoSuchElementException e){
        log.error(GOT_EXCEPTION, e);
        var errorDto =  new ErrorMessageResponse(
                "Сущность не найдена",
                e.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorDto);


    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessageResponse> handleValidationException(MethodArgumentNotValidException e){
        log.error(GOT_EXCEPTION, e);
        String detailedMessage = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));
        var errorDto =  new ErrorMessageResponse(
                "Ошибка валидации запроса",
                detailedMessage,
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorDto);


    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessageResponse> handleException(Exception e){
        log.error(GOT_EXCEPTION, e);
        var errorDto =  new ErrorMessageResponse(
                "Внутренняя ошибка",
                e.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorDto);


    }
}
