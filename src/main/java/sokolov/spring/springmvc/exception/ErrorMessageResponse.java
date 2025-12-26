package sokolov.spring.springmvc.exception;

import java.time.LocalDateTime;

public record ErrorMessageResponse(String error,
        String detail,
        LocalDateTime localDateTime) {
}
