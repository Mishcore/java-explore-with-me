package ru.practicum.ewm.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.Constants.TIME_FORMATTER;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiError {

    private String message;

    private String reason;

    private HttpStatus status;

    private final String timestamp = LocalDateTime.now().format(TIME_FORMATTER);

    private List<String> errors;
}
