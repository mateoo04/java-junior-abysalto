package hr.abysalto.hiring.api.junior.exception;

import hr.abysalto.hiring.api.junior.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ApiExceptionHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ApiErrorResponse> handleResponseStatus(
			ResponseStatusException ex,
			HttpServletRequest request) {
		HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
		String message = ex.getReason() == null ? status.getReasonPhrase() : ex.getReason();
		return ResponseEntity.status(status).body(toError(status, message, request));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleValidation(
			MethodArgumentNotValidException ex,
			HttpServletRequest request) {
		String message = ex.getBindingResult().getFieldErrors().stream()
				.findFirst()
				.map(error -> error.getField() + ": " + error.getDefaultMessage())
				.orElse("Validation failed");
		return ResponseEntity.badRequest().body(toError(HttpStatus.BAD_REQUEST, message, request));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiErrorResponse> handleUnreadableMessage(
			HttpMessageNotReadableException ex,
			HttpServletRequest request) {
		return ResponseEntity.badRequest()
				.body(toError(HttpStatus.BAD_REQUEST, "Request body is invalid or unreadable", request));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
		LOGGER.error("Unexpected API error", ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(toError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", request));
	}

	private ApiErrorResponse toError(HttpStatus status, String message, HttpServletRequest request) {
		return new ApiErrorResponse(
				LocalDateTime.now(),
				status.value(),
				status.getReasonPhrase(),
				message,
				request.getRequestURI());
	}
}
