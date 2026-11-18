package com.pickit.backend.common;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/** 오류도 성공 응답과 같은 형식으로 내려준다. data는 null이다. */
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        String message = ex instanceof ResponseStatusException e && e.getReason() != null
                ? e.getReason()
                : HttpStatus.valueOf(status.value()).getReasonPhrase();
        return new ResponseEntity<>(new ApiResponse<>(status.value(), message, null), headers, status);
    }
}
